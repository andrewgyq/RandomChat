package com.iems5722.project;

import android.graphics.Bitmap;

public class ChatMessage {
	private String title;
	private String message;
	private int layoutId;
	private Bitmap bitMap;
	
	public ChatMessage(String title, String message, Bitmap bitMap, int layoutId){
		this.title = title;
		this.message = message;
		this.bitMap = bitMap;
		this.layoutId = layoutId;
	}
	
	public Bitmap getBitMap() {
		return bitMap;
	}

	public void setBitMap(Bitmap bitMap) {
		this.bitMap = bitMap;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getLayoutId() {
		return layoutId;
	}

	public void setLayoutId(int layoutId) {
		this.layoutId = layoutId;
	}
	
	
}
