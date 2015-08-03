package com.maplestory.moewallpaperloader.view;


import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.maplestory.moewallpaperloader.R;
public class TitleView extends RelativeLayout  {
	
	private ImageButton configBtn;
	private EditText searchText;
	private ImageButton searchBtn;
	private OnClickListener onClickListener;
	
	public TitleView(Context context) {
		super(context);
	}
	public TitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.titleview, this);
		configBtn = (ImageButton) findViewById(R.id.preference_btn);
		searchText = (EditText) findViewById(R.id.search_et);
		searchBtn = (ImageButton) findViewById(R.id.search_btn);
	}



	
	public void setOnClickListener(OnClickListener listener){
		this.onClickListener = listener;
		configBtn.setOnClickListener(onClickListener);
		searchBtn.setOnClickListener(onClickListener);
	}
	public void setOnKeyListener(OnKeyListener listener){
		searchText.setOnKeyListener(listener);
	}
	
	public String getEditTextValue() {
		return searchText.getText().toString();
	}
	
}
