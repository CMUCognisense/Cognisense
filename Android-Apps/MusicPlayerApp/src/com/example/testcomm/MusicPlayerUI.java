package com.example.testcomm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.TextView;


public class MusicPlayerUI extends Activity {
	private Button selectSongs;
	private Button play;
	private Button stop;
	private String TAG = "MusicPlayer";
	private EditText editText = null;
	private TextView textView = null;
	ArrayAdapter<String> adapter = null;
	ListView lv;
	String selectedSong;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ui);

		editText = (EditText)findViewById(R.id.editText2);
		play = (Button)findViewById(R.id.button2);
		stop = (Button)findViewById(R.id.button3);
		stop.setVisibility(stop.GONE);
		play.setVisibility(play.GONE);
		textView = (TextView)findViewById(R.id.textView1);

		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
		lv = (ListView) findViewById(R.id.listView1);
		lv.setAdapter(adapter);
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				String songSelected = (String) lv.getItemAtPosition(position);
				adapter.clear();
				adapter.add(songSelected);
				play.setVisibility(play.VISIBLE);
				selectedSong = songSelected;  

			}
		});

		play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent serviceIntent = new Intent(MusicPlayerUI.this, MusicPlayerService.class);
				serviceIntent.putExtra("command", "SelectedSong");
				serviceIntent.putExtra("song", selectedSong);
				startService(serviceIntent);
				play.setVisibility(play.GONE);
				editText.setVisibility(editText.INVISIBLE);
				selectSongs.setVisibility(selectSongs.GONE);
				stop.setVisibility(stop.VISIBLE);
				textView.setText("Playing Now ..");

			}
		});
		
		
		stop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editText.setVisibility(editText.VISIBLE);
				selectSongs.setVisibility(selectSongs.VISIBLE);
				stop.setVisibility(stop.GONE);
				textView.setText("Enter User Name");
				adapter.clear();
				
				Intent serviceIntent = new Intent(MusicPlayerUI.this, MusicPlayerService.class);
				serviceIntent.putExtra("command", "Stop");
				startService(serviceIntent);	
				
			}
		});
		
		selectSongs = (Button)findViewById(R.id.button1);
		selectSongs.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				adapter.clear();
				Log.d(TAG,"Send Intent to service");
				Intent serviceIntent = new Intent(MusicPlayerUI.this, MusicPlayerService.class);
				serviceIntent.putExtra("command", "SelectSongs");
				serviceIntent.putExtra("user", editText.getText().toString());
				startService(serviceIntent);

			}
		});

		
		// start the service 
		Log.d(TAG, "Started the UI Activity staring service");
		Intent serviceIntent = new Intent(MusicPlayerUI.this, MusicPlayerService.class);
		startService(serviceIntent);		


		

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ui, menu);
		return true;
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG,"List of songs received from the service" + intent.getStringExtra("Values"));
			String[] values = intent.getStringExtra("Values").split("&");
			for(String value:values)
				adapter.add(value);
		}
	};

}
