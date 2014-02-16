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
import android.os.Handler;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;

import android.widget.*;
import java.util.*;

import org.json.*;

public class ModlistActivity extends Activity implements StatusChangeListener
{
	private ArrayList<ModItem> items = new ArrayList<ModItem>();
	private ModItemAdapter adapter;
	private Handler handler = new Handler();

	// Fetches system status every 5 secs.
	private class FetchStatusTimer implements Runnable
	{
		StatusChangeListener listener;
		private final int FETCH_STATUS_DELAY = 5000;

		FetchStatusTimer(StatusChangeListener listener)
		{
			this.listener = listener;
		}

		public void setRunningState(boolean appRunning)
		{
			if(appRunning)
			{
				handler.postDelayed(this, 0);
			}
			else
			{
				handler.removeCallbacks(this);
			}
		}

		// Forks an async task to fetch the status. This method is executed on the main thread. 
		@Override
		public void run() 
		{
			handler.postDelayed(this, FETCH_STATUS_DELAY);
			new FetchStatus(listener).execute();
		}
	}

	private FetchStatusTimer fetchStatusTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.modlist_activity);

		// Configure the main list
		adapter = new ModItemAdapter(this, View.generateViewId(), items);
		ListView listView = (ListView) findViewById(R.id.modlistView);
		listView.setAdapter(adapter);

		fetchStatusTimer = new FetchStatusTimer(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		fetchStatusTimer.setRunningState(true);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		fetchStatusTimer.setRunningState(false);
	}

	// Converts the json string into a ModItem array and notifies the adapter that data has changed.
	@Override
	public void onNewStatus(String status)
	{
		try
		{
			JSONObject jsonData = new JSONObject(status);
			JSONArray instances = jsonData.getJSONArray("data");

			items.clear();
			for(int i = 0; i < instances.length(); i++)
			{
				JSONObject instance = (JSONObject) instances.get(i);
				ModItem item = new ModItem(instance);
				items.add(i, item);
			}
		}
		catch(JSONException e)
		{
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_modlist, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle item selection
		switch (item.getItemId())
		{
		case R.id.menu_refresh:
			new FetchStatus(this).execute();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
