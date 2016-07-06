package com.example.administrator.testassistant;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 显示记事本的具体内容
 */
public class SelectNoteShow extends ActionBarActivity implements View.OnClickListener {

    private Button deleteBtn,backBtn;
    private ImageView imgView;
    private TextView textView;
    private NotesDB notesDB;
    private SQLiteDatabase dbWriter;
    private ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_note_show);

        //初始化
        initView();

        //获得图片地址
        if(getIntent().getStringExtra(NotesDB.PATH).equals("null")){
            imgView.setVisibility(View.GONE);
        }else{
            imgView.setVisibility(View.VISIBLE);
        }
        //获得记事本文字内容
        textView.setText(getIntent().getStringExtra(NotesDB.CONTENT));

        //显示图片
        Bitmap bitmap= BitmapFactory.decodeFile(getIntent().getStringExtra(NotesDB.PATH));
        imgView.setImageBitmap(bitmap);

    }

    /**
     * 初始化
     */
    public void initView(){
        deleteBtn=(Button)findViewById(R.id.noteDeleteBtn);
        backBtn=(Button)findViewById(R.id.noteBackBtn);
        imgView=(ImageView)findViewById(R.id.selectImgView);
        textView=(TextView)findViewById(R.id.selectTextView);
        notesDB=new NotesDB(this);
        dbWriter=notesDB.getWritableDatabase();
        deleteBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        actionBar=getSupportActionBar();
        actionBar.setTitle("记事本");
    }


    /**
     * button点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.noteDeleteBtn:
                deleteData();
                finish();
                break;

            case R.id.noteBackBtn:
                finish();
                break;
        }
    }


    /**
     * 删除记事本
     */
    public void deleteData(){
        dbWriter.delete(NotesDB.TABLE_NAME,
                NotesDB.ID+"="+getIntent().getIntExtra(NotesDB.ID,0),null);
    }
}
