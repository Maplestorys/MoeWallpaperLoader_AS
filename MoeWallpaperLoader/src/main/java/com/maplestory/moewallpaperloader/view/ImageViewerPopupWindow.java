package com.maplestory.moewallpaperloader.view;

import com.maplestory.moewallpaperloader.utils.ImageProfile;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.maplestory.moewallpaperloader.R;

public class ImageViewerPopupWindow extends PopupWindow{
	
	private ImageButton mIbSave,mIbCut,mIbLoad,mIbShare,mIbOther;
	private TextView tvId,tvScore,tvSize;
	private ListView lvImageTags;
	private View mImageViewerPopupWindow;
	
	public ImageViewerPopupWindow(Activity context,ImageProfile ip,OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mImageViewerPopupWindow = inflater.inflate(R.layout.image_popupwindow, null);
		
		//init views
		mIbSave = (ImageButton) mImageViewerPopupWindow.findViewById(R.id.btn_save);
		mIbCut = (ImageButton) mImageViewerPopupWindow.findViewById(R.id.btn_cut);
		mIbLoad = (ImageButton) mImageViewerPopupWindow.findViewById(R.id.btn_load);
		mIbShare = (ImageButton) mImageViewerPopupWindow.findViewById(R.id.btn_share);
		mIbOther = (ImageButton) mImageViewerPopupWindow.findViewById(R.id.btn_others);
		tvId = (TextView) mImageViewerPopupWindow.findViewById(R.id.tv_id);
		tvScore = (TextView) mImageViewerPopupWindow.findViewById(R.id.tv_score);
		tvSize = (TextView) mImageViewerPopupWindow.findViewById(R.id.tv_size);
		lvImageTags = (ListView) mImageViewerPopupWindow.findViewById(R.id.image_tag_list);
		
		
		//fill the  empty field
		tvId.setText(""+ip.getId());
		tvScore.setText(""+ip.getScore());
		tvSize.setText(""+ip.getJpeg_width()+"×"+ip.getJpeg_height());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_expandable_list_item_1, ip.getTags());
		lvImageTags.setAdapter(adapter);
		
		mIbSave.setOnClickListener(itemsOnClick);
		mIbCut.setOnClickListener(itemsOnClick);
		mIbLoad.setOnClickListener(itemsOnClick);
		mIbShare.setOnClickListener(itemsOnClick);
		mIbOther.setOnClickListener(itemsOnClick);
		
		//set the config
		this.setContentView(mImageViewerPopupWindow);
		this.setWidth(LayoutParams.FILL_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(false);
		this.setOutsideTouchable(false);
		
		
		
		
		
	}

}
