package homate.main;

import jukeme.survey.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
/*hadas*/
public class Loged extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_loged);
		
		
		
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_loged, menu);
		return true;
	}
	
	public void onExit(View view){
		MainActivity.exit=true;
		finish();
	}
	public void onRequest(View view){

		//Intent intent = new Intent(this, Loged.class);
		//startActivity(intent);
	}
	
	

}
