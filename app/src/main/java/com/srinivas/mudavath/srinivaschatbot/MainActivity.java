package com.srinivas.mudavath.srinivaschatbot;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

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
import com.srinivas.mudavath.network.VolleySingleton;
import com.srinivas.mudavath.pojo.MessageItem;
import com.srinivas.mudavath.pojo.ResponseItem;
import com.srinivas.mudavath.util.Util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        setContentView(R.layout.activity_main);
        initializeViews();
        setListenersToViews();

        volleySingleton=VolleySingleton.getInstance();

        linearLayoutManager=new LinearLayoutManager(mContext);
        linearLayoutManager.setReverseLayout(true);
        rv_chat_conversation.setLayoutManager(linearLayoutManager);
        rv_chat_conversation.addItemDecoration(new SimpleDividerItemDecoration(8));
        messageAdapter=new MessageAdapter(messageConversation);
        rv_chat_conversation.setAdapter(messageAdapter);

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

                    if(Util.isNetworkAvailable(mContext)){
                        et_message.setText("");
                        MessageItem messageItem=new MessageItem(MessageItem.SENT,message);
                        messageConversation.add(0, messageItem);
                        messageAdapter.notifyDataSetChanged();
                        sendMessage(message);
                    }else {
                        Util.showBottomToast(mContext,"Check your internet connection...");
                    }

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
                    errorStatus="Check your internet connection or try again";
                } else if (error instanceof ParseError) {
                    errorStatus= "Contact instavoice blogs, response can't be parsed.";
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
            MessageItem messageItem=new MessageItem(MessageItem.RECEIVED,responseItem.getMessage().getMessage());
            messageConversation.add(0,messageItem);
            messageAdapter.notifyDataSetChanged();
        }else {
            Util.showBottomToast(mContext,"onResponse - something went wrong...");
        }
    }
}
