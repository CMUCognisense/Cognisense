package com.example.doorbell;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends Activity implements OnClickListener{
	Button time,location,device,userSubmit,done;
	EditText userName;
	String TAG = "DEBUG";
	private RadioGroup notificationGroup;
	private RadioButton notify;
	boolean notification = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		addListenerOnButton();
		Log.v("DEBUG", "Started the UI Activity staring service");
		Intent serviceIntent = new Intent(MainActivity.this, DoorBellConfigurationService.class);
		startService(serviceIntent);
		userName = (EditText)findViewById(R.id.userName);
		userName.setText(p.getString("user",""));

	}

	public void addListenerOnButton() {
		
		userSubmit = (Button) findViewById(R.id.userSubmit);
		time = (Button) findViewById(R.id.time);
		device = (Button) findViewById(R.id.device);
		location = (Button) findViewById(R.id.location);
		done = (Button) findViewById(R.id.mainDone);
		time.setOnClickListener(this);
		device.setOnClickListener(this);
		location.setOnClickListener(this);
		userSubmit.setOnClickListener(this);
		done.setOnClickListener(this);
		notificationGroup = (RadioGroup) findViewById(R.id.radio);
	}

	public void onRadioButtonClicked(View view) {
		// Is the button now checked?
		boolean checked = ((RadioButton) view).isChecked();

		// Check which radio button was clicked
		switch(view.getId()) {
		case R.id.notify:
			if (checked)
				notification = true;
			break;
		case R.id.dontNotify:
			if (checked)
				notification = false;
			break;
		}
	}


	@Override
	public void onClick(View v) {		
		final Context context = this;
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		switch(v.getId()){
		case R.id.time : Intent intent = new Intent(context,Time.class);
		startActivity(intent); 
		break;
		case R.id.device: Intent intent1 = new Intent(context, Device.class);
		startActivity(intent1);
		break;
		case R.id.location : 	 Intent intent2 = new Intent(context, Location.class);
		startActivity(intent2);
		break;
		case R.id.userSubmit: 	Log.v(TAG,"Send Intent to service");
		Intent serviceIntent = new Intent(MainActivity.this, DoorBellConfigurationService.class);
		serviceIntent.putExtra("configuration", "USER");
		serviceIntent.putExtra("user", userName.getText().toString());
		p.edit().putString("user",userName.getText().toString()).commit();
		userName.setFocusable(false);
		startService(serviceIntent);
		break;		

		case R.id.mainDone: notificationGroup = (RadioGroup) findViewById(R.id.radio);

		Log.v(TAG," In the done case");
		int checkedRadioButton = notificationGroup.getCheckedRadioButtonId();		 

		switch (checkedRadioButton) {
		case R.id.notify : 	  notification = true;
		Log.v(TAG," Settting notification to true");
		break;
		case R.id.dontNotify : notification = false;
		Log.v(TAG," Settting notification to false");
		break;
		}

		Intent serviceIntent1 = new Intent(MainActivity.this, DoorBellConfigurationService.class);
		serviceIntent1.putExtra("configuration", "NOTIFICATION");
		serviceIntent1.putExtra("notify",notification);
		Log.v(TAG,"Sending Notify intent"+ notification);
		startService(serviceIntent1);
		break;

		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter("UIINTENT"));
	}

	@Override
	protected void onStop() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
		super.onStop();
	}


	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			/*Log.d(TAG,"List of songs received from the service" + intent.getStringExtra("Values"));
			String[] values = intent.getStringExtra("Values").split("&");
			for(String value:values)
				adapter.add(value);*/
		}
	};

}
