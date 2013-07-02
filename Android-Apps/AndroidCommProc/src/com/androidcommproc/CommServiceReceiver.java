package com.androidcommproc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CommServiceReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("Receiver", "receive");
		intent.setClass(context, CommService.class);
		
		context.startService(intent);
	}
}
