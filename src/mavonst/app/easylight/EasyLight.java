package mavonst.app.easylight;

import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;


public class EasyLight extends ActionBarActivity
{
    Camera mCam;
    ToggleButton mTorch;
    Parameters mCamParams;
    private Context mContext;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
	
	static final String[] LISTITEMS = { "Toggle LED" };
	
	boolean mHasFlash = false;
	
	private static final String APPNAME = EasyLight.class.getSimpleName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = EasyLight.this;
		if(!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
		{
			showAlertDialog("Error", "Sorry, your device doesn't support camera flash light!");
			return;
		}
		
		mCam = Camera.open();
		if(mCam == null){
			showAlertDialog("Error", "Sorry, the camera of your device is currently busy!");
			return;
		}
		mCamParams = mCam.getParameters();
		List<String> flashModes = mCamParams.getSupportedFlashModes();
		if(!flashModes.contains(Parameters.FLASH_MODE_TORCH))
		{
			showAlertDialog("Error", "Sorry, your device doesn't support flash torch!");
			return;
		}
		
        mTorch = (ToggleButton) findViewById(R.id.toggleLight);
        mTorch.setOnCheckedChangeListener(new OnCheckedChangeListener() { 
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
            		boolean isChecked) {
            	try{
            		if(isChecked){
            			mCamParams.setFlashMode(Parameters.FLASH_MODE_TORCH);
            			
            		}else{
            			mCamParams.setFlashMode(Parameters.FLASH_MODE_OFF);
            		}
            		mCam.setParameters(mCamParams);
            		mCam.startPreview();
            	}catch (Exception e) {
            		e.printStackTrace();
            		mCam.stopPreview();
            		mCam.release();
            	}
            }
        });
        mTorch.setChecked(true); //default activated
	}
	
	private void showAlertDialog(final String title, final String msg)
	{
		AlertDialog alert = new AlertDialog.Builder(EasyLight.this)
		.create();
		alert.setTitle(title);
		alert.setMessage(msg);
		alert.setButton(DialogInterface.BUTTON_POSITIVE, "Ok",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// closing the application
				finish();
			}
		});

		alert.show();
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    if(mCam == null){
	        mCam = Camera.open();
	    }
	}
	 
	@Override
	protected void onPause() {
	    super.onPause();
	    if(mCam != null){
	        mCam.release();
	    }
	}
	
	@Override
	protected void onStop() {
	    super.onStop();
	    if(mCam != null){
	        mCam.release();
	        mCam = null;
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		Log.i(APPNAME, "Created");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
}
