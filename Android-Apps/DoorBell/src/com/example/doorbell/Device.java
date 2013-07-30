package com.example.doorbell;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class Device extends Activity implements OnClickListener {
	Button done,search;
	Spinner devices;
	List<String> list = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device);
		populateSearchSpinner();
		addListenerOnButton();
	}

	public void addListenerOnButton() {
		done = (Button) findViewById(R.id.deviceDone);
		done.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		final Context context = this;
		switch(v.getId()){
		case R.id.deviceDone : Intent serviceIntent = new Intent(Device.this, DoorBellConfigurationService.class);
								serviceIntent.putExtra("configuration", "DEVICE");
								serviceIntent.putExtra("device", devices.getSelectedItem().toString());
								startService(serviceIntent);	
								Intent intent1 = new Intent(context, MainActivity.class);
								startActivity(intent1);
		}
	}
	
	
	private void populateSearchSpinner() {
		devices = (Spinner) findViewById(R.id.spinnerDevice);
		list.add("Speaker");
		list.add("LED");
		list.add("both");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		devices.setAdapter(dataAdapter);
	}
	
}
