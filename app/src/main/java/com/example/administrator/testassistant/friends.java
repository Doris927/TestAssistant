package com.example.administrator.testassistant;

import android.annotation.TargetApi;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class friends extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {


    private Button addFriendBtn,addFriendContentBtn;
    private ListView listView;
    private MyAdapter3 adapter;

    private SwipeRefreshLayout mSwipeLayout;
    List<Map<String, Object>> realList;

    private ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);


        try {
            init();//初始化
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //设置列表项点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(friends.this, SelectFriendContentShowAty.class);//跳转到状态的界面
                i.putExtra("contentID",
                        (String)realList.get(position).get("contentID"));
                i.putExtra("content",
                        (String)realList.get(position).get("content"));
                i.putExtra("name",
                        (String)realList.get(position).get("name"));
                i.putExtra("time",
                        CommonFunctions.getTime(Integer.parseInt((String)realList.get(position).get("time"))*(long)1000));

                i.putExtra("avatar",
                        (String)realList.get(position).get("avatar"));
                System.out.println("avatar:"+(String)realList.get(position).get("avatar"));
                startActivity(i);
            }
        });

        //设置actionbar的title
        actionBar=(ActionBar)getSupportActionBar();
        actionBar.setTitle("朋友圈");

    }

    @Override
    protected void onResume() {
        super.onResume();
        getBackground();//获得背景

        try {
            getData();//获得状态列表
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /*
    初始化
     */
    private void init() throws ExecutionException, InterruptedException, JSONException {
        addFriendBtn=(Button)findViewById(R.id.addFriendBtn);
        addFriendContentBtn=(Button)findViewById(R.id.addFriendContentBtn);
        listView=(ListView)findViewById(R.id.friendContentListView);

        addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(friends.this,AddFriendAty.class);
                startActivity(intent) ;

            }
        });

        //添加朋友事件
        addFriendContentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(friends.this,AddFriendContentAty.class);
                startActivity(intent) ;
            }
        });

        //设置刷新的进度条
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
        mSwipeLayout.setEnabled(false);

        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);

        //状态列表置顶时才能启动刷新
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                                         @Override
                                         public void onScrollStateChanged(AbsListView absListView, int i) {

                                         }

                                         @Override
                                         public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                             if (firstVisibleItem == 0)
                                                 mSwipeLayout.setEnabled(true);
                                             else
                                                 mSwipeLayout.setEnabled(false);
                                         }
                                     });

        //设置listview的数据源
        realList= new ArrayList<Map<String, Object>>();
        adapter=new MyAdapter3(friends.this,realList);
        listView.setAdapter(adapter);

            getData();//获得状态列表


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

    /*
    获得状态列表
     */
    private void getData() throws ExecutionException, InterruptedException, JSONException {
        Map<String,Object> map2=new HashMap<String,Object>();
        map2.put("userID",CommonFunctions.getUserID(friends.this)+"");
        map2.put("sessionID",CommonFunctions.getSessionID(friends.this));

        getContentTask task=new getContentTask(map2);
        task.execute(MyTask.url+"getContent");

    }



    /*
    刷新事件
     */
    @Override
    public void onRefresh() {
        try {
            getData();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
    获得状态的通信类
     */
    class getContentTask extends MyTask{

        public getContentTask(Map<String, Object> map) {
            super(map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s==null||s.isEmpty()) {
                Toast.makeText(friends.this, "网络连接出现问题", Toast.LENGTH_LONG).show();
                return;
            }
            final List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

            Map<String, Object> map = new HashMap<String, Object>();
            String jsonString= s;
            JSONTokener jsonParser = new JSONTokener(jsonString);
            System.out.println(jsonString);
            JSONObject info = null;
            Integer i=0;
            String str="";
            JSONArray arr=null;
            realList.clear();
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
                        map = new HashMap<String, Object>();
                        map.put("content", item.getString("content"));
                        map.put("time", item.getString("time"));
                        map.put("name",item.getString("friendName"));
                        map.put("contentID",item.getString("contentID"));
                        map.put("avatar",item.getString("avatar"));
                        realList.add(map);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }else{
                CommonFunctions.showSimpleDialog(str,friends.this);
            }


            //更新列表
            adapter.notifyDataSetChanged();

            //关闭刷新进度
            mSwipeLayout.setRefreshing(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_friends, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //菜单点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.add_friend_item:

            {
                Intent intent = new Intent();
                intent.setClass(friends.this,AddFriendAty.class);
                startActivity(intent) ;}
                break;
            // action with ID action_settings was selected
            case R.id.add_content_item:
            {
                Intent intent = new Intent();
                intent.setClass(friends.this,AddFriendContentAty.class);
                startActivity(intent) ;}

                break;
            default:
                break;
        }
        return true;
    }


}
