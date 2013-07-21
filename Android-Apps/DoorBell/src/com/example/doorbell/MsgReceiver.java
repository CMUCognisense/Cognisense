package com.example.doorbell;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MsgReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("Receiver", "receiveed a message ");

		intent.setClass(context, DoorBellConfigurationService.class);
		context.startService(intent);
	}
}
