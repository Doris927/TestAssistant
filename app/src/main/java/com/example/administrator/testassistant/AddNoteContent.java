package com.example.administrator.testassistant;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AddNoteContent extends ActionBarActivity implements View.OnClickListener {

    private  String flag;
    private Button cancelBtn,saveBtn;
    private EditText editText;
    private ImageView imgView;
    private VideoView videoView;
    private NotesDB notesDB;
    private SQLiteDatabase dbWriter;
    private File phoneFile;
    private android.support.v7.app.ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note_content);
        flag=getIntent().getStringExtra("flag");//获得flag
        initView();

        notesDB=new NotesDB(this);
        dbWriter=notesDB.getWritableDatabase();
    }

    //初始化
    private void initView(){
        cancelBtn=(Button)findViewById(R.id.cancelContentBtn);
        saveBtn=(Button)findViewById(R.id.saveContentBtn);
        editText=(EditText)findViewById(R.id.contentEditText);
        imgView=(ImageView)findViewById(R.id.contentImgView);
        videoView=(VideoView)findViewById(R.id.contentVideoView);
        cancelBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        actionBar=getSupportActionBar();
        //添加文字
        if (flag.equals("1")){
            imgView.setVisibility(View.GONE);//ImageView设置为不可见

            actionBar.setTitle("添加文字");//设置actionbar的title
        }
        //添加图文
        if (flag.equals("2")){
            imgView.setVisibility(View.VISIBLE);//ImageView设置为可见

            Intent iimg = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//跳转到拍摄图片
            phoneFile = new File(Environment.getExternalStorageDirectory()//获得图片
                    .getAbsoluteFile() + "/" + getTime() + ".jpg");
            iimg.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(phoneFile));//将图片地址返回
            startActivityForResult(iimg, 1);
            actionBar.setTitle("添加图文");//设置actionbar的title
        }


    }


    /*
    点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.saveContentBtn://保存事件
                addDB();
                finish();
                break;
            case R.id.cancelContentBtn://取消事件
                finish();
                break;
        }
    }

    /*
    添加记事本内容到数据库
     */
    public void addDB(){
        ContentValues cv = new ContentValues();
        cv.put(NotesDB.CONTENT, editText.getText().toString());
        cv.put(NotesDB.TIME, getTime());
        cv.put(NotesDB.PATH, phoneFile + "");
        dbWriter.insert(NotesDB.TABLE_NAME,null,cv);//保存至数据库
    }

    /*
    获得时间
     */
    public String  getTime(){
        SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date curDate=new Date();
        String str=format.format(curDate);
        return str;
    }

    /*
    处理拍摄图片的返回值，获得图片
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Bitmap bitmap = BitmapFactory.decodeFile(phoneFile
                    .getAbsolutePath());
            imgView.setImageBitmap(bitmap);
        }

    }
}
