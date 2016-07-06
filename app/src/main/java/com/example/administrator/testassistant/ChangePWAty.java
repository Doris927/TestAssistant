package com.example.administrator.testassistant;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChangePWAty extends ActionBarActivity {

    private EditText originalPWEt,newPW1Et,newPW2Et;
    private Button changePWBtn;
    private ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwaty);

        //初始化
        originalPWEt=(EditText)findViewById(R.id.originalPasswordEt);
        newPW1Et=(EditText)findViewById(R.id.changePasswordEt);
        newPW2Et=(EditText)findViewById(R.id.changePasswordEt2);
        changePWBtn=(Button)findViewById(R.id.changePasswordSureButton);
        //新密码提交事件
        changePWBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String originalPw=originalPWEt.getText().toString();
                String pw1=newPW1Et.getText().toString();
                String pw2=newPW2Et.getText().toString();
                if (originalPw.isEmpty()){//原密码为空
                    CommonFunctions.showSimpleDialog("请输入原密码",ChangePWAty.this);
                }else if (pw1.isEmpty()){//新密码为空
                    CommonFunctions.showSimpleDialog("请输入新的密码",ChangePWAty.this);
                }else if (pw2.isEmpty()){//重复密码为空
                    CommonFunctions.showSimpleDialog("请重复一次新密码",ChangePWAty.this);
                }else if (!pw1.equals(pw2)){//重复密码不同
                    CommonFunctions.showSimpleDialog("两次密码输入不一致",ChangePWAty.this);
                }else {//发送修改密码请求
                    Map<String,Object> map2=new HashMap<String,Object>();
                    map2.put("userID",CommonFunctions.getUserID(ChangePWAty.this)+"");
                    map2.put("sessionID",CommonFunctions.getSessionID(ChangePWAty.this));
                    map2.put("originalPassword",originalPw);
                    map2.put("newPassword",pw1);
                    ChangePWTask task=new ChangePWTask(map2);
                    task.execute(MyTask.url+"changePassword");
                }
            }
        });

        //设置actionbar的title
        actionBar=getSupportActionBar();
        actionBar.setTitle("修改密码");
    }



    //修改密码的通信类
    class ChangePWTask extends MyTask{

        public ChangePWTask(Map<String, Object> map) {
            super(map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s==null||s.isEmpty()){
                Toast.makeText(ChangePWAty.this, "网络连接出现问题", Toast.LENGTH_LONG).show();
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
                CommonFunctions.showSimpleDialog(str,ChangePWAty.this);
                finish();
            }else{
                CommonFunctions.showSimpleDialog(str,ChangePWAty.this);
            }



        }
    }
}
