package com.androidcommproc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
/**
 * This is a broadcast receiver which receives intent from applications and 
 * start the android communication process with the intent.
 * @author Pengcheng
 *
 */
public class CommServiceReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		intent.setClass(context, CommService.class);
		context.startService(intent);
	}
}
