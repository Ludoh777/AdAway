/*
 * Copyright (C) 2011 Dominik Schürmann <dominik@dominikschuermann.de>
 *
 * This file is part of AdAway.
 * 
 * AdAway is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AdAway is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AdAway.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.adaway.service;

import java.util.Calendar;

import org.adaway.helper.PreferencesHelper;
import org.adaway.util.Constants;
import org.adaway.util.Log;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.commonsware.cwac.wakeful.WakefulIntentService.AlarmListener;

public class UpdateListener implements AlarmListener {
    public void scheduleAlarms(AlarmManager mgr, PendingIntent pi, Context ctxt) {
        // register when enabled in preferences
        if (PreferencesHelper.getUpdateCheckDaily(ctxt)) {
            Log.i(Constants.TAG, "Schedule update check...");

            // every day at 9 am
            Calendar calendar = Calendar.getInstance();
            // if it's after or equal 9 am schedule for next day
            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 9) {
                calendar.add(Calendar.DAY_OF_YEAR, 1); // add, not set!
            }
            calendar.set(Calendar.HOUR_OF_DAY, 9);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            if (Constants.DEBUG_UPDATE_CHECK_SERVICE) {
                // for debugging execute service ever minute
                mgr.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                        60 * 1000, pi);
            } else {
                mgr.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pi);
            }
        }
    }

    public void sendWakefulWork(Context ctxt) {
        Intent updateIntent = new Intent(ctxt, UpdateService.class);
        updateIntent.putExtra(UpdateService.EXTRA_APPLY_AFTER_CHECK, true);
        WakefulIntentService.sendWakefulWork(ctxt, updateIntent);
    }

    public long getMaxAge() {
        if (Constants.DEBUG_UPDATE_CHECK_SERVICE) {
            return (60 * 1000);
        } else {
            return (AlarmManager.INTERVAL_DAY + 60 * 1000);
        }
    }
}
