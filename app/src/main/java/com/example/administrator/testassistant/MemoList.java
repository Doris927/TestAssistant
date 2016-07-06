package com.example.administrator.testassistant;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MemoList extends ActionBarActivity {

    private Button addMemoBtn;
    private ListView memoListView;
    private static final String KEY_ALARM_LIST = "memoList";
    private MemosDB memosDB;
    private SQLiteDatabase dbReader;
    private Cursor cursor;
    MyAdapter2 myAdapter;
    private SQLiteDatabase dbWriter;
    private AlarmManager alarmManager;
    private ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_list);
        init();//初始化
    }

    /*
    初始化
     */
    private void init(){
        addMemoBtn=(Button)findViewById(R.id.addMemoBtn);
        memoListView=(ListView)findViewById(R.id.memoListView);
        alarmManager=(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        //添加备忘录事件
        addMemoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MemoList.this,AddMemoContent.class);
                startActivity(intent) ;
            }
        });

        //获得可读可写的数据库
        memosDB=new MemosDB(this);
        dbReader=memosDB.getReadableDatabase();
        dbWriter=memosDB.getWritableDatabase();

        //设置备忘录列表点击事件
        memoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                new AlertDialog.Builder(MemoList.this).setTitle("操作选项").setItems(new CharSequence[]{"删除"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        int id=cursor.getInt(cursor.getColumnIndex(NotesDB.ID));
                                        delMemo(id);
                                        break;
                                }

                            }
                        }).setNegativeButton("取消", null).show();

            }
        });

        //设置actionbar的title
        actionBar=getSupportActionBar();
        actionBar.setTitle("备忘录");

    }





    //备忘录的类
    private static class MemoData{
        public MemoData(String str,long time){
            this.Time=time;
            date=new Date(time);
            SimpleDateFormat format=new SimpleDateFormat("MM/dd HH:mm:ss");
            timeLabel=format.format(date);
            content=str;
        }

        public String getTimeLabel(){
            return timeLabel;
        }

        public long getTime() {
            return Time;
        }

        public int getID(){
            return  (int)(getTime()/1000/60);
        }

        public String getContent(){
            return  content;
        }

        @Override
        public String toString(){
            return  getTimeLabel();
        }
        private String timeLabel="";
        private long Time=0;
        private String content="";
        private Date date;
    }

    @Override
    protected void onResume(){
        super.onResume();
        selectDB();
    }

    //获得备忘录列表
    public void selectDB(){
        cursor=dbReader.query(MemosDB.TABLE_NAME,null,null,null,null,null,null);
        myAdapter=new MyAdapter2(this,cursor);
        memoListView.setAdapter(myAdapter);
    }

    //删除备忘录
    private void delMemo(int id){
        dbWriter.delete(MemosDB.TABLE_NAME,
                MemosDB.ID+"="+id,null);
        alarmManager.cancel(PendingIntent.getBroadcast(this,id,
                new Intent(this, MemoReceiver.class), 0));
        selectDB();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_memo_list, menu);
        return super.onCreateOptionsMenu(menu);
    }



}
