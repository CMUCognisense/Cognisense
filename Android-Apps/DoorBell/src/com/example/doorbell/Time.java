package com.example.doorbell;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

public class Time extends Activity implements OnClickListener{

	Button done,notSet;
	int startHour,startMinute,endHour,endMinute;
	TimePicker start;
	TimePicker end;
	String TAG = "DEBUG";
	TextView t;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time);
		start =(TimePicker)findViewById(R.id.timePicker1);
		start.setIs24HourView(true);
		end =(TimePicker)findViewById(R.id.timePicker2);
		end.setIs24HourView(true);
		t = (TextView)findViewById(R.id.PreviousTime);
		if(p.getBoolean("set",false)==true){
		t.setText("PREVIOUS TIME SETTINGS :"+"Start Hour: "+p.getInt("shour",-1)+" "+"Start Minute : "+ p.getInt("smin", -1)+" "+
				"End Hour: "+p.getInt("ehour",-1)+" "+"End Minute "+p.getInt("emin", -1));
		}else{
			t.setText("PREVIOUS TIME SETTINGS :"+"Start Hour: Not set"+" "+"Start Minute : Not set" +" "+
					"End Hour: Not set"+" "+"End Minute: Not set ");
		}
		
		addListenerOnButton();
	}

	public void addListenerOnButton() {
		done = (Button) findViewById(R.id.timeDone);
		notSet = (Button) findViewById(R.id.dontSet);
		done.setOnClickListener(this);
		notSet.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		final Context context = this;
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		switch(v.getId()){
		case R.id.timeDone : 
							startHour = start.getCurrentHour();
							startMinute = start.getCurrentMinute();
							endHour = end.getCurrentHour();
							endMinute = end.getCurrentMinute();
							p.edit().putBoolean("set",true).commit();
							p.edit().putInt("shour",startHour).commit();
							p.edit().putInt("smin",startMinute).commit();
							p.edit().putInt("ehour",endHour).commit();
							p.edit().putInt("emin",endMinute).commit();
							Log.v(TAG,"Start HOUR :"+startHour+"Start Minute"+startMinute+
									"End hour"+endHour + "End Minute"+endMinute);
							Intent serviceIntent = new Intent(Time.this, DoorBellConfigurationService.class);
							serviceIntent.putExtra("configuration", "TIME");
							serviceIntent.putExtra("set",true);
							serviceIntent.putExtra("starthour", startHour);
							serviceIntent.putExtra("startmin", startMinute);
							serviceIntent.putExtra("endhour", endHour);
							serviceIntent.putExtra("endmin", endMinute);
							startService(serviceIntent);
							break;
		
		case R.id.dontSet :		p.edit().putBoolean("set",false).commit();
								p.edit().putInt("shour",-1).commit();
								p.edit().putInt("smin",-1).commit();
								p.edit().putInt("ehour",-1).commit();
								p.edit().putInt("emin",-1).commit();
								Intent serviceIntent1 = new Intent(Time.this, DoorBellConfigurationService.class);
								serviceIntent1.putExtra("configuration", "TIME");
								serviceIntent1.putExtra("set",false);
								startService(serviceIntent1);
								break;
			
		}		
		 Intent intent1 = new Intent(context, MainActivity.class);
		 startActivity(intent1);
	}
	
}
