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
import android.widget.Toast;
import android.widget.ToggleButton;


public class EasyLight extends ActionBarActivity
{
	private static final String APPNAME = EasyLight.class.getSimpleName();
	
	Camera mCam;
    ToggleButton mTorch;
    Parameters mCamParams;
    private Context mContext;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    
    private long backPressed = 0; //counting number of back pressings of the user, if reaching 2 the app will be closed

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
            		boolean isChecked)
            {            	
            	try{
                	if(isChecked){
                		activateTorch();
                	}else{
                		deactivateTorch();
                	}
            	}catch (Exception e) {
            		e.printStackTrace();
            	}
            }
        });
        mTorch.setChecked(true); //default activated
        Toast.makeText(getApplicationContext(), "Hi there!", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		backPressed = 0;
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
	
	private void activateTorch() throws Exception
	{
		mCamParams.setFlashMode(Parameters.FLASH_MODE_TORCH);
		mCamParams.setFocusMode("FOCUS_MODE_INFINITY");
		mCam.setParameters(mCamParams);
		mCam.startPreview();
	}
	
	private void deactivateTorch() throws Exception
	{
		mCamParams.setFlashMode(Parameters.FLASH_MODE_OFF);
		mCam.setParameters(mCamParams);
		mCam.stopPreview();
	}
	
	@Override
	protected void onPause() {
	    super.onPause();
//	    if(mCam != null){
//	        mCam.release();
//	    }
	    backPressed = 0;
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    if(mCam == null){
	        mCam = Camera.open();
	        try {
				if(mTorch.isChecked())
					activateTorch();
				else
					deactivateTorch();
			} catch (Exception e) {
				e.printStackTrace();
			}
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
    public void onBackPressed() {
        if ((backPressed + 2000) > System.currentTimeMillis())
        {
            finish();
        }
        else
        {
            Toast.makeText(getApplicationContext(), getString(R.string.to_quit_msg), Toast.LENGTH_SHORT).show();
            backPressed = System.currentTimeMillis();
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
