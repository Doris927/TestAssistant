package com.example.administrator.testassistant;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/15.
 * 基础通信类
 */
class MyTask extends AsyncTask<String, Void, String> {
    final HttpClient client = new DefaultHttpClient();
    public static   final String url="http://greenteabitch.coding.io?action=";
    final Map<String,Object> map;


    public MyTask(final Map<String, Object> map) {
        this.map=map;

    }
    @Override
    protected String doInBackground(String... params) {



        String urlString=params[0];
        HttpPost post=new HttpPost(urlString);
        List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
        Iterator<String> iter = map.keySet().iterator();
        String key;
        while (iter.hasNext()) {

            key = iter.next();
            list.add(new BasicNameValuePair(key, (String) map.get(key)));
        }

        try {
            post.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));//设置传输的内容
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {

            HttpResponse response=client.execute(post);//执行连接
            String value= EntityUtils.toString(response.getEntity());//获得返回值

            return value;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
