package mavonst.app.easylight;

import java.security.acl.NotOwnerException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Notification;
import android.app.Notification.Action;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;

public class EasyLightNotification extends Activity {

	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.notification);
	  }
	  
	  public static void generateNotification(Context context, String message, Class<?> view_cls){
		  Intent notificationIntent = new Intent(context, view_cls);
		  notificationIntent.setAction(Utils.ACTION_NOTIFICATION_CLICKED);
		  notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		  PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		  NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
		  .setSmallIcon(R.drawable.ic_launcher)
		  .setContentTitle(context.getString(R.string.app_name))
		  .setContentIntent(intent)
		  .addAction(0,"quit", intent)
		  //		        .setPriority(PRIORITY_HIGH) //private static final PRIORITY_HIGH = 5;
		  .setContentText(message)
		  .setAutoCancel(true)
		  .setOngoing(true);
//		  .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
		  NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		  mNotificationManager.notify(0, mBuilder.build());
	  }
}
