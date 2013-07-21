package com.example.locationrepresentation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This is the broadcast receiver that receives intent sent from the 
 * android communication process and deliver the intent to the registration
 * service
 *
 * @author Pengcheng
 *
 */

public class MsgReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		//pass the intent to the application
		intent.setClass(context, RegistrationService.class);
		context.startService(intent);
	}
}
