package com.windsw.timezonehelper;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.TimeUtils;
import android.widget.TextView;

import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String iso = tm.getNetworkCountryIso();
        String numeric = tm.getNetworkOperator();
        String name = tm.getNetworkOperatorName();

        TimeZone zone = TimeZone.getDefault();
        String version = TimeUtils.getTimeZoneDatabaseVersion();

        String msg = "Welcome to " + name + "(" + numeric + ", " + iso + "), " + zone.getID() + "(" + version + ")";

        TextView tv = (TextView) findViewById(R.id.text_content);
        tv.setText(msg);
    }
}
