package tm.app.asp;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;

public class BootReceiver extends BroadcastReceiver {
	private static final String POINT_LATITUDE_KEY = "POINT_LATITUDE_KEY";
    private static final String POINT_LONGITUDE_KEY = "POINT_LONGITUDE_KEY";

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences prefs = 
		        context.getSharedPreferences("tm.app.asp",Context.MODE_PRIVATE);
		        Location location = new Location("POINT_LOCATION");
		        location.setLatitude(prefs.getFloat(POINT_LATITUDE_KEY, 0));
		        location.setLongitude(prefs.getFloat(POINT_LONGITUDE_KEY, 0));
		        
		        Calendar startCal = Calendar.getInstance();  
				Calendar endCal = Calendar.getInstance();  


				startCal.set(Calendar.HOUR_OF_DAY, prefs.getInt("startHour", 0));
        		startCal.set(Calendar.SECOND, 0);
        		startCal.set(Calendar.MINUTE, prefs.getInt("startMinute", 0));
        		endCal.set(Calendar.HOUR_OF_DAY, prefs.getInt("endHour", 0));
        		endCal.set(Calendar.SECOND, 0);
        		endCal.set(Calendar.MINUTE, prefs.getInt("endMinute", 0));
		setAlarms(context, startCal, endCal);
		
	}
	
    public void setAlarms(Context context, Calendar startCalendar, Calendar endCalendar) {
    	
    	
    	Intent intent = new Intent(context.getApplicationContext(), ASPAlarmReceiver.class);			
    	intent.putExtra("start", true);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				context.getApplicationContext(), 234324255, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		alarmManager.cancel(pendingIntent);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,startCalendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);	
		
		Intent endIntent = new Intent(context.getApplicationContext(), ASPAlarmReceiver.class);
		endIntent.putExtra("start", false);
		PendingIntent endPendingIntent = PendingIntent.getBroadcast(
				context.getApplicationContext(), 234324243, endIntent, 0);
		AlarmManager endAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		endAlarmManager.cancel(endPendingIntent);
		endAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,endCalendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, endPendingIntent);
    }

}
