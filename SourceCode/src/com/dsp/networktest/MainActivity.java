package com.dsp.networktest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	private String LOGTAG = "DSP-->>";
	private CheckBox cboxGateway;
	private CheckBox cboxInternet;
	private EditText etextGateway;
	private EditText etextInternet;
	private Button btonBeginTest;
	private Switch switchAutoLaunch;
	private TextView tviewPingResult01;
	private TextView tviewPingResult02;
	private ProgressBar pbarPing;
	private EditText etextDelayPing;
	private Button btonKeyTest;
	
	private String delayPingTime = "10";
	private String gatewayAddress = "192.168.0.1";
	private String internetAddress = "www.baidu.com";
	private boolean isPingGateway = false;
	private boolean isPingInternet = false;
	String pingInfo = "";
	private int pingGatewayRes = -1;
	private int pingInternetRes = -1;
	
	private ResultReceive resReceive = null;
	private ResultHandler resHandler = new ResultHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        cboxGateway = (CheckBox) findViewById(R.id.cbox_gateway);
        cboxInternet = (CheckBox) findViewById(R.id.cbox_internet);
        btonBeginTest = (Button) findViewById(R.id.button_test);
        etextGateway = (EditText) findViewById(R.id.etext_gateway);
        etextInternet = (EditText) findViewById(R.id.etext_internet);
        switchAutoLaunch = (Switch) findViewById(R.id.switch_autolaunch);
        tviewPingResult01 = (TextView) findViewById(R.id.textview_ping_result01);
        tviewPingResult02 = (TextView) findViewById(R.id.textview_ping_result02);
        pbarPing = (ProgressBar) findViewById(R.id.pbar_ping);
        etextDelayPing = (EditText) findViewById(R.id.etext_delay_ping);
        etextDelayPing.setText(delayPingTime);  
        btonKeyTest = (Button) findViewById(R.id.button_keytest);
                        
        btonBeginTest.setOnClickListener(new BeginTestListener());  
        btonKeyTest.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, KeyTestActivity.class);
				startActivity(intent);
			}
		});
        
    	resReceive = new ResultReceive();
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction("com.dsp.networktest.PING_FINISH");
        registerReceiver(resReceive, iFilter);
        
        cboxGateway.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Log.v(LOGTAG, "isChecked=" + isChecked);
				SharedPreferences.Editor dBaseEditor = getSharedPreferences("dbase_all", Context.MODE_PRIVATE).edit();
				if(isChecked == true) {
					dBaseEditor.putBoolean("is_ping_gateway", true);
					isPingGateway = true;
					etextGateway.setEnabled(true);
		    		//etextGateway.setFocusable(true);
		    		etextGateway.setFocusableInTouchMode(true);
				}
				else {
					dBaseEditor.putBoolean("is_ping_gateway", false);
					isPingGateway = false;
					etextGateway.setEnabled(false);
		    		etextGateway.setFocusable(false);
				}
				dBaseEditor.commit();
			}
		});
        
        cboxInternet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Log.v(LOGTAG, "isChecked=" + isChecked);
				SharedPreferences.Editor dBaseEditor = getSharedPreferences("dbase_all", Context.MODE_PRIVATE).edit();
				if(isChecked == true) {
					dBaseEditor.putBoolean("is_ping_internet", true);
					isPingInternet = true;
					etextInternet.setEnabled(true);
		    		//etextInternet.setFocusable(true);
					etextInternet.setFocusableInTouchMode(true);
				}
				else {
					dBaseEditor.putBoolean("is_ping_internet", false);
					isPingInternet = false;
					etextInternet.setEnabled(false);
		    		etextInternet.setFocusable(false);
				}
				dBaseEditor.commit();
			}
		});
        
        switchAutoLaunch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Log.v(LOGTAG, "isChecked=" + isChecked);
				SharedPreferences.Editor dBaseEditor = getSharedPreferences("dbase_all", Context.MODE_PRIVATE).edit();
				if(isChecked == true) {
					dBaseEditor.putString("autolaunch", "true");
					etextDelayPing.setEnabled(true);
					etextDelayPing.setFocusableInTouchMode(true);
				}
				else {
					dBaseEditor.putString("autolaunch", "false");
					etextDelayPing.setEnabled(false);
					etextDelayPing.setFocusable(false);
				}
				dBaseEditor.commit();
			}
		});
        
        etextDelayPing.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				Log.v(LOGTAG, "hasFocus=" + hasFocus);
				if(hasFocus==false) {
					String str = null;
					str = etextDelayPing.getText().toString().trim();
					if( str != null && !(str.equals(delayPingTime))) {
						SharedPreferences.Editor dBaseEditor = getSharedPreferences("dbase_all", Context.MODE_PRIVATE).edit();
						dBaseEditor.putString("delayPingTime", str);
						dBaseEditor.commit();
					}					
				}
			}
		});

        etextGateway.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				Log.v(LOGTAG, "hasFocus=" + hasFocus);
				if(hasFocus==false) {
					String str = null;
					str = etextGateway.getText().toString().trim();
					if( str != null && !(str.equals(gatewayAddress))) {
						SharedPreferences.Editor dBaseEditor = getSharedPreferences("dbase_all", Context.MODE_PRIVATE).edit();
						dBaseEditor.putString("gatewayAddress", str);
						dBaseEditor.commit();
					}					
				}
			}
		});
        
        etextInternet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				Log.v(LOGTAG, "hasFocus=" + hasFocus);
				String str = null;
				str = etextInternet.getText().toString().trim();
				if( str != null && !(str.equals(internetAddress))) {
					SharedPreferences.Editor dBaseEditor = getSharedPreferences("dbase_all", Context.MODE_PRIVATE).edit();
					dBaseEditor.putString("internetAddress", str);
					dBaseEditor.commit();
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
    		
    	str = dBase.getString("delayPingTime", "default").trim();
    	if(!(str.equals("default")) && !(str.equals(""))) {
    		delayPingTime = str;
    	}
    	etextDelayPing.setText(delayPingTime);   
    	
    	str = dBase.getString("gatewayAddress", "default").trim();
    	if(!(str.equals("default"))) {
    		gatewayAddress = str;
    	}
    	str = dBase.getString("internetAddress", "default").trim();
    	if(!(str.equals("default"))) {
    		internetAddress = str;
    	}
        etextGateway.setText(gatewayAddress);         
        etextInternet.setText(internetAddress);
        
        str = dBase.getString("autolaunch", "false").trim();
    	if(str.equals("false")) {
    		switchAutoLaunch.setChecked(false);
    		etextDelayPing.setEnabled(false);
			etextDelayPing.setFocusable(false);
    	}
    	else {
    		switchAutoLaunch.setChecked(true);
    		etextDelayPing.setEnabled(true);
			etextDelayPing.setFocusableInTouchMode(true);
    		
    	}
    	
    	b = dBase.getBoolean("is_ping_gateway", false);
    	if(b == true) {
    		cboxGateway.setChecked(true);
    		isPingGateway = true;
    		etextGateway.setEnabled(true);
    		//etextGateway.setFocusable(true);
    		etextGateway.setFocusableInTouchMode(true);
    	}
    	else {
    		cboxGateway.setChecked(false);
    		isPingGateway = false;
    		etextGateway.setEnabled(false);
    		etextGateway.setFocusable(false);
    	}
    	
    	b = dBase.getBoolean("is_ping_internet", false);
    	if(b == true) {
    		cboxInternet.setChecked(true);
    		isPingInternet = true;
    		etextInternet.setEnabled(true);
    		//etextInternet.setFocusable(true);
    		etextInternet.setFocusableInTouchMode(true);
    	}
    	else {
    		cboxInternet.setChecked(false);
    		isPingInternet = false;
    		etextInternet.setEnabled(false);
    		etextInternet.setFocusable(false);
    	}
        
		super.onResume();
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(resReceive != null) {
			unregisterReceiver(resReceive);
			resReceive = null;
		}
		super.onDestroy();
	}



	class BeginTestListener implements View.OnClickListener {
    	
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			String str = null;
			SharedPreferences.Editor dBaseEditor = getSharedPreferences("dbase_all", Context.MODE_PRIVATE).edit();
			str = etextGateway.getText().toString().trim();
			if( str != null) {
				dBaseEditor.putString("gatewayAddress", str);
			}
			str = etextInternet.getText().toString().trim();
			if( str != null) {
				dBaseEditor.putString("internetAddress", str);
			}		
			
			str = etextDelayPing.getText().toString().trim();
			if( str != null ) {
				dBaseEditor.putString("delayPingTime", str);
			}	
			dBaseEditor.commit();
			
			Intent service = new Intent(MainActivity.this, PingService.class);
			service.putExtra("is_ping_gateway", isPingGateway);
			service.putExtra("is_ping_internet", isPingInternet);
			MainActivity.this.startService(service);
			tviewPingResult02.setText("");
			pbarPing.setVisibility(View.VISIBLE);
		}
    	
    }
	
	class ResultHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			String str = new String("");
			Bundle data = msg.getData();
