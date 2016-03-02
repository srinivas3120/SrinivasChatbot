package com.srinivas.mudavath.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.srinivas.mudavath.pojo.MessageItem;
import com.srinivas.mudavath.srinivaschatbot.R;
import com.srinivas.mudavath.util.Util;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Mudavath Srinivas on 01-03-2016.
 */
public class MessageAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MESSAGE_TYPE_SEND= 0;
    private static final int MESSAGE_TYPE_RECEIVE = 1;
    private static final int MESSAGE_VIEW_MORE= 2;

    private ArrayList<MessageItem> messageConversation=new ArrayList<MessageItem>();
    View.OnClickListener clickListener;

    public MessageAdapter(ArrayList<MessageItem> messageConversation){
        this.messageConversation=messageConversation;
    }

    public MessageAdapter(ArrayList<MessageItem> messageConversation, View.OnClickListener clickListener){
        this.messageConversation=messageConversation;
        this.clickListener=clickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder=null;
        ViewHolderForSendMessage viewHolderForSendMessage = null;
        ViewHolderForReceivedMessage viewHolderForReceivedMessage = null;
        ViewHolderForViewMore viewHolderForViewMore=null;

        switch (viewType) {
            case MESSAGE_TYPE_SEND:
                viewHolderForSendMessage = new ViewHolderForSendMessage(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_sent_layout, parent,false));
                viewHolder=viewHolderForSendMessage;
                break;
            case MESSAGE_TYPE_RECEIVE:
                viewHolderForReceivedMessage = new ViewHolderForReceivedMessage(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_received_layout, parent,false));
                viewHolder=viewHolderForReceivedMessage;
                break;
            case MESSAGE_VIEW_MORE:
                View viewMore = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_more_layout, parent,false);

                viewHolderForViewMore = new ViewHolderForViewMore(viewMore);
                viewHolder = viewHolderForViewMore;
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(position==messageConversation.size()){
            ViewHolderForViewMore viewHolderForViewMore = (ViewHolderForViewMore) holder;
            viewHolderForViewMore.rl_view_more.setTag(position);
            return;
        }

        String message;
        switch (messageConversation.get(position).getMessageType()){
            case "s":
                ViewHolderForSendMessage viewHolderForSendMessage=(ViewHolderForSendMessage)holder;
                message=messageConversation.get(position).getMessage();
                if(!TextUtils.isEmpty(message)) {
                    viewHolderForSendMessage.tv_message.setText(message);
                }else {
                    viewHolderForSendMessage.tv_message.setText(message);
                }

                viewHolderForSendMessage.tv_time.setText(Util.getMessageTime(messageConversation.get(position).getCreatedAt()));

                if(messageConversation.get(position).getMessageStatus()==1){
                    viewHolderForSendMessage.iv_status.setImageResource(R.drawable.ic_tick);
                }else {
                    viewHolderForSendMessage.iv_status.setImageResource(R.drawable.ic_clock);
                }

                break;
            case "r":
                ViewHolderForReceivedMessage viewHolderForReceivedMessage=(ViewHolderForReceivedMessage)holder;
                message=messageConversation.get(position).getMessage();
                if(!TextUtils.isEmpty(message)) {
                    viewHolderForReceivedMessage.tv_message.setText(message);
                }else {
                    viewHolderForReceivedMessage.tv_message.setText(message);
                }
                viewHolderForReceivedMessage.tv_time.setText(Util.getMessageTime(messageConversation.get(position).getCreatedAt()));

                break;
            default:
                break;

        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == messageConversation.size()){
            return MESSAGE_VIEW_MORE;
        }
        if (messageConversation.get(position).getMessageType()== MessageItem.SEND_TYPE) {
            return MESSAGE_TYPE_SEND;
        } else if (messageConversation.get(position).getMessageType()== MessageItem.RECEIVE_TYPE) {
            return MESSAGE_TYPE_RECEIVE;
        } else {
            return MESSAGE_TYPE_SEND;
        }
    }

    @Override
    public int getItemCount() {
        if(messageConversation.size()>=30){
            return messageConversation.size()+1;
        }else if(messageConversation.size()>0){
            return messageConversation.size();
        }else {
            return -1;
        }
    }


    private class ViewHolderForReceivedMessage extends RecyclerView.ViewHolder {
        TextView tv_message;
        TextView tv_time;

        public ViewHolderForReceivedMessage(View itemView) {
            super(itemView);
            tv_message= (TextView) itemView.findViewById(R.id.tv_message);
            tv_time= (TextView) itemView.findViewById(R.id.tv_time);
        }
    }

    private class ViewHolderForSendMessage extends RecyclerView.ViewHolder {
        TextView tv_message;
        TextView tv_time;
        ImageView iv_status;

        public ViewHolderForSendMessage(View itemView) {
            super(itemView);
            tv_message= (TextView) itemView.findViewById(R.id.tv_message);
            tv_time= (TextView) itemView.findViewById(R.id.tv_time);
            iv_status= (ImageView) itemView.findViewById(R.id.iv_status);
        }
    }

    public class ViewHolderForViewMore extends RecyclerView.ViewHolder{

        TextView tv_view_more;
        RelativeLayout rl_view_more;
        public ViewHolderForViewMore(View itemView) {
            super(itemView);
            tv_view_more= (TextView) itemView.findViewById(R.id.tv_view_more);
            rl_view_more= (RelativeLayout) itemView.findViewById(R.id.rl_view_more);
            rl_view_more.setOnClickListener(clickListener);
        }
    }

}
