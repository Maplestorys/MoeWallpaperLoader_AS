package com.maplestory.moewallpaperloader;

import com.maplestory.moewallpaperloader.fragment.ImgPreviewFragment.searchCallback;
import com.maplestory.moewallpaperloader.fragment.TabFragment;
import com.maplestory.moewallpaperloader.fragment.ImgPreviewFragment;
import com.maplestory.moewallpaperloader.fragment.WallpaperSelectFragment;
import com.maplestory.moewallpaperloader.utils.Item;
import com.maplestory.moewallpaperloader.utils.ItemAdapter;
import com.maplestory.moewallpaperloader.view.ChangeColorIconWithTextView;
import com.maplestory.moewallpaperloader.view.TitleView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DateFormat.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;  
import android.support.v4.app.FragmentPagerAdapter;  
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;  
import android.support.v4.view.ViewPager.OnPageChangeListener;  
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MoeWallpaperLoader extends FragmentActivity implements OnPageChangeListener,OnClickListener,searchCallback {
	private  WallpaperManager wallpaperManager ;
	private ViewPager mViewPager;  
    private List<Fragment> mTabs = new ArrayList<Fragment>();  
    private FragmentStatePagerAdapter mAdapter;  
    private List<ChangeColorIconWithTextView> mTabIndicator = new ArrayList<ChangeColorIconWithTextView>();
    private String IMAGE_PATH="/sdcard/WallpapersDownloader";
	public NotificationManager mNotificationManager;
	private TitleView titleView;
	private final static String TAG = "CustomActivity";
	private Fragment imgPreview = null;
	/** 按钮：显示自定义通知 */
	private Button btn_show_custom;
	/** 按钮：显示自定义带按钮的通知 */
	private Button btn_show_custom_button;
	/** Notification 的ID */
	int notifyId = 101;
	/** NotificationCompat 构造器*/
	NotificationCompat.Builder mBuilder;
	/** 是否在播放*/
	public boolean isPlay = false;
	/** 通知栏按钮广播 */
	public ButtonBroadcastReceiver bReceiver;
	/** 通知栏按钮点击事件对应的ACTION */
	public final static String ACTION_BUTTON = "com.notifications.intent.action.ButtonClick";
	private void initService() {
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}
	private Boolean isAutoChangeWallpaper;
	private List<String> images = new ArrayList();
	private List<String> imagePaths = new ArrayList();;
	private int totalImage ;
	private int currentImage = 0;
	private String tags="";
	private Handler FragmentUIHandler = null;
	private Handler imagePreviewHandler = null;
	public void setImagePreviewHandler(Handler handler)
	{
		this.imagePreviewHandler = handler;
	}
	public void setUIHandler(Handler handler){
		this.FragmentUIHandler = handler;
	}
	public String getTags(){
		return this.tags;
	}
	
    private void initFile(){
		 File f = new File(IMAGE_PATH);  
         File[] files = f.listFiles();
         if(files != null){
             int count = files.length;// 文件个数  
             for (int i = 0; i < count; i++) {  
                 File file = files[i];  
                 if(getExtensionName(file.getName()).equals("jpg")){
                	 String path = "file:///mnt/sdcard/WallpapersDownloader" + "/" + file.getName();
                	 String paths = "/sdcard/WallpapersDownloader/"+file.getName();
                	 images.add(path);
                	 imagePaths.add(paths);
                 }
             }  
             totalImage = imagePaths.size();
             
         }  
    }
    
    public static String getExtensionName(String filename) {    
        if ((filename != null) && (filename.length() > 0)) {    
            int dot = filename.lastIndexOf('.');    
            if ((dot >-1) && (dot < (filename.length() - 1))) {    
                return filename.substring(dot + 1);    
            }    
        }    
        return filename;    
    }    
    
    private void initStorageFolder(){
    	File file = new File(IMAGE_PATH);
    	if(!file.exists()){
    		file.mkdir();
    	}
    }
    
    @Override  
    protected void onCreate(Bundle savedInstanceState)  
    {  	
    	System.out.println("activity on create");
    	Bundle activityBundle = ((UILApplication)getApplication()).getActivityBundle();
    	if(activityBundle != null){
    		super.onCreate(activityBundle);  
    	}else{
    		super.onCreate(savedInstanceState);  
    	}
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_moe_wallpaper_loader);  
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);  
        titleView = (TitleView) findViewById(R.id.title_view);
        wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        initDatas();  
        initStorageFolder();
        initService();
        initFile();
        mViewPager.setAdapter(mAdapter);  
        mViewPager.setOnPageChangeListener(this);  
        titleView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				switch (arg0.getId()) {
					case (R.id.search_btn):
						System.out.println("search btn pressed");
						tags = titleView.getEditTextValue();
						Message msg = new Message();
						FragmentUIHandler.sendMessage(msg);
						((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(MoeWallpaperLoader.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
						break;
					case (R.id.preference_btn):
						System.out.println("config btn pressed");
						Intent intent = new Intent(MoeWallpaperLoader.this, SettingsActivity.class);
						startActivity(intent);
						break;
				}

			}
		});
		View.OnKeyListener keyListener = new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				System.out.println("key press");
				System.out.println(event.getAction());
				System.out.println(keyCode);
				if(keyCode==66){
					tags = titleView.getEditTextValue();
					Message msg = new Message();
					FragmentUIHandler.sendMessage(msg);
					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(MoeWallpaperLoader.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}
				return false;
			}
		};
		titleView.setOnKeyListener(keyListener);
    }

	@Override
	protected void onResume() {
		super.onResume();

		isAutoChangeWallpaper = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("auto_change_wallpaper",false);
		initButtonReceiver();
		if(isAutoChangeWallpaper) {
			showButtonNotify();
		}else {
			mNotificationManager.cancelAll();
		}
	}

	private void initDatas()
    {  
  
        imgPreview = new ImgPreviewFragment();
        mTabs.add(imgPreview);
        Fragment WallpaperSelectFragment = new WallpaperSelectFragment();
        mTabs.add(WallpaperSelectFragment);
        mAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager())  
        {  
  
            @Override  
            public int getCount()  
            {  
            	//System.out.println(mTabs.size());
                return mTabs.size();  
            }  
  
            @Override  
            public Fragment getItem(int arg0)  
            {  
                return mTabs.get(arg0);  
            }  
        };  
  
        initTabIndicator();  
  
    }  
  
    @Override  
    public boolean onCreateOptionsMenu(Menu menu)  
    {  
     //   getMenuInflater().inflate(R.menu.main, menu);  
        return true;  
    }  
  
    private void initTabIndicator()  
    {  
        ChangeColorIconWithTextView one = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_one);  
        ChangeColorIconWithTextView two = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_two);  

  
        mTabIndicator.add(one);  
        mTabIndicator.add(two);  

  
        one.setOnClickListener(this);  
        two.setOnClickListener(this);  

  
        one.setIconAlpha(1.0f);  
    }  
  
    @Override  
    public void onPageSelected(int arg0)  
    {  
    }  
  
    @Override  
    public void onPageScrolled(int position, float positionOffset,  
            int positionOffsetPixels)  
    {  
        // Log.e("TAG", "position = " + position + " , positionOffset = "  
        // + positionOffset);  
    	initFile();
    	if(position==1){
    		imagePreviewHandler.sendEmptyMessage(0);
    	}
        if (positionOffset > 0)  
        {  
            ChangeColorIconWithTextView left = mTabIndicator.get(position);  
            ChangeColorIconWithTextView right = mTabIndicator.get(position + 1);  
  
            left.setIconAlpha(1 - positionOffset);  
            right.setIconAlpha(positionOffset);  

        }  
  
    }  
  
    @Override  
    public void onPageScrollStateChanged(int state)  
    {  
  
    }  
  
    @Override  
    public void onClick(View v)  
    {  
  
        resetOtherTabs();  
  
        switch (v.getId())  
        {  
        
        case R.id.id_indicator_one:  
            mTabIndicator.get(0).setIconAlpha(1.0f);  
            mViewPager.setCurrentItem(0, false);  
            break;  
        case R.id.id_indicator_two:  
            mTabIndicator.get(1).setIconAlpha(1.0f);  
            mViewPager.setCurrentItem(1, false);  
            break;  
  
        }  
  
    }  
  
    /** 
     * 重置其他的Tab 
     */  
    private void resetOtherTabs()  
    {  
        for (int i = 0; i < mTabIndicator.size(); i++)  
        {  
            mTabIndicator.get(i).setIconAlpha(0);  
        }  
    }  
  
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	mNotificationManager.cancelAll();
    	super.onDestroy();
    	
    	
    }
    
	public void showButtonNotify(){
		NotificationCompat.Builder mBuilder = new Builder(this);
		RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.view_custom_button);
		Drawable dw = wallpaperManager.getDrawable();
		BitmapDrawable bd = (BitmapDrawable) dw;
		Bitmap bitmap = bd.getBitmap();
		mRemoteViews.setImageViewBitmap(R.id.custom_song_icon, bitmap);
		//API3.0 以上的时候显示按钮，否则消失
		
		mRemoteViews.setTextViewText(R.id.tv_custom_song_singer, "壁纸更换服务 ");
		
		//如果版本号低于（3。0），那么不显示按钮
		
			mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.VISIBLE);
			//
			if(isPlay){
				mRemoteViews.setImageViewResource(R.id.btn_custom_play, R.drawable.btn_pause);
				mRemoteViews.setTextViewText(R.id.tv_custom_song_name, "运行中");
			}else{
				mRemoteViews.setImageViewResource(R.id.btn_custom_play, R.drawable.btn_play);
				mRemoteViews.setTextViewText(R.id.tv_custom_song_name, "暂停中");
			}
		

		//点击的事件处理
		Intent buttonIntent = new Intent(ACTION_BUTTON);
		/* 上一张按钮 */
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PREV_ID);
		//这里加了广播，所及INTENT的必须用getBroadcast方法
		PendingIntent intent_prev = PendingIntent.getBroadcast(this, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_prev, intent_prev);
		/* 开始/暂停  按钮 */
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PALY_ID);
		PendingIntent intent_paly = PendingIntent.getBroadcast(this, 2, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_play, intent_paly);
		/* 下一张 按钮  */
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_NEXT_ID);
		PendingIntent intent_next = PendingIntent.getBroadcast(this, 3, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_next, intent_next);

		mBuilder.setContent(mRemoteViews)
				.setContentIntent(getDefalutIntent(Notification.FLAG_ONGOING_EVENT))
				.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
				.setTicker("MoeWallpaperLoader")
				.setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
				.setOngoing(true)
				.setSmallIcon(R.drawable.pic_image);
		Notification notify = mBuilder.build();
		notify.flags = Notification.FLAG_ONGOING_EVENT;
		//会报错，还在找解决思路
