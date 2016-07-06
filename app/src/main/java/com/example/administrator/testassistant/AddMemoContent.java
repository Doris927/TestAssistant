package com.example.administrator.testassistant;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class AddMemoContent extends ActionBarActivity implements View.OnClickListener{

    private Button cancelBtn,saveBtn;
    private EditText editText;
    private MemosDB memosDB;
    private SQLiteDatabase dbWriter;
    private TimePicker timePicker;
    private DatePicker datePicker;
    private AlarmManager alarmManager;
    private int alarmId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memo_content);
        init();
    }

    //初始化
    private void init(){
        cancelBtn=(Button)findViewById(R.id.cancelMemoBtn);
        saveBtn=(Button)findViewById(R.id.saveMemoBtn);
        editText=(EditText)findViewById(R.id.memoEditText);
        timePicker=(TimePicker)findViewById(R.id.memoTimePicker);
        memosDB=new MemosDB(this);
        dbWriter=memosDB.getWritableDatabase();
        timePicker.setIs24HourView(true);
        datePicker=(DatePicker)findViewById(R.id.memoDatePicker);
        datePicker.setMinDate((new Date(System.currentTimeMillis())).getDate());
        alarmManager=(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        saveBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        resizePikcer(timePicker);//调整timePicker和datePicker的大小
        resizePikcer(datePicker);
    }




    @Override
    public void onClick(View v) {//Button点击事件
        switch (v.getId()){
            case R.id.saveMemoBtn://保存事件
                addDB();

                break;
            case R.id.cancelMemoBtn://取消事件
                finish();
                break;
            default:break;
        }
    }

    private void addDB(){//添加备忘录



        //SQLiteDatabase dbReader=memosDB.getReadableDatabase();


        //获得设置的时间
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,datePicker.getYear());
        calendar.set(Calendar.MONTH,datePicker.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH,datePicker.getDayOfMonth());
        calendar.set(Calendar.HOUR_OF_DAY,timePicker.getCurrentHour());
        calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Calendar currentTime = Calendar.getInstance();//获得当前时间
        if (calendar.getTimeInMillis()<=currentTime.getTimeInMillis()) {//如果设置事件早于当前时间则添加失败
            Dialog alertDialog = new AlertDialog.Builder(this).
                    setTitle("提醒").
                    setMessage("输入时间不能早于当前时间").
                    setIcon(R.drawable.ic_launcher).
                    setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).
                    create();

            alertDialog.show();
            return;

        }else{//添加成功
            Intent memoReceiver=new Intent(AddMemoContent.this,MemoReceiver.class);
            memoReceiver.putExtra("content",editText.getText().toString());
            //设置闹钟
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    PendingIntent.getBroadcast(AddMemoContent.this,getID(),memoReceiver, 0));
        }

        alarmId=getID();
        ContentValues cv = new ContentValues();
        cv.put(MemosDB.CONTENT, editText.getText().toString());
        cv.put(MemosDB.TIME, getTime());
        cv.put(MemosDB.ALARM_ID,alarmId);

        dbWriter.insert(MemosDB.TABLE_NAME,null,cv);

        finish();
    }

    public String  getTime(){//获得时间

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,datePicker.getYear());
        calendar.set(Calendar.MONTH,datePicker.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH,datePicker.getDayOfMonth());
        calendar.set(Calendar.HOUR_OF_DAY,timePicker.getCurrentHour());
        calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);


        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


       return format.format(calendar.getTimeInMillis());
    }

    public int getID(){//获得id
        Calendar currentTime= Calendar.getInstance();
        return  (int)(currentTime.getTimeInMillis()/1000/60);

    }

    /**
     * 调整FrameLayout大小
     * @param tp
     */
    private void resizePikcer(FrameLayout tp){//调整Picker的大小
        List<NumberPicker> npList = findNumberPicker(tp);
        for(NumberPicker np:npList){
            resizeNumberPicker(np);//调整内部每一个NumberPicker的大小
        }
    }

    /**
     * 得到viewGroup里面的numberpicker组件
     * @param viewGroup
     * @return
     */
    private List<NumberPicker> findNumberPicker(ViewGroup viewGroup){
        List<NumberPicker> npList = new ArrayList<NumberPicker>();
        View child = null;
        if(null != viewGroup){
            for(int i = 0;i<viewGroup.getChildCount();i++){
                child = viewGroup.getChildAt(i);
                if(child instanceof NumberPicker){
                    npList.add((NumberPicker)child);
                }
                else if(child instanceof LinearLayout){
                    List<NumberPicker> result = findNumberPicker((ViewGroup)child);
                    if(result.size()>0){
                        return result;
                    }
                }
            }
        }
        return npList;
    }

    /*
		 * 调整numberpicker大小
		 */
    private void resizeNumberPicker(NumberPicker np){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(70,  LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 0, 10, 0);
        np.setLayoutParams(params);
    }
}
