package homate.config;

import org.json.JSONException;
import org.json.JSONObject;

import homate.server.HTTPIntentService;
import homate.server.ServerActions;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Bills extends Activity {

	private ServerActions myactions;
	private IntentFilter filter;
	private BroadcastReceiver receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_bills);
		
myactions = new ServerActions(this, getResources().getString(R.string.GET_BILLS_TOTAL));
		
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String response = intent.getStringExtra(HTTPIntentService.PARAM_OUT_MSG);
				if (!response.equals("")) {
					try {
						JSONObject obj = new JSONObject(response);
						if (obj != null ) {
							
							if(obj.getString(ServerActions.ACTION_COMMAND).equals(ServerActions.ACTION_GET_BILLS_TOTAL)){
								//dismiss progress dialog
								System.out.println(obj.toString());  //TODO debug-remove

								if (obj.getString(ServerActions.SERVER_RET_VAL).equals("1")) {
								
									View v = (TextView)findViewById(R.id.billstext2);
									((TextView) v).setText(obj.getString(ServerActions.SERVER_MSG));
									
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
		filter = new IntentFilter(getResources().getString(R.string.GET_BILLS_TOTAL));
		
		
		
	}
	
	public void onResume() {
		super.onResume();
		registerReceiver(receiver, filter);	
		getBillsTotal(null);
	}
	
	public void onPause() {
		super.onPause();
		// Unregister filter
		unregisterReceiver(receiver);
		// call finish on self
		
	}
	

	private void getBillsTotal(Object object) {
		SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.PREFS_FILE), 0);
		String group = settings.getString( getResources().getString(R.string.PREFS_GRP),"0");
		String username = settings.getString( getResources().getString(R.string.PREFS_USER),"username");
		
		try {
			myactions.get_bills_total(username,group);
		} catch (RuntimeException e) {
			Log.e("LoginActivity","Failed Call Menu: "+e);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bills, menu);
		return true;
	}
	
	
	public void showBills(View view) {
		Intent launcher = new Intent(getBaseContext(), BillsCategory.class);
		Button b = (Button)view;
	    String buttonText = b.getText().toString();
		launcher.putExtra("category", buttonText);
		launcher.putExtra("personalFlag", false);
		startActivity(launcher);
		finish();

	}
	
	public void personalBills(View view) {
		Intent launcher = new Intent(getBaseContext(), BillsCategory.class);
		SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.PREFS_FILE), 0);
		String userName = settings.getString(getResources().getString(R.string.PREFS_USER),"username");
		launcher.putExtra("category", "hello "+userName+",\nyour unpayed bills are:");
		launcher.putExtra("personalFlag", true);
		startActivity(launcher);
		finish();

	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onBackPressed() 
	{
		Intent launcher = new Intent(getBaseContext(), Homenu.class);
		startActivity(launcher);
		finish();
	}
	
	
}
