package com.maplestory.moewallpaperloader.utils;

import java.net.URI;

public class imageValues {
	public static final String ID = "id";
	public static final String PREVIEW_URL = "preview_url";
	public static final String JPEG_URL = "jpeg_url";
	public static final String TAGS = "tags";
	public static final String SCORE = "score";
	private int id;
	private String tags;
	private int score;
	private String preview_url;
	private String jpeg_url;
	public imageValues(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getPreview_url() {
		return preview_url;
	}
	public void setPreview_url(String preview_url) {
		this.preview_url = preview_url;
	}
	public String getJpeg_url() {
		return jpeg_url;
	}
	public void setJpeg_url(String jpeg_url) {
		this.jpeg_url = jpeg_url;
	}
}
