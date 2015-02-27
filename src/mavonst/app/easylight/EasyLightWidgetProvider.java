package mavonst.app.easylight;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

/**
 * The widget for the EasyLight to directly activate and deactivate the flashlight 
 */
public class EasyLightWidgetProvider extends AppWidgetProvider
{   
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Log.d(Utils.APPNAME, "widget update");

		// Perform this loop procedure for each App Widget that belongs to this provider
		for (int appWidgetId: appWidgetIds) {
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_home_screen);
			views.setOnClickPendingIntent(R.id.widget_button_off, getPendingSelfIntent(context, Utils.ACTION_WIDGET_OFF_CLICKED));
			views.setOnClickPendingIntent(R.id.widget_button_on, getPendingSelfIntent(context, Utils.ACTION_WIDGET_ON_CLICKED));
			// Tell the AppWidgetManager to perform an update on the current app widget
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(Utils.APPNAME, "widget receive action " + intent.getAction());
		
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_home_screen);
		// Create a fresh intent 
		Intent serviceIntent = new Intent(context, EasyLightService.class);		
		if (intent.getAction().equals(Utils.ACTION_WIDGET_ON_CLICKED))
		{
			context.startService(serviceIntent);
			remoteViews.setViewVisibility(R.id.widget_button_on, View.GONE);
			remoteViews.setViewVisibility(R.id.widget_button_off, View.VISIBLE);
		}
		else if (intent.getAction().equals(Utils.ACTION_WIDGET_OFF_CLICKED))
		{
			context.stopService(serviceIntent);
			remoteViews.setViewVisibility(R.id.widget_button_on, View.VISIBLE);
			remoteViews.setViewVisibility(R.id.widget_button_off, View.GONE);
		}
		
		pushWidgetUpdate(context, remoteViews);		
		super.onReceive(context, intent);
}

	@Override
	public void onDisabled(Context context) {
		context.stopService(new Intent(context, EasyLightService.class));
		super.onDisabled(context);
	}
	
	private static PendingIntent getPendingSelfIntent(Context context, String action)
	{
        // An explicit intent directed at the current class (the "self").
        Intent intent = new Intent(context, EasyLightWidgetProvider.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
	}

	private static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
		ComponentName widget = new ComponentName(context,
				EasyLightWidgetProvider.class);
		AppWidgetManager.getInstance(context).updateAppWidget(widget, remoteViews);
	}
}
