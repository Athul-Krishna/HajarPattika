package com.example.athulk.hajarpattika;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.athulk.hajarpattika.data.SubjectContract;
import com.example.athulk.hajarpattika.data.SubjectProvider;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks{

    private static final int SUBJECT_LOADER = 1;
    private static final int UPDATE_LOADER = 2;
    SubjectCursorAdapter mCursorAdapter;

    private static final String REQUEST_URL = "https://jsonblob.com/api/jsonBlob/af1367b0-b462-11e9-93a0-f3eb5e919b90";
    private static double VERSION = 2.0;
    private double minVersion;
    private String newsString = "Please connect to the internet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences(FirstTimeActivity.FIRST_TIME, Context.MODE_PRIVATE);
        boolean firstTime = sharedPreferences.getBoolean(FirstTimeActivity.FIRST_TIME, true);
        if(firstTime){
            Intent intent = new Intent(this, FirstTimeActivity.class);
            startActivity(intent);
        }

        ListView subjectListView = (ListView)findViewById(R.id.list);
        mCursorAdapter = new SubjectCursorAdapter(MainActivity.this, null);
        subjectListView.setAdapter(mCursorAdapter);

        getLoaderManager().initLoader(SUBJECT_LOADER, null, this);

        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()){
            getLoaderManager().initLoader(UPDATE_LOADER, null,this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.undo:
                undo_previous();
                return true;
            case R.id.data:
                Intent intentData = new Intent(MainActivity.this, DataActivity.class);
                startActivity(intentData);
                return true;
            case R.id.credits:
                Intent intent = new Intent(MainActivity.this, CreditsActivity.class);
                intent.putExtra("news",newsString);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void undo_previous(){

        int i = UndoData.getInstance().getPos();
        SparseIntArray arr = UndoData.getInstance().getArray();

        if(i == -1){
            Toast.makeText(this, "Nothing to Undo", Toast.LENGTH_SHORT).show();
        }
        else if(arr.keyAt(i)%10 == 0){
            decrementAttended(arr.valueAt(i));
            arr.removeAt(i);
            i--;
        }
        else if(arr.keyAt(i)%10 == 5){
            decrementBunked(arr.valueAt(i));
            arr.removeAt(i);
            i--;
        }
        UndoData.getInstance().putPos(i);
        UndoData.getInstance().putArray(arr);
    }

    public void decrementAttended(int pos){
        String[] projection = {
                SubjectContract.SubjectEntry._ID,
                SubjectContract.SubjectEntry.COLUMN_ATTEND
        };
        Cursor cursor = getContentResolver().query(SubjectContract.SubjectEntry.CONTENT_URI, projection, null, null, null);
        int attendedColumnIndex = cursor.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_ATTEND);
        cursor.moveToPosition(pos-1);
        float attended = cursor.getFloat(attendedColumnIndex);
        cursor.close();
        ContentValues values = new ContentValues();
        float attendCountNew = attended-1;
        values.put(SubjectContract.SubjectEntry.COLUMN_ATTEND, attendCountNew);
        int rowsAffected = getContentResolver().update(ContentUris.withAppendedId(SubjectContract.SubjectEntry.CONTENT_URI, pos), values, null, null);
        Toast.makeText(MainActivity.this, "Previous operation was undone", Toast.LENGTH_SHORT).show();
    }

    public void decrementBunked(int pos){
        String[] projection = {
                SubjectContract.SubjectEntry._ID,
                SubjectContract.SubjectEntry.COLUMN_BUNK
        };
        Cursor cursor = getContentResolver().query(SubjectContract.SubjectEntry.CONTENT_URI, projection, null, null, null);
        int bunkedColumnIndex = cursor.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_BUNK);
        cursor.moveToPosition(pos-1);
        float bunked = cursor.getFloat(bunkedColumnIndex);
        cursor.close();
        ContentValues values = new ContentValues();
        float bunkCountNew = bunked-1;
        values.put(SubjectContract.SubjectEntry.COLUMN_BUNK, bunkCountNew);
        int rowsAffected = getContentResolver().update(ContentUris.withAppendedId(SubjectContract.SubjectEntry.CONTENT_URI, pos), values, null, null);
        Toast.makeText(MainActivity.this, "Previous operation was undone", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (id == SUBJECT_LOADER) {
            String[] projection = {
                    SubjectContract.SubjectEntry._ID,
                    SubjectContract.SubjectEntry.COLUMN_NAME,
                    SubjectContract.SubjectEntry.COLUMN_ATTEND,
                    SubjectContract.SubjectEntry.COLUMN_BUNK,
                    SubjectContract.SubjectEntry.COLUMN_UPDATE
            };
            return new CursorLoader(this,
                    SubjectContract.SubjectEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);
        } else if (id == UPDATE_LOADER ) {
            return new UpdateLoader(this, REQUEST_URL);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        int id = loader.getId();// find which loader you called
        if (id == SUBJECT_LOADER) {
            mCursorAdapter.swapCursor((Cursor)data);
        } else if (id == UPDATE_LOADER) {
            UpdateInfo info = (UpdateInfo)data;
            minVersion = info.getmVersion();
            newsString = info.getmNews();
            if(VERSION < minVersion){
                showForceUpdateDialog();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        int id = loader.getId();
        if (id == SUBJECT_LOADER) {
            mCursorAdapter.swapCursor(null);
        } else if (id ==UPDATE_LOADER ) {

        }
    }

    public void showForceUpdateDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("Update required");
        alertDialogBuilder.setMessage("Please update to latest version.");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.show();
    }
}
