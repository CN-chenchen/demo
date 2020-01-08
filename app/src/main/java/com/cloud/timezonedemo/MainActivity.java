package com.cloud.timezonedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    TextView tvStartTime;

    TextView tvEndTime;

    TextView tvResultTime;

    TimeZone timeZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStartTime = findViewById(R.id.tv_startTime);
        tvEndTime = findViewById(R.id.tv_endTime);
        tvResultTime = findViewById(R.id.tv_resultTime);
        timeZone = TimeZone.getTimeZone("America/Chicago");

        test();
    }

    public void calculate(View view) {
        long currentTimeMillis = System.currentTimeMillis();
        long zeroStartTime = TimeZoneUtils.getZeroStartTime(90, currentTimeMillis);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dateFormat.setTimeZone(timeZone);

        tvStartTime.setText(dateFormat.format(new Date(zeroStartTime)));
        tvEndTime.setText(dateFormat.format(new Date(currentTimeMillis)));

        long[] timeOfDay = TimeZoneUtils.getTimeOfDay(zeroStartTime, currentTimeMillis);
        System.out.println("timeOfDay: " + Arrays.toString(timeOfDay));

        if (timeOfDay[0] == 0 || timeOfDay[1] == 1) {
            tvResultTime.setText("日期范围内均是24小时");
        } else {
            tvResultTime.setText(String.format(Locale.getDefault(),
                    "%s是%d小时",
                    dateFormat.format(new Date(timeOfDay[0])).substring(0, 10),
                    timeOfDay[1] / 60 / 60 / 1000));
        }
    }

    private void test() {
        timeZone = TimeZone.getTimeZone("America/Chicago");

        //11月3号 00:00的时间
        boolean daylightTime1 = timeZone.inDaylightTime(
                new Date(1572757200000L));

        boolean daylightTime2 = timeZone.inDaylightTime(
                new Date(1572757200000L + 60 * 60 * 1000L * 2 - 1));

        boolean inDaylightTime = timeZone.inDaylightTime(
                new Date(1572757200000L + 60 * 60 * 1000L * 2));

        System.out.println("daylightTime1: " + daylightTime1);
        System.out.println("daylightTime2: " + daylightTime2);
        System.out.println("inDaylightTime: " + inDaylightTime);
    }

}
