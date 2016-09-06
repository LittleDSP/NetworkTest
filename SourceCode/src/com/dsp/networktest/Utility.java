package com.dsp.networktest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Utility {
	private String LOGTAG = "DSP-->>";
	Context mcontext = null;
	SharedPreferences dbase;
	public Utility(Context context)
	{
		mcontext = context;
		
	}
	public boolean getBootFlag()
	{
		if(mcontext == null) {
			Log.v(LOGTAG, "Utility.mcontext is null");
		}
		dbase = mcontext.getSharedPreferences("dbase_all", Context.MODE_PRIVATE);
		String str = dbase.getString("autolaunch", "false");
		Log.v(LOGTAG, "Utility autolaunch is " + str);
		if(str.equals("true")) {
			return true;
		}
		else {
			return false;
		}
	}
	
}
