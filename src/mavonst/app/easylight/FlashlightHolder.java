package mavonst.app.easylight;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.widget.Toast;


/**
 * This singleton class is used to hold an {@code android.hardware.Camera} instance.
 * <p>The {@code open()} and {@code release()} calls are similar to the ones
* in {@code android.hardware.Camera}.
 *
 */
public class FlashlightHolder {

	private static FlashlightHolder mInstance;
	Camera mCam;
	Parameters mCamParams;
	Context mContext;
	boolean isFlashOn = false;
	
	private FlashlightHolder(Context base) {
		mContext = base;
		isFlashOn = open();
	}
	
	public static FlashlightHolder getInstance(Context context)
	{
		if(mInstance != null)
		{
			return mInstance;
		} else {
			return new FlashlightHolder(context);
		}
	}
	
	public synchronized boolean open()
	{
		try{
			if(!mContext.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
			{
				showAlertDialog("Error", "Sorry, your device doesn't support camera flash light!");
				return false;
			}
			return bindCamera();
		} catch(Exception e)
		{
			 Log.d(Utils.APPNAME, "Exception opening flashlight " + e.getMessage());
			 return false;
		}
	}
	
	public synchronized void close()
	{
		try{
			if(mCam == null) {
				return;
			}
			mCamParams.setFlashMode(Parameters.FLASH_MODE_OFF);
			mCam.setParameters(mCamParams);
			mCam.setPreviewCallback(null);
			mCam.stopPreview();
			mCam.release();
			mCam = null;
			mInstance = null;
		} catch(Exception e)
		{
			 Log.d(Utils.APPNAME, "Exception closing flashlight " + e.getMessage());
		}
	}
	
	public synchronized void on()
	{
		try{
			if(mCam == null)
			{
				Toast.makeText(mContext.getApplicationContext(), R.string.camera_is_occupied_msg, Toast.LENGTH_LONG).show();
				return;
			}
			mCamParams.setFlashMode(Parameters.FLASH_MODE_TORCH);
			mCam.setParameters(mCamParams);
			mCam.startPreview();
			isFlashOn = true;
		} catch(Exception e)
		{
			 Log.d(Utils.APPNAME, "Exception turning-on flashlight " + e.getMessage());
		}
	}
	
	public synchronized void off()
	{
		try{
			mCamParams.setFlashMode(Parameters.FLASH_MODE_OFF);
			mCam.setParameters(mCamParams);
			mCam.stopPreview();
			mCam.setPreviewCallback(null);
			isFlashOn = false;
		} catch(Exception e)
		{
			 Log.d(Utils.APPNAME, "Exception turning-off flashlight " + e.getMessage());
		}
		
	}
	
	public synchronized void toggle()
	{
		isFlashOn = !isFlashOn;
		
		if(isFlashOn)
		{
			on();
		} else{
			off();
		}
	}
	
	public synchronized boolean isFlashOn()
	{
		return isFlashOn;
	}
	
	private boolean bindCamera()
	{
		mCam = Camera.open();
		if(mCam == null){
			Toast.makeText(mContext.getApplicationContext(), R.string.camera_is_occupied_msg, Toast.LENGTH_LONG).show();
			return false;
		}
		mCamParams = mCam.getParameters();
		List<String> flashModes = mCamParams.getSupportedFlashModes();
		if(!flashModes.contains(Parameters.FLASH_MODE_TORCH))
		{
			Toast.makeText(mContext.getApplicationContext(),"Sorry, your device doesn't support flash torch!", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
	
	private void showAlertDialog(final String title, final String msg)
	{
		final AlertDialog alert = new AlertDialog.Builder(mContext)
		.create();
		alert.setTitle(title);
		alert.setMessage(msg);
		alert.setButton(DialogInterface.BUTTON_POSITIVE, "Ok",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// closing the alert
				alert.dismiss();
			}
		});
		alert.show();
	}
}
