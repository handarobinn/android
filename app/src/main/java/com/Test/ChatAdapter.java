package com.Test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class ChatAdapter extends BaseAdapter {

    private TextView chatText;
    private List<Message> messageList = new ArrayList<Message>();
    private Context context;


    public ChatAdapter(Context context) {
        this.context = context;
    }

    public int getCount() {
        return this.messageList.size();
    }

    public Message getItem(int index) {
        return this.messageList.get(index);
    }

    @Override
    public long getItemId(int i) {
        return this.messageList.get(i).getMsg_id();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        Message messageObj = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (messageObj.isSend) {
            row = inflater.inflate(R.layout.row_item_right, parent, false);
        }else{
            row = inflater.inflate(R.layout.row_item_left, parent, false);
        }
        chatText = (TextView) row.findViewById(R.id.msgr);
        chatText.setText(messageObj.message);
        return row;
    }

    public void add(Message message){
        messageList.add(message);
        notifyDataSetChanged();
    }

    public void remove(int index){
        messageList.remove(index);
        notifyDataSetChanged();
    }

    public void adddatabase(ArrayList<Message> lst){
        messageList.addAll(lst);
        notifyDataSetChanged();
    }
}