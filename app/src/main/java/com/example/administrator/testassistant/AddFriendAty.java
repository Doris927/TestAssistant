package com.example.administrator.testassistant;


import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class AddFriendAty extends ActionBarActivity {

    private Button addFriendBtn;
    private EditText addFriendName;
    private ListView lv;
    private String friendName;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_aty);
        init();
    }

    //初始化
    private void init(){

        addFriendBtn=(Button)findViewById(R.id.sureAddFriendBtn);
        addFriendName=(EditText)findViewById(R.id.addFriendNameEt);
        lv=(ListView)findViewById(R.id.friendListView);

        //添加好友事件
        addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendName=addFriendName.getText().toString();
                if(friendName.isEmpty()){//用户名为空
                    CommonFunctions.showSimpleDialog("好友名不能为空",AddFriendAty.this);
                }else{//发送添加好友请求
                    Map<String,Object> map=new HashMap<String,Object>();
                    map.put("userID",CommonFunctions.getUserID(AddFriendAty.this)+"");
                    map.put("sessionID", CommonFunctions.getSessionID(AddFriendAty.this));
                    map.put("friendName",friendName);
                    String jsonString= null;
                    try {
                        jsonString = CommonFunctions.getJson("addFriend", map);//获得返回的结果
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                     if(jsonString==null||jsonString.isEmpty()) {//网络连接出现问题
                        Toast.makeText(AddFriendAty.this, "网络连接出现问题", Toast.LENGTH_LONG).show();
                        return;
                    }

                    JSONTokener jsonParser = new JSONTokener(jsonString);//将JsonString转为JsonObject
                    System.out.println(jsonString);
                    JSONObject info = null;
                    Integer i=0;
                    String str="";
                    try {
                        info = (JSONObject) jsonParser.nextValue();
                        i=info.getInt("code");//获得code
                        str=info.getString("info");//获得提醒信息
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(i==200){//添加成功
                        CommonFunctions.showSimpleDialog(str,AddFriendAty.this);
                    }else{//添加失败
                        CommonFunctions.showSimpleDialog(str,AddFriendAty.this);
                    }
                }

            }
        });

        actionBar=getSupportActionBar();
        actionBar.setTitle("添加好友");//设置ActionBar的title
    }



}
