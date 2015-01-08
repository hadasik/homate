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
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class BillsPayment extends Activity implements ListView.OnItemClickListener {
	
	String[] status;
	String[] userDetails;
	String[] users;
	String[] statusSettings;
	boolean flag = false;
	private ServerActions myactions;
	private IntentFilter filter;
	private BroadcastReceiver receiver;
	private ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_bills_payment);
		
		
		myactions = new ServerActions(this, getResources().getString(R.string.BILL_PAYMENT));
		
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String response = intent.getStringExtra(HTTPIntentService.PARAM_OUT_MSG);
				if (!response.equals("")) {
					try {
						JSONObject obj = new JSONObject(response);
						if (obj != null ) {
							
							if(obj.getString(ServerActions.ACTION_COMMAND).equals(ServerActions.ACTION_PAYMENT_DETAILS)){
								status = obj.getString(ServerActions.SERVER_MSG).split("@");
								
								setPaymentList();
								
								ArrayAdapter<String> adapter =new ArrayAdapter<String>(context,R.layout.simple_list_item_homate,userDetails);
								list.setAdapter(adapter);
								
							}else{
							Toast.makeText(context,obj.getString(ServerActions.SERVER_MSG),
									Toast.LENGTH_LONG).show();
							
							showBillByBillID();
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
		filter = new IntentFilter(getResources().getString(R.string.BILL_PAYMENT));
		
		// check saved SharedPref for login status

		
		
		
	}

	@Override
	public void onStart() {
		super.onStart();
		status = getIntent().getExtras().getStringArray("status");
		setPaymentList();
		
		View v = (TextView)findViewById(R.id.BPTEXT);
		((TextView) v).setText(status[0]);
		
		
		 
		list = (ListView)findViewById(R.id.BPlist);
		ArrayAdapter<String> adapter =new ArrayAdapter<String>(this,R.layout.simple_list_item_homate,userDetails);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
	}
	
	private void setPaymentList() {
		int i = 2;
		int j=0;
		userDetails = new String[(status.length-2)/2];
		users = new String[(status.length-2)/2];
		statusSettings = new String[(status.length-2)/2];
		while(i<status.length){
			System.out.println("name " +status[i]);
			System.out.println("statust " +status[i+1]);
			
			users[j] = status[i];
			statusSettings[j] = status[i+1];
			
			if(status[i+1].equals("0"))
				userDetails[j] = status[i] + " - inform as payed";
			if(status[i+1].equals("1"))
				userDetails[j] = status[i] + " - waiting for confirmation";
			if(status[i+1].equals("2"))
				userDetails[j] = status[i] + " - payed";
			i=i+2;
			j++;
		}
	}
	
	public void onPause() {
		super.onPause();
		// Unregister filter
		unregisterReceiver(receiver);
		// call finish on self
		
		flag = true;
		
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(receiver, filter);		
		if(flag) showBillByBillID();
	}
	

	public void onBackPressed(){
		finish();
	}

	@Override
	public void onItemClick(AdapterView<?> ad, View v, final int i, long j) {
		flag = true;
		System.out.println("CLICKED !!!!!");
		SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.PREFS_FILE), 0);
		String username = settings.getString( getResources().getString(R.string.PREFS_USER),"usename");
		if(statusSettings[i].equals("0")) {
			if(username.equals(users[i])) myactions.request_approval(username,status[1]);
			else Toast.makeText(this,"you can only update your payments",
					Toast.LENGTH_LONG).show();
		}
		
		if(statusSettings[i].equals("1")) {
			
			 PopupMenu popup = new PopupMenu(this, v);
			 MenuInflater inflater = popup.getMenuInflater();
			 inflater.inflate(R.menu.decision_payment, popup.getMenu());
			 
			 
			  //registering popup with OnMenuItemClickListener  
	         popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {  
	          public boolean onMenuItemClick(MenuItem item) {  
	        	  SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.PREFS_FILE), 0);
	      		  String userName = settings.getString( getResources().getString(R.string.PREFS_USER),"username");
	      	    switch (item.getItemId()) {
	      	        case R.id.payment_accept:
	      	        	myactions.set_payment(userName,users[i],status[1],2);
	      	            return true;
	      	        case R.id.payment_decline:
	      	        	myactions.set_payment(userName,users[i],status[1],0);
	      	            return true;
	      	        default:
	      	            return false;
	      	    }  
	          }

			
	         });  
			 
			 
			 popup.show();
			 
			 
	
		}
	}
		
	
	private void showBillByBillID() {
		myactions.get_payment_details(status[1]);
		
	}
	
	
	
}
