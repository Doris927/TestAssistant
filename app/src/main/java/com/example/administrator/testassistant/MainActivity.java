package com.example.administrator.testassistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.mqtt.MqttException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


/*
登录界面
 */
public class MainActivity extends Activity {

    EditText unameEt,upasswordEt;
    Button loginbtn,registerbtn;

    String inputUname,inputUpassword;

    private SessionProcess sessionProcess;
    private CommonFunctions commonFunctions;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }





        sessionProcess=new SessionProcess(this);
        commonFunctions =new CommonFunctions();





        unameEt=(EditText)findViewById(R.id.unameTextEdit);
        upasswordEt=(EditText)findViewById(R.id.upasswordTextEdit);
        loginbtn=(Button)findViewById(R.id.loginButton);
        registerbtn=(Button)findViewById(R.id.registerButton);



        //新用户注册跳转
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent intent = new Intent();
                intent.setClass(MainActivity.this,Register.class);
                startActivity(intent) ;
            }
        });

        //用户登录
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputUname=unameEt.getText().toString();
                inputUpassword=upasswordEt.getText().toString();

                if(inputUname.isEmpty()){//用户名为空
                   CommonFunctions.showSimpleDialog("用户名为空",MainActivity.this);

                }else if(inputUpassword.isEmpty()){//密码为空
                    CommonFunctions.showSimpleDialog("密码为空",MainActivity.this);
                }else{//发送登录请求


                    //readNet(url+"login");

                    // 设置HTTP POST请求参数必须用NameValuePair对象

//                    //登录成功页面跳转
//                    Intent intent = new Intent();
//                    intent.setClass(MainActivity.this,index.class);
//                    startActivity(intent) ;

                        login();

                }
            }
        });
    }

    /*
    登录
     */
    private void login() {
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("uname",inputUname);
        map.put("upassword", inputUpassword);
       LoginTask task=new LoginTask(map);
        task.execute(MyTask.url+"login");
    }


//    public  void readNet(String url){
//
//        new AsyncTask<String, Void, String>() {
//            @Override
//            protected String doInBackground(String... params) {
//                String urlString=params[0];
//                HttpPost post=new HttpPost(urlString);
//                List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
//                list.add(new BasicNameValuePair("uname",inputUname));
//                list.add(new BasicNameValuePair("upassword", inputUpassword));
//                try {
//                    post.setEntity(new UrlEncodedFormEntity(list));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//
//                try {
//
//                    HttpResponse response=client.execute(post);
//                    String value= EntityUtils.toString(response.getEntity());
//
//                    return value;
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(String result) {
//                JSONTokener jsonParser = new JSONTokener(result);
//                JSONObject info = null;
//                Integer i=0;
//                String str="";
//                try {
//                    info = (JSONObject) jsonParser.nextValue();
//                    i=info.getInt("code");
//                    str=info.getString("info");
//                    sessionProcess.saveSessionID(info.getString("sessionID"));
//                    System.out.println(sessionProcess.getSessionID());
//                    sessionProcess.saveUserID(info.getInt("userID"));
//                    System.out.println(sessionProcess.getUserID());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//                if(i==200){
//
//                   Intent intent = new Intent();
//                    intent.setClass(MainActivity.this,index.class);
//                    startActivity(intent) ;
//               }else{
//                   Dialog alertDialog = new AlertDialog.Builder(MainActivity.this).
//                           setTitle("提醒").
//                           setMessage(str).
//                           setIcon(R.drawable.ic_launcher).
//                           setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                               @Override
//                               public void onClick(DialogInterface dialog, int which) {
//
//                               }
//                           }).
//                           create();
//
//                   alertDialog.show();
//               }
//            }
//
//        }.execute(url);
//
//    }




//        new AsyncTask<String, Void, String>() {
//            @Override
//            protected String doInBackground(String... params) {
//                String urlString=params[0];
//                HttpPost post=new HttpPost(urlString);
//                List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
//                list.add(new BasicNameValuePair("userID",sessionProcess.getUserID()+""));
//                list.add(new BasicNameValuePair("sessionID",sessionProcess.getSessionID()));
//                try {
//                    post.setEntity(new UrlEncodedFormEntity(list));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//
//                try {
//
//                    HttpResponse response=client.execute(post);
//                    String value= EntityUtils.toString(response.getEntity());
//
//                    return value;
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(String result) {
//                JSONTokener jsonParser = new JSONTokener(result);
//                JSONObject info = null;
//                Integer i=0;
//                String str="";
//                try {
//                    info = (JSONObject) jsonParser.nextValue();
//                    i=info.getInt("code");
//                    str=info.getString("info");
//                    sessionProcess.saveSessionID(info.getString("sessionID"));
//                    System.out.println(sessionProcess.getSessionID());
//                    sessionProcess.saveUserID(info.getInt("userID"));
//                    System.out.println(sessionProcess.getUserID());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//                if(i==200){
//
//                    Intent intent = new Intent();
//                    intent.setClass(MainActivity.this,index.class);
//                    startActivity(intent) ;
//                }else{
//                    Dialog alertDialog = new AlertDialog.Builder(MainActivity.this).
//                            setTitle("提醒").
//                            setMessage(str).
//                            setIcon(R.drawable.ic_launcher).
//                            setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                }
//                            }).
//                            create();
//
//                    alertDialog.show();
//                }
//            }
//
//        }.execute(url+action);



    @Override
    protected void onResume() {
        super.onResume();

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

//        SharedPreferences p = getSharedPreferences(PushService.TAG, MODE_PRIVATE);
//        boolean started = p.getBoolean(PushService.PREF_STARTED, false);
//        if (started){
//
//            PushService.actionStop(getApplicationContext());
//        }
    }




    /*
    登录的通信类
     */
    class LoginTask extends MyTask{

        public LoginTask(Map<String, Object> map) {
            super(map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s==null||s.isEmpty()) {
                Toast.makeText(MainActivity.this,"网络连接出现问题",Toast.LENGTH_LONG).show();
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
                sessionProcess.saveSessionID(info.getString("sessionID"));
                System.out.println(sessionProcess.getSessionID());
                sessionProcess.saveUserID(info.getInt("userID"));
                System.out.println(sessionProcess.getUserID());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(i==200){
                //getPushMessage();

                Intent intent = new Intent();
                intent.setClass(MainActivity.this,index.class);
                startActivity(intent) ;
            }else{
                CommonFunctions.showSimpleDialog(str,MainActivity.this);
            }



        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}
