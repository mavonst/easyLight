package mavonst.app.easylight;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Service to couple the flashlight with different views such as the main application and the widget
 */
public class EasyLightService extends Service
{
	private final IBinder mBinder = new LocalBinder();
	FlashlightHolder flashlight;
	
    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
    	EasyLightService getService() {
            // Return this instance of EasyLightService so clients can call public methods
            return EasyLightService.this;
        }
    }

//	/**
//	 * Constructor of this working horse
//	 */
//	public EasyLightService() {
//		super(EasyLightService.class.getName());
//	}

	@Override
	public void onCreate() {
		Log.d(Utils.APPNAME, "create service");
		super.onCreate();
		flashlight = FlashlightHolder.getInstance(this.getApplicationContext());
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(Utils.APPNAME, "start service");
		flashlight.on();
		return START_STICKY;
	}
	
//	@Override
//	protected void onHandleIntent(Intent intent) {
////		//get input
////		msgFromActivity = intent.getStringExtra(EXTRA_KEY_IN);
////		extraOut = "Hello: " +  msgFromActivity;
//		if(!intent.getAction().equals(Utils.ACTION_ACTIVATE_TORCH))
//		{
//			return;
//		}
//		
//		boolean flashlightIsActive = intent.getBooleanExtra("ACTIVE", true);
//		
//		//total 10 sec
//		for(int i = 0; i <=10; i++){
//
//			try {
//				Thread.sleep(500); //every .5 sec
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//
//			//--- Try to comment it ---//
////			if(intent.){
////				break;
//			}
//	}
	
	@Override
	public boolean stopService(Intent name) {
		Log.d(Utils.APPNAME, "stop service");
		flashlight.off();
		return super.stopService(name);
	}
	
	@Override
	public void onDestroy() {
		Log.d(Utils.APPNAME, "destroy service");
		flashlight.close();
	}

	public void activateTorch()
	{
		Log.d(Utils.APPNAME, "activate Flashlight");
		flashlight.on();
		EasyLightNotification.generateNotification(getApplicationContext(), "click to stop", EasyLight.class);

	}
	
	public void deactivateTorch()
	{
		Log.d(Utils.APPNAME, "deactivate Flashlight");
		flashlight.off();
	}
	
	public void toggleTorch()
	{
		Log.d(Utils.APPNAME, "toggle Flashlight");
		flashlight.toggle();
	}
	
	public boolean isTorchActive()
	{
		return flashlight.isFlashOn;
	}
}
