package com.example.athulk.hajarpattika;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.athulk.hajarpattika.data.SubjectContract;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SubjectCursorAdapter extends CursorAdapter {

    Date d;
    Map<String,String> map = new HashMap<String,String>();

    public SubjectCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        TextView subjectText = (TextView)view.findViewById(R.id.subject);
        TextView attendanceText = (TextView)view.findViewById(R.id.attendance);
        TextView statusText = (TextView)view.findViewById(R.id.status);
        TextView updatedText = (TextView)view.findViewById(R.id.last_updated);

        int attendedColumnIndex = cursor.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_ATTEND);
        int bunkedColumnIndex = cursor.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_BUNK);
        int nameColumnIndex = cursor.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_NAME);
        int idColumnIndex = cursor.getColumnIndex(SubjectContract.SubjectEntry._ID);
        int updatedColumnIndex = cursor.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_UPDATE);

        final float attended = cursor.getInt(attendedColumnIndex);
        final float bunked = cursor.getInt(bunkedColumnIndex);
        final String name = cursor.getString(nameColumnIndex);
        final int idCount = cursor.getInt(idColumnIndex);
        final String updated = cursor.getString(updatedColumnIndex);

        float total = attended+bunked;
        String formattedAttendance = "100";
        int status=0;
        float attendance = calcAttendance(attended,total);
        if(attendance == 100){
            formattedAttendance = formatAttendance100(attendance);
        }else{
            formattedAttendance = formatAttendance(attendance);
        }
        if(attendance >= 75){
            status = 1;
        }
        int hours = bunkStatus(attended, bunked, attendance, status);

        subjectText.setText(name);
        attendanceText.setText(formattedAttendance);
        updatedText.setText(updated);

        if(status == 1){
            statusText.setText("Can Bunk "+hours+" Hour(s).");
        }
        else{
            hours++;
            statusText.setText("Attend Next "+hours+" Hour(s).");
        }
        
        Button plusButton = (Button)view.findViewById(R.id.button_plus);
        Button minusButton = (Button)view.findViewById(R.id.button_minus);

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you attended this class?");
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int i = UndoData.getInstance().getPos();
                        SparseIntArray arr = UndoData.getInstance().getArray();
                        // User clicked confirm button
                        if (i == -1) {
                            i++;
                            arr.put(i*10, idCount);
                        }
                        else{
                            i++;
                            arr.append(i*10, idCount);
                        }
                        UndoData.getInstance().putPos(i);
                        UndoData.getInstance().putArray(arr);
                        incrementAttended(context, attended, idCount);

                        d = new Date();
                        String date = d.getDay()+"/"+d.getMonth()+"  "+d.getHours()+":"+d.getMinutes();
                        map.put(date, name);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you bunked this class?");
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int i = UndoData.getInstance().getPos();
                        SparseIntArray arr = UndoData.getInstance().getArray();
                        // User clicked OK button
                        if (i == -1) {
                            i++;
                            arr.put((i*10)+5, idCount);
                        }
                        else{
                            i++;
                            arr.append((i*10)+5, idCount);
                        }
                        UndoData.getInstance().putPos(i);
                        UndoData.getInstance().putArray(arr);
                        incrementBunked(context, bunked, idCount);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private int bunkStatus(float bAttended, float bBunked, float bAttendance, int bStatus){
        int count=-1;
        float total = bAttended+bBunked;
        if(bStatus == 1){
            while(bAttendance >= 75){
                total++;
                count++;
                bAttendance = calcAttendance(bAttended, total);
            }
        }
        else{
            while(bAttendance < 75){
                bAttended++;
                total++;
                count++;
                bAttendance = calcAttendance(bAttended, total);
            }
        }
        return count;
    }

    private float calcAttendance(float cAttended, float cTotal){
        if(cTotal != 0){
            return (cAttended*100)/cTotal;
        }
        else{
            return 100;
        }
    }

    private String formatAttendance(Float attnd){
        DecimalFormat attendanceFormat = new DecimalFormat("00.0");
        return attendanceFormat.format(attnd);
    }

    private String formatAttendance100(Float attnd){
        DecimalFormat attendanceFormat = new DecimalFormat("000");
        return attendanceFormat.format(attnd);
    }

    private void incrementAttended(Context context, float attended, int idCount){
        ContentValues values = new ContentValues();
        float attendCountNew = attended+1;
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat tf = new SimpleDateFormat("h:mm a");
        String formattedDate = "Last updated on "+df.format(c)+" at "+tf.format(c);
        values.put(SubjectContract.SubjectEntry.COLUMN_ATTEND, attendCountNew);
        values.put(SubjectContract.SubjectEntry.COLUMN_UPDATE, formattedDate);
        int rowsAffected = context.getContentResolver().update(ContentUris.withAppendedId(SubjectContract.SubjectEntry.CONTENT_URI, idCount), values, null, null);
        Toast.makeText(context, "Total classes attended: "+attendCountNew, Toast.LENGTH_SHORT).show();
    }

    private void incrementBunked(Context context, float bunked, int idCount){
        ContentValues values = new ContentValues();
        float bunkCountNew = bunked+1;
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat tf = new SimpleDateFormat("h:mm a");
        String formattedDate = "Last updated on "+df.format(c)+" at "+tf.format(c);
        values.put(SubjectContract.SubjectEntry.COLUMN_BUNK, bunkCountNew);
        values.put(SubjectContract.SubjectEntry.COLUMN_UPDATE, formattedDate);
        int rowsAffected = context.getContentResolver().update(ContentUris.withAppendedId(SubjectContract.SubjectEntry.CONTENT_URI, idCount), values, null, null);
        Toast.makeText(context, "Total classes bunked: "+bunkCountNew, Toast.LENGTH_SHORT).show();
    }
}
