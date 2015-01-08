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
	
	boolean personalFlag;
	String category;
	private ServerActions myactions;
	private IntentFilter filter;
	private BroadcastReceiver receiver;
	private ListView list;
	private String[] bills = {""};
	private String[] billIDs = {""};
	private String[][] splitBills;

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
									if(personalFlag){
										showBillByPerson();
									}else {
										showBillByCategory();
									}
								}
							}
							
							if(obj.getString(ServerActions.ACTION_COMMAND).equals(ServerActions.ACTION_SHOW_BILLS)){
								
								parseShowBills(obj.getString(ServerActions.SERVER_MSG));
								ArrayAdapter<String> adapter =new ArrayAdapter<String>(context,R.layout.simple_list_item_homate,bills);
								list.setAdapter(adapter);
								
							}
							
							if(obj.getString(ServerActions.ACTION_COMMAND).equals(ServerActions.ACTION_REMOVE_BILL)){
								
								Toast.makeText(context,obj.getString(ServerActions.SERVER_MSG),
										Toast.LENGTH_LONG).show();
								
								if(obj.getString(ServerActions.SERVER_RET_VAL).equals("1")) {
									if(personalFlag){
										showBillByPerson();
									}else {
										showBillByCategory();
									}
								}
								
							}
							if(obj.getString(ServerActions.ACTION_COMMAND).equals(ServerActions.ACTION_SHOW_PERSONAL_BILLS)){
								
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
		billIDs =  new String[wholeBills.length];
		splitBills = new String[wholeBills.length][];
		for(int i=0; i <wholeBills.length; i++){
			splitBills[i] = (wholeBills[i]).split("@");
			bills[i] = splitBills[i][0];
			if(splitBills[i].length>1) billIDs[i] =  splitBills[i][1];
		}
		
	}
	

	@Override
	public void onStart() {
		super.onStart();
		category = getIntent().getExtras().getString("category");
		personalFlag = getIntent().getExtras().getBoolean("personalFlag");
		View v = (TextView)findViewById(R.id.BCTEXT);
		((TextView) v).setText(category);
		if(personalFlag){
		findViewById(R.id.BCDATEedit).setVisibility(View.GONE);
		findViewById(R.id.BCTOTALedit).setVisibility(View.GONE);
		findViewById(R.id.addBillButton).setVisibility(View.GONE);
		findViewById(R.id.billbutton2).setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(receiver, filter);	
		if(personalFlag){
			showBillByPerson();
		}else {
			showBillByCategory();
		}
		
	}
	
	private void showBillByCategory() {
		SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.PREFS_FILE), 0);
		String groupID = settings.getString( getResources().getString(R.string.PREFS_GRP),"0");
		String username = settings.getString( getResources().getString(R.string.PREFS_USER),"usename");
		
		myactions.show_bill(groupID, username,category);
		
	}
	private void showBillByPerson() {
		SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.PREFS_FILE), 0);
		String username = settings.getString( getResources().getString(R.string.PREFS_USER),"usename");
		String group = settings.getString( getResources().getString(R.string.PREFS_GRP),"0");
		myactions.show_personal_bill(username,group);
		
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
	
	
	public void onItemClick(AdapterView<?> ad, View v, final int i, long j) {
		 PopupMenu popup = new PopupMenu(this, v);
		 MenuInflater inflater = popup.getMenuInflater();
		 inflater.inflate(R.menu.bills_actions, popup.getMenu());
		 
		 
		  //registering popup with OnMenuItemClickListener  
         popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {  
          public boolean onMenuItemClick(MenuItem item) {  
        	  SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.PREFS_FILE), 0);
      		  String userName = settings.getString( getResources().getString(R.string.PREFS_USER),"username");
      		  String groupID = settings.getString( getResources().getString(R.string.PREFS_GRP),"0");
      	    switch (item.getItemId()) {
      	        case R.id.menu_bill_details:
      	        	
      	        	billDetail(i);
      	            return true;
      	        case R.id.menu_bill_remove:
      	        	myactions.removeBill(userName,billIDs[i],groupID);
      	            return true;
      	        default:
      	            return false;
      	    }  
          }

		
         });  
		 
		 
		 popup.show();
	}
	
	
	private void billDetail(int i) {
		Intent launcher = new Intent(getBaseContext(), BillsPayment.class);
		launcher.putExtra("status", splitBills[i]);
		startActivity(launcher);
		
	}  
}
