package com.example.locationrepresentation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class EnterMap extends Activity implements OnClickListener{

	private Button home,floor,room,view;
	private TextView homeText, floorText, roomText;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.house_map);
		// Initialize the views
		init();
	}

	// Do initialization on the views
	public void init() {
		home = (Button) findViewById(R.id.home);
		floor = (Button) findViewById(R.id.floor);
		room = (Button) findViewById(R.id.room);
		view = (Button) findViewById(R.id.view);

		homeText = (TextView) findViewById(R.id.hometext);
		floorText = (TextView) findViewById(R.id.floortext);
		roomText = (TextView) findViewById(R.id.roomtext);

		home.setOnClickListener(this);
		floor.setOnClickListener(this);
		room.setOnClickListener(this);
		view.setOnClickListener(this);
	}


	public void onClick(View v) {
		final Context context = this;
		switch(v.getId()){
		case R.id.home : 
			// This will add home to the database			
			// disable the home button
			home.setEnabled(false);
			Intent addHome = new Intent(context, RegistrationService.class);
			addHome.putExtra("command", "ADDHOME");
			addHome.putExtra("HOME", homeText.getText().toString());
			// clear the text field so that user won't be confused
			homeText.setText("");
			startService(addHome);
			break;

		case R.id.floor: 
			// This will add floor to the database
			Intent addFloor = new Intent(context, RegistrationService.class);
			addFloor.putExtra("command", "ADDFLOOR");
			addFloor.putExtra("FLOOR", floorText.getText().toString());
			// clear the text field so that user won't be confused
			floorText.setText("");
			startService(addFloor);
			break;

		case R.id.room: 
			// This will add room to the database
			Intent addRoom = new Intent(context, RegistrationService.class);
			addRoom.putExtra("command", "ADDROOM");
			addRoom.putExtra("ROOM", roomText.getText().toString());
			// clear the text field so that user won't be confused
			roomText.setText("");
			startService(addRoom);
			break;

		case R.id.view: 
			Intent viewMap = new Intent(EnterMap.this, ViewMap.class);
			startActivity(viewMap);
			break;				 
		}
	}
}