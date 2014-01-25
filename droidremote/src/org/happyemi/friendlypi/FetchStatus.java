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

import android.os.AsyncTask;

import java.io.*;
import java.net.*;

// This async task fetches system status and signals the listener the new JSON string 
public class FetchStatus extends AsyncTask<Void, Void, String>
{
	private StatusChangeListener listener;
	
	FetchStatus(StatusChangeListener listener)
	{
		this.listener = listener;
	}
	
	protected String doInBackground(Void... params) 
	{		
		try 
		{
			URL url = new URL("http://10.0.2.2:8080/status");
			BufferedReader buf = new BufferedReader(new InputStreamReader(url.openStream()));
			return buf.readLine();
		}
		catch(IOException e)
		{
		}
		return "";
	}
	
	protected void onPostExecute(String result) 
    {
		if(result.isEmpty())
			return;
		
		listener.onNewStatus(result);
    }
}