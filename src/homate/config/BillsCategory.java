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

public class BillsCategory extends Activity implements ListView.OnItemClickListener {
	
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
		setContentView(R.layout.activity_bills_category);
		
		myactions = new ServerActions(this, getResources().getString(R.string.BILL_CATEGORY));
		
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String response = intent.getStringExtra(HTTPIntentService.PARAM_OUT_MSG);
				if (!response.equals("")) {
					try {
						JSONObject obj = new JSONObject(response);
						if (obj != null ) {
							
							if(obj.getString(ServerActions.ACTION_COMMAND).equals(ServerActions.ACTION_ADD_BILLS)){
								//dismiss progress dialog
								System.out.println(obj.toString());  //TODO debug-remove

								Toast.makeText(context,obj.getString(ServerActions.SERVER_MSG),
										Toast.LENGTH_LONG).show();
								
								if(obj.getString(ServerActions.SERVER_RET_VAL).equals("1")){
								showBillByCategory();
								}
							}
							
							if(obj.getString(ServerActions.ACTION_COMMAND).equals(ServerActions.ACTION_SHOW_BILLS)){
								
								parseShowBills(obj.getString(ServerActions.SERVER_MSG));
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
		filter = new IntentFilter(getResources().getString(R.string.BILL_CATEGORY));
		
		// check saved SharedPref for login status
						 
		list = (ListView)findViewById(R.id.BClist);
		ArrayAdapter<String> adapter =new ArrayAdapter<String>(this,R.layout.simple_list_item_homate,bills);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// Unregister filter
		unregisterReceiver(receiver);
		// call finish on self
		
	}
	
	
	private void parseShowBills(String msg) {
		String[] wholeBills = msg.split("#");
		bills = new String[wholeBills.length];
		for(int i=0; i <wholeBills.length; i++){
			bills[i] = ((wholeBills[i]).split("@"))[0];
		}
		
	}
	

	@Override
	public void onStart() {
		super.onStart();
		category = getIntent().getExtras().getString("category");
		View v = (TextView)findViewById(R.id.BCTEXT);
		((TextView) v).setText(category);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(receiver, filter);	
		showBillByCategory();
		
	}
	
	private void showBillByCategory() {
		SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.PREFS_FILE), 0);
		String groupID = settings.getString( getResources().getString(R.string.PREFS_GRP),"0");
		String username = settings.getString( getResources().getString(R.string.PREFS_USER),"usename");
		
		myactions.show_bill(groupID, username,category);
		
	}

	public void onBackPressed() 
	{
		Intent launcher = new Intent(getBaseContext(), Bills.class);
		startActivity(launcher);
		finish();
	}
	
	
	public void addBill(View view){
		String total = (((EditText)findViewById(R.id.BCTOTALedit)).getText().toString());
		String date = (((EditText)findViewById(R.id.BCDATEedit)).getText().toString());

		SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.PREFS_FILE), 0);
		String groupID = settings.getString( getResources().getString(R.string.PREFS_GRP),"0");
		String username = settings.getString( getResources().getString(R.string.PREFS_USER),"usename");
		
		myactions.add_bill(groupID, username,category,total,date);
	}
	
	
	
	public void showArchive(View view) 
	{
		Intent launcher = new Intent(getBaseContext(), BillsArchive.class);
		launcher.putExtra("category", category);
		startActivity(launcher);
	}
	
	
	public void onItemClick(AdapterView<?> ad, View v, int i, long j) {
		
	}
	
	
	
	
}
