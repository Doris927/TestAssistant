package com.example.administrator.testassistant;

import android.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class AddFriendContentAty extends ActionBarActivity {

    private EditText editText;
    private Button saveBtn,cancelBtn;
    private String content;
    private android.support.v7.app.ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_content_aty);
        init();
    }

    //初始化
    private void init(){
        editText=(EditText)findViewById(R.id.addFriendContentEt);
        saveBtn=(Button)findViewById(R.id.saveFriendContentBtn);
        cancelBtn=(Button)findViewById(R.id.cancelFriendContentBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {//撤销事件
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {//保存事件
            @Override
            public void onClick(View v) {
                content=editText.getText().toString();
                if(content.isEmpty()){//状态内容为空
                    CommonFunctions.showSimpleDialog("心情内容不能为空",AddFriendContentAty.this);
                }else if(content.length()>140){//状态内容超过140字
                    CommonFunctions.showSimpleDialog("字数不能超过140字",AddFriendContentAty.this);
                }else{//发送添加好友请求
                    Map<String,Object> map=new HashMap<String,Object>();
                    map.put("userID",CommonFunctions.getUserID(AddFriendContentAty.this)+"");
                    map.put("sessionID", CommonFunctions.getSessionID(AddFriendContentAty.this));
                    map.put("content",content);
                    String jsonString= null;
                    try {
                        jsonString = CommonFunctions.getJson("addContent", map);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(jsonString==null||jsonString.isEmpty()) {
                        Toast.makeText(AddFriendContentAty.this, "网络连接出现问题", Toast.LENGTH_LONG).show();
                        return;
                    }
                    JSONTokener jsonParser = new JSONTokener(jsonString);
                    System.out.println(jsonString);
                    JSONObject info = null;
                    Integer i=0;
                    String str="";
                    try {
                        info = (JSONObject) jsonParser.nextValue();
                        i=info.getInt("code");
                        str=info.getString("info");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(i==200){//状态发布成功

                        finish();
                    }else{//发布失败
                        CommonFunctions.showSimpleDialog(str,AddFriendContentAty.this);

                    }
                }
            }
        });

        actionBar=getSupportActionBar();
        actionBar.setTitle("添加状态");
    }



}
