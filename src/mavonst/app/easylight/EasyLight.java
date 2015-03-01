package mavonst.app.easylight;

import mavonst.app.easylight.EasyLightService.LocalBinder;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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
	ToggleButton mTorch;
	EasyLightService mService;
	boolean mBound;
	
	private long backPressed = 0; //counting number of back pressings of the user, when pressed more than 1 time in 2s the app will be closed

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mTorch = (ToggleButton) findViewById(R.id.toggleLight);
		mTorch.setOnCheckedChangeListener(new OnCheckedChangeListener() { 
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked)
			{    
				try{
					if(isChecked){
						mService.activateTorch();
					}else{
						mService.deactivateTorch();
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		Toast.makeText(getApplicationContext(), R.string.hello_msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		Log.d(Utils.APPNAME, "onStart");
		Intent intent = new Intent(this, EasyLightService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		backPressed = 0;
//		EasyLightNotification.generateNotification(getApplicationContext(), EasyLight.class, "whoop whoop");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(Utils.APPNAME, "onResume");
	}
	
	@Override
	protected void onPause() {
		Log.d(Utils.APPNAME, "onPause");
		backPressed = 0;
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.d(Utils.APPNAME, "onStop");
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		if(mBound)
		{
			unbindService(mConnection);
			mBound = false;
		}
		super.onDestroy();
	}


	@Override
	public void onBackPressed() {
		Log.d(Utils.APPNAME, "back pressed");
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
		super.onCreateOptionsMenu(menu);
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		Log.d(Utils.APPNAME, "Created");
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
