package homate.config;

import homate.main.MainActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class Homenu extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_homenu);
	}
	
	public void onStart(){
		super.onStart();
		SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.PREFS_FILE), 0);
		String groupName = settings.getString(getResources().getString(R.string.PREFS_GRP_NAME), "House of Fun");
		
		TextView onScreenGroupName = (TextView) findViewById(R.id.textGroupName);
		onScreenGroupName.setText(groupName);
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
	
	public void settings(View view) 
	{
		Intent intent = new Intent(getBaseContext(),GroupPrefs.class);
		startActivity(intent);
		
		
		finish();
		
	}
	
	public void bills(View view) 
	{
		Intent intent = new Intent(getBaseContext(),Bills.class);
		startActivity(intent);
		
		
		finish();
		
	}
	
	
	
	public void shop(View view) 
	{
		Intent intent = new Intent(getBaseContext(),Shopping.class);
		startActivity(intent);
		
		
		finish();
		
	}
	
	public void cleaning(View view) 
	{
		Intent intent = new Intent(getBaseContext(),Cleaning.class);
		startActivity(intent);
		
		
		finish();
		
	}


}
