package homate.main;
import java.util.Timer;

import org.json.JSONException;
import org.json.JSONObject;

import homate.config.GroupPrefs;
import homate.config.Homenu;
import homate.config.R;
import homate.server.HTTPIntentService;
import homate.server.ServerActions;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;




public class Loged extends Activity {
	
	private BroadcastReceiver receiver;
	private IntentFilter filter;
	private ServerActions myactions;
	Timer timer = new Timer();
	private Handler handler;
	private boolean isUpdating;
	private Thread groupUpdaterInstance;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_loged);	
		handler = new Handler();
		
		myactions = new ServerActions(this,  getResources().getString(R.string.SERVER_GRP));
		

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
							
							System.out.println(obj.toString());  //TODO debug-remove
							
							if(obj.getString(ServerActions.ACTION_COMMAND).equals(ServerActions.ACTION_GET_GROUP)){
								if (!(obj.getString(ServerActions.SERVER_RET_VAL).equals("0"))) {
									/** Register Verified */
									
									isUpdating = false;
									groupUpdaterInstance.interrupt();
									
									// edit SharedPref and change status to true
									Log.d("MainActivity","Saving SharedPrefs");
									SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.PREFS_FILE), 0);
									SharedPreferences.Editor editor = settings.edit();
									System.out.println("group id is:"+obj.getString(ServerActions.SERVER_RET_VAL));
									editor.putString( getResources().getString(R.string.PREFS_GRP), obj.getString(ServerActions.SERVER_RET_VAL));
									editor.putString( getResources().getString(R.string.PREFS_GRP_NAME), obj.getString(ServerActions.SERVER_MSG));
									editor.commit();
									
									// call menu
									try {
										Log.d("MainActivity","Call Menu");
										// unregister receiver
										Intent launcher = new Intent(context, Homenu.class);
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
									SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.PREFS_FILE), 0);
									SharedPreferences.Editor editor = settings.edit();
									editor.putString( getResources().getString(R.string.PREFS_GRP), obj.getString(ServerActions.SERVER_DATA));
									editor.commit();
									System.out.println("respons: "+obj.toString());
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
					Toast.makeText(context,"Unexpected error occured,Please Try again later",
							Toast.LENGTH_LONG).show();
				}
			}
		};

		// Register filter
		filter = new IntentFilter( getResources().getString(R.string.SERVER_GRP));
		
		
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
	
	public void onStart(){
		super.onStart();
		groupUpdaterInstance= new Thread(new GroupUpdater());

		
	}
	
	public void onResume() {
		super.onResume();
		registerReceiver(receiver, filter);	
		
		isUpdating = true;
		groupUpdaterInstance.start();
		
		}
	
	@Override
	public void onPause() {
		super.onPause();
		// Unregister filter
		unregisterReceiver(receiver);
		// call finish on self
		
		isUpdating = false;
		groupUpdaterInstance.interrupt();
		
	}
	
	class GroupUpdater implements Runnable {
			        @Override
			        public void run() {
			            while (isUpdating) {
			                try {
			                    Thread.sleep(15000);
			                } catch (InterruptedException e) {
			                    e.printStackTrace();
			                }
			                handler.post(new Runnable() {
			                    @Override
			                    public void run() {
			                        updateGroup(null);
			                    }
			                });
			            }
			        }
			    }
	
	//public void onRequest(View view){

		//Intent intent = new Intent(this, Loged.class);
		//startActivity(intent);
	//}
	
	public void updateGroup(View view)
	{
		
		// save sharedPrefs, status is false until confirmation
		SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.PREFS_FILE), 0);
		String userName = settings.getString( getResources().getString(R.string.PREFS_USER),"username");
		
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
				SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.PREFS_FILE), 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean( getResources().getString(R.string.PREFS_STATS), false);
				editor.commit();
				Intent intent = new Intent(getBaseContext(),MainActivity.class);
				startActivity(intent);
				
				
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
		// save sharedPrefs, status is false until confirmation
		SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.PREFS_FILE), 0);
		String userName = settings.getString( getResources().getString(R.string.PREFS_USER),"username");
		
		myactions.create_group(userName);
		
	
		
	}
	
	public void onBackPressed() 
	{
		
		final AlertDialog.Builder editalert = new AlertDialog.Builder(this);
		editalert.setTitle("Exit");
		editalert.setIcon(android.R.drawable.ic_dialog_alert);
		editalert.setCancelable(true);
		final TextView in = new TextView(this);
		in.setText("Do you really wish to exit? ");
		in.setTextSize(22);
		editalert.setView(in);
		editalert.setView(in);
		editalert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				System.out.println("backpressed");

				isUpdating = false;
				groupUpdaterInstance.interrupt();
				
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
	

	
	
	
}
