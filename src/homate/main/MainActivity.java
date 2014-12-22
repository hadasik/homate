package homate.main;
import homate.config.Homenu;
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
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity 
{
	final protected String TAG = this.getClass().getName();
	public ProgressDialog pd;
	private BroadcastReceiver receiver;
	private IntentFilter filter;
	private ServerActions myactions;
	private String userName;
	private String passWord;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		Log.d("Main","onCreate() called");
		myactions = new ServerActions(this, getResources().getString(R.string.SERVER_LOGIN));
		

		// setup receiver - should check if there are some updates in server data base
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				System.out.println("===LoginActivity "+"Broadcast Received===");
				String response = intent.getStringExtra(HTTPIntentService.PARAM_OUT_MSG);
				System.out.println("========after getting a response ====");
				if (!response.equals("")) {
					try {
						JSONObject obj = new JSONObject(response);
						System.out.println("========after creating json obj ====");
						if (obj != null ) {
							//dismiss progress dialog
							pd.dismiss();
							System.out.println("========before json to str ====");
							System.out.println(obj.toString());  //TODO debug-remove
							System.out.println("========after json to str ====");

							if (obj.getString(ServerActions.SERVER_RET_VAL).equals("1")) {
								/** Register Verified */

								// edit SharedPref and change status to true
								Log.d("MainActivity","Saving SharedPrefs");
								SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.PREFS_FILE), 0);
								SharedPreferences.Editor editor = settings.edit();
								editor.putBoolean(getResources().getString(R.string.PREFS_STATS), true);
								System.out.println("NEW group id is:"+obj.getString(ServerActions.SERVER_DATA));								
								editor.putString(getResources().getString(R.string.PREFS_GRP), obj.getString(ServerActions.SERVER_DATA));
								editor.putString(getResources().getString(R.string.PREFS_GRP_NAME), obj.getString(ServerActions.SERVER_DATA2));
								editor.commit();
								
								
								// call menu
								try {
									Log.d("MainActivity","Call Menu");
									// unregister receiver
									Intent launcher;
									if(!(obj.getString(ServerActions.SERVER_DATA).equals("0"))){
										launcher = new Intent(context, Homenu.class);
									}else{
										launcher = new Intent(context, Loged.class);
									}
									launcher.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//remove login screen from stack
									launcher.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);// Stay single instance
									startActivity(launcher);
									finish();
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
		filter = new IntentFilter(getResources().getString(R.string.SERVER_LOGIN));
		
		// check saved SharedPref for login status
						 
	}

	public void onStart(){
		super.onStart();
		
		SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.PREFS_FILE), 0);
		boolean login_stats = settings.getBoolean(getResources().getString(R.string.PREFS_STATS), false);
		if (login_stats) {
			try {
				pd = ProgressDialog.show(this, "Loading..", "Please wait",true,true);
				
				userName = settings.getString(getResources().getString(R.string.PREFS_USER),"username");
				passWord = settings.getString(getResources().getString(R.string.PREFS_PWD),"password");
				myactions.new_user_registery(userName, passWord);
			} catch (RuntimeException e) {
				Log.e("LoginActivity","Failed Call Menu: "+e);
			}
		}
	}
	
	@Override
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
		SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.PREFS_FILE), 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(getResources().getString(R.string.PREFS_STATS), false);
		userName = (((EditText)findViewById(R.id.userName)).getText().toString());
		passWord = (((EditText)findViewById(R.id.password)).getText().toString());
		
		editor.putString(getResources().getString(R.string.PREFS_USER), userName);
		editor.putString(getResources().getString(R.string.PREFS_PWD), passWord);
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
		final TextView in = new TextView(this);
		in.setText("Do you really wish to exit? ");
		in.setTextSize(22);
		editalert.setView(in);
		editalert.setView(in);
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
