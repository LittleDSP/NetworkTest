package com.dsp.networktest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.view.animation.Interpolator;
import android.widget.Toast;


public class PingService extends Service{
	
	private String LOGTAG = "DSP-->>";
	Thread thread01 = null;
	Thread thread02 = null;
	PingThreadMonitor pingThreadMonitor = null;
	private String gatewayAddress = "192.168.1.1";
	private String internetAddress = "www.baidu.com";
	private String delayPingTime = "10";
	private boolean isPingGateway = false;
	private boolean isPingInternet = false;
	private boolean isBootup = false;
	
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
		pingThreadMonitor = new PingThreadMonitor();
		thread02 = new Thread(pingThreadMonitor);
		thread02.start();
		super.onCreate();
	}	
	
	private void getDate()
	{
		String str = null;
		//获取用户保存的数据
    	SharedPreferences dBase = getSharedPreferences("dbase_all", Context.MODE_PRIVATE);
    	
    	//如果用户没保存有数据，就用默认的，变量定义时候的值
    	str = dBase.getString("gatewayAddress", "default").trim();
    	if(!(str.equals("default"))) {
    		gatewayAddress = str;
    	}
    	str = dBase.getString("internetAddress", "default").trim();
    	if(!(str.equals("default"))) {
    		internetAddress = str;
    	}
    	str = dBase.getString("delayPingTime", "default").trim();
    	if(!(str.equals("default"))) {
    		delayPingTime = str;
    	}
        
    	isPingGateway = dBase.getBoolean("is_ping_gateway", false);
    	isPingInternet = dBase.getBoolean("is_ping_internet", false);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.v(LOGTAG, "PingService onStartCommand. ");
		getDate();
		isBootup = intent.getBooleanExtra("is_bootup", false);
		if(null == thread01) {
			thread01 = new Thread(new PingThread());
			thread01.start();
			Log.v(LOGTAG, "Ping Thread created");
		}
		else {
			if(thread01.isAlive()==true) {
				Log.v(LOGTAG, "Ping Thread is running...");
			}
			else {
				thread01 = new Thread(new PingThread());
				thread01.start();
				Log.v(LOGTAG, "Ping Thread created");
			}
		}
		
		return super.onStartCommand(intent, flags, startId);
	}



	class PingThread implements Runnable {

    	int pingNum = 3;
    	int statusGateway = -1;
    	int statusInternet = -1;
    	BufferedReader buffReader;
    	String str = null;
    	String pingRes=" ";
    	String pingInfo=" ";
    	int counter = 1;
    	
    	SharedPreferences dBase = getSharedPreferences("dbase_all", Context.MODE_PRIVATE);
    	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(isBootup == true) {
				counter = 1000;
				int delayTime = Integer.parseInt(delayPingTime);
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
				counter = 1;
			}			
			
			while((counter--) > 0) {
				if((counter%10) == 0) {
					pingRes = "";
					pingInfo = "";
				}
			try {
				str = dBase.getString("autolaunch", "false").trim();
		    	if(str.equals("false")) {
		    		counter = 0;
		    	}

				if(isPingGateway == true) {
					Log.v(LOGTAG, "Ping gateway begin...");
					Process pingGatewayProcess = Runtime.getRuntime().exec("/system/bin/ping -W 10 -c " + pingNum + " " + gatewayAddress);
					pingThreadMonitor.openPingMonitor(pingGatewayProcess, 60);
					statusGateway = pingGatewayProcess.waitFor();
					pingThreadMonitor.closePingMonitor();
					
					Log.v(LOGTAG, "Ping gateway over...");
					if(0 == statusGateway) {
						Log.v(LOGTAG, "ping Gateway SUCCESS");
						pingRes = pingRes + "ping 网关成功";
					}
					else {
						Log.v(LOGTAG, "ping Gateway FAIL");
						pingRes = pingRes + "ping 网关失败";
					}
					buffReader = new BufferedReader(new InputStreamReader(pingGatewayProcess.getInputStream()));
					while((str=buffReader.readLine()) != null) {
						pingInfo = pingInfo + "\n\r"+ str;
					}
					//buffReader.close();
					Log.v(LOGTAG, pingInfo);
				}
				
				if(isPingInternet == true) {
					Log.v(LOGTAG, "Ping internet begin...");
					Process pingInternetProcess = Runtime.getRuntime().exec("/system/bin/ping -W 10 -c " + pingNum + " " + internetAddress);
					pingThreadMonitor.openPingMonitor(pingInternetProcess, 60);
					statusInternet = pingInternetProcess.waitFor();
					pingThreadMonitor.closePingMonitor();
					
					Log.v(LOGTAG, "Ping gateway over...");
					if(0 == statusInternet) {
						Log.v(LOGTAG, "ping Internet SUCCESS");
						pingRes = pingRes + "\n\r" + "ping 外网成功";
					}
					else {
						Log.v(LOGTAG, "ping Internet FAIL");
						pingRes = pingRes + "\n\r" + "ping 外网失败";
					}
					buffReader = new BufferedReader(new InputStreamReader(pingInternetProcess.getInputStream()));
					while((str=buffReader.readLine()) != null) {
						pingInfo = pingInfo + "\n\r"+ str;
					}
					buffReader.close();
					Log.v(LOGTAG, pingInfo);
				}
				Intent intent = new Intent("com.dsp.networktest.PING_FINISH");
				intent.putExtra("ping_gateway", statusGateway);
				intent.putExtra("ping_internet", statusInternet);
				pingInfo = pingRes + "\n\r" + pingInfo;
				intent.putExtra("ping_result", pingInfo);
				PingService.this.sendBroadcast(intent);
				//sendBroadcastAsUser(intent, user)
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			boolean reboot = false;
			
			if(isBootup==true) {
				if(isPingGateway==true) {
					if(statusGateway==0) {
						reboot = true;
					}
					else {
						reboot = false;
					}
				}
				if(isPingInternet==true) {
					if(statusInternet==0) {
						reboot = true;
					}
					else {
						reboot = false;
					}
				}

				if(reboot==true) {	
					try {
//						Intent iReboot = new Intent(Intent.ACTION_REBOOT);  
//						//iReboot.putExtra(Intent.EXTRA_KEY_EVENT, false);  	
//						//iReboot.addFlags(Intent.FLAG_FROM_BACKGROUND);
//						iReboot.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						//iReboot.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 					
//						PingService.this.startActivity(iReboot);
						
						Process killproc = Runtime.getRuntime().exec("/system/bin/adb shell");
						java.io.DataOutputStream os = new java.io.DataOutputStream(killproc.getOutputStream());
						os.writeBytes("/system/xbin/su \n");
						os.writeBytes("busybox pkill -9 DVBServer \n");
						os.writeBytes("exit\n");
						os.flush();
						
						PowerManager pManager=(PowerManager) getSystemService(Context.POWER_SERVICE);  
						pManager.reboot("");
						break;
					} catch(Exception e) {
						Handler handler = new Handler(Looper.getMainLooper());
						handler.post(new Runnable() {							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(PingService.this, "重启失败，请检测apk权限是否有签名系统权限！", Toast.LENGTH_LONG).show();
							}
						});
						
					}
				}
				
				
			}
			
			if(isBootup == true) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			}
		}
    }
	
	
	class PingThreadMonitor implements Runnable {
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
}
