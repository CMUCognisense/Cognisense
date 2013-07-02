package com.doorbellapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MsgReceiver extends BroadcastReceiver{
	private String msg;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		msg = intent.getStringExtra("received_msg");
		Log.e("Receiver", "APP1Recv " + msg);

		//pass the intent to the application
		intent.setClass(context, MainActivity.class);
		context.startActivity(intent);
	}
}
