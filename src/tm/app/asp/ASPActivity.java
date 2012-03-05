package tm.app.asp;

import java.util.Calendar;
import tm.app.asp.R;
import tm.app.asp.ASPAlarmReceiver;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class ASPActivity extends Activity {
	private static final String POINT_LATITUDE_KEY = "POINT_LATITUDE_KEY";
    private static final String POINT_LONGITUDE_KEY = "POINT_LONGITUDE_KEY";
    private LocationManager locateMe;
    private LocationListener MyLocationListener = new MyLocationListener();
    private SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;
    private TimePicker startTime; //= (TimePicker)findViewById(R.id.startTime);
    private TimePicker endTime; //=// (TimePicker)findViewById(R.id.endTime);
    
    EditText latitudeEditText;
    EditText longitudeEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        prefs =  this.getSharedPreferences("tm.app.asp",Context.MODE_PRIVATE);
        prefsEditor = prefs.edit();
        locateMe = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Button startButton = (Button)findViewById(R.id.startButton);
        Button findCoordinatesButton = (Button) findViewById(R.id.findCoord);

        Button stopServicesButton = (Button)findViewById(R.id.stopServices);
        final EditText radText = (EditText)findViewById(R.id.radText);
        startTime = (TimePicker)findViewById(R.id.startTime);
        endTime = (TimePicker)findViewById(R.id.endTime);
        
        startTime.setCurrentHour(16);
        startTime.setCurrentMinute(00);
        endTime.setCurrentHour(17);
        endTime.setCurrentMinute(00);
       
        
        findCoordinatesButton.setOnClickListener(new OnClickListener() {            
            public void onClick(View v) {
            	System.out.println("findCoord Button");
            	locateMe.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, -1, MyLocationListener);
            }
        });
        
        stopServicesButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				removeAllAlarms();
			}
		});
        
        startButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				float RAD;
				if (!radText.getText().toString().equals("")) {
					try {
						RAD = Float.valueOf(radText.getText().toString());
						if (RAD >= 100) {
							Calendar startCalendar = Calendar.getInstance();  
							Calendar endCalendar = Calendar.getInstance();        			
							startCalendar.set(Calendar.HOUR_OF_DAY, startTime.getCurrentHour());
							startCalendar.set(Calendar.SECOND, 0);
							startCalendar.set(Calendar.MINUTE, startTime.getCurrentMinute());
							endCalendar.set(Calendar.HOUR_OF_DAY, endTime.getCurrentHour());
							endCalendar.set(Calendar.SECOND, 0);
							endCalendar.set(Calendar.MINUTE, endTime.getCurrentMinute());
							float radInFeet;
							float DEFAULT_RAD_IN_METERS = 100;
							if (radText.getText() == null) {
								saveRad(DEFAULT_RAD_IN_METERS);
							} else {
								try {
									radInFeet = (Float.valueOf(radText.getText().toString()));
									saveRad(radInFeet / 3.28f);
								} catch (NumberFormatException e) {
									saveRad(DEFAULT_RAD_IN_METERS);
								}
							}
							setAlarms(startCalendar, endCalendar);
						} else {
							Toast.makeText(ASPActivity.this, "Distance must be at least 100 Feet", Toast.LENGTH_LONG).show();
						}
					} catch (NumberFormatException e) {
						Toast.makeText(ASPActivity.this, "Distance must be all numeric", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(ASPActivity.this, "You must enter a radius", Toast.LENGTH_LONG).show();
				}
		
        		//setAlarms(startCalendar, endCalendar);
			}
        });
    }
    
    protected void onResume()
    {
       super.onResume();

       startTime.setCurrentHour(16);
       startTime.setCurrentMinute(00);
       endTime.setCurrentHour(17);
       endTime.setCurrentMinute(00);
    }
    
    public void removeAllAlarms() {
    	System.out.println("removing alamrs");
    	Intent intent = new Intent(getApplicationContext(), ASPAlarmReceiver.class);			
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				ASPActivity.this.getApplicationContext(), 234324255, intent, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
		
		Intent endIntent = new Intent(getApplicationContext(), ASPAlarmReceiver.class);
		endIntent.putExtra("start", false);
		PendingIntent endPendingIntent = PendingIntent.getBroadcast(
				getApplicationContext(), 234324243, endIntent, 0);
		AlarmManager endAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTimeInMillis(System.currentTimeMillis());
		endAlarmManager.set(AlarmManager.RTC_WAKEUP,endCalendar.getTimeInMillis(), endPendingIntent);
		endAlarmManager. cancel(endPendingIntent);	
    }
    
    public void setAlarms(Calendar startCalendar, Calendar endCalendar) {
    	System.out.println("Setting alarms....");
    	saveTimesInPreferences(startCalendar.get(Calendar.HOUR),
    			startCalendar.get(Calendar.MINUTE),
    			endCalendar.get(Calendar.HOUR),
    			endCalendar.get(Calendar.MINUTE));
    	Intent intent = new Intent(getApplicationContext(), ASPAlarmReceiver.class);			
    	intent.putExtra("start", true);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				ASPActivity.this.getApplicationContext(), 234324255, intent, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,startCalendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);	
		
		Intent endIntent = new Intent(getApplicationContext(), ASPAlarmReceiver.class);
		endIntent.putExtra("start", false);
		PendingIntent endPendingIntent = PendingIntent.getBroadcast(
				getApplicationContext(), 234324243, endIntent, 0);
		AlarmManager endAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		endAlarmManager.cancel(endPendingIntent);
		endAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,endCalendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, endPendingIntent);
    }
 
    private void saveProximityAlertPoint() {
    	System.out.println("saveing prox point");
        Location location = locateMe.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location==null) {
            Toast.makeText(this, "Location was not found, please try again", Toast.LENGTH_LONG).show();
            return;
        }
        saveCoordinatesInPreferences((float)location.getLatitude(),(float)location.getLongitude());
    }
 
    private void saveCoordinatesInPreferences(float latitude, float longitude) {
    	System.out.println("saving coordinates in prefs" + latitude + " and long " + longitude);
        prefsEditor.putFloat(POINT_LATITUDE_KEY, latitude);
        prefsEditor.putFloat(POINT_LONGITUDE_KEY, longitude);
        prefsEditor.commit();
    }
    
    private void saveTimesInPreferences(int startHour, int startMinute, int endHour, int endMinute) {
    	System.out.println("times are: " + startHour + "-" + startMinute + "-" + endHour + "-" + endMinute);
    	prefsEditor.putInt("startHour", startHour);
    	prefsEditor.putInt("startMinute", startMinute);
    	prefsEditor.putInt("endHour", endHour);
    	prefsEditor.putInt("endMinute", endMinute);
    	prefsEditor.commit();
    }
    
    private void saveRad(float RadiusInMeters) {
    	System.out.println("Saving Radius of :" + RadiusInMeters);
    	prefsEditor.putFloat("Radius", RadiusInMeters);
    	prefsEditor.commit();
    }

    public class MyLocationListener implements LocationListener {
        public void onLocationChanged(Location location) {
        	System.out.println("Accuracy is "  + location.getAccuracy());
        	System.out.println("Do I ave accuracy: " + location.hasAccuracy());
        	if (location.getAccuracy() < 30 && location.hasAccuracy()) {
        		Toast.makeText(ASPActivity.this, 
                    "Your location has been saved", Toast.LENGTH_LONG).show();
        		saveProximityAlertPoint();
        		locateMe.removeUpdates(MyLocationListener);
        	}
        }
        public void onStatusChanged(String s, int i, Bundle b) {            
        }
        public void onProviderDisabled(String s) {
        }
        public void onProviderEnabled(String s) {            
        }
    }
}