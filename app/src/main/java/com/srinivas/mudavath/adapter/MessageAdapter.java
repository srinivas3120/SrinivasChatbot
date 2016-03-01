package com.srinivas.mudavath.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.srinivas.mudavath.pojo.MessageItem;
import com.srinivas.mudavath.srinivaschatbot.R;

import java.util.ArrayList;

/**
 * Created by Mudavath Srinivas on 01-03-2016.
 */
public class MessageAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MESSAGE_TYPE_SENT= 0;
    private static final int MESSAGE_TYPE_RECEIVED = 1;

    private ArrayList<MessageItem> messageConversation=new ArrayList<MessageItem>();

    public MessageAdapter(ArrayList<MessageItem> messageConversation){
        this.messageConversation=messageConversation;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolderForMessage viewHolderForMessage = null;

        switch (viewType) {
            case MESSAGE_TYPE_SENT:
                viewHolderForMessage = new ViewHolderForMessage(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_sent_layout, null));
                break;
            case MESSAGE_TYPE_RECEIVED:
                viewHolderForMessage = new ViewHolderForMessage(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_received_layout, null));
                break;
        }
        return viewHolderForMessage;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolderForMessage viewHolderForMessage=(ViewHolderForMessage)holder;
        String message=messageConversation.get(position).getMessage();
        if(!TextUtils.isEmpty(message)) {
            viewHolderForMessage.tv_message.setText(message);
        }else {
            viewHolderForMessage.tv_message.setText(message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messageConversation.get(position).getMessageType()==MESSAGE_TYPE_SENT) {
            return MESSAGE_TYPE_SENT;
        } else if (messageConversation.get(position).getMessageType()==MESSAGE_TYPE_RECEIVED) {
            return MESSAGE_TYPE_RECEIVED;
        } else {
            return MESSAGE_TYPE_SENT;
        }
    }

    @Override
    public int getItemCount() {
        return messageConversation.size();
    }


    private class ViewHolderForMessage extends RecyclerView.ViewHolder {
        TextView tv_message;
        TextView tv_time;

        public ViewHolderForMessage(View itemView) {
            super(itemView);
            tv_message= (TextView) itemView.findViewById(R.id.tv_message);
            tv_time= (TextView) itemView.findViewById(R.id.tv_time);
        }
    }

}
