package tm.app.asp;

import tm.app.asp.ProximityIntentReceiver;
import tm.app.asp.ASPService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class ASPService extends Service implements LocationListener  {
	LocationManager locationManager;
	BroadcastReceiver ProximityIntentReceiver;
	private static final String PROX_ALERT_INTENT = "tm.app.asp.PROX_ALERT_INTENT";
	Intent intent = new Intent(PROX_ALERT_INTENT);
	private static final String POINT_LATITUDE_KEY = "POINT_LATITUDE_KEY";
    private static final String POINT_LONGITUDE_KEY = "POINT_LONGITUDE_KEY";
    private static float RADIUS = 0;
	PendingIntent proximityIntent;
	Location location;
    SharedPreferences prefs;
    LocationManager proxLM;
	
	@Override
	public void onCreate() {
		System.out.println("You started a service...");
		prefs = this.getSharedPreferences("tm.app.asp",Context.MODE_PRIVATE);
		ProximityIntentReceiver = new ProximityIntentReceiver();
		Location thisLoc;
		//intent = new Intent();
		//intent.setClass(ASPService.this, tm.app.asp.ProximityIntentReceiver.class);
		thisLoc = retrievelocationFromPreferences();
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		addProximityAlert(thisLoc.getLatitude(), thisLoc.getLongitude());
		System.out.println("Done adding Prox Alert");
	}
	public void onDestroy() {
		System.out.println("hit onDestroy");
		if (locationManager == null) {
			 System.out.println("onDestroy LM is null");
		} else {
			System.out.println("onDestroy LM is not null");
		}	
		locationManager.removeUpdates(this);
		System.out.println("removed Prox Alerts");
		unregisterReceiver(ProximityIntentReceiver);
		System.out.println("Unregistered Receiver");
	}
	
	private void addProximityAlert(double latitude, double longitude) {
		proximityIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        RADIUS = prefs.getFloat("Radius", 150);
        System.out.println("Rad is retreived " + RADIUS);
        //latitude = 44.05294418334961;
        //longitude = -95.6604995727539;
        System.out.println("Final lat/long is: " + latitude + " and " + longitude);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1 * 1000L, 10, this);
        /*locationManager.addProximityAlert(
            latitude, // the latitude of the central point of the alert region
            longitude, // the longitude of the central point of the alert region
            RADIUS, // the radius of the central point of the alert region, in meters
            60* 60 * 1000L, // time for this proximity alert, in milliseconds, or -1 to indicate no expiration 
            proximityIntent // will be used to generate an Intent to fire when entry to or exit from the alert region is detected
       );*/
        
       IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);  
       registerReceiver(ProximityIntentReceiver, filter);
       System.out.println("started Prox Alert");
       
    }
		
    public class MyLocationListener implements LocationListener {
        public void onLocationChanged(Location location) {
        	System.out.println("Location changed in service");
        }
        public void onStatusChanged(String s, int i, Bundle b) {            
        }
        public void onProviderDisabled(String s) {
        }
        public void onProviderEnabled(String s) {            
        }
    }
    
    private Location retrievelocationFromPreferences() {
        location = new Location("POINT_LOCATION");
        location.setLatitude(prefs.getFloat(POINT_LATITUDE_KEY, 0));
        location.setLongitude(prefs.getFloat(POINT_LONGITUDE_KEY, 0));
        System.out.println("retreiving locs" + location.getLatitude() + " and long is " + location.getLongitude());
        return location;
    }

	public void onLocationChanged(Location location) {
		System.out.println("Loc changed at bottom onLocChanged");
		RADIUS = prefs.getFloat("Radius", 150);
		System.out.println("Rad is " + RADIUS);
		Location pointLocation = retrievelocationFromPreferences();
		float distance = location.distanceTo(pointLocation);
        //Toast.makeText(ASPService.this, 
        //        "Distance: "+distance + " - " + location.hasAccuracy() + "-" + location.getAccuracy(), Toast.LENGTH_SHORT).show();
        System.out.println("Distance is " + distance + location.getAccuracy());
        if (location.hasAccuracy() && location.getAccuracy()<=25) {
        		if (location.distanceTo(pointLocation) < RADIUS) {
        			System.out.println("in accuracy statement");
        			//Toast.makeText(ASPService.this, "You are close enough to fire the alert", Toast.LENGTH_SHORT);
        			try {
        				proximityIntent.send(ASPService.this, 0, intent);
        			} catch (PendingIntent.CanceledException e) {
        				System.out.println( "Sending contentIntent failed: " );
        			}
        		}
        }
	}
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		System.out.println("hit onBind");
		return null;
	}
}
