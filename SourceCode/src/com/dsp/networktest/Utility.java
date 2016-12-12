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
	
	public int getRebootStyle()
	{
		if(mcontext == null) {
			Log.v(LOGTAG, "Utility.mcontext is null");
		}
		dbase = mcontext.getSharedPreferences("dbase_all", Context.MODE_PRIVATE);
		int style = dbase.getInt("rebootStyle", 0);
		Log.v(LOGTAG, "Utility rebootStyle is " + style);
		return style;
	}
	
	public boolean getBootFlagOfKeyTest()
	{
		if(mcontext == null) {
			Log.v(LOGTAG, "Utility.mcontext is null");
		}
		dbase = mcontext.getSharedPreferences("dbase_all", Context.MODE_PRIVATE);
		String str = dbase.getString("autolaunch_key", "false");
		Log.v(LOGTAG, "Utility autolaunch is " + str);
		if(str.equals("true")) {
			return true;
		}
		else {
			return false;
		}
	}
	
}
