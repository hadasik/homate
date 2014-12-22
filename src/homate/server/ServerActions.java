package homate.server;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;

public class ServerActions 
{
	/* URL key */
	//public static final String URL = "http://127.0.0.1:8080";
	//public static final String URL = "http://192.168.1.14:8080";
	public static final String URL = "http://local-turbine-778.appspot.com";

	/* Actions defines */
	public static final String ACTION_COMMAND = "action";
	public static final String ACTION_NEW_USER_REGISTER = "new_user_registry";
	public static final String ACTION_CREATE_NEW_GROUP = "create_new_group";
	public static final String ACTION_ADD_TO_GROUP = "add_to_group";
	public static final String ACTION_EDIT_GROUP_NAME = "edit_group_name";
	
	public static final String ACTION_GET_GROUP = "get_group";

	/* Message Constants */
	public static final String SERVER_RET_VAL = "return value";
	public static final String SERVER_MSG = "msg";
	public static final String SERVER_DATA = "data";
	public static final String SERVER_DATA2 = "data2";
	public static final String SERVER_ACTION = "action";

	/* Variables */
	private Activity activity;
	private String br_action_name;

	//Constructor
	public ServerActions(Activity activity,final String br_action_name) 
	{
		this.activity = activity;
		if (br_action_name != null) 
			this.br_action_name = br_action_name;
		 
		else 
			this.br_action_name = "default";
	}

	// new_user_register
	public void new_user_registery(final String username, final String password) 
	{
		JSONObject info = new JSONObject();
		/** Add data to JSON object */
		try 
		{
			info.put(ACTION_COMMAND, ACTION_NEW_USER_REGISTER);
			info.put("username", username);
			info.put("password", password);
			System.out.println("try to send to server!!");
		}
		catch (JSONException e) 
		{
			Log.e("JSONObject","IOException:" +  e.toString());
		} 
		finally 
		{
			/** call intentService */
			Intent msgIntent = new Intent(this.activity, HTTPIntentService.class);
			msgIntent.putExtra(HTTPIntentService.PARAM_URL, URL);
			msgIntent.putExtra(HTTPIntentService.PARAM_IN_MSG, info.toString());
			msgIntent.putExtra(HTTPIntentService.PARAM_BR_ACTION_NAME, br_action_name);
			System.out.println("finally send to server");

			this.activity.startService(msgIntent); 
			System.out.println("start http activity");
		}
	}

	public void get_group(String userName) {
		JSONObject info = new JSONObject();
		/** Add data to JSON object */
		try 
		{
			info.put(ACTION_COMMAND, ACTION_GET_GROUP);
			info.put("username", userName);
			System.out.println("try to send to server request for group id!!");
		}
		catch (JSONException e) 
		{
			Log.e("JSONObject","IOException:" +  e.toString());
		} 
		finally 
		{
			/** call intentService */
			Intent msgIntent = new Intent(this.activity, HTTPIntentService.class);
			msgIntent.putExtra(HTTPIntentService.PARAM_URL, URL);
			msgIntent.putExtra(HTTPIntentService.PARAM_IN_MSG, info.toString());
			msgIntent.putExtra(HTTPIntentService.PARAM_BR_ACTION_NAME, br_action_name);
			System.out.println("finally send group request to server");

			this.activity.startService(msgIntent); 
			System.out.println("start http activity to get group id");
		}
		
	}

	public void create_group(String userName) {
		JSONObject info = new JSONObject();
		/** Add data to JSON object */
		try 
		{
			info.put(ACTION_COMMAND, ACTION_CREATE_NEW_GROUP);
			info.put("username", userName);
			System.out.println("try to send to server request for group creation!!");
		}
		catch (JSONException e) 
		{
			Log.e("JSONObject","IOException:" +  e.toString());
		} 
		finally 
		{
			/** call intentService */
			Intent msgIntent = new Intent(this.activity, HTTPIntentService.class);
			msgIntent.putExtra(HTTPIntentService.PARAM_URL, URL);
			msgIntent.putExtra(HTTPIntentService.PARAM_IN_MSG, info.toString());
			msgIntent.putExtra(HTTPIntentService.PARAM_BR_ACTION_NAME, br_action_name);
			System.out.println("finally send group creation request to server");

			this.activity.startService(msgIntent); 
			System.out.println("start http activity to get created group id");
		}
		
	}

	public void edit_group_name(String groupID, String groupName) {
		JSONObject info = new JSONObject();
		/** Add data to JSON object */
		try 
		{
			info.put(ACTION_COMMAND, ACTION_EDIT_GROUP_NAME);
			info.put("groupID", groupID);
			info.put("groupName", groupName);
			System.out.println("try to send to server request for group name edit!!");
		}
		catch (JSONException e) 
		{
			Log.e("JSONObject","IOException:" +  e.toString());
		} 
		finally 
		{
			/** call intentService */
			Intent msgIntent = new Intent(this.activity, HTTPIntentService.class);
			msgIntent.putExtra(HTTPIntentService.PARAM_URL, URL);
			msgIntent.putExtra(HTTPIntentService.PARAM_IN_MSG, info.toString());
			msgIntent.putExtra(HTTPIntentService.PARAM_BR_ACTION_NAME, br_action_name);
			System.out.println("finally send group name edit request to server");

			this.activity.startService(msgIntent); 
			System.out.println("start http activity to set group name");
		}
		
	}
	
	
}
