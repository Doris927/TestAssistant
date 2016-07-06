package com.example.administrator.testassistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

/**
 * Created by Administrator on 2015/4/14.
 */
public class CommonFunctions {

    //显示提醒对话框
    public static void showSimpleDialog(String info,Context context) {

        Dialog alertDialog = new AlertDialog.Builder(context).
                setTitle("提醒").
                setMessage(info).
                setIcon(R.drawable.ic_launcher).
                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).
                create();

        alertDialog.show();
    }

    public static final String KEY_SESSION_ID="sessionID";
    public static final String KEY_USER_ID="userID";

    //保存sessionID
    public static void saveSessionID(String sessionID,Context context){
        SharedPreferences.Editor editor=context.getSharedPreferences(settings.class.getName(), Context.MODE_PRIVATE).edit();
        editor.putString(KEY_SESSION_ID,sessionID);
        editor.commit();
    }

    //获得sessionID
    public static String getSessionID(Context context){
        SharedPreferences sp = context.getSharedPreferences(settings.class.getName(), Context.MODE_PRIVATE);
        String sessionID = sp.getString(KEY_SESSION_ID, null);

        return sessionID;
    }

    //保存userID
    public static void saveUserID(int userID,Context context){
        SharedPreferences.Editor editor=context.getSharedPreferences(settings.class.getName(), Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_USER_ID,userID);
        editor.commit();
    }

    //获得userID
    public static int getUserID(Context context){
        SharedPreferences sp = context.getSharedPreferences(settings.class.getName(), Context.MODE_PRIVATE);
        int userID = sp.getInt(KEY_USER_ID, -1);
        return userID;
    }

    //将时间转为一定格式的字符串
    public static String getTime(long time)
    {
       java.text.SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        GregorianCalendar gc=new GregorianCalendar();
        gc.setTimeInMillis(time);

        gc.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(gc.getTime());
    }

    //检测网络连接
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
