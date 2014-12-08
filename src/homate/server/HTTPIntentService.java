package homate.server;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Scanner;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class HTTPIntentService extends IntentService 
{
	public static final String PARAM_BR_ACTION_NAME = "br_action_name";
    public static final String PARAM_URL = "url_msg";
    public static final String PARAM_IN_MSG = "imsg";
    public static final String PARAM_OUT_MSG = "omsg";
    private final int TIMEOUT_VALUE = 5000;

    public HTTPIntentService() 
    {
        super("HTTPIntentService");
        Log.d("HTTPIntentService","First Created");
        System.out.println("HTTPIntentService"+"First Created");
    }

    @Override
    protected void onHandleIntent(Intent intent) 
    {
    	//=============================================================================
    	// Read intent strings
    	String response = "null string error";
    	String url = intent.getStringExtra(PARAM_URL);
    	String post_data = intent.getStringExtra(PARAM_IN_MSG);
    	String action_name = intent.getStringExtra(PARAM_BR_ACTION_NAME);
    	System.out.println("post data"+post_data);
    	System.out.println("url"+url);
    	if (url != null && post_data != null ) {
    		response = "";
    	    OutputStream output = null;
    		Log.d("HTTP","post on url: "+ url.toString());  
    		
    		try {
    			/**
    			 *  HttpPost for sending data with the url
    			 *  HttpClient for getting response
    			 */
    			URL loginUrl = new URL(url);
    			System.out.println(url);
    			
    			HttpURLConnection client=(HttpURLConnection)loginUrl.openConnection();
    			//set timeout
    			client.setConnectTimeout(TIMEOUT_VALUE);
    			client.setReadTimeout(TIMEOUT_VALUE);

    			//set the output to true, indicating you are outputting(uploading) POST data
    			client.setDoOutput(true);
    			//once you set the output to true, you don't really need to set the request method to post, but I'm doing it anyway
    			client.setRequestMethod("POST");

    			//Android documentation suggested that you set the length of the data you are sending to the server, BUT
    			// do NOT specify this length in the header by using conn.setRequestProperty("Content-Length", length);
    			//use this instead.
    			System.out.println("This is the post format = " + post_data.toString());
    			client.setFixedLengthStreamingMode(post_data.toString().length());
    			client.setRequestProperty("Content-Type", "application/json; charset=utf-8");

    			//send the POST out
    			output = client.getOutputStream();
    			output.write(post_data.toString().getBytes());

    			//start listening to the stream
    			Scanner inStream = new Scanner(client.getInputStream());
    			
    			//process the stream
    			while(inStream.hasNextLine())
    				response+=(inStream.nextLine());
    			
    			System.out.println("response recieved:"+response);
    			// disconnect client
    			client.disconnect();

    		} catch(MalformedURLException e){
    			Log.e("HTTP","MalformedURLException: " + e.toString());
    		} catch (SocketTimeoutException e) {
    			Log.e("HTTP","timeOut with: " + e.toString());
    		} catch (IOException e) {
    			Log.e("HTTP","IOException :" +  e.toString());
    		} finally {
    			if (output != null) { 
    				try {
						output.close();
					} catch (IOException e) {
						Log.e("HTTP","IOException after close: " +  e.toString());
					} 
    			}
    		}
    	}
		
    	// Broadcast back the result to target
    	Intent broadcastIntent = new Intent();
    	broadcastIntent.setAction(action_name);	// set broadcast action name
    	broadcastIntent.putExtra(PARAM_OUT_MSG, response);
    	Log.d("SimpleIntentService", "onHandleIntent: sending broadcast");
    	sendBroadcast(broadcastIntent);
    }
}