package com.example.adminsidedemoproject;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.adminsidedemoproject.Model.AttendanceReportData;
import com.example.adminsidedemoproject.Utility.NetworkChangeListener;

public class ShowCalendar extends AppCompatActivity {
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();

    Toolbar toolbar;
    CustomCalendarView customCalendarView;
    AttendanceReportData attendanceReportData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_calendar);

        toolbar=findViewById(R.id.show_attendance_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        customCalendarView= findViewById(R.id.custom_cal_view);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        //actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        actionbar.setDisplayShowCustomEnabled(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.attendance_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i=new Intent(this,MainActivity.class);
                startActivity(i);
                return true;
            case R.id.get_report:
                /*AttendanceReportData attendanceReportData=customCalendarView.getAttendanceData();
                Log.d("data",attendanceReportData.getCurrentDate());*/
                attendanceReportData = customCalendarView.getAttendanceData();
                if (attendanceReportData != null) {
                    Intent b = new Intent(this, GenerateAttendancePdfActivity.class);
                    b.putExtra("attendance_data", attendanceReportData);
                    startActivity(b);
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong!!", Toast.LENGTH_SHORT).show();
                }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onStart() {
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}