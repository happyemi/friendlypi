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
import android.app.Application;
import android.content.Context;

public class FriendlypiApplication extends Application 
{
	private static FriendlypiApplication singleton;

	public static FriendlypiApplication getInstance()
	{
		return singleton;
	}

	@Override
	public void onCreate() 
	{
		super.onCreate();
		singleton = this;
	}

	public String getHost()
	{
		return getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE).getString(
				getString(R.string.pref_key_host), "");
	}

	public String getPort()
	{
		return getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE).getString(
				getString(R.string.pref_key_port), getString(R.string.pref_default_port));
	}
	
	public String getServerUrl()
	{
		String host = getHost();
		String port = getPort();
		return "http://" + host + ":" + port;
	}

	public int getRefreshIntervalSecs()
	{
		String time = getSharedPreferences(getString(R.string.app_name),
				Context.MODE_PRIVATE).getString(
				getString(R.string.pref_key_update), "0");
		
		int ret = 0;
		try
		{
			ret = Integer.parseInt(time);
		} 
		catch (NumberFormatException e)
		{
			ret = 0;
		}
		
		return ret;
	}
}
