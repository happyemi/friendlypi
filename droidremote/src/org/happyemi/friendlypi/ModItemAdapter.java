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

import android.widget.*;
import android.view.*;
import android.content.Context;
import android.os.AsyncTask;
import android.graphics.Typeface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

class ActionClickListener implements View.OnClickListener
{
	private class SendCommand extends AsyncTask<Void, Void, String>
	{
		private StatusChangeListener listener;
		private String instance;
		private String actionId;
		
		SendCommand(StatusChangeListener listener, String instance, String actionId)
		{
			this.instance = instance;
			this.actionId = actionId;
			this.listener = listener;
		}
		
		protected String doInBackground(Void... params) 
		{		
			try 
			{
				URL url = new URL("http://10.0.2.2:8080/command/" + instance + "/" + actionId);
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
	
	private SendCommand sendCommand;
	
	ActionClickListener(StatusChangeListener listener, String instance, String actionId) 
	{
		sendCommand = new SendCommand(listener, instance, actionId);
	}
	
	public void onClick(View v)
	{
		sendCommand.execute();
	}
}

public class ModItemAdapter extends ArrayAdapter<ModItem>
{
	private int resource;
	
	static private int nameResId = View.generateViewId();
	static private int statusResId = View.generateViewId();
	static private int actionContainerResId = View.generateViewId();
	private StatusChangeListener listener;

	private static View createListItem(Context context, int resource)
	{
		// Generate a vertical linear layout
		LinearLayout itemView = new LinearLayout(context);
		itemView.setId(resource);
		itemView.setOrientation(LinearLayout.VERTICAL);
		
		// Set layout parameters for children
		int heightParam = LinearLayout.LayoutParams.WRAP_CONTENT;
		int widthParam = LinearLayout.LayoutParams.WRAP_CONTENT;

		// Add the first TextView (instance name)
		TextView captionView = new TextView(context);
		captionView.setId(nameResId);
		captionView.setTextSize(context.getResources().getDimension(R.dimen.instance));
		itemView.addView(captionView, new LinearLayout.LayoutParams(heightParam, widthParam));
		
		// Add the second TextView (instance status)
		TextView statusView = new TextView(context);
		statusView.setId(statusResId);
		statusView.setTextSize(context.getResources().getDimension(R.dimen.status));
		itemView.addView(statusView, new LinearLayout.LayoutParams(heightParam, widthParam));
		
		// Add a horizontal linear layout to contain the buttons
		LinearLayout actionContainer = new LinearLayout(context);
		actionContainer.setId(actionContainerResId);
		itemView.addView(actionContainer, new LinearLayout.LayoutParams(widthParam, widthParam));

		return itemView;
	}

	public ModItemAdapter(Context context, int resource, List<ModItem> items)
	{
		super(context, resource, items);
		this.resource = resource;
		this.listener = (StatusChangeListener)context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ModItem item = getItem(position);

		if (convertView == null)
		{
			convertView = createListItem(getContext(), resource);
		}
		TextView captionView = (TextView) convertView.findViewById(nameResId);
		TextView statusView = (TextView) convertView.findViewById(statusResId);
		captionView.setText(item.getCaption());
		captionView.setTypeface(null, Typeface.BOLD);
		statusView.setText("Status: " + item.getStatus());
		
		// Setup all Action buttons
		LinearLayout actionContainer = (LinearLayout)convertView.findViewById(actionContainerResId);
		actionContainer.removeAllViews();
		for(int i = 0; i < item.getActionCount(); i++)
		{
			Button button = new Button(getContext());
			button.setText(item.getActionLabel(i));
			button.setOnClickListener(new ActionClickListener(listener, item.getName(), item.getActionId(i)));
			actionContainer.addView(button);
		}
		return convertView;
	}
}