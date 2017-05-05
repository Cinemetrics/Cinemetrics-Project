package edu.umkc.ase.cinemetrics;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener{

    TimePicker alarmTimePicker;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;

    Map<Integer,String> alarms=new HashMap<>();

    Button btnDatePicker, btnTimePicker;
    EditText txtDate, txtTime, txtAlarmName;
//    TextView lblAlarmName,  lblAlarmTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    Calendar calendar;
    Intent intent;
    String userName;
    String movieName;
    boolean alarm;
    int alarmId = 0;
    String alarmType="";
    int alarmNumber=0;
    ToggleButton toggleButton;

    public static String alarmActivityStatus = "off";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.widthPixels;
        getWindow().setLayout((int)(width*0.9), (int)(height*0.6));

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        btnDatePicker=(Button)findViewById(R.id.btn_date);
        btnTimePicker=(Button)findViewById(R.id.btn_time);
        txtDate=(EditText)findViewById(R.id.in_date);
        txtTime=(EditText)findViewById(R.id.in_time);
        txtAlarmName =(EditText)findViewById(R.id.txt_alarmName);
//        lblAlarmName = (TextView)findViewById(R.id.lbl_alarmName);
//        lblAlarmTime = (TextView)findViewById(R.id.lbl_alarmTime);
        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        toggleButton = (ToggleButton)findViewById(R.id.toggleButton);
        Bundle bundle = getIntent().getExtras();
        movieName = bundle.getString("moviename");
        userName = bundle.getString("userName");
        alarmType = bundle.getString("alarm");
        if(alarmType.equals("off"))
        {
            alarmNumber = bundle.getInt("id");
            deleteAlarm(alarmNumber);
        }
        txtAlarmName.setText("Set Reminder: "+movieName);

        if(toggleButton.isChecked())
        {
            alarmActivityStatus = "on";
        }
        else
        {
            alarmActivityStatus = "off";
        }
    }

    @Override
    public void onClick(View v) {
        calendar = Calendar.getInstance();
        if (v == btnDatePicker) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            calendar.set(Calendar.MONTH, monthOfYear);
                            calendar.set(Calendar.YEAR, year);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == btnTimePicker) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            txtTime.setText(hourOfDay + ":" + minute);



                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }
    //date time
    public void setAlarm(View view) {
        long time;
        if (((ToggleButton) view).isChecked()) {
            Toast.makeText(AlarmActivity.this, "ALARM ON", Toast.LENGTH_SHORT).show();

            intent = new Intent(this, AlarmReceiver.class);

            Bundle extras = new Bundle();
            extras.putString("movie", txtAlarmName.getText().toString()); //movie name
            intent.putExtras(extras);

            //to fix unique alarm Id
            final int _id = (int) System.currentTimeMillis();
            String alarmName = txtAlarmName.getText().toString();
            String alarmTime = calendar.getTime().toString();

            alarms.put(_id,alarmName);
            alarmId = _id;
            pendingIntent = PendingIntent.getBroadcast(this, _id, intent, 0);

            time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));
            if (System.currentTimeMillis() > time) {
                if (calendar.AM_PM == 0)
                    time = time + (1000 * 60 * 60 * 12);
                else
                    time = time + (1000 * 60 * 60 * 24);
            }
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 0, pendingIntent);
            alarm = true;
        }
        else
        {
//            intent.putExtra("extra", "no");
            alarmManager.cancel(pendingIntent);
            sendBroadcast(intent);
            alarm = false;
            Toast.makeText(AlarmActivity.this, "ALARM OFF", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        Bundle extras = new Bundle();
        extras.putString("movieName", movieName); //movie name
        extras.putString("userName", userName);
        extras.putInt("_id", alarmId);
        if(alarm == true)
        {
            extras.putString("activity", "alarm");
        }
        intent.putExtras(extras);

        startActivity(intent);
    }
    public void deleteAlarm(int id)
    {
//        for (Map.Entry<Integer, String> entry : alarms.entrySet()) {
//            if (entry.getValue().equals(txtAlarmName.getText().toString())) {
                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);    //OneShotAlarm is the broadcast receiver you use for alarm
//              PendingIntent sender = PendingIntent.getBroadcast(this, entry.getKey(), intent, 0);
                PendingIntent sender = PendingIntent.getBroadcast(this, id, intent, 0);
                am.cancel(sender);
//            }
//        }
//        for (int i = 0; i <= alarms.size(); i++) {
//            if (alarms.containsValue(lblAlarmName.getText().toString())) {
//                int k = alarms.getKey();
//                String str = lblAlarmName.getText().toString();
//                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);    //OneShotAlarm is the broadcast receiver you use for alarm
//                PendingIntent sender = PendingIntent.getBroadcast(this, (int)alarms.get(k), intent, 0);
//                am.cancel(sender);
//            }
//        }
    }
}
