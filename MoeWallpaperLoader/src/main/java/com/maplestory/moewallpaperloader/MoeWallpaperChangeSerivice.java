package com.maplestory.moewallpaperloader;

import java.util.Timer;
import java.util.TimerTask;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

public class MoeWallpaperChangeSerivice extends Service {
	Timer timer;
	TimerTask mTimerTask;
	public final static String ACTION_BUTTON = "com.notifications.intent.action.ButtonClick";
	public final static String INTENT_BUTTONID_TAG = "ButtonId";
	/** 上一首 按钮点击 ID */
	public final static int BUTTON_PREV_ID = 1;
	/** 播放/暂停 按钮点击 ID */
	public final static int BUTTON_PALY_ID = 2;
	/** 下一首 按钮点击 ID */
	public final static int BUTTON_NEXT_ID = 3;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE); 
		int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
		long timeOrLengthofWait = 10000;
		Intent buttonIntent = new Intent(ACTION_BUTTON);
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_NEXT_ID);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, buttonIntent, 0);
		am.setInexactRepeating(alarmType, SystemClock.elapsedRealtime() ,timeOrLengthofWait, pendingIntent);
		super.onStart(intent, startId);
	}
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("服务销毁。。。。。。。。。。。。。。。。。。");
		Intent buttonIntent = new Intent(ACTION_BUTTON);
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_NEXT_ID);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, buttonIntent, 0);
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.cancel(pendingIntent);
		System.out.println("删除Intent");
		super.onDestroy();
	}
	
}