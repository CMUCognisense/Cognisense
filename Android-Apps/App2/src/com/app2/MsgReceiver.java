package com.app2;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MsgReceiver extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context context, Intent intent) {
		//once the intent is received from the comm process
		//deliver it to the activity
		intent.setClass(context, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}
