package com.dsp.networktest;

import com.dsp.networktest.MainActivity.BeginTestListener;
import com.dsp.networktest.MainActivity.ResultHandler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

public class KeyTestActivity extends Activity {
	
	private String LOGTAG = "DSP-->>";
	private Button btonBeginTest;
	private Switch switchAutoLaunch;
	private EditText etextDelayTest;
	private TextView tviewResult01;
	
	private String delayTestTime = "10";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.key_test);

        btonBeginTest = (Button) findViewById(R.id.button_test);
        switchAutoLaunch = (Switch) findViewById(R.id.switch_autolaunch_key);
        etextDelayTest = (EditText) findViewById(R.id.etext_delay_test);
        etextDelayTest.setText(delayTestTime);  
        tviewResult01 = (TextView) findViewById(R.id.textview_test_result01);
                        
        btonBeginTest.setOnClickListener(new BeginTestListener()); 
        
        switchAutoLaunch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Log.v(LOGTAG, "isChecked=" + isChecked);
				SharedPreferences.Editor dBaseEditor = getSharedPreferences("dbase_all", Context.MODE_PRIVATE).edit();
				if(isChecked == true) {
					dBaseEditor.putString("autolaunch_key", "true");
					etextDelayTest.setEnabled(true);
					etextDelayTest.setFocusableInTouchMode(true);
				}
				else {
					dBaseEditor.putString("autolaunch_key", "false");
					etextDelayTest.setEnabled(false);
					etextDelayTest.setFocusable(false);
				}
				dBaseEditor.commit();
			}
		});
        
        etextDelayTest.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				Log.v(LOGTAG, "hasFocus=" + hasFocus);
				if(hasFocus==false) {
					String str = null;
					str = etextDelayTest.getText().toString().trim();
					if( str != null && !(str.equals(delayTestTime))) {
						SharedPreferences.Editor dBaseEditor = getSharedPreferences("dbase_all", Context.MODE_PRIVATE).edit();
						dBaseEditor.putString("delayTestTime", str);
						dBaseEditor.commit();
					}					
				}
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		String str;
    	boolean b = false;
    	SharedPreferences dBase = getSharedPreferences("dbase_all", Context.MODE_PRIVATE);
    		
    	str = dBase.getString("delayTestTime", "default").trim();
    	if(!(str.equals("default")) && !(str.equals(""))) {
    		delayTestTime = str;
    	}
    	etextDelayTest.setText(delayTestTime);   
    	
        
        str = dBase.getString("autolaunch_key", "false").trim();
    	if(str.equals("false")) {
    		switchAutoLaunch.setChecked(false);
    		etextDelayTest.setEnabled(false);
    		etextDelayTest.setFocusable(false);
    	}
    	else {
    		switchAutoLaunch.setChecked(true);
    		etextDelayTest.setEnabled(true);
    		etextDelayTest.setFocusableInTouchMode(true);
    		
    	}
    	
		super.onResume();
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		Log.v(LOGTAG, "KeyTestActivity -> dispatchKeyEvent:" + event.getKeyCode());
		Message msg = new Message();
		msg.what = 1;
		Bundle data = new Bundle();
		data.putString("keycode", Integer.toString(event.getKeyCode()));
		msg.setData(data);
		resHandler.sendMessage(msg);
		
		return super.dispatchKeyEvent(event);
	}

	private ResultHandler resHandler = new ResultHandler();
	
	class ResultHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Bundle data = msg.getData();
			tviewResult01.setText(data.getString("keycode", "default"));
		}
	}
	
	class BeginTestListener implements View.OnClickListener {
    	
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			String str = null;
			SharedPreferences.Editor dBaseEditor = getSharedPreferences("dbase_all", Context.MODE_PRIVATE).edit();
			str = etextDelayTest.getText().toString().trim();
			if( str != null ) {
				dBaseEditor.putString("delayTestTime", str);
			}	
			dBaseEditor.commit();
			
			Log.v(LOGTAG, "BeginTestListener...onClick");
			Intent service = new Intent(KeyTestActivity.this, KeySendService.class);
//			service.putExtra("is_ping_gateway", isPingGateway);
//			service.putExtra("is_ping_internet", isPingInternet);
			KeyTestActivity.this.startService(service);
		}
    	
    }

}
