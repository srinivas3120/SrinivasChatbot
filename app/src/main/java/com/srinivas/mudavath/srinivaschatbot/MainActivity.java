package com.srinivas.mudavath.srinivaschatbot;

import android.content.ContentValues;
import android.content.Context;
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
import com.srinivas.mudavath.util.Util;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        ChatBotDBHelper dbHelper = new ChatBotDBHelper();
        Env.init(this.getApplication(), dbHelper, null, true);

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

    private void initializeViews() {
        rv_chat_conversation= (RecyclerView) findViewById(R.id.rv_chat_conversation);
        et_message= (EditText) findViewById(R.id.et_message);
        iv_send_message= (ImageView) findViewById(R.id.iv_send_message);
    }

    private void setListenersToViews() {
        iv_send_message.setOnClickListener(this);
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
                    if(Util.isNetworkAvailable(mContext)){
                        MessageItem messageItem=new MessageItem(createdAt,createdAt,MessageItem.SEND_TYPE,MessageItem.SENT_STATUS,message);
                        messageConversation.add(0, messageItem);
                        messageAdapter.notifyDataSetChanged();
                        //insert into database
                        insertMessageIntoDB(messageItem);
                        sendMessage(message);
                    }else {
                        /*MessageItem messageItem=new MessageItem(createdAt,createdAt,MessageItem.SEND_TYPE,MessageItem.PENDING_STATUS,message);
                        messageConversation.add(0, messageItem);
                        messageAdapter.notifyDataSetChanged();*/
                        Util.showBottomToast(mContext,"Check your internet connection...");
                    }
                    //insert into database

                }
                break;
            default:
                break;
        }
    }

    private void sendMessage(String message) {
        JsonObjectRequest jsonObjectRequest =new JsonObjectRequest(Request.Method.GET, Util.buildGetUrl(message), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parseResponse(response);
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

    private void parseResponse(JSONObject response) {
        Gson gson = new Gson();
        //convert the json string to object
        ResponseItem responseItem = gson.fromJson(response.toString(), ResponseItem.class);

        if(responseItem.getSuccess()==1){
            String createdAt=System.currentTimeMillis()+"";
            MessageItem messageItem=new MessageItem(createdAt,createdAt,MessageItem.RECEIVE_TYPE,MessageItem.SENT_STATUS,responseItem.getMessage().getMessage());
            messageConversation.add(0, messageItem);
            messageAdapter.notifyDataSetChanged();
            //insert into database
            //play ringtone
            playReceivedMessageRingtone();
            insertMessageIntoDB(messageItem);
        }else {
            Util.showBottomToast(mContext,"onResponse - something went wrong...");
        }
    }

    private void playReceivedMessageRingtone() {
        MediaPlayer mp = MediaPlayer.create(mContext, R.raw.received_msg);;
        if(mp.isPlaying()){
            mp.stop();
        }

        Log.e("_______","_______ : : : : " + mp.getDuration());
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
                        getOldMessages(messageConversation.get(messageConversation.size() - 1).getMessageId());
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



    private void getOldMessages(){
        new GetMessagesTask().execute();
    }
    private void getOldMessages(String lastMessageId){
        new GetMessagesTask().execute(lastMessageId);
    }

    private void insertMessageIntoDB(MessageItem messageItem){
        new InsertMessageTask(messageItem).execute();
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
                selection=MessageTable.MESSAGE_ID+" < ? "+" ORDER BY 2 DESC limit 30 ";
                selectionArgs[0]= params[0];
            }else {
                selection=MessageTable.MESSAGE_ID+" is not null ORDER BY 2 DESC "+" limit 30 ";
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
}
