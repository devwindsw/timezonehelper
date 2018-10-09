/*
 * Copyright (C) 2018 Wind Soft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.windsw.timezonehelper;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.TimeUtils;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateTextView(true);
    }

    private void updateTextView(boolean extended) {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String country = tm.getNetworkCountryIso();
        String numeric = tm.getNetworkOperator();
        String name = tm.getNetworkOperatorName();

        TimeZone zone = TimeZone.getDefault();
        String version = TimeUtils.getTimeZoneDatabaseVersion();

        //String msg = "Welcome to " + name + "(" + numeric + ", " + iso + "), " + zone.getID() + "(" + version + ")\n";

        TextView tv = (TextView) findViewById(R.id.contentCountry);
        tv.setText(country);

        tv = (TextView) findViewById(R.id.contentZoneId);
        tv.setText(zone.getID());

        tv = (TextView) findViewById(R.id.contentZoneDesc);
        tv.setText(zone.getDisplayName());

        tv = (TextView) findViewById(R.id.contentVersion);
        tv.setText(version);

        if (extended) {
            tv = (TextView) findViewById(R.id.contentZones);
            ArrayList<TimeZone> zones = TimeZoneLookupHelper.getTimeZonesWithUniqueOffsets(this, country);
            if (zones != null) {
                for (TimeZone z : zones) {
                     tv.append(z.getID() + " " + z.getDisplayName() + "\n");
                }
            }
        }
    }
}
