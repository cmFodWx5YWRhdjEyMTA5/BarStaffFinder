package com.cavaliers.mylocalbartender.server.chat;


import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cavaliers.mylocalbartender.R;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends ArrayAdapter<ChatData> {

    private List<ChatData> chatDataList = new ArrayList<>();

    private Context context;

    private TextView message_text;

    public ChatAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
        this.context = context;
    }

    @Override
    public void add(@Nullable ChatData data) {
        chatDataList.add(data);
        super.add(data);
    }

    @Override
    public int getCount() {
        return chatDataList.size();
    }

    @Nullable
    @Override
    public ChatData getItem(int position) {
        return chatDataList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //if convertview is null create a new one
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.message_layout, parent, false);
        }

        //get the message bubble from message_layout
        message_text = (TextView) convertView.findViewById(R.id.message_bubble);

        //Get the data for the message (getItem returns a ChatData instance with the data)
        ChatData data = getItem(position);
        String message = data.getMessage();
        boolean myMessage = data.isMyMessage();

        message_text.setText(message);
        //Tertiary operator to set the style of the message bubble
        message_text.setBackgroundResource(myMessage ? R.drawable.chat_message_sent : R.drawable.chat_message_received);

        //Set the gravity of the message depending on my message
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if(myMessage) layoutParams.gravity = Gravity.RIGHT;
        else layoutParams.gravity = Gravity.LEFT;
        message_text.setLayoutParams(layoutParams);


        return convertView;
    }
}
