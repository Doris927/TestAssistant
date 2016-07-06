package com.example.administrator.testassistant;

import android.annotation.TargetApi;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设置界面
 */
public class settings extends ActionBarActivity implements View.OnClickListener {

    Button changeBg,changePW,memoBtn,logoutBtn;
    ImageView avatarIV;
    private final String KEY_AVATAR="avatar_path";
    private final String KEY_BACKGROUND="background_path";
    private ImageLoader imageLoader;
    EditText ipEt;
    Button openPushServiceBtn;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initView();//初始化



    }

    private void initView(){//////////初始化
        changeBg=(Button)findViewById(R.id.changeBgBtn);
        changePW=(Button)findViewById(R.id.changePWBtn);
        memoBtn=(Button)findViewById(R.id.memoBtn);
        logoutBtn=(Button)findViewById(R.id.logoutBtn);
        avatarIV=(ImageView)findViewById(R.id.avatar);
        ipEt=(EditText)findViewById(R.id.pushIpEt);
        openPushServiceBtn=(Button)findViewById(R.id.openPushService);

       //设置button的点击事件
        changeBg.setOnClickListener(this);
        changePW.setOnClickListener(this);
        memoBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        avatarIV.setOnClickListener(this);

        getAvatar();//获得头像
        getBackground();//获得背景

        //打开推送服务事件
        openPushServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip=ipEt.getText().toString();
                if(!ip.isEmpty()) {

                    getPushMessage(ip);
                }else{
                    CommonFunctions.showSimpleDialog("ip地址不能为空",settings.this);
                }
            }
        });
        //设置actionbar的title
        actionBar=(android.support.v7.app.ActionBar)getSupportActionBar();
        actionBar.setTitle("更多");

    }


    /**
     * button点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.changeBgBtn:
                changeBackground();
                break;
            case R.id.changePWBtn:
                changePassword();
                break;
            case R.id.memoBtn:
                setMemoBtn();
                break;
            case R.id.logoutBtn:
                logout();
                break;
            case R.id.avatar:
                changeAvatar();
                break;
        }
    }

    /**
     * 修改密码，跳转到修改密码的界面
     */
    private void changePassword(){
        Intent intent = new Intent();
        intent.setClass(settings.this,ChangePWAty.class);
        startActivity(intent) ;
    }

    /**
     * 注销
     */
    private void logout(){
        CommonFunctions.saveSessionID("",this);
        CommonFunctions.saveUserID(-1,this);
        finish();
        Intent intent = new Intent();
        intent.setClass(settings.this,MainActivity.class);
        startActivity(intent) ;
    }

    /*
    修改头像
     */
    private void changeAvatar(){//////修改头像

        Intent intent = new Intent();
                /* 开启Pictures画面Type设定为image */
        intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
        intent.setAction(Intent.ACTION_GET_CONTENT);
                /* 取得相片后返回本画面 */
        startActivityForResult(intent, 1);
    }

    /**
     * 修改背景
     */
    private void changeBackground(){
        Intent intent = new Intent();
                /* 开启Pictures画面Type设定为image */
        intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
        intent.setAction(Intent.ACTION_GET_CONTENT);
                /* 取得相片后返回本画面 */
        startActivityForResult(intent, 2);
    }

    /**
     * 备忘录
     */
    private void setMemoBtn(){
        Intent intent = new Intent();
        intent.setClass(settings.this,MemoList.class);
        startActivity(intent);
    }

    /**
     * 根据请求code，修改头像或者背景
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 1:////////////修改头像的
                if(resultCode==RESULT_OK){
                    Uri uri = data.getData();
                    Log.e("uri", uri.toString());
                    saveAvatar(uri.toString());
                    ContentResolver cr = this.getContentResolver();
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));

                /* 将Bitmap设定到ImageView */
                        avatarIV.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        Log.e("Exception", e.getMessage(),e);
                    }
                    String path=getRealFilePath(settings.this,uri);
                    System.out.println("path:"+path);
                    Map<String,Object> map2=new HashMap<String,Object>();
                    map2.put("userID",CommonFunctions.getUserID(settings.this)+"");
                    map2.put("sessionID",CommonFunctions.getSessionID(settings.this));
                    UploadAvatarTask task=new UploadAvatarTask(path,map2);
                    task.execute(UploadImageTask.url+"uploadAvatar");
                }

                break;
            case 2://///////////修改背景的

                if(resultCode==RESULT_OK){
                    Uri uri = data.getData();
                    Log.e("uri", uri.toString());
                    saveBackground(uri.toString());
                    ContentResolver cr = this.getContentResolver();
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                        Drawable drawable =new BitmapDrawable(bitmap);
                        this.getWindow().setBackgroundDrawable(drawable);

                    } catch (FileNotFoundException e) {
                        Log.e("Exception", e.getMessage(), e);
                    }
                }

                break;
        }


    }

    //保存头像
    private void saveAvatar(String uri){
        SharedPreferences.Editor editor=this.getSharedPreferences(settings.class.getName(), Context.MODE_PRIVATE).edit();

        editor.putString(KEY_AVATAR,uri);


        editor.commit();

    }

    /**
     * 将uri转为绝对路径
     * @param context
     * @param uri
     * @return
     */
    public static String getRealFilePath( final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 获得头像，从网络中
     */
    private void getAvatar(){
        SharedPreferences sp = this.getSharedPreferences(settings.class.getName(), Context.MODE_PRIVATE);
        String content = sp.getString(KEY_AVATAR, null);


            imageLoader=new ImageLoader(this);
            String path="http://greenteabitch.coding.io/"+content;
            imageLoader.DisplayImage(path,avatarIV);

    }

    /**
     * 保存背景
     * @param uri
     */
    private void saveBackground(String uri){
        SharedPreferences.Editor editor=this.getSharedPreferences(settings.class.getName(), Context.MODE_PRIVATE).edit();
        editor.putString(KEY_BACKGROUND,uri);

        editor.commit();
    }

    /**
     * 获得背景
     */
    private void getBackground(){
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

    /**
     * 上传头像的通信类
     */
    class UploadAvatarTask extends UploadImageTask{

        public UploadAvatarTask(String imagePath, final Map<String,Object> map) {
            super(imagePath,map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.isEmpty()||s==null) {
                Toast.makeText(settings.this, "网络连接出现问题", Toast.LENGTH_LONG).show();
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
                    saveAvatar(info.getString("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else{
                CommonFunctions.showSimpleDialog(str,settings.this);
            }



        }
    }

    /**
     * 打开推送服务
     * @param ip
     */
    private void getPushMessage(String ip) {
        String mDeviceID;
        mDeviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        SharedPreferences.Editor editor = getSharedPreferences(PushService.TAG, MODE_PRIVATE).edit();
        editor.putString(PushService.PREF_DEVICE_ID, mDeviceID);
        editor.commit();
        SharedPreferences p = getSharedPreferences(PushService.TAG, MODE_PRIVATE);
        boolean started = p.getBoolean(PushService.PREF_STARTED, false);
        if (!started){
            PushService.setIp(ip);
            PushService.actionStart(getApplicationContext());
        }else{
            CommonFunctions.showSimpleDialog("推送功能已经打开",settings.this);
        }
    }




}
