package com.example.administrator.testassistant;


import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/*
主页面
 */
public class main_home extends ActionBarActivity  {

    TextView daysText;
    private ListView testInfoLv;
    private ArrayList<String> titleArrayList = new ArrayList<String>();
    private ArrayList<String> linkArrayList=new ArrayList<String>();
    private ArrayAdapter<String> arrayAdapter;
    private ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        daysText=(TextView)findViewById(R.id.daysFromTestText);
        daysText.setText(getDays()+" 天");
        testInfoLv=(ListView)findViewById(R.id.testInfoListView);
        arrayAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,titleArrayList);
        testInfoLv.setAdapter(arrayAdapter);

        testInfoLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openBrowser(linkArrayList.get(position));
            }
        });

       actionBar=(ActionBar)getSupportActionBar();
       actionBar.setTitle("主页");
       actionBar.hide();



    }

    //获得天数
    private long getDays(){
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String nowTime = myFormatter.format(now);
        String testTime="2015-12-12";
        long day = 0;
        try {
            java.util.Date date = myFormatter.parse(testTime);
            java.util.Date mydate = myFormatter.parse(nowTime);
            day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
           return -1;
        }
        return day;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CommonFunctions.isNetworkConnected(this)) {
            getTestInfo();
        }else{
            Toast.makeText(this, "当前无网络连接", Toast.LENGTH_LONG).show();
        }

        getBackground();

    }

    //获得背景
    private void getBackground(){
        final String KEY_BACKGROUND="background_path";
        SharedPreferences sp = this.getSharedPreferences(settings.class.getName(), Context.MODE_PRIVATE);
        String content = sp.getString(KEY_BACKGROUND, null);

        if (content!=null) {
            Uri uri = Uri.parse(content);
            Log.e("uri", uri.toString());

            ContentResolver cr = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                Drawable drawable =new BitmapDrawable(bitmap);
                this.getWindow().setBackgroundDrawable(drawable);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(),e);
            }
        }
    }


    //获得考试信息
    private void getTestInfo()
    {
        Map<String,Object> map2=new HashMap<String,Object>();
        map2.put("userID",CommonFunctions.getUserID(main_home.this)+"");
        map2.put("sessionID",CommonFunctions.getSessionID(main_home.this));

        GetTestInfoTask task=new GetTestInfoTask(map2);
        task.execute(MyTask.url+"getTestInfo");
    }

    //获得考试信息的通信类
    class GetTestInfoTask extends MyTask{

        public GetTestInfoTask(Map<String, Object> map) {
            super(map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s==null||s.isEmpty()) {
                Toast.makeText(main_home.this, "网络连接出现问题", Toast.LENGTH_LONG).show();
                return;
            }
            Map<String, Object> map = new HashMap<String, Object>();
            String jsonString= s;
            JSONTokener jsonParser = new JSONTokener(jsonString);
            System.out.println(jsonString);
            JSONObject info = null;
            Integer i=0;
            String str="";
            JSONArray arr=null;
            titleArrayList.clear();
            linkArrayList.clear();
            try {
                info = (JSONObject) jsonParser.nextValue();
                i=info.getInt("code");
                str=info.getString("info");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(i==200){
                try {
                    arr=new JSONArray(info.getString("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("data:"+arr.toString());

                for (int j = 0; j < arr.length(); j++) {
                    JSONObject item = null;
                    try {
                        item = arr.getJSONObject(j);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    assert item != null;
                    try {

                        titleArrayList.add(item.getString("title"));
                        linkArrayList.add(item.getString("link"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }else{
                CommonFunctions.showSimpleDialog(str,main_home.this);
            }


            //更新列表
            arrayAdapter.notifyDataSetChanged();


        }
    }

    //点击列表的时候跳转到相应的网页
    private void openBrowser(String url){
        //urlText是一个文本输入框，输入网站地址
        //Uri  是统一资源标识符
        Uri  uri = Uri.parse(url);
        Intent intent = new  Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }





}
