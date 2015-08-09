package com.maplestory.moewallpaperloader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;


import com.maplestory.moewallpaperloader.view.TouchImageView;
import com.maplestory.moewallpaperloader.view.mImagePager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class LargeImageView extends Activity {
    protected static final String TAG = "MainActivity";
    private String[] ImagePaths;
    int position;
    private mImagePager mViewPager;
    private DisplayImageOptions options;
    ImageLoader imageLoader;
    private DialogFragment mMenuDialogFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_large_image_view);
        ImagePaths = getIntent().getStringArrayExtra("imagePaths");
        position = getIntent().getIntExtra("position", -1);
        mViewPager = (mImagePager) findViewById( R.id.id_viewPager);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)	 //设置图片的解码类型
                .build();
        imageLoader = ImageLoader.getInstance();
        // 将图片显示任务增加到执行池，图片将被显示到ImageView当轮到此ImageView
        mViewPager.setAdapter(new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                View view = (View) object;
                container.removeView(view);
                view = null;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                TouchImageView imageView = new TouchImageView(LargeImageView.this);
                imageLoader.displayImage(ImagePaths[position], imageView, options);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                container.addView(imageView);
                mViewPager.setObjectForPosition(imageView, position);
                return imageView;
            }

            @Override
            public int getCount() {
                return ImagePaths.length;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewPager.setCurrentItem(position);
    }



}