//		notify.contentView = mRemoteViews;
//		notify.contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
		mNotificationManager.notify(200, notify);
		System.out.println("=========================================...");
	}
    
	/** 带按钮的通知栏点击广播接收 */
	public void initButtonReceiver(){
		bReceiver = new ButtonBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_BUTTON);
		registerReceiver(bReceiver, intentFilter);
	}
	
	public final static String INTENT_BUTTONID_TAG = "ButtonId";
	/** 上一首 按钮点击 ID */
	public final static int BUTTON_PREV_ID = 1;
	/** 播放/暂停 按钮点击 ID */
	public final static int BUTTON_PALY_ID = 2;
	/** 下一首 按钮点击 ID */
	public final static int BUTTON_NEXT_ID = 3;



	/**
	 *	 广播监听按钮点击时间 
	 */
	public class ButtonBroadcastReceiver extends BroadcastReceiver{

		private int nextImage;
		private int previousImage;
		public ButtonBroadcastReceiver() {
			// TODO Auto-generated constructor stub
	
		}
		@Override
		public void onReceive(Context context, Intent intent) {
			
			nextImage = (currentImage+1)%totalImage;
			previousImage = Math.abs((currentImage-1)%totalImage);
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals(ACTION_BUTTON)){
				//通过传递过来的ID判断按钮点击属性或者通过getResultCode()获得相应点击事件
				int buttonId = intent.getIntExtra(INTENT_BUTTONID_TAG, 0);
				switch (buttonId) {
				case BUTTON_PREV_ID:
					Log.d(TAG , "上一首");
					Bitmap nextWallpaper = BitmapFactory.decodeFile(imagePaths.get(previousImage)); 
					currentImage = previousImage;
					try {
						wallpaperManager.setBitmap(nextWallpaper);
//						Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_SHORT).show(); 
						} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally {
						nextWallpaper = null;
					}
					showButtonNotify();
					break;
				case BUTTON_PALY_ID:
					String play_status = "";
					isPlay = !isPlay;
					if(isPlay){
						play_status = "开始播放";
						Intent intents = new Intent();
						intents.setClass(getApplicationContext(), MoeWallpaperChangeSerivice.class);
						startService(intents);
						System.out.println("开始服务 ");
					}else{
						play_status = "已暂停";
						Intent intent1 = new Intent();
						intent1.setClass(getApplicationContext(), MoeWallpaperChangeSerivice.class);
						stopService(intent1);
						System.out.println("停止服务 ");
					}
					showButtonNotify();
					break;
				case BUTTON_NEXT_ID:
					Log.d(TAG , "下一首");
 
					nextWallpaper = BitmapFactory.decodeFile(imagePaths.get(nextImage)); 
					currentImage = nextImage;
					try {
						wallpaperManager.setBitmap(nextWallpaper);
	//					Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_SHORT).show(); 
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally {
						nextWallpaper = null;
					}  
					showButtonNotify();
					break;
				default:
					break;
				}
			}
		}
	}
	
	
	public PendingIntent getDefalutIntent(int flags){
		PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(), flags);
		return pendingIntent;
	}

	@Override
	public void search() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		System.out.println("save activity state");
		((UILApplication)getApplication()).setActivityBundle(outState);
	}

	
	
	
}
