package com.example.administrator.testassistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import java.util.concurrent.ExecutionException;

/**
 * 注册的界面
 */
public class Register extends Activity {

    private Button submitBtn;
    private EditText unameEt,upasswordEt,upasswordEt2;
    private String name,password1,password2;
    private String str;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    /**
     * 初始化
     */
   private void init(){

       submitBtn=(Button)findViewById(R.id.registerSureButton);
       unameEt=(EditText)findViewById(R.id.registerUnameEt);
       upasswordEt=(EditText)findViewById(R.id.registerUpasswordEt);
       upasswordEt2=(EditText)findViewById(R.id.registerUpasswordEt2);
       //提交事件
       submitBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

                   register();

           }
       });

   }

    //注册
    private void register(){
        name=unameEt.getText().toString();
        password1=upasswordEt.getText().toString();
        password2=upasswordEt2.getText().toString();

        if (name.isEmpty()){
            str="用户名为空";
            CommonFunctions.showSimpleDialog(str,Register.this);
        }else if (password1.isEmpty()){
            str="密码为空";
            CommonFunctions.showSimpleDialog(str,Register.this);
        }else if (!password1.equals(password2)){
            str="两次输入的密码不一致";
            CommonFunctions.showSimpleDialog(str,Register.this);
        }else{
            if (CommonFunctions.isNetworkConnected(this)) {
                submit();
            }else{
                Toast.makeText(this, "当前无网络连接", Toast.LENGTH_LONG).show();
            }

        }
    }

    //发送注册请求
    public  void submit() {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uname", name);
        map.put("upassword", password1);
        RegisterTask task=new RegisterTask(map);
        task.execute(MyTask.url+"register");
    }


    /**
     * 注册通信类
     */
    class RegisterTask extends MyTask{

        public RegisterTask(Map<String, Object> map) {
            super(map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.isEmpty()||s==null) {
                Toast.makeText(Register.this,"网络连接出现问题",Toast.LENGTH_LONG).show();
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

            try {
                info = (JSONObject) jsonParser.nextValue();
                i=info.getInt("code");
                str=info.getString("info");
                if(i==200){
                    //getPushMessage();
                    CommonFunctions.saveSessionID(info.getString("sessionID"),Register.this);
                    System.out.println( CommonFunctions.getSessionID(Register.this));
                    CommonFunctions.saveUserID(info.getInt("userID"),Register.this);
                    System.out.println( CommonFunctions.getUserID(Register.this));
                    Intent intent = new Intent();
                    intent.setClass(Register.this,index.class);
                    startActivity(intent) ;
                }else{
                    CommonFunctions.showSimpleDialog(str,Register.this);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }




        }
    }


}
