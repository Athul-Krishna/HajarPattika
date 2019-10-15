package com.example.athulk.hajarpattika;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.athulk.hajarpattika.data.SubjectContract;

import org.w3c.dom.Text;

public class DataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        TextView nameView = (TextView)findViewById(R.id.dataName);
        TextView attendView = (TextView)findViewById(R.id.dataAttend);
        TextView bunkView = (TextView)findViewById(R.id.dataBunk);

        nameView.setText("\n");
        attendView.setText("\n");
        bunkView.setText("\n");

        String[] projection = {
                SubjectContract.SubjectEntry.COLUMN_NAME,
                SubjectContract.SubjectEntry.COLUMN_ATTEND,
                SubjectContract.SubjectEntry.COLUMN_BUNK
        };
        Cursor cursor = getContentResolver().query(SubjectContract.SubjectEntry.CONTENT_URI, projection, null, null, null);
        try{
            int nameColumnIndex = cursor.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_NAME);
            int attendColumnIndex = cursor.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_ATTEND);
            int bunkColumnIndex = cursor.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_BUNK);

            while(cursor.moveToNext()){
                String name = cursor.getString(nameColumnIndex);
                int attend = cursor.getInt(attendColumnIndex);
                int bunk = cursor.getInt(bunkColumnIndex);

                nameView.append(name + "\n");
                attendView.append(String.valueOf(attend) + "\n");
                bunkView.append(String.valueOf(bunk) + "\n");
            }
        }finally {
            cursor.close();
        }
    }
}
