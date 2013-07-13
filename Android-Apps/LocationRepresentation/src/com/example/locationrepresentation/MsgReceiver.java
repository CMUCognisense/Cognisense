package com.example.locationrepresentation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MsgReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		//pass the intent to the application
		intent.setClass(context, RegistrationService.class);
		context.startService(intent);
	}
}
