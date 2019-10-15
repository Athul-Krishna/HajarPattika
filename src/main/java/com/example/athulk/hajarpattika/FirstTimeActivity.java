package com.example.athulk.hajarpattika;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.athulk.hajarpattika.data.SubjectContract;

import java.util.Calendar;

public class FirstTimeActivity extends AppCompatActivity {

    public static final String FIRST_TIME = "true";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(this, NotifyManager.class);
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        //cal.add(Calendar.SECOND, 10);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, broadcast);

        SharedPreferences sharedPreferences = getSharedPreferences(FIRST_TIME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(FIRST_TIME, false).apply();

        Thread splash = new Thread(){
            @Override
            public void run() {
                try{
                    sleep(8*1000);
                    Intent intent = new Intent(getBaseContext(),MainActivity.class);
                    finish();
                    startActivity(intent);
                }catch(Exception e){}
            }
        };
        splash.start();

        insertSubject("IT & Coding");
        insertSubject("Microwave & Radar");
        insertSubject("Optical Comm.");
        insertSubject("Computer Comm.");
        insertSubject("Control Systems");
        insertSubject("Elective");
        insertSubject("Seminar & Project");
        insertSubject("Communication Lab");

        Toast.makeText(this, "Successfully set up the database", Toast.LENGTH_SHORT).show();
    }

    private void insertSubject(String name){
        ContentValues values = new ContentValues();
        values.put(SubjectContract.SubjectEntry.COLUMN_NAME, name);
        values.put(SubjectContract.SubjectEntry.COLUMN_ATTEND, 0);
        values.put(SubjectContract.SubjectEntry.COLUMN_BUNK, 0);
        values.put(SubjectContract.SubjectEntry.COLUMN_UPDATE, "Last updated on 01-08-2019");
        Uri newUri = getContentResolver().insert(SubjectContract.SubjectEntry.CONTENT_URI, values);
    }
}
