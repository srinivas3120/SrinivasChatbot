package com.srinivas.mudavath.srinivaschatbot;

import android.content.ContentValues;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.srinivas.mudavath.adapter.MessageAdapter;
import com.srinivas.mudavath.database.ChatBotDBHelper;
import com.srinivas.mudavath.database.DatabaseMgr;
import com.srinivas.mudavath.database.Env;
import com.srinivas.mudavath.database.MessageTable;
import com.srinivas.mudavath.network.VolleySingleton;
import com.srinivas.mudavath.pojo.MessageItem;
import com.srinivas.mudavath.pojo.ResponseItem;
import com.srinivas.mudavath.receivers.NetworkStateReceiver;
import com.srinivas.mudavath.util.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NetworkStateReceiver.NetworkStateReceiverListener {

    Context mContext;
    RecyclerView rv_chat_conversation;
    EditText et_message;
    ImageView iv_send_message;

    private ArrayList<MessageItem> messageConversation=new ArrayList<MessageItem>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private VolleySingleton volleySingleton;
    private View.OnClickListener viewClickListener;
    private TextView tv_view_more;
    private ProgressBar pb_loading_view_more;
    private NetworkStateReceiver networkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;

        setUpDB();
        setUpNetworkListener();
        setContentView(R.layout.activity_main);
        initializeViews();
        setListenersToViews();
        mClickListeners();

        volleySingleton=VolleySingleton.getInstance();

        linearLayoutManager=new LinearLayoutManager(mContext);
        linearLayoutManager.setReverseLayout(true);
        rv_chat_conversation.setLayoutManager(linearLayoutManager);
        rv_chat_conversation.addItemDecoration(new SimpleDividerItemDecoration(8));
        messageAdapter=new MessageAdapter(messageConversation,viewClickListener);
        rv_chat_conversation.setAdapter(messageAdapter);

        getOldMessages();

    }

    private void setUpDB() {
        ChatBotDBHelper dbHelper = new ChatBotDBHelper();
        Env.init(this.getApplication(), dbHelper, null, true);
    }

    private void setUpNetworkListener() {
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void initializeViews() {
        rv_chat_conversation= (RecyclerView) findViewById(R.id.rv_chat_conversation);
        et_message= (EditText) findViewById(R.id.et_message);
        iv_send_message= (ImageView) findViewById(R.id.iv_send_message);
    }

    private void setListenersToViews() {
        iv_send_message.setOnClickListener(this);
    }

    private void mClickListeners(){
        viewClickListener=new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                final int position = (Integer) v.getTag();
                switch (v.getId()){
                    case R.id.rl_view_more:
                        tv_view_more=getTextViewAt(position, R.id.tv_view_more);
                        pb_loading_view_more=getProgressBarAt(position, R.id.pb_loading_view_more);
                        tv_view_more.setText("Loading...");
                        pb_loading_view_more.setVisibility(View.VISIBLE);
                        getOldMessages(messageConversation.get(messageConversation.size() - 1).getCreatedAt());
                        break;
                }
            }
        };
    }

    private TextView getTextViewAt(int itemIndex, int id) {
        int visiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
        View v = rv_chat_conversation.getChildAt(itemIndex - visiblePosition);
        TextView textView = null;
        if (v != null) {
            textView = (TextView) v
                    .findViewById(id);
        }
        return textView;
    }

    private ProgressBar getProgressBarAt(int itemIndex, int id) {
        int visiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
        View v = rv_chat_conversation.getChildAt(itemIndex - visiblePosition);
        ProgressBar progressBar = null;
        if (v != null) {
            progressBar = (ProgressBar) v
                    .findViewById(id);
        }
        return progressBar;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_send_message:
                String message=et_message.getText().toString().trim();
                if(TextUtils.isEmpty(message)){
                    Util.showBottomToast(mContext,"message can't be empty!");
                }else {
                    et_message.setText("");
                    String createdAt=System.currentTimeMillis()+"";
                    MessageItem messageItem=new MessageItem(createdAt,createdAt,MessageItem.SEND_TYPE,MessageItem.PENDING_STATUS,message);
                    messageConversation.add(0, messageItem);
                    messageAdapter.notifyDataSetChanged();
                    insertMessageIntoDB(messageItem);
                    if(Util.isNetworkAvailable(mContext)){
                        sendMessage(messageItem);
                    }else {
                        Util.showBottomToast(mContext,"Check your internet connection...");
                    }

                }
                break;
            default:
                break;
        }
    }

    private void sendMessage(final MessageItem messageItem) {
        JsonObjectRequest jsonObjectRequest =new JsonObjectRequest(Request.Method.GET, Util.buildGetUrl(messageItem.getMessage()), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parseResponse(response,messageItem);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorStatus=null;
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    errorStatus="Check your internet connection";
                } else if (error instanceof AuthFailureError) {
                    errorStatus= "Authentication Error";
                } else if (error instanceof ServerError) {
                    errorStatus= "Server Error";
                } else if (error instanceof NetworkError) {
                    errorStatus="Check your internet connection";
                } else if (error instanceof ParseError) {
                    errorStatus= " response can't be parsed.";
                }

                Util.showBottomToast(mContext,errorStatus);
            }
        });
        jsonObjectRequest.setShouldCache(false);
        volleySingleton.addToRequestQueue(jsonObjectRequest);
    }

    private void parseResponse(JSONObject response,MessageItem messageItem) {
        Gson gson = new Gson();
        //convert the json string to object
        ResponseItem responseItem = gson.fromJson(response.toString(), ResponseItem.class);

        if(responseItem.getSuccess()==1){
            String sentMsgCreatedAt=System.currentTimeMillis()+"";
            String receivedMsgCreatedAt=(System.currentTimeMillis()+50)+"";
            MessageItem receivedMessageItem=new MessageItem(receivedMsgCreatedAt,receivedMsgCreatedAt,MessageItem.RECEIVE_TYPE,MessageItem.SENT_STATUS,responseItem.getMessage().getMessage());
            MessageItem sentMessageItem=new MessageItem(messageItem.getMessageId(),sentMsgCreatedAt,MessageItem.SEND_TYPE,
                    MessageItem.SENT_STATUS,messageItem.getMessage());

            messageConversation.add(0, receivedMessageItem);
            updateMessageConversationList(sentMessageItem);
            playReceivedMessageRingtone();
            insertMessageIntoDB(receivedMessageItem);
            insertMessageIntoDB(sentMessageItem);

        }else {
            Util.showBottomToast(mContext,"Network error - something went wrong...");
        }
    }

    private void getOldMessages(){
        new GetMessagesTask().execute();
    }
    private void getOldMessages(String createdAt){
        new GetMessagesTask().execute(createdAt);
    }

    private void insertMessageIntoDB(MessageItem messageItem){
        new InsertMessageTask(messageItem).execute();
    }

    @Override
    public void onNetworkAvailable() {
        new GetPendingMessagesTask().execute();
        Log.e("onNetworkAvailable", "onNetworkAvailable");
    }

    @Override
    public void onNetworkUnavailable() {
        Log.e("onNetworkUnavailable", "onNetworkUnavailable");
    }

    private void updateMessageConversationList(MessageItem sentMessageItem) {
        String messageId=sentMessageItem.getMessageId();
        for (int i = 0; i < messageConversation.size(); i++) {
            if (messageId.equals(messageConversation.get(i).getMessageId())) {
                messageConversation.get(i).setCreatedAt(sentMessageItem.getCreatedAt());
                messageConversation.get(i).setMessageStatus(sentMessageItem.getMessageStatus());

                Collections.sort(messageConversation, new Comparator<MessageItem>() {
                    @Override
                    public int compare(MessageItem p1, MessageItem p2) {
                        return -new Double(Double.parseDouble(p1.getCreatedAt())).compareTo(new Double(Double.parseDouble(p2.getCreatedAt()))); // Ascending
                    }

                });
                messageAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    private void playReceivedMessageRingtone() {
        MediaPlayer mp = MediaPlayer.create(mContext, R.raw.received_msg);;
        if(mp.isPlaying()){
            mp.stop();
        }
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mp != null) {
                    mp.stop();
                    mp.release();
                    mp = null;
                }
            }
        });
        mp.start();


    }



    private class InsertMessageTask extends AsyncTask<String,String,MessageItem>{

        MessageItem messageItem;
        InsertMessageTask(MessageItem messageItem){
            this.messageItem=messageItem;
        }
        @Override
        protected MessageItem doInBackground(String... params) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MessageTable._ID,messageItem.getMessageId());
            contentValues.put(MessageTable.MESSAGE_ID,messageItem.getMessageId());
            contentValues.put(MessageTable.CREATED_AT,messageItem.getCreatedAt());
            contentValues.put(MessageTable.MESSAGE_TYPE,messageItem.getMessageType());
            contentValues.put(MessageTable.MESSAGE_STATUS,messageItem.getMessageStatus());
            contentValues.put(MessageTable.MESSAGE,messageItem.getMessage());
            int returnCode = DatabaseMgr.insertRow(MessageTable.TABLE_NAME, contentValues);
            Log.e("insertReturnCode = ", "" + returnCode);

            return null;
        }
    }

    private class GetMessagesTask extends AsyncTask<String,String,ArrayList<MessageItem>>{

        private String[] columns={MessageTable.MESSAGE_ID, MessageTable.CREATED_AT,
                MessageTable.MESSAGE_TYPE, MessageTable.MESSAGE_STATUS, MessageTable.MESSAGE};

        private String selection;
        private String[] selectionArgs=new String[1];

        @Override
        protected ArrayList<MessageItem> doInBackground(String... params) {

            Cursor cur=null;
            ArrayList<MessageItem> messageItems=new ArrayList<MessageItem>();

            if(params.length>0 && params[0]!=null){
                selection=MessageTable.CREATED_AT+" < ? "+" ORDER BY 2 DESC limit 30 ";
                selectionArgs[0]= params[0];
            }else {
                selection=MessageTable.CREATED_AT+" is not null ORDER BY 2 DESC "+" limit 30 ";
                selectionArgs=null;
            }
            cur = DatabaseMgr.selectRows(MessageTable.TABLE_NAME,columns,selection,selectionArgs);
            try {
                while (cur != null && cur.moveToNext()) {
                    String messageId=cur.getString(cur.getColumnIndex(MessageTable.MESSAGE_ID));
                    String createdAt=cur.getString(cur.getColumnIndex(MessageTable.CREATED_AT));
                    String messageType=cur.getString(cur.getColumnIndex(MessageTable.MESSAGE_TYPE));
                    int messageStatus=cur.getInt(cur.getColumnIndex(MessageTable.MESSAGE_STATUS));
                    String message=cur.getString(cur.getColumnIndex(MessageTable.MESSAGE));
                    messageItems.add(new MessageItem(messageId, createdAt, messageType.equals("s")?"s":"r", messageStatus, message));
                }
            }finally {
                if (cur != null) {
                    cur.close();
                }
            }
            return messageItems;
        }

        @Override
        protected void onPostExecute(ArrayList<MessageItem> messageItems) {
            if(messageItems.size()==0 && tv_view_more!=null){
                tv_view_more.setText("No more messages");
            }else if(tv_view_more!=null){
                tv_view_more.setText("Load Earlier Messages");
            }
            if(pb_loading_view_more!=null){
                pb_loading_view_more.setVisibility(View.GONE);
            }
            messageConversation.addAll(messageItems);
            messageAdapter.notifyDataSetChanged();
        }
    }

    private class GetPendingMessagesTask extends AsyncTask<String,String,ArrayList<MessageItem>>{

        private String[] columns={MessageTable.MESSAGE_ID, MessageTable.CREATED_AT,
                MessageTable.MESSAGE_TYPE, MessageTable.MESSAGE_STATUS, MessageTable.MESSAGE};

        private String selection;
        private String[] selectionArgs=new String[1];

        @Override
        protected ArrayList<MessageItem> doInBackground(String... params) {

            Cursor cur=null;
            ArrayList<MessageItem> messageItems=new ArrayList<MessageItem>();

            selection=MessageTable.MESSAGE_TYPE+" ='"+MessageItem.SEND_TYPE+"' and " +
                    MessageTable.MESSAGE_STATUS+" ="+MessageItem.PENDING_STATUS
                    +" ORDER BY 2 ASC ";
            selectionArgs=null;

            cur = DatabaseMgr.selectRows(MessageTable.TABLE_NAME,columns,selection,selectionArgs);
            try {
                while (cur != null && cur.moveToNext()) {
                    String messageId=cur.getString(cur.getColumnIndex(MessageTable.MESSAGE_ID));
                    String createdAt=cur.getString(cur.getColumnIndex(MessageTable.CREATED_AT));
                    String messageType=cur.getString(cur.getColumnIndex(MessageTable.MESSAGE_TYPE));
                    int messageStatus=cur.getInt(cur.getColumnIndex(MessageTable.MESSAGE_STATUS));
                    String message=cur.getString(cur.getColumnIndex(MessageTable.MESSAGE));
                    messageItems.add(new MessageItem(messageId, createdAt, messageType.equals("s")?"s":"r", messageStatus, message));
                }
            }finally {
                if (cur != null) {
                    cur.close();
                }
            }
            return messageItems;
        }

        @Override
        protected void onPostExecute(ArrayList<MessageItem> messageItems) {
            startSendingMessages(messageItems);
        }
    }

    private void startSendingMessages(ArrayList<MessageItem> messageItems) {
        for (int i = 0; i < messageItems.size(); i++) {
            sendMessage(messageItems.get(i));
        }
    }

}
