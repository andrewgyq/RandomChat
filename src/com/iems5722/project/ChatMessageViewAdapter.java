package com.iems5722.project;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChatMessageViewAdapter extends BaseAdapter {
	  
    private  ArrayList<ChatMessage>  list;  
    private  Context  context;  
    
    public ChatMessageViewAdapter(Context  context, ArrayList<ChatMessage>  list) {  
        this.context = context;  
        this.list =  list;  
    }  
    
    @SuppressLint("ViewHolder")
	@Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
    	ChatMessage chatMessage = list.get(position);  
        int itemlayout = chatMessage.getLayoutId();  
        
        LinearLayout  layout = new LinearLayout(context);  
        LayoutInflater  vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        vi.inflate(itemlayout, layout, true);  
        
        TextView  title  = (TextView) layout.findViewById(R.id.messagedetail_row_title);
        title.setText(chatMessage.getTitle());
        
        TextView  message  =(TextView) layout.findViewById(R.id.messagedetail_row_text);
        if(chatMessage.getMessage() != null){
        	message.setText(chatMessage.getMessage()); 
        }else{
        	message.setVisibility(View.GONE);
        }
        
        ImageView  imageView  =(ImageView) layout.findViewById(R.id.messagedetail_row_image);
        if(chatMessage.getBitMap() != null){
        	imageView.setImageBitmap(chatMessage.getBitMap());
        }else{
        	imageView.setVisibility(View.GONE);
        }
        return layout;  
    }

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}  
  
}
