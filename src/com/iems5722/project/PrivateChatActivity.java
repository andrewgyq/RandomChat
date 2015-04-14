package com.iems5722.project;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class PrivateChatActivity extends Activity {
	private Button button;
	private EditText editText;
	String targetUrl;
	
	class SocketListener implements Runnable{
		String str;

		@Override
		public void run() {
			 DatagramSocket socket;
             DatagramPacket packet;
             byte[] buf = new byte[256];
             System.out.println ("Thread running");
             
             try{
            	 socket = new DatagramSocket (4569);
            	 while(true){
            		 packet = new DatagramPacket (buf, buf.length);
                     socket.receive (packet);
            		 String s = new String (packet.getData());
            		 System.out.println(s);
            	 }
             }
             catch (Exception e){
                   Log.e(getClass().getName(), e.getMessage());
             }
		}
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_private_chat);
		Intent intent = getIntent();
		targetUrl = intent.getStringExtra("targetUrl");
		this.setTitle(targetUrl);
		
		button = (Button) findViewById(R.id.Button);
		editText = (EditText) findViewById(R.id.EditText);
		
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendmessage(targetUrl);	
			}
		});
		
		Thread t = new Thread (new SocketListener ());
        t.start();
		
	}

	private void sendmessage(final String targetUrl){
		new AsyncTask< Void, Void, String>(){
			@Override
			protected String doInBackground(Void... params) {
				try {
					String message = editText.getText().toString().trim();
					final DatagramSocket socket = new DatagramSocket ();
					byte[] buf = new byte[256];
					buf = message.getBytes();
					InetAddress address = InetAddress.getLocalHost();
					System.out.println(address);
                    final DatagramPacket packet = new DatagramPacket (buf, buf.length, address, 4569);
                    System.out.println ("About to send message");
                    socket.send (packet);
                    System.out.println ("Sent message");
                    socket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		}.execute(null, null, null);
	}
	
}
