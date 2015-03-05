package com.iems5722.project;

public class ChatMessage {
	private String date;
	private String message;
	private int layoutId;
	
	public ChatMessage(String date, String message, int layoutId){
		this.date = date;
		this.message = message;
		this.layoutId = layoutId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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