//			if(pingGateway == 0) {
//				str = str + "ping 网关成功";
//			}
//			else {
//				str = str + "ping 网关失败";
//			}
//			str = str + "\n\r";
//			if(pingInternet == 0) {
//				str = str + "ping 外网成功";
//			}
//			else {
//				str = str + "ping 外网失败";
//			}
//			tviewPingResult01.setText(str);
			tviewPingResult02.setText(data.getString("ping_result", "default"));
			pbarPing.setVisibility(View.GONE);
			
//			Intent iReboot = new Intent(Intent.ACTION_REBOOT);
//			iReboot.putExtra("nowait", 1);
//			iReboot.putExtra("interval", 1);
//			iReboot.putExtra("window", 0);
//			sendBroadcast(iReboot);
			
//			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//			Log.v(LOGTAG, "reboot kernel .....");
//		    pm.reboot("CHONGQI");	
//		    Log.v(LOGTAG, "reboot kernel");
			
//			Intent iReboot = new Intent(Intent.ACTION_REBOOT);  
//			iReboot.putExtra(Intent.EXTRA_KEY_EVENT, false);  
//			iReboot.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
//			iReboot.setFlags(Intent.FLAG_FROM_BACKGROUND);
//			MainActivity.this.startActivity(iReboot);
			
			boolean isReboot = false;
			if(isPingGateway==true) {
				if(pingGatewayRes==0) {
					isReboot = true;
				}
				else {
					isReboot = false;
				}
			}
			if(isPingInternet==true) {
				if(pingInternetRes==0) {
					isReboot = true;
				}
				else {
					isReboot = false;
				}
			}
			Log.v(LOGTAG, "ResultHandler isReboot=" + isReboot);
			if(isReboot==true) {
				new AlertDialog.Builder(MainActivity.this).setTitle("提示")
					.setMessage("测试成功，确认重启吗？")
					.setPositiveButton("重启", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							try {
//								Intent iReboot = new Intent(Intent.ACTION_REBOOT);  
//								iReboot.putExtra(Intent.EXTRA_KEY_EVENT, false);  
//								iReboot.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
//								iReboot.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//								iReboot.setFlags(Intent.FLAG_FROM_BACKGROUND);
//								MainActivity.this.startActivity(iReboot);
								
//								Process killproc = Runtime.getRuntime().exec("/system/bin/adb shell");
//								java.io.DataOutputStream os = new java.io.DataOutputStream(killproc.getOutputStream());
//								os.writeBytes("/system/xbin/su \n");
//								os.writeBytes("busybox pkill -9 DVBServer \n");
//								os.writeBytes("exit\n");
//								os.flush();
//								
//								PowerManager pManager=(PowerManager) getSystemService(Context.POWER_SERVICE);  
//								pManager.reboot("");
								
								try {
				                    
				                    //获得ServiceManager类
				                    Class ServiceManager = Class
				                       .forName("android.os.ServiceManager");
				                     
				                    //获得ServiceManager的getService方法
				                    Method getService = ServiceManager.getMethod("getService", java.lang.String.class);
				                     
				                    //调用getService获取RemoteService
				                    Object oRemoteService = getService.invoke(null,Context.POWER_SERVICE);
				                     
				                    //获得IPowerManager.Stub类
				                    Class cStub = Class
				                       .forName("android.os.IPowerManager$Stub");
				                    //获得asInterface方法
				                    Method asInterface = cStub.getMethod("asInterface", android.os.IBinder.class);
				                    //调用asInterface方法获取IPowerManager对象
				                    Object oIPowerManager = asInterface.invoke(null, oRemoteService);
				                    //获得shutdown()方法
				                    Method shutdown = oIPowerManager.getClass().getMethod("shutdown",boolean.class,boolean.class);
				                    //调用shutdown()方法
				                    shutdown.invoke(oIPowerManager,false,true);           
				               
							          } catch (Exception e) {         
							               Log.e(LOGTAG, e.toString(), e);        
							          }
								
							} catch(Exception e) {
								Toast.makeText(MainActivity.this, "重启失败，请检测apk权限是否有签名系统权限！", Toast.LENGTH_LONG).show();
							} finally {
								Log.d(LOGTAG, "finally");
							}
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					}).show();
				super.handleMessage(msg);
			}
		}
		
	}
	
	class ResultReceive extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction() != "com.dsp.networktest.PING_FINISH") {
				Log.v(LOGTAG, "onReceive action:" + intent.getAction());
				return;
			}
			pingInfo = intent.getStringExtra("ping_result");
			pingGatewayRes = intent.getIntExtra("ping_gateway", -1);
			pingInternetRes = intent.getIntExtra("ping_internet", -1);
			Log.v(LOGTAG, "onReceive data:" + pingInfo);
			Message msg = new Message();
			msg.what = 1;
			Bundle data = new Bundle();
			data.putString("ping_result", pingInfo);
			data.putString("ping_result", pingInfo);
			data.putString("ping_result", pingInfo);
			msg.setData(data);
			resHandler.sendMessage(msg);
		}
		
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
