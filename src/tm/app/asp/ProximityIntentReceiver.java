package tm.app.asp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.util.Log;
public class ProximityIntentReceiver extends BroadcastReceiver {
    
    private static final int NOTIFICATION_ID = 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        
        String key = LocationManager.KEY_PROXIMITY_ENTERING;

        Boolean entering = intent.getBooleanExtra(key, false);
        
        if (entering) {
            Log.d(getClass().getSimpleName(), "entering");
        }
        else {
            Log.d(getClass().getSimpleName(), "exiting");
        }
        
        NotificationManager notificationManager = 
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, null, 0);        
        
        Notification notification = createNotification();
        

        	notification.setLatestEventInfo(context, 
        			"Alternate Side Parking Reminder", "Remember to park on the right side", pendingIntent);
        	Intent intentNew = new Intent(context, ASPService.class);
        	context.stopService(intentNew);
        	
        	Intent endIntent = new Intent(context.getApplicationContext(), ASPAlarmReceiver.class);
        	PendingIntent endPendingIntent = PendingIntent.getBroadcast(
    				context.getApplicationContext(), 234324243, endIntent, 0);
    		AlarmManager endAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    		System.out.println("Trying to cancel endPendingintent within ProxIntentRecevier");
    		endAlarmManager.cancel(endPendingIntent);
        
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
    
    private Notification createNotification() {
        Notification notification = new Notification();
        
        notification.icon = R.drawable.asp_launcher;
        notification.when = System.currentTimeMillis();
        
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        
        notification.ledARGB = Color.GREEN;
        notification.ledOnMS = 1500;
        notification.ledOffMS = 1500;
        notification.defaults = Notification.DEFAULT_SOUND;
        
        return notification;
    }
    
}