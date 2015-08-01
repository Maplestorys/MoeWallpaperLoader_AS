package com.maplestory.moewallpaperloader.utils;

import android.graphics.drawable.Drawable;

public class Item
{
	String name;
	String url;
	Drawable backGround;
	
	public void setBackGround(Drawable backGround) {
		this.backGround = backGround;
	}
	
	public Drawable getBackGround() {
		return this.backGround;
	}
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getUrl()
	{
		return url;
	}
	public void setUrl(String url)
	{
		this.url = url;
	}
	public Item(String name, String url ,Drawable background)
	{
		super();
		this.backGround=background;
		this.name = name;
		this.url = url;
	}
}
