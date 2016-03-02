package com.srinivas.mudavath.util;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mudavath Srinivas on 08-02-2016.
 */

public class Util {
    private static ProgressDialog progressDialog = null;
    private static Toast toast=null;
    public static String HOST_URL ="http://www.personalityforge.com/api/chat/?";
    public static String STRING_API_KEY ="apiKey";
    public static String API_KEY ="6nt5d1nJHkqbkphe";
    public static String STRING_CHAT_BOT_ID ="chatBotID";
    public static String CHAT_BOT_ID ="63906";
    public static String STRING_EXTERNAL_ID ="externalID";
    public static String EXTERNAL_ID ="chirag1";
    public static String STRING_MESSAGE ="message";
    public static String FIRST_NAME ="Srinivas";
    public static String LAST_NAME ="Mudavath";
    public static String STRING_FIRST_NAME ="firstName";
    public static String STRING_LAST_NAME ="lastName";
    public static String STRING_GENDER ="gender";
    public static String GENDER ="m";




    public static void showProgressDialog(Context context, String message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void showProgressDialog(Context context, String message,boolean cancelable) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(cancelable);
        progressDialog.show();
    }

    public static void dismissProgressDialog() {
        try {
            if ((progressDialog != null) && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (final IllegalArgumentException e) {
            // Handle or log or ignore
        } catch (final Exception e) {
            // Handle or log or ignore
        } finally {
            progressDialog = null;
        }
    }

    public static void showCenteredToast(Context ctx, String msg) {
        if(toast==null){
            toast = Toast.makeText(ctx, msg, Toast.LENGTH_LONG);
        }else {
            toast.setText(msg);
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showBottomToast(Context ctx, String msg) {
        if(toast==null){
            toast = Toast.makeText(ctx, msg, Toast.LENGTH_LONG);
        }else{
            toast.setText(msg);
        }
        toast.show();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        return connManager.getActiveNetworkInfo() != null
                && connManager.getActiveNetworkInfo().isConnected();
    }

    public static final List<Long> times = Arrays.asList(
            TimeUnit.DAYS.toMillis(365),
            TimeUnit.DAYS.toMillis(30),
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.HOURS.toMillis(1),
            TimeUnit.MINUTES.toMillis(1),
            TimeUnit.SECONDS.toMillis(1));
    public static final List<String> timesString = Arrays.asList("year","month","day","hour","min","sec");

    public static String getMessageTime(long duration) {
        duration=System.currentTimeMillis()-duration;
        StringBuffer res = new StringBuffer();
        for(int i=0;i< times.size(); i++) {
            Long current = times.get(i);
            long temp = duration/current;
            if(temp>0) {
                res.append(temp).append(" ").append( timesString.get(i) ).append(temp > 1 ? "s" : "").append(" ago");
                break;
            }
        }
        if("".equals(res.toString()))
            return "Just now";
        else
            return res.toString();
    }

    public static String getMessageTime(String time){
        long duration;
        if(TextUtils.isEmpty(time)){
            duration=System.currentTimeMillis();
        }else {
            duration=Long.parseLong(time);
        }
        return getMessageTime(duration);
    }

    public static String buildGetUrl(String message){
        Uri builtUri = Uri.parse(HOST_URL)
                .buildUpon()
                .appendQueryParameter(STRING_API_KEY, API_KEY)
                .appendQueryParameter(STRING_MESSAGE, message)
                .appendQueryParameter(STRING_CHAT_BOT_ID, CHAT_BOT_ID)
                .appendQueryParameter(STRING_EXTERNAL_ID, EXTERNAL_ID)
                .appendQueryParameter(STRING_FIRST_NAME, FIRST_NAME)
                .appendQueryParameter(STRING_LAST_NAME, LAST_NAME)
                .appendQueryParameter(STRING_GENDER, GENDER)
                .build();
        return builtUri.toString();
    }
}