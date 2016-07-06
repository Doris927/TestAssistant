package com.example.administrator.testassistant;

import android.opengl.Visibility;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class SelectFriendContentShowAty extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener{

    private TextView contentTv,timeTv,nameTv;
    private ImageView imageView;
    private ListView listView;
    private CommentAdapter adapter;
    List<Map<String, Object>> realList;
    String contentID;
    private EditText addCommentEt;
    private Button addCommentBtn,showAddCommentBtn;
    private SwipeRefreshLayout mSwipeLayout;
    private LinearLayout addCommentLayout;
    private ImageLoader imageLoader;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend_content_show_aty);

        contentTv=(TextView)findViewById(R.id.selectFriendContentTv);
        timeTv=(TextView)findViewById(R.id.selectContentTimeTv);
        nameTv=(TextView)findViewById(R.id.selectContentFriendNameTv);
        listView=(ListView)findViewById(R.id.selectContentCommentLv);
        addCommentEt=(EditText)findViewById(R.id.addCommentEt);
        addCommentBtn=(Button)findViewById(R.id.addCommentBtn);
        showAddCommentBtn=(Button)findViewById(R.id.showAddCommentBtn);
        addCommentLayout=(LinearLayout)findViewById(R.id.addCommentLayout);
        imageView=(ImageView)findViewById(R.id.selectContentAvatarImageView);

        System.out.println("avatar:"+getIntent().getStringExtra("avatar"));
        imageLoader=new ImageLoader(this);
        String path="http://greenteabitch.coding.io/"+getIntent().getStringExtra("avatar");
        imageLoader.DisplayImage(path,imageView);
        contentID=getIntent().getStringExtra("contentID");
        contentTv.setText(getIntent().getStringExtra("content"));
        timeTv.setText(getIntent().getStringExtra("time"));
        nameTv.setText(getIntent().getStringExtra("name"));

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_comment);
        mSwipeLayout.setEnabled(false);

        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);

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



        realList= new ArrayList<Map<String, Object>>();



        adapter=new CommentAdapter(SelectFriendContentShowAty.this,realList);
        listView.setAdapter(adapter);

        addCommentBtn.setOnClickListener(new View.OnClickListener() {/////////添加评论
            @Override
            public void onClick(View v) {
                String comment=addCommentEt.getText().toString();
                if (comment.isEmpty()){
                    CommonFunctions.showSimpleDialog("评论不能为空",SelectFriendContentShowAty.this);
                }else{
                    addComment(comment);
                    addCommentLayout.setVisibility(View.GONE);
                }
            }
        });

        showAddCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCommentLayout.setVisibility(View.VISIBLE);
            }
        });

        actionBar=getSupportActionBar();
        actionBar.setTitle("朋友圈");

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

    private void getData() throws ExecutionException, InterruptedException, JSONException {
        Map<String,Object> map2=new HashMap<String,Object>();
        map2.put("userID",CommonFunctions.getUserID(SelectFriendContentShowAty.this)+"");
        map2.put("sessionID",CommonFunctions.getSessionID(SelectFriendContentShowAty.this));
        map2.put("contentID",contentID);

        GetCommentTask task=new GetCommentTask(map2);
        task.execute(MyTask.url+"getComment");

    }

    private void addComment(String comment) {
        Map<String,Object> map2=new HashMap<String,Object>();
        map2.put("userID",CommonFunctions.getUserID(SelectFriendContentShowAty.this)+"");
        map2.put("sessionID",CommonFunctions.getSessionID(SelectFriendContentShowAty.this)+"");
        map2.put("contentID",contentID+"");
        map2.put("comment",comment);

        AddCommentTask task=new AddCommentTask(map2);
        task.execute(MyTask.url+"addComment");

    }

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

    class GetCommentTask extends MyTask{

        public GetCommentTask(Map<String, Object> map) {
            super(map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s==null||s.isEmpty()) {
                Toast.makeText(SelectFriendContentShowAty.this,"网络连接出现问题",Toast.LENGTH_LONG).show();
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
                if(arr!=null) {
                    System.out.println("data:" + arr.toString());
                    realList.clear();
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
                            map.put("comment", item.getString("comment"));
                            map.put("time", item.getString("time"));
                            map.put("name", item.getString("name"));
                            map.put("avatar",item.getString("avatar"));
                            realList.add(map);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }


            }else{
                CommonFunctions.showSimpleDialog(str,SelectFriendContentShowAty.this);
            }


            adapter.notifyDataSetChanged();
            mSwipeLayout.setRefreshing(false);

        }
    }


    class AddCommentTask extends MyTask{

        public AddCommentTask(Map<String, Object> map) {
            super(map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s==null||s.isEmpty()){
                Toast.makeText(SelectFriendContentShowAty.this, "网络连接出现问题", Toast.LENGTH_LONG).show();
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
            try {
                info = (JSONObject) jsonParser.nextValue();
                i=info.getInt("code");
                str=info.getString("info");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(i==200){

                CommonFunctions.showSimpleDialog(str,SelectFriendContentShowAty.this);
            }else{
                CommonFunctions.showSimpleDialog(str,SelectFriendContentShowAty.this);
            }

            try {
                getData();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();


        }
    }






}
