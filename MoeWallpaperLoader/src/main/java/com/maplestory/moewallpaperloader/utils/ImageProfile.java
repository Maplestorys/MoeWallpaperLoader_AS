package com.maplestory.moewallpaperloader.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class ImageProfile implements Serializable{
	public final int getId() {
		return id;
	}
	public final List<String> getTags() {
		return tags;
	}
	public final String getAuthor() {
		return author;
	}
	public final int getFile_size() {
		return file_size;
	}
	public final String getFile_url() {
		return file_url;
	}
	public final String getSample_url() {
		return sample_url;
	}
	public final String getPreview_url() {
		return preview_url;
	}
	public final String getJpeg_url() {
		return jpeg_url;
	}
	public final int getJpeg_width() {
		return jpeg_width;
	}
	public final int getJpeg_height() {
		return jpeg_height;
	}
	public final int getScore() {
		return score;
	}
	private int id;
	private int score;
	private List<String> tags;
	private String author;
	private int file_size;
	private String file_url;
	private String sample_url;
	private String preview_url;
	private String jpeg_url;
	private int jpeg_width;
	private int jpeg_height;
	public ImageProfile(String jsonStr) {
		// TODO Auto-generated constructor stub
		try{
			JSONObject iv = new JSONObject(jsonStr);
			this.id = (int) iv.getInt("id");
			this.score = (int) iv.getInt("score");
			String tag =  (String) iv.getString("tags");
			this.tags =  (List<String>) Arrays.asList(tag.split(" "));
			this.author = (String) iv.getString("author");
			this.file_size = (int) iv.getInt("file_size");
			this.file_url = (String) iv.getString("file_url");
			this.sample_url = (String) iv.getString("sample_url");
			this.preview_url = (String) iv.getString("preview_url");
			this.jpeg_url = (String) iv.getString("jpeg_url");
			this.jpeg_width = (int) iv.getInt("jpeg_width");
			this.jpeg_height = (int) iv.getInt("jpeg_height");
		}catch(JSONException e) {
			e.printStackTrace();
		}
		
	}

}
