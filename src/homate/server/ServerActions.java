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
	public static final String ACTION_ADD_MEMBER = "add_member";
	public static final String ACTION_LEAVE_GROUP = "leave_group";
	public static final String ACTION_GET_MEMBERS = "get_members";
	public static final String ACTION_REMOVE_MEMBER = "remove_member";
	public static final String ACTION_SET_ADMIN = "set_admin";
	public static final String ACTION_GET_SHOPPING_LIST = "get_shopping_list";
	public static final String ACTION_REMOVE_SHOPPING_ITEM = "remove_shopping_item";
	public static final String ACTION_ADD_SHOPPING_ITEM = "add_shopping_item";
	public static final String ACTION_GET_BILLS_TOTAL = "get_bills_total";
	public static final String ACTION_GET_GROUP = "get_group";
	public static final String ACTION_ADD_BILLS = "add_bill";
	public static final String ACTION_SHOW_BILLS = "show_bill";
	public static final String ACTION_SHOW_BILLS_ARCHIVE = "show_archive";
	public static final String ACTION_REMOVE_BILL = "remove_payment";
	public static final String ACTION_SHOW_USER_BILLS = "show_user_bills";
	public static final String ACTION_REQUEST_BILL_APPROVAL = "request_approval";
	public static final String ACTION_SET_PAYMENT = "set_payment";
	public static final String ACTION_PAYMENT_DETAILS = "get_payment_details";
	public static final String ACTION_SHOW_PERSONAL_BILLS = "get_personal_bills";
	public static final String ACTION_GET_CLEANING_ORDER = "get_cleaning_order";
	public static final String ACTION_SET_CLEANING_PREFS = "set_cleaning_prefs";
	
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

	public void add_member(String userName, String memberName) {
		JSONObject info = new JSONObject();
		/** Add data to JSON object */
		try 
		{
			info.put(ACTION_COMMAND, ACTION_ADD_MEMBER);
			info.put("username", userName);
			info.put("memberName", memberName);
			System.out.println("try to send to server request for member addition!!");
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
			System.out.println("finally send add member request to server");

			this.activity.startService(msgIntent); 
			System.out.println("start http activity to set group name");
		}
		
	}

	public void leaveGroup(String userName) {
		JSONObject info = new JSONObject();
		/** Add data to JSON object */
		try 
		{
			info.put(ACTION_COMMAND, ACTION_LEAVE_GROUP);
			info.put("username", userName);
			System.out.println("try to send to server request for leaving group!!");
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
			System.out.println("finally send leave group request to server");

			this.activity.startService(msgIntent); 
			System.out.println("start http activity to leave group");
		}
		
	}

	public void get_members(String group) {
		JSONObject info = new JSONObject();
		/** Add data to JSON object */
		try 
		{
			info.put(ACTION_COMMAND, ACTION_GET_MEMBERS);
			info.put("groupID", group);
			System.out.println("try to send to server request getting members!!");
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
			System.out.println("finally send get members request to server");

			this.activity.startService(msgIntent); 
			System.out.println("start http activity to get members");
		}
		
	}

	public void removeMember(String userName, String member) {
		JSONObject info = new JSONObject();
		/** Add data to JSON object */
		try 
		{
			info.put(ACTION_COMMAND, ACTION_REMOVE_MEMBER);
			info.put("username", userName);
			info.put("memberName", member);

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


			this.activity.startService(msgIntent); 

		}
		
	}

	public void setAdmin(String userName, String member) {
		JSONObject info = new JSONObject();
		/** Add data to JSON object */
		try 
		{
			info.put(ACTION_COMMAND, ACTION_SET_ADMIN);
			info.put("username", userName);
			info.put("newAdmin", member);

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


			this.activity.startService(msgIntent); 

		}
		
	}

	public void get_shopping_list(String group) {
		JSONObject info = new JSONObject();
		/** Add data to JSON object */
		try 
		{
			info.put(ACTION_COMMAND, ACTION_GET_SHOPPING_LIST);
			info.put("groupID", group);
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
			System.out.println("finally send get members request to server");

			this.activity.startService(msgIntent); 
		
		}
		
	}

	public void add_shopping_item(String username, String group,String item) {
		JSONObject info = new JSONObject();
		/** Add data to JSON object */
		try 
		{
			info.put(ACTION_COMMAND, ACTION_ADD_SHOPPING_ITEM);
			info.put("groupID", group);
			info.put("username", username);
			System.out.println("THE ITEM IS  "+item);
			info.put("item", item);
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
			System.out.println("finally send get members request to server");

			this.activity.startService(msgIntent); 
		
		}
		
	}

	public void remove_shopping_item(String username, String group,String items) {
		JSONObject info = new JSONObject();
		/** Add data to JSON object */
		try 
		{
			info.put(ACTION_COMMAND, ACTION_REMOVE_SHOPPING_ITEM);
			info.put("groupID", group);
			info.put("username", username);
			info.put("items", items);
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

			this.activity.startService(msgIntent); 
		
		}
		
	}

	public void get_bills_total(String username, String group) {
		JSONObject info = new JSONObject();
		/** Add data to JSON object */
		try 
		{
			info.put(ACTION_COMMAND, ACTION_GET_BILLS_TOTAL);
			info.put("groupID", group);
			info.put("username", username);
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

			this.activity.startService(msgIntent); 
		
		}
		
	}

	public void add_bill(String groupID, String username, String category,String total, String date) {
		JSONObject info = new JSONObject();
		/** Add data to JSON object */
		try 
		{
			info.put(ACTION_COMMAND, ACTION_ADD_BILLS);
			info.put("groupID", groupID);
			info.put("username", username);
			info.put("category", category);
			info.put("bill", total);
			info.put("date", date);
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

			this.activity.startService(msgIntent); 
		
		}
		
	}

	public void show_bill(String groupID, String username, String category) {
		JSONObject info = new JSONObject();
		/** Add data to JSON object */
		try 
		{
			info.put(ACTION_COMMAND, ACTION_SHOW_BILLS);
			info.put("groupID", groupID);
			info.put("username", username);
			info.put("category", category);
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

			this.activity.startService(msgIntent); 
		
		}
		
	}

	public void show_bill_archive(String groupID, String category) {
		JSONObject info = new JSONObject();
		/** Add data to JSON object */
		try 
		{
			info.put(ACTION_COMMAND, ACTION_SHOW_BILLS_ARCHIVE);
			info.put("groupID", groupID);
			info.put("category", category);
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

			this.activity.startService(msgIntent); 
		
		}
		
	}

	public void removeBill(String userName, String billID, String groupID) {
		JSONObject info = new JSONObject();
		/** Add data to JSON object */
		try 
		{
			info.put(ACTION_COMMAND, ACTION_REMOVE_BILL);
			info.put("username", userName);
			info.put("bill_id", billID);
			info.put("groupID", groupID);
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

			this.activity.startService(msgIntent); 
		
		}
		
	}
	
	public void request_approval(String userName, String billID) {
		JSONObject info = new JSONObject();
		/** Add data to JSON object */
		try 
		{
			info.put(ACTION_COMMAND, ACTION_REQUEST_BILL_APPROVAL);
			info.put("username", userName);
			info.put("bill_id", billID);
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

			this.activity.startService(msgIntent); 
		
		}
		
	}
	
	
	public void get_payment_details(String billID) {
		JSONObject info = new JSONObject();
		/** Add data to JSON object */
		try
		{
			info.put(ACTION_COMMAND, ACTION_PAYMENT_DETAILS);
			info.put("bill_id", billID);
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

			this.activity.startService(msgIntent); 
		
		}
		
	}
	public void set_payment(String adminName,String userName, String billID,int status) {
		JSONObject info = new JSONObject();
		/** Add data to JSON object */
		try
		{
			info.put(ACTION_COMMAND, ACTION_SET_PAYMENT);
			info.put("admin_name", adminName);
			info.put("username", userName);
			info.put("bill_id", billID);
			info.put("status", status);
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

			this.activity.startService(msgIntent); 
		
		}
		
	}

	public void show_personal_bill(String username,String group) {
		JSONObject info = new JSONObject();
		/** Add data to JSON object */
		try
		{
			info.put(ACTION_COMMAND, ACTION_SHOW_PERSONAL_BILLS);
			info.put("username", username);
			info.put("groupID", group);
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

			this.activity.startService(msgIntent); 
		
		}
		
	}

	public void getCleaningOrder(String group) {
		
		JSONObject info = new JSONObject();
		/** Add data to JSON object */
		try
		{
			info.put(ACTION_COMMAND, ACTION_GET_CLEANING_ORDER);
			info.put("groupID", group);
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

			this.activity.startService(msgIntent); 
		
		}
		
		
	}

	public void set_cleaning_prefs(String userName, String group, String days, String strorder) {
		JSONObject info = new JSONObject();
		/** Add data to JSON object */
		try
		{
			info.put(ACTION_COMMAND, ACTION_SET_CLEANING_PREFS);
			info.put("groupID", group);
			info.put("username", userName);
			info.put("days", days);
			info.put("order", strorder);
			
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

			this.activity.startService(msgIntent); 
		
		}
		
	}
	
	
	
	
}
