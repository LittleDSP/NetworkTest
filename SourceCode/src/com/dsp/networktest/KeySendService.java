package com.dsp.networktest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

import android.app.Instrumentation;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.animation.Interpolator;
import android.widget.Toast;


public class KeySendService extends Service{
	
	String LOGTAG = "DSP-->>";
	Thread thread01 = null;
	Thread thread02 = null;
	KeySendThreadMonitor KeySendThreadMonitor = null;
	private String delayTestTime = "10";
	private boolean isBootup = false;
	private boolean noSendPowerkey = false;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		
		return null;
	}
	
	

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.v(LOGTAG, "PingService onCreate. ");
		getDate();
		KeySendThreadMonitor = new KeySendThreadMonitor();
		thread02 = new Thread(KeySendThreadMonitor);
		thread02.start();
		registerBCReceiver();
		super.onCreate();
	}	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unregisterBCReceiver();
		super.onDestroy();
	}



	private void getDate()
	{
		String str = null;
		//获取用户保存的数据
    	SharedPreferences dBase = getSharedPreferences("dbase_all", Context.MODE_PRIVATE);
    	
    	//如果用户没保存有数据，就用默认的，变量定义时候的值
    	str = dBase.getString("delayTestTime", "default").trim();
    	if(!(str.equals("default"))) {
    		delayTestTime = str;
    	}
	}	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.v(LOGTAG, "PingService onStartCommand. ");
		getDate();
		isBootup = intent.getBooleanExtra("is_bootup", false);
		if(null == thread01) {
			thread01 = new Thread(new KeySendThread());
			thread01.start();
			Log.v(LOGTAG, "Ping Thread created");
		}
		else {
			if(thread01.isAlive()==true) {
				Log.v(LOGTAG, "Ping Thread is running...");
			}
			else {
				thread01 = new Thread(new KeySendThread());
				thread01.start();
				Log.v(LOGTAG, "Ping Thread created");
			}
		}
		
		return super.onStartCommand(intent, flags, startId);
	}



	class KeySendThread implements Runnable {

    	BufferedReader buffReader;
    	String str = null;
    	int counter = 1;
    	
    	SharedPreferences dBase = getSharedPreferences("dbase_all", Context.MODE_PRIVATE);
    	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			noSendPowerkey = false;
			if(isBootup == true) {
				counter = 2;
				int delayTime = Integer.parseInt(delayTestTime);
				if(delayTime>0) {
					try {
						Thread.sleep(delayTime*1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			else {
//				counter = 2;
			}			
			
			while((counter--) > 0) {
				str = dBase.getString("autolaunch_key", "false").trim();
		    	if(str.equals("false")) {
		    		counter = 0;
		    	}			
		    	
		    	Log.v(LOGTAG, "Key send running...");
		    	sendKeypad(KeyEvent.KEYCODE_DPAD_RIGHT);
		    	sendKeypad(KeyEvent.KEYCODE_DPAD_DOWN);
		    	sendKeypad(KeyEvent.KEYCODE_DPAD_LEFT);
		    	sendKeypad(KeyEvent.KEYCODE_DPAD_UP);
		    				
		    	if(counter == 0) {
		    		break;
		    	}
				if(isBootup == true) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			if(noSendPowerkey != true) {
//				Log.i(LOGTAG, "Send powerkey...");
//				sendKeypad(KeyEvent.KEYCODE_POWER);
//				Intent ipoweroff = new Intent(Intent.ACTION_SHUTDOWN);  
//				//iReboot.putExtra(Intent.EXTRA_KEY_EVENT, false);  	
//				//iReboot.addFlags(Intent.FLAG_FROM_BACKGROUND);
//				ipoweroff.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				//iReboot.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 					
//				KeySendService.this.sendBroadcast(ipoweroff);
				
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
				
//				try {
//					Process shotdownproc;
//					shotdownproc = Runtime.getRuntime().exec("/system/bin/adb shell");
//					java.io.DataOutputStream os = new java.io.DataOutputStream(shotdownproc.getOutputStream());
//					os.writeBytes("/system/xbin/su \n");
////					os.writeBytes("input keyevent 26 \n");
//					os.writeBytes("reboot \n");
//					os.writeBytes("exit\n");
//					os.flush();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
			
		}
		
		private void sendKeypad(int keypad)
		{
			Instrumentation inst = new Instrumentation();
	    	inst.sendKeyDownUpSync(keypad);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
	
	
	class KeySendThreadMonitor implements Runnable {
		private boolean isPingThreadBegin = false;
		private int timeout = 60;
		Process pingProcess;
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true) {
				if(isPingThreadBegin == true) {
					try {
						int cnt = timeout;
						while(cnt > 0) {
							cnt--;
							Thread.sleep(1000);
							if(isPingThreadBegin != true) {
								Log.v(LOGTAG, "pingProcess exit normally.");
								break;
							}
							if(cnt == 0) {
								if(pingProcess != null) {
									pingProcess.destroy();
									Log.v(LOGTAG, "pingProcess exit by destroy.");
								}
							}
						}	
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					isPingThreadBegin = false;
				}
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		

		
		public void openPingMonitor(Process pingProcess, int timeout)
		{
			this.pingProcess = pingProcess;
			this.timeout = timeout;
			isPingThreadBegin = true;
		}
		
		public void closePingMonitor()
		{
			pingProcess = null;
			isPingThreadBegin = false;
		}
		
	}
	
	private HomeKeyEventBroadCastReceiver homekeyBCR = new HomeKeyEventBroadCastReceiver();
	
	private void registerBCReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		registerReceiver(homekeyBCR, filter);
	}
	
	private void unregisterBCReceiver() {
		unregisterReceiver(homekeyBCR);
	}
	
	
	private int homeKeyCount = 0;
	class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.v(LOGTAG, "HomeKeyEventBroadCastReceiver......." + intent.getAction());
			homeKeyCount++;
			if(homeKeyCount>10) {
				noSendPowerkey = true;
			}
			if(intent.getAction().equals("android.intent.action.CLOSE_SYSTEM_DIALOGS")) {
				Log.v(LOGTAG, "receive broadcast:android.intent.action.ACTION_CLOSE_SYSTEM_DIALOGS");
				//noSendPowerkey = true;
			}
		}  
	}
}
