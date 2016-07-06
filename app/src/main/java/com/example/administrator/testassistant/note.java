package com.example.administrator.testassistant;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
记事本列表界面
 */
public class note extends ActionBarActivity implements View.OnClickListener {


    private SQLiteDatabase DBWriter;
    private Button textBtn,imgBtn,videoBtn;
    private ListView lv;
    private Intent intent;
    private MyAdapter myAdapter;
    private NotesDB notesDB;
    private SQLiteDatabase deReader;
    private Cursor cursor;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        initView();//初始化
    }

    /**
     * 初始化
     */
    public void initView(){
        lv=(ListView)findViewById(R.id.notesListView);
        textBtn=(Button)findViewById(R.id.textBtn);
        imgBtn=(Button)findViewById(R.id.imgBtn);
        videoBtn=(Button)findViewById(R.id.videoBtn);
        textBtn.setOnClickListener(this);
        imgBtn.setOnClickListener(this);
        videoBtn.setOnClickListener(this);
        notesDB=new NotesDB(this);
        deReader=notesDB.getReadableDatabase();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);


                Intent i = new Intent(note.this, SelectNoteShow.class);
                i.putExtra(NotesDB.ID,
                        cursor.getInt(cursor.getColumnIndex(NotesDB.ID)));
                i.putExtra(NotesDB.CONTENT, cursor.getString(cursor
                        .getColumnIndex(NotesDB.CONTENT)));
                i.putExtra(NotesDB.TIME,
                        cursor.getString(cursor.getColumnIndex(NotesDB.TIME)));
                i.putExtra(NotesDB.PATH,
                        cursor.getString(cursor.getColumnIndex(NotesDB.PATH)));

                startActivity(i);

            }
        });

        actionBar=(ActionBar)getSupportActionBar();
        actionBar.setTitle("记事本");
    }

    //添加到数据库，测试用
    public  void addDB(){
        ContentValues cv=new ContentValues();
        cv.put(NotesDB.CONTENT,"hello notes");
        cv.put(NotesDB.TIME,getTime());
        DBWriter.insert(NotesDB.TABLE_NAME,null,cv);
    }

    /**
     * 获得备忘录列表
     */
    public void selectDB(){
        cursor=deReader.query(NotesDB.TABLE_NAME,null,null,null,null,null,null);
        myAdapter=new MyAdapter(this,cursor);
        lv.setAdapter(myAdapter);
    }

    /**
     * 获得时间
     * @return
     */
    public String  getTime(){
        SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date curDate=new Date();
        String str=format.format(curDate);
        return str;
    }


    /**
     * 设置点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        intent=new Intent(this,AddNoteContent.class);
        switch (v.getId()){
            case R.id.textBtn:
                intent.putExtra("flag","1");
                startActivity(intent);
                break;
            case R.id.imgBtn:
                intent.putExtra("flag","2");
                startActivity(intent);
                break;

        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        selectDB();
        getBackground();
    }

    private void getBackground(){
        final String KEY_BACKGROUND="background_path";
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        intent=new Intent(this,AddNoteContent.class);
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.add_text_note_item:

                intent.putExtra("flag","1");
                startActivity(intent);

                break;
            case R.id.add_picture_note_item:
                intent.putExtra("flag","2");
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

}
