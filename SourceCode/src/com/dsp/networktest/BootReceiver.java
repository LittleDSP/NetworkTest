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
		if(util.getBootFlag() == false) {
			Log.v(LOGTAG, "BootReceiver getBootFlag is false");
			return;
		}
		if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			Log.v(LOGTAG, "receive broadcast:android.intent.action.BOOT_COMPLETED");
			
			Intent intent02 = new Intent(context, PingService.class);
			intent02.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent02.putExtra("is_bootup", true);
			//context.startActivity(intent02);
			context.startService(intent02);
			
		}
	}

}
