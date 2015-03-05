package com.iems5722.project;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ChatActivity extends Activity {
	
	private Button button;
	private EditText editText;
	private ListView  chatView;
	private ArrayList<ChatMessage>  list = new ArrayList<ChatMessage>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		Intent intent = getIntent();
		String targetUrl = intent.getStringExtra("targetUrl"); 
		
		this.setTitle(targetUrl);
		
		startChat();
	}

	private void startChat() {
		button = (Button) findViewById(R.id.Button);
		editText = (EditText) findViewById(R.id.EditText);
		chatView = (ListView) findViewById(R.id.ChatView);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String date = getDate();
				String message = editText.getText().toString();  
				
				int sendLayout = R.layout.sender_layout;  
				ChatMessage sendMessage = new ChatMessage(date, message, sendLayout);  
	            list.add(sendMessage);  
	            
	            int receiveLayout = R.layout.receiver_layout;  
	            ChatMessage receiveMessage = new ChatMessage(date, "×Ô¶¯»Ø¸´(for test!)",receiveLayout);  
	            list.add(receiveMessage);
	            
	            chatView.setAdapter(new ChatMessageViewAdapter(ChatActivity.this,list));  
	            editText.setText("");  
			}
		});
	}

	@SuppressLint("SimpleDateFormat")
	protected String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
		Date date = new Date();
		return sdf.format(date);
	}

}
