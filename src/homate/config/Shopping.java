package homate.config;

import org.json.JSONException;
import org.json.JSONObject;

import homate.server.HTTPIntentService;
import homate.server.ServerActions;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Shopping extends Activity implements ListView.OnItemClickListener, OnTouchListener{
	
	private ServerActions myactions;
	private IntentFilter filter;
	private BroadcastReceiver receiver;
	private String[] items = {""};
	private Boolean[] checks;
	private ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_shopping);
		
		
		myactions = new ServerActions(this, getResources().getString(R.string.SHOPPING));
		
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String response = intent.getStringExtra(HTTPIntentService.PARAM_OUT_MSG);
				if (!response.equals("")) {
					try {
						JSONObject obj = new JSONObject(response);
						if (obj != null ) {
							if((obj.getString(ServerActions.ACTION_COMMAND).equals(ServerActions.ACTION_ADD_SHOPPING_ITEM))
									||(obj.getString(ServerActions.ACTION_COMMAND).equals(ServerActions.ACTION_REMOVE_SHOPPING_ITEM)))
							{
								if(obj.getString(ServerActions.SERVER_RET_VAL).equals("1")) getShoppingList();
								getShoppingList();
							}
							
							
							if(obj.getString(ServerActions.ACTION_COMMAND).equals(ServerActions.ACTION_GET_SHOPPING_LIST)){
								System.out.println(obj.toString());  //TODO debug-remove
								
								if(obj.getString(ServerActions.SERVER_RET_VAL).equals("1")){
									items = obj.getString(ServerActions.SERVER_MSG).split("#");
									checks = new Boolean[items.length];
									for(int i = 0;i<checks.length;i++){
										checks[i] = false;
									}
									ArrayAdapter<String> adapter =new ArrayAdapter<String>(context,R.layout.simple_list_item_shopping,items);
									list.setAdapter(adapter);
									}else{
										Toast.makeText(context,obj.getString(ServerActions.SERVER_MSG),
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
		filter = new IntentFilter(getResources().getString(R.string.SHOPPING));
		
		// check saved SharedPref for login status
						 
		list = (ListView)findViewById(R.id.shoppinglist);
		ArrayAdapter<String> adapter =new ArrayAdapter<String>(this,R.layout.simple_list_item_shopping,items);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		
		getShoppingList();
		
		findViewById(R.id.shoppingLayout).setOnTouchListener(this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shopping, menu);
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
	
	public void onResume() {
		super.onResume();
		registerReceiver(receiver, filter);	
		
	}
	public void getShoppingList() {
		SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.PREFS_FILE), 0);
		String group = settings.getString( getResources().getString(R.string.PREFS_GRP),"0");
		
		
			try {
				myactions.get_shopping_list(group);
			} catch (RuntimeException e) {
				Log.e("LoginActivity","Failed Call Menu: "+e);
			}
		
	}
	
	public void addItem(View view) {
		SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.PREFS_FILE), 0);
		String group = settings.getString( getResources().getString(R.string.PREFS_GRP),"0");
		String username = settings.getString( getResources().getString(R.string.PREFS_USER),"username");
		String item = (((EditText)findViewById(R.id.editshoppingitem)).getText().toString());
		
			try {
				myactions.add_shopping_item(username,group," "+item+" ");
			} catch (RuntimeException e) {
				Log.e("LoginActivity","Failed Call Menu: "+e);
			}
			
			((EditText)findViewById(R.id.editshoppingitem)).getText().clear();
			
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		
	}

	
	public void updateShoppingList(View view) {
		SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.PREFS_FILE), 0);
		String group = settings.getString( getResources().getString(R.string.PREFS_GRP),"0");
		if(isRemoved()){
			String username = settings.getString( getResources().getString(R.string.PREFS_USER),"username");
			try {
				myactions.remove_shopping_item(username,group,getChanges());
			} catch (RuntimeException e) {
				Log.e("LoginActivity","Failed Call Menu: "+e);
			}
			
		}else{
			getShoppingList();
		}
		
	}
	public boolean isRemoved(){
		for(int i =0;i < checks.length;i++){
			if(checks[i]  == true) return true;
		}
		return false;
	} 
	
	public String getChanges(){
		String ans = "";
		for(int i =0;i < checks.length;i++){
			if(checks[i]  == true) ans=ans+items[i]+"#";
		}
		return ans;
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
	
	
	@Override
	public void onItemClick(AdapterView<?> ad, View v, int i, long j) {
		System.out.println("the new size is "+(checks.length)+i);
		checks[i] = !(checks[i]);
		Paint paint = new Paint();
		paint.setStrokeWidth(2);
		if(checks[i]) {
			((TextView) v).setPaintFlags(((TextView) v).getPaintFlags()  |Paint.STRIKE_THRU_TEXT_FLAG);
			((TextView) v).setTextColor(Color.BLACK);
		}
		else{
			((TextView) v).setPaintFlags(((TextView) v).getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
			((TextView) v).setTextColor(Color.parseColor("#FFA07A"));
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		return false;
	}
	
	
}
