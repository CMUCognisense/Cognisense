package com.androidcommproc;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity {
	private Button connect;
	private Button close;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		connect = (Button)findViewById(R.id.main_connect);
		connect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent serviceIntent = new Intent(MainActivity.this, CommService.class);
				startService(serviceIntent);
				//stopService(serviceIntent);
			}
		});
		
		close = (Button)findViewById(R.id.main_close);
		close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent serviceIntent = new Intent(MainActivity.this, CommService.class);
				//stopService(serviceIntent);
				finish();
			}
		});
	}
}
