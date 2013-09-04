package ravi.yoli;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;


public class MainActivity extends Activity implements
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener,
	OnItemClickListener, OnItemLongClickListener
{
	private LocationClient mLocationClient;
	EditText mPlaceInput;
	ListView mPlaceList;
	ItemAdapter mAdapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parse.initialize(this, "RJhQljyB0RBU2Q1Shk4nMmmvqBxz9UgJatpqytBs", "vUqYU4AjQfqqnrDZqOEoFmBQ2Hwp03Zhobb6STxf"); 
        ParseAnalytics.trackAppOpened(getIntent());
        ParseObject.registerSubclass(Item.class);
        
        mPlaceInput = (EditText) findViewById(R.id.place_input);
        mPlaceList = (ListView) findViewById(R.id.place_list);
        mAdapter = new ItemAdapter(this, new ArrayList<Item>());
        mPlaceList.setAdapter(mAdapter);
        mPlaceList.setOnItemClickListener(this);
        mPlaceList.setOnItemLongClickListener(this);
        updateData();
        
        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        mLocationClient = new LocationClient(this, this, this);
        
    }

    private void updateData()
    {
    	
    	ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
    	query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
    	query.findInBackground(new FindCallback<Item>() {
    		
    		@Override
    		public void done(List<Item> items, ParseException error)
    		{
    			if (items != null)
    			{
    				mAdapter.clear();
    				mAdapter.addItems(items);
    			}
    		}
    		
    	});
    }
    
    public void submitPlace(View v)
    {
    	if (mPlaceInput.getText().length() > 0)
    	{
    		Item item = new Item();
    		Location currentLocation = mLocationClient.getLastLocation();
    		item.setDescription(mPlaceInput.getText().toString());
    		item.setLatitude(currentLocation.getLatitude());
    		item.setLongitude(currentLocation.getLongitude());
    		item.saveEventually();
    		mAdapter.insert(item, 0);
    		mPlaceInput.setText("");    		
    	}
    }
    
    /*
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
    }
    
    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }
    
    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
    }
    
    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }
    
    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            //showErrorDialog(connectionResult.getErrorCode());
        	Log.e("MainActivity", "Error connecting to Google Play Service. Code# " + connectionResult.getErrorCode());
        	
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
 // Global constants
    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    
    
    /*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK :
                    /*
                     * Try the request again
                     */
                    break;
                }
            
        }
        
    }
    
    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
            // Get the error code
            int errorCode = resultCode;
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    errorCode,
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);
            return false;
        }
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{
		Item item = mAdapter.getItem(position);
		TextView description = (TextView) findViewById(R.id.place_description);
		
		String locuri = "geo:" + item.getLatitude() + "," + item.getLongitude() + "?q=" + item.getLatitude() + "," + item.getLongitude() + "(" + description.getText() + ")";
		Uri location = Uri.parse(locuri);
		System.out.println(location);
		
		
		Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
		PackageManager packman = getPackageManager();
		List<ResolveInfo> activities = packman.queryIntentActivities(mapIntent, 0);
		boolean isIntentSafe = activities.size() > 0;
		
		if (isIntentSafe)
		{
			startActivity(mapIntent);
		}
		else
		{
			Toast.makeText(this, "No registered app to show location", Toast.LENGTH_SHORT).show();
		}
		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
			long id) 
	{
		// TODO: Must display an alert box before deleting the item
		Item item = mAdapter.getItem(position);
		
		if (item != null)
		{
			item.deleteEventually();
			mPlaceList.removeViewAt(position);
		}
		
		return true;
	}
    
}
