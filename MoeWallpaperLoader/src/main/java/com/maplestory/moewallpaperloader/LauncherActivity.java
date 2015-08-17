package com.maplestory.moewallpaperloader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class LauncherActivity extends Activity {
    private static final String TAG = "Launcher_Aty";
    private ImageView iv_bg;

    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    System.out.println("Begin new Intent");
                    Intent intent = new Intent();
                    intent.setClass(LauncherActivity.this,MoeWallpaperLoader.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                    finish();
                    break;
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_launcher);
        iv_bg = (ImageView)findViewById(R.id.launcher_img);
        Drawable launcher_img = getResources().getDrawable(R.drawable.launcher_img);
        iv_bg.setBackground(launcher_img);
        System.out.println("OnLoaded");

    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        myHandler.sendEmptyMessageDelayed(0, 1000);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
