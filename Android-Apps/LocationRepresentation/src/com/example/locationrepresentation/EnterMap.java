package com.example.locationrepresentation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity provide an interface where user can enter the home map 
 * for the home. At one time user can only enter one home, multiple floors
 * and multiple rooms.
 * 
 * @author Pengcheng
 *
 */
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


	@Override
	public void onClick(View v) {
		final Context context = this;
		switch(v.getId()){
		case R.id.home: 
			//first check if the text field is empty
			if(isEmpty(homeText)){
				Toast.makeText(this, "Please enter home name", Toast.LENGTH_SHORT).show();
				return;
			}
				
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
			//first check if the text field is empty
			if(isEmpty(floorText)){
				Toast.makeText(this, "Please enter floor name", Toast.LENGTH_SHORT).show();
				return;
			}
			
			// This will add floor to the database
			Intent addFloor = new Intent(context, RegistrationService.class);
			addFloor.putExtra("command", "ADDFLOOR");
			addFloor.putExtra("FLOOR", floorText.getText().toString());

			// clear the text field so that user won't be confused
			floorText.setText("");

			startService(addFloor);
			break;

		case R.id.room: 
			//first check if the text field is empty
			if(isEmpty(roomText)){
				Toast.makeText(this, "Please enter room name", Toast.LENGTH_SHORT).show();
				return;
			}
			
			// This will add room to the database
			Intent addRoom = new Intent(context, RegistrationService.class);
			addRoom.putExtra("command", "ADDROOM");
			addRoom.putExtra("ROOM", roomText.getText().toString());

			// clear the text field so that user won't be confused
			roomText.setText("");

			startService(addRoom);
			break;

		case R.id.view: 
			// if the user wants to view the map he has already entered
			Intent viewMap = new Intent(EnterMap.this, ViewMap.class);
			startActivity(viewMap);
			break;				 
		}
	}

	/**
	 * This method takes TextView as an input and examine the text to 
	 * check if the field is empty or it contains only white spaces.
	 * @param textView
	 * @return
	 */
	private boolean isEmpty(TextView textView) {
		if (textView.getText().toString().trim().equals(""))
			return true;
		else
			return false;
	}
}