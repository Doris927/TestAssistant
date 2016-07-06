package com.example.administrator.testassistant;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
加载页面
 */
public class LoadActivity extends ActionBarActivity {

    private ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        //初始化
        actionBar=getSupportActionBar();
        actionBar.hide();
            //检验网络连接，若有网络则检验登录状态
            if (CommonFunctions.isNetworkConnected(this)) {
                checkLogin();
            }else{
                Toast.makeText(this,"当前无网络连接",Toast.LENGTH_LONG).show();
            }





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_load, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //自动登录，检验登录信息是否过期
    private void checkLogin() {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userID", CommonFunctions.getUserID(LoadActivity.this) + "");
        map.put("sessionID", CommonFunctions.getSessionID(LoadActivity.this));
        CheckLoginTask task = new CheckLoginTask(map);
        task.execute(MyTask.url + "checkLogin");
    }

    //自动登录的通信类
    class CheckLoginTask extends MyTask{

        public CheckLoginTask(Map<String, Object> map) {
            super(map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s==null||s.isEmpty()) {
                Toast.makeText(LoadActivity.this, "网络连接出现问题", Toast.LENGTH_LONG).show();
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


            try {
                info = (JSONObject) jsonParser.nextValue();
                i=info.getInt("code");
                str=info.getString("info");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(i==200){
                //getPushMessage();
                Intent intent = new Intent();
                intent.setClass(LoadActivity.this,index.class);
                startActivity(intent) ;
                finish();

            }else{

                Intent intent = new Intent();
                intent.setClass(LoadActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

            }



        }
    }

}
