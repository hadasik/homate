package homate.main;
import org.json.JSONException;
import org.json.JSONObject;

import homate.config.GroupPrefs;
import homate.config.HomateMenu;
import homate.config.R;
import homate.server.HTTPIntentService;
import homate.server.ServerActions;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Loged extends Activity {
	
	public static final String PREFS_STATS	="login_status";
	public static final String PREFS_USER 	="username";
	public static final String PREFS_GRP	="userGroupID";
	private BroadcastReceiver receiver;
	private IntentFilter filter;
	private ServerActions myactions;
	public ProgressDialog pd;
	public static final String PREFS_FILE	="UserPrefHomate.dat";
	public static final String SERVER_GRP ="Homate.server.ServerActions.ACTION_GET_GROUP";
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_loged);	
		
myactions = new ServerActions(this, SERVER_GRP);
		

		// setup receiver - should check if there are some updates in server data base
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				System.out.println("LogedActivity"+" Broadcast Received");
				String response = intent.getStringExtra(HTTPIntentService.PARAM_OUT_MSG);
				if (!response.equals("")) {
					try {
						JSONObject obj = new JSONObject(response);
						if (obj != null ) {
							//dismiss progress dialog
							pd.dismiss();
							System.out.println(obj.toString());  //TODO debug-remove
							
							if(obj.getString(ServerActions.ACTION_COMMAND).equals(ServerActions.ACTION_GET_GROUP)){
								if (!(obj.getString(ServerActions.SERVER_RET_VAL).equals("0"))) {
									/** Register Verified */
									
									// edit SharedPref and change status to true
									Log.d("MainActivity","Saving SharedPrefs");
									SharedPreferences settings = getSharedPreferences(PREFS_FILE, 0);
									SharedPreferences.Editor editor = settings.edit();
									System.out.println("group id is:"+obj.getString(ServerActions.SERVER_RET_VAL));
									editor.putString(PREFS_GRP, obj.getString(ServerActions.SERVER_RET_VAL));
									editor.commit();
									
									// call menu
									try {
										Log.d("MainActivity","Call Menu");
										// unregister receiver
										Intent launcher = new Intent(context, HomateMenu.class);
										launcher.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//remove login screen from stack
										launcher.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);// Stay single instance
										startActivity(launcher);
										finish();
									} catch (RuntimeException e) {
										Log.e("MainActivity","Failed Call Menu: "+e);
									}

								}else{
									Toast.makeText(context, "YOU WERE NOT ADDED TO A GROUP",
											Toast.LENGTH_LONG).show();
								}
							}
							if(obj.getString(ServerActions.ACTION_COMMAND).equals(ServerActions.ACTION_CREATE_NEW_GROUP)){
								if(obj.getString(ServerActions.SERVER_RET_VAL).equals("1")){
									// edit SharedPref and change status to true
									Log.d("MainActivity","Saving SharedPrefs");
									SharedPreferences settings = getSharedPreferences(PREFS_FILE, 0);
									SharedPreferences.Editor editor = settings.edit();
									System.out.println("group id is:"+obj.getString(ServerActions.SERVER_DATA));
									editor.putString(PREFS_GRP, obj.getString(ServerActions.SERVER_DATA));
									editor.commit();
									System.out.println("respons: "+obj.toString());
									System.out.println("saved group id: " + settings.getString(PREFS_GRP,"UU"));
									Toast.makeText(context, "GROUP CREATION SUCCESSFUL",
											Toast.LENGTH_LONG).show();
									try {
										Log.d("MainActivity","Call Menu");
										// unregister receiver
										
										Intent launcher = new Intent(context, GroupPrefs.class);
										startActivity(launcher);
										finish();
										
									} catch (RuntimeException e) {
										Log.e("MainActivity","Failed Call Menu: "+e);
									}
								}else{
									Toast.makeText(context, "GROUP CREATION UNSUCCESSFUL",
											Toast.LENGTH_LONG).show();
								}
							}
							
							
							
						}
					} catch (JSONException e) {
						Log.e("BroadcastReceiver","JSON error: "+e);
					} 
				}else{
					//dismiss progress dialog
					pd.dismiss();
					Toast.makeText(context,"Unexpected error occured,Please Try again later",
							Toast.LENGTH_LONG).show();
				}
			}
		};

		// Register filter
		filter = new IntentFilter(SERVER_GRP);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_loged, menu);
		return true;
	}
	
	public void onExit(View view){
		
		finish();
	}
	
	public void onResume() {
		super.onResume();
		registerReceiver(receiver, filter);	
		}
	
	@Override
	public void onPause() {
		super.onPause();
		// Unregister filter
		unregisterReceiver(receiver);
		// call finish on self
		
	}
	
	
	public void onRequest(View view){

		//Intent intent = new Intent(this, Loged.class);
		//startActivity(intent);
	}
	
	public void updateGroup(View view)
	{
		pd = ProgressDialog.show(this, "Loading..", "Please wait",true,true);
		// save sharedPrefs, status is false until confirmation
		SharedPreferences settings = getSharedPreferences(PREFS_FILE, 0);
		String userName = settings.getString(PREFS_USER,"username");
		
		myactions.get_group(userName);
	}
	
	
	public void logout(View view) 
	{
		final AlertDialog.Builder editalert = new AlertDialog.Builder(this);
		editalert.setTitle("logout");
		editalert.setIcon(android.R.drawable.ic_dialog_alert);
		editalert.setCancelable(true);
		
		final TextView in = new TextView(this);
		
		in.setText("Do you really wish to logout? ");
		in.setTextSize(22);
		editalert.setView(in);
		editalert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				SharedPreferences settings = getSharedPreferences(PREFS_FILE, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean(PREFS_STATS, false);
				editor.commit();
				
				
				
				finish();
			}
		});
		editalert.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		editalert.create();

		editalert.show();
	}
	
	public void newGroupPrefs(View view){
		pd = ProgressDialog.show(this, "Loading..", "Please wait",true,true);
		// save sharedPrefs, status is false until confirmation
		SharedPreferences settings = getSharedPreferences(PREFS_FILE, 0);
		String userName = settings.getString(PREFS_USER,"username");
		
		myactions.create_group(userName);
		
	
		
	}
	
}
