package tm.app.asp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ASPAlarmReceiver extends BroadcastReceiver  {
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent intentNew = new Intent(context, ASPService.class);
		
		if (intent.getExtras().getBoolean("start")) {
			System.out.println("starting service");
			context.startService(intentNew);
			//context.unregisterReceiver(this);
		} else {
			System.out.println("stopping service");
			context.stopService(intentNew);
		}
	}
}
