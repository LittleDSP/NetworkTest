package com.dsp.networktest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver{

	private String LOGTAG = "DSP-->>"; 
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.v(LOGTAG, "BootReceiver.......");
		Utility util = new Utility(context);

		if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			Log.v(LOGTAG, "receive broadcast:android.intent.action.BOOT_COMPLETED");
			
			if(util.getBootFlag() == true) {
				Intent intent01 = new Intent(context, PingService.class);
				intent01.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent01.putExtra("is_bootup", true);
				//context.startActivity(intent01);
				context.startService(intent01);
			}
			else {
				Log.v(LOGTAG, "BootReceiver getBootFlag is false");
			}
			
			if(util.getBootFlagOfKeyTest() == true) {
				Log.v(LOGTAG, "BootReceiver getBootFlagOfKey is true");
				Intent intent02 = new Intent(context, KeySendService.class);
				intent02.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent02.putExtra("is_bootup", true);
				//context.startActivity(intent02);
				context.startService(intent02);
			}
			else {
				Log.v(LOGTAG, "BootReceiver getBootFlagOfKey is false");
			}
			
		}
	}

}
