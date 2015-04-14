package com.iems5722.project;

import java.net.URISyntaxException;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class P2PChatActivity extends Activity {
	private Activity  activity;
	private Button button;
	private EditText editText;
	private Socket mSocket;
	{
	    try {
	        mSocket = IO.socket("http://52.74.25.92:3000");
	    } catch (URISyntaxException e) {}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_p2p_chat);
		activity = this;
		
		button = (Button) findViewById(R.id.P2PButton);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				attemptSend();
			}
		});
		
		mSocket.on("private", onPrivate);
		mSocket.connect();
	}
	
	private void attemptSend() {
	    
		editText = (EditText) findViewById(R.id.P2PEditText);
		
		String message = editText.getText().toString().trim();
	    if (TextUtils.isEmpty(message)) {
	        return;
	    }  
				
        editText.setText("");  
        mSocket.emit("private", message);
	}


	private Emitter.Listener onPrivate = new Emitter.Listener() {
	    @Override
	    public void call(final Object... args) {
	    	activity.runOnUiThread(new Runnable() {
	    		 @Override
	             public void run() {
	    			String message = (String) args[0];
	    			System.out.println(message);
	    		 }
	    	});
	    }
	};
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
