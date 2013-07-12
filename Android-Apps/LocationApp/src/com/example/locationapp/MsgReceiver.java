package com.example.locationapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MsgReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("Receiver", "receive");
		intent.setClass(context, LocationService.class);
		
		context.startService(intent);
	}
}
