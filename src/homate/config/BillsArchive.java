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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BillsArchive extends Activity implements ListView.OnItemClickListener{

	
	String category;
	private ServerActions myactions;
	private IntentFilter filter;
	private BroadcastReceiver receiver;
	private ListView list;
	private String[] bills = {""};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_bills_archive);
		
		myactions = new ServerActions(this, getResources().getString(R.string.BILL_ARCHIVE));
		
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String response = intent.getStringExtra(HTTPIntentService.PARAM_OUT_MSG);
				if (!response.equals("")) {
					try {
						JSONObject obj = new JSONObject(response);
						if (obj != null ) {
							
							if(obj.getString(ServerActions.ACTION_COMMAND).equals(ServerActions.ACTION_SHOW_BILLS_ARCHIVE)){
								
								bills = obj.getString(ServerActions.SERVER_MSG).split("#");
								ArrayAdapter<String> adapter =new ArrayAdapter<String>(context,R.layout.simple_list_item_homate,bills);
								list.setAdapter(adapter);
								
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
		filter = new IntentFilter(getResources().getString(R.string.BILL_ARCHIVE));
		
		// check saved SharedPref for login status
						 
		list = (ListView)findViewById(R.id.BAlist);
		ArrayAdapter<String> adapter =new ArrayAdapter<String>(this,R.layout.simple_list_item_homate,bills);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bills_archive, menu);
		return true;
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
	
	public void onBackPressed(){
		finish();
	}
	
	public void onPause() {
		super.onPause();
		// Unregister filter
		unregisterReceiver(receiver);
		// call finish on self
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(receiver, filter);	
		showBillArchiveByCategory();
		
	}

	private void showBillArchiveByCategory() {
		SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.PREFS_FILE), 0);
		String groupID = settings.getString( getResources().getString(R.string.PREFS_GRP),"0");
		
		myactions.show_bill_archive(groupID,category);
		
	}
	
	@Override
	public void onStart() {
		super.onStart();
		category = getIntent().getExtras().getString("category");
		View v = (TextView)findViewById(R.id.BATEXT);
		String title = "Archive - "+category;
		((TextView) v).setText(title);
	}
	
	public void onItemClick(AdapterView<?> ad, View v, int i, long j) {
		
	}
	
}
