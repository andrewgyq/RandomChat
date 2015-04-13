package com.iems5722.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class PrivateChatActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_private_chat);
		Intent intent = getIntent();
		String targetUrl = intent.getStringExtra("targetUrl");
		this.setTitle(targetUrl);
	}

}
