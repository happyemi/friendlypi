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
import java.util.*;
import org.json.*;

public class ModItem
{
	private class Action
	{
		public String id;
		public String label;
	}
	
	private String instanceName;
	private String caption;
	private String status;
	private Vector<Action> actions = new Vector<Action>();
	
	public ModItem(JSONObject jsonItem)
	{
		try
		{
			instanceName = jsonItem.getString("name");
			caption = jsonItem.getString("caption");
			status = jsonItem.getString("status");
			JSONArray jsonActions = jsonItem.getJSONArray("actions");
			for(int i = 0; i < jsonActions.length(); i++)
			{
				JSONObject jsonAction = (JSONObject) jsonActions.get(i);
				Action action = new Action();
				action.id = jsonAction.getString("id");
				action.label = jsonAction.getString("label");
				actions.add(action);
			}
		}
		catch(JSONException e)
		{
		}
	}
	
	public String getName()
	{
		return instanceName;
	}
	
	public String getStatus()
	{
		return status;
	}
	
	public String getCaption()
	{
		return caption;
	}
	
	public String getActionLabel(int index)
	{
		return actions.elementAt(index).label;
	}
	
	public String getActionId(int index)
	{
		return actions.elementAt(index).id;
	}
	
	public int getActionCount()
	{
		return actions.size();
	}
}