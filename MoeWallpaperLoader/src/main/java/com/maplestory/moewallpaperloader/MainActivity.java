package com.maplestory.moewallpaperloader;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

































import com.maplestory.moewallpaperloader.utils.ImageProfile;
import com.maplestory.moewallpaperloader.view.ImageViewerPopupWindow;
import com.maplestory.moewallpaperloader.view.MatrixImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity  {
	
	private ImageView iv;
	private TextView tv;
	private ImageProfile ipTest;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private ImageViewerPopupWindow ivDetails;
	private Handler mHandler; 
	private Handler UIupdatHandler;
	private boolean status = false;
    private long  mReference = 0 ;  
    private DownloadManager downloadManager ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		iv = (MatrixImageView) this.findViewById(R.id.image_loader);
		tv = (TextView) this.findViewById(R.id.tv_occupy);
		ipTest = (ImageProfile) this.getIntent().getSerializableExtra("imageObject");
		imageLoader = ImageLoader.getInstance();
		UIupdatHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(status){
					ivDetails.update(iv, -1, -1);
					status = false;
				}else{
					ivDetails.update(tv, -1, -1);
					status = true;
				}
			}
		};
		 mHandler = new Handler() {
		    	@Override
		    	public void handleMessage(Message msg) {
		    		// TODO Auto-generated method stub
		    		super.handleMessage(msg);
		    		ivDetails = new ImageViewerPopupWindow(MainActivity.this,ipTest,itemsOnClick);
		    		ivDetails.showAsDropDown(iv);
		    		imageLoader.displayImage(ipTest.getSample_url(), iv, options);
		    	}
		    };
		((MatrixImageView) iv).setOuterHandler(UIupdatHandler);
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	    options = new DisplayImageOptions.Builder()
	    .imageScaleType(ImageScaleType.NONE)
		.cacheInMemory(true)						// 设置下载的图片是否缓存在内存中
		.cacheOnDisc(true)							// 设置下载的图片是否缓存在SD卡中
		.displayer(new SimpleBitmapDisplayer())	// 设置成圆角图片
		.build();	

	   
	    Message msg = new Message();
	    mHandler.sendMessageDelayed(msg, 300);
	}
    private OnClickListener  itemsOnClick = new OnClickListener(){

		public void onClick(View v) {
			switch(v.getId()){
				case(R.id.btn_save):
					Log.i("Moeloader", "save");
				System.out.println(	Environment.getExternalStoragePublicDirectory("/WallpapersDownloader/"));
		        Request request = new Request( Uri.parse( ipTest.getSample_url() ) );
		        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
		        request.setMimeType(mimeTypeMap.getMimeTypeFromExtension(ipTest.getSample_url()));
		        request.setDestinationInExternalPublicDir("/WallpapersDownloader/"  , ipTest.getId()+".jpg" );
		        request.allowScanningByMediaScanner() ; 
		        request.setVisibleInDownloadsUi( true ) ;
		        downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE) ;
		        mReference = downloadManager.enqueue( request ) ;
					break;
				case(R.id.btn_cut):
					Log.i("Moeloader", "cut");
					break;
				case(R.id.btn_load):
					Log.i("Moeloader", "load");
					break;
				case(R.id.btn_share):
					Log.i("Moeloader", "share");
					break;
				case(R.id.btn_others):
					Log.i("Moeloader", "others");
					break;
			}
				
		}
    	
    };

    
    /**
     * 设置状态栏中显示Notification
     */
    void setNotification(Request request ) {
        //设置Notification的标题
        request.setTitle( "壁纸下载" ) ;
        
        //设置描述
        request.setDescription( ipTest.getTags().toString() ) ;

        //request.setNotificationVisibility( Request.VISIBILITY_VISIBLE ) ;

        request.setNotificationVisibility( Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED ) ;

        //request.setNotificationVisibility( Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION ) ;

        //request.setNotificationVisibility( Request.VISIBILITY_HIDDEN ) ; 
    }

    void setDownloadFilePath( Request request ){

    	System.out.println(	Environment.getExternalStoragePublicDirectory("/WallpapersDownloader/"));
        //设置文件存放路径
        request.setDestinationInExternalPublicDir(  "/WallpapersDownloader/"  , ipTest.getId()+".jpg" ) ;  
    //    request.setDestinationInExternalPublicDir(  "/sdcard/WallpapersDownloader/"  , ipTest.getId()+".jpg" ) ;
    }
    
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	ivDetails=null;
    	super.onDestroy();
  
    }
}
