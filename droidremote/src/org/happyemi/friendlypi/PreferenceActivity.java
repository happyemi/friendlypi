/*
	Copyright 2013-2014 Emiliano Mennucci

     This file is part of FriendlyPi.
 
     FriendlyPi is free software: you can redistribute it and/or modify
     it under the terms of the GNU General Public License as published by
     the Free Software Foundation, either version 3 of the License, or
     (at your option) any later version.
 
     FriendlyPi is distributed in the hope that it will be useful,
     but WITHOUT ANY WARRANTY; without even the implied warranty of
     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
     GNU General Public License for more details.
 
     You should have received a copy of the GNU General Public License
     along with FriendlyPi.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.happyemi.friendlypi;

import android.os.Bundle;
import android.preference.*;
import android.app.Activity;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.SharedPreferences;

public class PreferenceActivity extends Activity
{
	public static class PreferenceFragment extends
			android.preference.PreferenceFragment implements
			OnSharedPreferenceChangeListener
	{
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			getPreferenceManager().setSharedPreferencesName(getString(R.string.app_name));
			addPreferencesFromResource(R.xml.preferences);
			getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onResume()
		{
			super.onResume();
			for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++)
			{
				Preference pref = getPreferenceScreen().getPreference(i);
				updateItemSummary(pref);
			}
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
		{
			updateItemSummary(findPreference(key));
		}
		
		private void updateItemSummary(Preference pref)
		{
			// If the preference is an editText, update the summary with the text
			if (pref instanceof EditTextPreference)
			{
				EditTextPreference textPref = (EditTextPreference) pref;
				pref.setSummary(textPref.getText());
			}
			
			// If the preference is a preferenceGroup, update the summary with the current selection
			else if(pref instanceof ListPreference)
			{
				ListPreference listPref = (ListPreference) pref;
				pref.setSummary(listPref.getEntry());
			}
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new PreferenceFragment())
				.commit();
	}
}
