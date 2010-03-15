/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.android.launcher2;

import com.android.launcher2.R;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.appwidget.AppWidgetHost;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;

/**
 * With this activity, users can set preferences for Launcher2 Mod
 */
public class LauncherPreferenceActivity extends PreferenceActivity {
    // Symbolic names for the keys used for preference lookup
    public static final String LAUNCHER2_LONGPRESS_ADD = "pref_key_launcher2_longpress_add";
	public static final String LAUNCHER2_SCREEN_SIZE = "pref_key_launcher2_screen_size";
	public static final String LAUNCHER2_AUTO_ORIENTATION = "pref_key_launcher2_auto_orientation";
	public static final String LAUNCHER2_APP1_PACKAGE = "pref_key_launcher2_app1_package";
	public static final String LAUNCHER2_APP1_CLASS = "pref_key_launcher2_app1_class";
	public static final String LAUNCHER2_APP1_URI = "pref_key_launcher2_app1_uri";
	public static final String LAUNCHER2_APP2_PACKAGE = "pref_key_launcher2_app2_package";
	public static final String LAUNCHER2_APP2_CLASS = "pref_key_launcher2_app2_class";
	public static final String LAUNCHER2_APP2_URI = "pref_key_launcher2_app2_uri";
	public static final String LAUNCHER2_APP3_PACKAGE = "pref_key_launcher2_app3_package";
	public static final String LAUNCHER2_APP3_CLASS = "pref_key_launcher2_app3_class";
	public static final String LAUNCHER2_APP3_URI = "pref_key_launcher2_app3_uri";
	public static final String LAUNCHER2_APP4_PACKAGE = "pref_key_launcher2_app4_package";
	public static final String LAUNCHER2_APP4_CLASS = "pref_key_launcher2_app4_class";
	public static final String LAUNCHER2_APP4_URI = "pref_key_launcher2_app4_uri";
	public static final String LAUNCHER2_RESTART = "pref_key_launcher2_restart";

    // Menu entries
    private static final int MENU_RESTORE_DEFAULTS    = 1;

	private Preference mScreenSize;
	private Preference mRestart;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.preferences);

		mScreenSize = findPreference(LAUNCHER2_SCREEN_SIZE);
		mRestart = findPreference(LAUNCHER2_RESTART);
		setScreenSizeDisplay();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.clear();
        menu.add(0, MENU_RESTORE_DEFAULTS, 0, R.string.restore_default);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESTORE_DEFAULTS:
                restoreDefaultPreferences();
                return true;
        }
        return false;
    }

    private void restoreDefaultPreferences() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit().clear().commit();
        setPreferenceScreen(null);
        addPreferencesFromResource(R.xml.preferences);
    }

	private int getScreenSize() {
		SharedPreferences mPrefs = mScreenSize.getSharedPreferences();
		return mPrefs.getInt(LauncherPreferenceActivity.LAUNCHER2_SCREEN_SIZE, 5);
	}

	private void setScreenSizeDisplay() {
        mScreenSize.setSummary(
                getString(R.string.pref_summary_launcher2_screen_size,
                        getScreenSize()));
    }

	private void askRestart() {
		new AlertDialog.Builder(this)
			  .setTitle("Confirm")
		      .setMessage("Restart Launcher2?")
		      .setPositiveButton("Yes", restartLauncher2)
			  .setNegativeButton("No", cancelRestart)
		      .show();
	}

	@Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == mScreenSize) {
			new NumberPickerDialog(this,
                    mScreenSizeListener,
                    getScreenSize(),
                    1,
                    7,
                    R.string.pref_title_launcher2_screen_size,
					R.string.pref_summary_launcher2_set_screen_size).show();
			new AlertDialog.Builder(this)
				  .setTitle("WARNING")
			      .setMessage("Please remove all your icons/widgets before resizing the screen to a smaller size. Press the back button to continue.")
			      .show();
        } else if (preference == mRestart) {
			askRestart();
		}

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

	DialogInterface.OnClickListener restartLauncher2 =
		new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				ActivityManager actmgr = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE); 
				actmgr.restartPackage("com.android.launcher2");
			}
	};
	
	DialogInterface.OnClickListener cancelRestart =
		new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
	};

	NumberPickerDialog.OnNumberSetListener mScreenSizeListener =
        new NumberPickerDialog.OnNumberSetListener() {
            public void onNumberSet(int size) {
				if (size != getScreenSize()) {
					SharedPreferences.Editor editor = mScreenSize.getEditor();
					editor.putInt(LauncherPreferenceActivity.LAUNCHER2_SCREEN_SIZE, size);
					editor.commit();
				
					Launcher.resetWidgets = true;
				
	                setScreenSizeDisplay();
	
					askRestart();
				}
            }
    };
}