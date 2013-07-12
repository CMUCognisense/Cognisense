package com.example.testcomm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MsgReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("Receiver", "receive");
		intent.setClass(context, MusicPlayerService.class);
		
		context.startService(intent);
	}
}
