/*
 * Copyright (C) 2006 The Android Open Source Project
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
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TimeZone;

public class TimeZoneLookupHelper {
    public static final boolean DBG = true;
    public static final String TAG = "TZLookupHelper";

    private static final Object sLastLockObj = new Object();
    private static ArrayList<TimeZone> sLastZones = null;
    private static String sLastCountry = null;

    public static ArrayList<TimeZone> getTimeZones(Context context, String country) {
        synchronized (sLastLockObj) {
            if ((country != null) && country.equals(sLastCountry)) {
                if (DBG) Log.d(TAG, "getTimeZones(" + country + "): return cached version");
                return sLastZones;
            }
        }
        ArrayList<TimeZone> tzs = new ArrayList<TimeZone>();
        if (country == null) {
            if (DBG) Log.d(TAG, "getTimeZones(null): return empty list");
            return tzs;
        }

        Resources r = context.getResources();
        XmlResourceParser parser = null;

        try {
            parser = r.getXml(R.xml.time_zones_by_country);
        } catch (Resources.NotFoundException resourceNotFoundException) {
            if (DBG) Log.d(TAG, "NotFoundException " + resourceNotFoundException);
        }

        if (parser == null) return tzs;

        try {
            beginDocument(parser, "timezones");
            while (true) {
                nextElement(parser);
                String element = parser.getName();
                if (element == null || !(element.equals("timezone"))) {
                    break;
                }
                String code = parser.getAttributeValue(null, "code");
                if (country.equals(code)) {
                    if (parser.next() == XmlPullParser.TEXT) {
                        String zoneIdString = parser.getText();
                        TimeZone tz = TimeZone.getTimeZone(zoneIdString);
                        if (tz.getID().startsWith("GMT") == false) {
                            // tz.getID doesn't start not "GMT" so its valid
                            tzs.add(tz);
                            if (DBG) {
                                Log.d(TAG, "getTimeZone('" + country + "'): found tz.getID=="
                                        + ((tz != null) ? tz.getID() : "<no tz>"));
                            }
                        }
                    }
                }
            }
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Got xml parser exception getTimeZone('" + country + "'): e=", e);
        } catch (IOException e) {
            Log.e(TAG, "Got IO exception getTimeZone('" + country + "'): e=", e);
        } finally {
            parser.close();
        }
        synchronized (sLastLockObj) {
            // Cache the last result;
            sLastZones = tzs;
            sLastCountry = country;
            return sLastZones;
        }
    }

    private static final void beginDocument(XmlPullParser parser, String firstElementName) throws XmlPullParserException, IOException {
        int type;
        while ((type = parser.next()) != parser.START_TAG
                && type != parser.END_DOCUMENT) {
            ;
        }
        if (type != parser.START_TAG) {
            throw new XmlPullParserException("No start tag found");
        }
        if (!parser.getName().equals(firstElementName)) {
            throw new XmlPullParserException("Unexpected start tag: found " + parser.getName() +
                    ", expected " + firstElementName);
        }
    }

    private static final void nextElement(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        int type;
        while ((type=parser.next()) != parser.START_TAG
                && type != parser.END_DOCUMENT) {
            ;
        }
    }
}