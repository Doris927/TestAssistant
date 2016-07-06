package com.example.administrator.testassistant;



import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TimePicker;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class alarmClock extends ActionBarActivity {

    private ImageButton addAlarmBtn;
    private ListView alarmListView;
    private ArrayAdapter<AlarmData> adapter;
    private static final String KEY_ALARM_LIST = "alarmList";
    private AlarmManager alarmManager;
    private ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_clock);

        //初始化
        addAlarmBtn=(ImageButton)findViewById(R.id.addAlarmBtn);
        alarmListView=(ListView)findViewById(R.id.alarmListView);
        alarmManager=(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        //设置Listview的适配器
        adapter=new ArrayAdapter<AlarmData>(this,android.R.layout.simple_list_item_1 );
        alarmListView.setAdapter(adapter);
        readSavedAlarmList();//读取闹钟列表

        //添加闹钟事件
        addAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAlarm();
            }
        });

        //闹钟列表点击事件
        alarmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(alarmClock.this).setTitle("操作选项").setItems(new CharSequence[]{"删除"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        deleteAlarm(position);
                                        break;
                                }

                            }
                        }).setNegativeButton("取消", null).show();
            }
        });

        //设置actionbar的title
        actionBar=(ActionBar)getSupportActionBar();
        actionBar.setTitle("闹钟");

    }

    //添加闹钟
    private void addAlarm(){
        //获得当前时间
        Calendar c=Calendar.getInstance();

        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            Boolean tap = true;//防止button被多次点击，闹钟重复添加
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if(tap){
                    //获得设置的时间
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    //获得当前时间
                    Calendar currentTime = Calendar.getInstance();

                    //早于当前时间则闹钟推迟到第二天
                    if (calendar.getTimeInMillis()<=currentTime.getTimeInMillis()) {
                        calendar.setTimeInMillis(calendar.getTimeInMillis()+24*60*60*1000);
                    }

                    AlarmData ad = new AlarmData(calendar.getTimeInMillis());
                    adapter.add(ad);//添加闹钟到列表
                    //设置闹钟
                    alarmManager.set(AlarmManager.RTC_WAKEUP,
                            ad.getTime(),
                            PendingIntent.getBroadcast(alarmClock.this, ad.getID(), new Intent(alarmClock.this, AlarmReceiver.class), 0));
                    saveAlarmList();//保存闹钟列表


                    tap=false;

                }

            }
        },c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),true).show();
    }

    /*
    保存闹钟列表到SharedPreferences
     */
    private void saveAlarmList(){
        SharedPreferences.Editor editor=this.getSharedPreferences(alarmClock.class.getName(), Context.MODE_PRIVATE).edit();
        StringBuffer stringBuffer=new StringBuffer();
        for(int i=0;i<adapter.getCount();i++){
            stringBuffer.append(adapter.getItem(i).getTime()).append(",");
        }
        if(stringBuffer.length()>1){
            String content=stringBuffer.toString().substring(0, stringBuffer.length() - 1);
            editor.putString(KEY_ALARM_LIST,content);
            System.out.println(content);
        }else{
            editor.putString(KEY_ALARM_LIST,null);
        }

        editor.commit();
    }

    /*
    获得闹钟列表
     */
    private void readSavedAlarmList(){
        SharedPreferences sp = this.getSharedPreferences(alarmClock.class.getName(), Context.MODE_PRIVATE);
        String content = sp.getString(KEY_ALARM_LIST, null);

        if (content!=null) {//将闹钟列表显示到listview中
            String[] timeStrings = content.split(",");
            for (String string : timeStrings) {
                adapter.add(new AlarmData(Long.parseLong(string)));
            }
        }

    }

    /*
    删除闹钟
     */
    private void deleteAlarm(int position){
        AlarmData ad=adapter.getItem(position);
        adapter.remove(ad);
        saveAlarmList();//保存闹钟列表
        //取消闹钟
        alarmManager.cancel(PendingIntent.getBroadcast(this, ad.getID(),
                new Intent(this,AlarmReceiver.class),0));
    }

    //闹钟的类
    private static class AlarmData{
        public AlarmData(long time){
            this.Time=time;
            date=new Date(time);
            SimpleDateFormat format=new SimpleDateFormat("MM/dd HH:mm:ss");
            timeLabel=format.format(date);
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

        @Override
        public String toString(){
            return  getTimeLabel();
        }
        private String timeLabel="";
        private long Time=0;
        private Date date;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBackground();//获得背景图片
    }

    /*
    获得背景图片
     */
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
        getMenuInflater().inflate(R.menu.menu_alarm_clock, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*
    菜单点击事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.add_alarm_item:

                addAlarm();

            break;

            default:
                break;
        }
        return true;
    }


}
