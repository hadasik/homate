package homate.config;

import org.json.JSONException;
import org.json.JSONObject;

import homate.main.Loged;
import homate.server.HTTPIntentService;
import homate.server.ServerActions;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class GroupPrefs extends Activity implements ListView.OnItemClickListener {

	
	private ServerActions myactions;
	private IntentFilter filter;
	private BroadcastReceiver receiver;
	private String[] members = {""};
	private ListView list;
	private int memberIndex;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_group_prefs);
		
		myactions = new ServerActions(this, getResources().getString(R.string.EDIT_GROUP_NAME));
		
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String response = intent.getStringExtra(HTTPIntentService.PARAM_OUT_MSG);
				if (!response.equals("")) {
					try {
						JSONObject obj = new JSONObject(response);
						if (obj != null ) {
							
							if(obj.getString(ServerActions.ACTION_COMMAND).equals(ServerActions.ACTION_EDIT_GROUP_NAME)){
								//dismiss progress dialog
								System.out.println(obj.toString());  //TODO debug-remove

								if (obj.getString(ServerActions.SERVER_RET_VAL).equals("1")) {
									/** edit group name verified */

									Toast.makeText(context,"group name changed",
											Toast.LENGTH_LONG).show();
								}else Toast.makeText(context,"cannot change group name",
										Toast.LENGTH_LONG).show();
							

							}
							
							if(obj.getString(ServerActions.ACTION_COMMAND).equals(ServerActions.ACTION_ADD_MEMBER)){
								System.out.println(obj.toString());  //TODO debug-remove

								Toast.makeText(context,obj.getString(ServerActions.SERVER_MSG),
											Toast.LENGTH_LONG).show();
								get_members(null);
							

							}
							
							if(obj.getString(ServerActions.ACTION_COMMAND).equals(ServerActions.ACTION_LEAVE_GROUP)){
								//dismiss progress dialog
								System.out.println(obj.toString());  //TODO debug-remove

								Toast.makeText(context,obj.getString(ServerActions.SERVER_MSG),
										Toast.LENGTH_LONG).show();
								if(obj.getString(ServerActions.SERVER_RET_VAL).equals("1")){
									Intent launcher = new Intent(getBaseContext(), Loged.class);
									startActivity(launcher);
									finish();
									
									
								}
							

							}
							
							if(obj.getString(ServerActions.ACTION_COMMAND).equals(ServerActions.ACTION_GET_MEMBERS)){
								System.out.println(obj.toString());  //TODO debug-remove
								
								members = obj.getString(ServerActions.SERVER_MSG).split("#");
								ArrayAdapter<String> adapter =new ArrayAdapter<String>(context,R.layout.simple_list_item_homate,members);
								list.setAdapter(adapter);
							}
							
							if(obj.getString(ServerActions.ACTION_COMMAND).equals(ServerActions.ACTION_REMOVE_MEMBER)){
								System.out.println(obj.toString());  //TODO debug-remove

								Toast.makeText(context,obj.getString(ServerActions.SERVER_MSG),
										Toast.LENGTH_LONG).show();
								
								if(obj.getString(ServerActions.SERVER_RET_VAL).equals("1")){
									Intent launcher = new Intent(getBaseContext(), Loged.class);
									startActivity(launcher);
									finish();
									
									
								}else{get_members(null);}
								
								
								
							}
							
							if(obj.getString(ServerActions.ACTION_COMMAND).equals(ServerActions.ACTION_SET_ADMIN)){
								System.out.println(obj.toString());  //TODO debug-remove

								Toast.makeText(context,obj.getString(ServerActions.SERVER_MSG),
										Toast.LENGTH_LONG).show();

								
							}
							
							
							
							
							

							
						}
					} catch (JSONException e) {
						Log.e("BroadcastReceiver","JSON error: "+e);
					} 
				}else{
					Toast.makeText(context,"Unexpected error occured,Please Try again later",
							Toast.LENGTH_LONG).show();
				}
			}
		};

		// Register filter
		filter = new IntentFilter(getResources().getString(R.string.EDIT_GROUP_NAME));
		
		// check saved SharedPref for login status
						 
		list = (ListView)findViewById(R.id.listViewmembers);
		ArrayAdapter<String> adapter =new ArrayAdapter<String>(this,R.layout.simple_list_item_homate,members);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group_prefs, menu);
		return true;
	}

	public void editGroupName(View view){
		SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.PREFS_FILE), 0);
		SharedPreferences.Editor editor = settings.edit();
		String groupName = (((EditText)findViewById(R.id.editGroupNameText)).getText().toString());
		editor.putString( getResources().getString(R.string.PREFS_GRP_NAME), groupName);
		editor.commit();
		
		
			try {	
				String groupID = settings.getString( getResources().getString(R.string.PREFS_GRP),"0");
				
				myactions.edit_group_name(groupID, groupName);
			} catch (RuntimeException e) {
				Log.e("LoginActivity","Failed Call Menu: "+e);
			}
		
		
	}
	
	public void onResume() {
		super.onResume();
		registerReceiver(receiver, filter);	
		get_members(null);
	}
	@Override
	public void onPause() {
		super.onPause();
		// Unregister filter
		unregisterReceiver(receiver);
		// call finish on self
		
	}
	
	
	public void onBackPressed() 
	{
		Intent launcher = new Intent(getBaseContext(), Homenu.class);
		startActivity(launcher);
		finish();
	}
	
	
	public void addMember(View view){
		SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.PREFS_FILE), 0);
		String memberName = (((EditText)findViewById(R.id.editAddMemberText)).getText().toString());
		String userName = settings.getString( getResources().getString(R.string.PREFS_USER),"username");
		
		
			try {
				myactions.add_member(userName, memberName);
			} catch (RuntimeException e) {
				Log.e("LoginActivity","Failed Call Menu: "+e);
			}
	}
	
	
	public void leaveGroup(View view){
		SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.PREFS_FILE), 0);
		String userName = settings.getString( getResources().getString(R.string.PREFS_USER),"username");
		
		
			try {
				myactions.leaveGroup(userName);
			} catch (RuntimeException e) {
				Log.e("LoginActivity","Failed Call Menu: "+e);
			}
	}
	
	public void get_members(View view){
		SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.PREFS_FILE), 0);
		String group = settings.getString( getResources().getString(R.string.PREFS_GRP),"0");
		
		
			try {
				myactions.get_members(group);
			} catch (RuntimeException e) {
				Log.e("LoginActivity","Failed Call Menu: "+e);
			}
	}



	@Override
	public void onItemClick(AdapterView<?> ad, View v, int i, long j) {
		((TextView) v).setTextColor(Color.CYAN);
		memberIndex = i;
		 PopupMenu popup = new PopupMenu(this, v);
		 MenuInflater inflater = popup.getMenuInflater();
		 inflater.inflate(R.menu.actions, popup.getMenu());
		 
		 
		  //registering popup with OnMenuItemClickListener  
         popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {  
          public boolean onMenuItemClick(MenuItem item) {  
        	  SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.PREFS_FILE), 0);
      		  String userName = settings.getString( getResources().getString(R.string.PREFS_USER),"username");
      	    switch (item.getItemId()) {
      	        case R.id.menu_remove:
      	        	
      	        	myactions.removeMember(userName,members[memberIndex]);
      	            return true;
      	        case R.id.menu_admin:
      	        	myactions.setAdmin(userName,members[memberIndex]);
      	            return true;
      	        default:
      	            return false;
      	    }  
          }  
         });  
		 
		 
		 popup.show();
		
	}


	
}


