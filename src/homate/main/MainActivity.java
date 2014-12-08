package homate.main;
import homate.config.R;
import homate.server.HTTPIntentService;
import homate.server.ServerActions;
import org.json.JSONException;
import org.json.JSONObject;
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
import android.widget.Toast;

public class MainActivity extends Activity 
{
	public static final String PREFS_FILE	="UserPrefHomate.dat";
	public static final String PREFS_STATS	="login_status";
	public static final String SERVER_LOGIN ="jukeMe.survey.ServerActions.br.login";
	final protected String TAG = this.getClass().getName();
	final protected String SENDER_ID = "882694835341";
	public ProgressDialog pd;
	public static final String PREFS_USER 	="jukeMe";
	public static final String PREFS_PWD	="password";
	private BroadcastReceiver receiver;
	private IntentFilter filter;
	private ServerActions myactions;
	public static boolean exit=false;
	private String userName;
	private String passWord;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		Log.d("Main","onCreate() called");
		myactions = new ServerActions(this, SERVER_LOGIN);
		
		// check saved SharedPref for login status
		SharedPreferences settings = getSharedPreferences(PREFS_FILE, 0);
		boolean login_stats = settings.getBoolean(PREFS_STATS, false);
		if (login_stats) {
			try {
				// Skip login 
				Log.d("LoginActivity","Skip Login");
				Intent launcher = new Intent(this, Loged.class);
				startActivity(launcher);
				finish();
			} catch (RuntimeException e) {
				Log.e("LoginActivity","Failed Call Menu: "+e);
			}
		} 

		// setup receiver - should check if there are some updates in server data base
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				System.out.println("LoginActivity"+"Broadcast Received");
				String response = intent.getStringExtra(HTTPIntentService.PARAM_OUT_MSG);
				if (!response.equals("")) {
					try {
						JSONObject obj = new JSONObject(response);
						if (obj != null ) {
							//dismiss progress dialog
							pd.dismiss();
							System.out.println(obj.toString());  //TODO debug-remove

							if (obj.getString(ServerActions.SERVER_RET_VAL).equals("1")) {
								/** Register Verified */

								// edit SharedPref and change status to true
								Log.d("MainActivity","Saving SharedPrefs");
								SharedPreferences settings = getSharedPreferences(PREFS_FILE, 0);
								SharedPreferences.Editor editor = settings.edit();
								editor.putBoolean(PREFS_STATS, true);
								editor.commit();
								
								// call menu
								try {
									Log.d("MainActivity","Call Menu");
									// unregister receiver
									Intent launcher = new Intent(context, Loged.class);
									launcher.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//remove login screen from stack
									launcher.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);// Stay single instance
									startActivity(launcher);
								} catch (RuntimeException e) {
									Log.e("MainActivity","Failed Call Menu: "+e);
								}

							}
							
							/** Anyway Print Message from Server */
							//TODO change error messages from server to local to support other languages
							Toast.makeText(context, obj.getString(ServerActions.SERVER_MSG),
									Toast.LENGTH_LONG).show();
							
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
		filter = new IntentFilter(SERVER_LOGIN);


	}
	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(receiver, filter);	
		if(exit){
			finish();
		}
	}
	@Override
	public void onPause() {
		super.onPause();
		// Unregister filter
		unregisterReceiver(receiver);
		// call finish on self
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void onLogin(View view)
	{
		pd = ProgressDialog.show(this, "Loading..", "Please wait",true,true);
		// save sharedPrefs, status is false until confirmation
		SharedPreferences settings = getSharedPreferences(PREFS_FILE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(PREFS_STATS, false);
		userName = (((EditText)findViewById(R.id.userName)).getText().toString());
		passWord = (((EditText)findViewById(R.id.password)).getText().toString());
		
		editor.putString(PREFS_USER, userName);
		editor.putString(PREFS_PWD, passWord);
		editor.commit();
		myactions.new_user_registery(userName, passWord);
		
	}
	
	public void onExit(View view)
	{
		finish();
	}

	@Override
	public void onBackPressed() 
	{
		final AlertDialog.Builder editalert = new AlertDialog.Builder(this);
		editalert.setTitle("Exit");
		editalert.setIcon(android.R.drawable.ic_dialog_alert);
		editalert.setCancelable(true);
		final EditText input = new EditText(this);
		input.setHint("Do you really wish to exit? ");
		editalert.setView(input);
		editalert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				System.out.println("backpressed");
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
