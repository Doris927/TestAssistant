package com.example.administrator.testassistant;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/23.
 */
public class UploadImageTask extends AsyncTask<String, Void, String> {

    final HttpClient client = new DefaultHttpClient();
        public static   final String url="http://greenteabitch.coding.io?action=";
        File file;
        final Map<String,Object> map;
        public UploadImageTask(String imagePath, final Map<String,Object> map) {
            file=new File(imagePath);
            this.map=map;
        }
        @Override
        protected String doInBackground(String... params) {
            String urlString=params[0];
            HttpPost post=new HttpPost(urlString);

            FileBody fileBody = new FileBody(file);
            MultipartEntity entity = new MultipartEntity();

            // image 是服务端读取文件的 key
            entity.addPart("file", fileBody);


            Iterator<String> iter = map.keySet().iterator();
            String key;
            while (iter.hasNext()) {

                key = iter.next();
                try {
                    entity.addPart(key, new StringBody((String)map.get(key),Charset.forName(org.apache.http.protocol.HTTP.UTF_8)));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            post.setEntity(entity);


            try {

            HttpResponse response=client.execute(post);
            String value= EntityUtils.toString(response.getEntity());

            return value;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }



}
