package com.example.administrator.testassistant;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by Administrator on 2015/4/12.
 * 备忘录广播接受类
 */
public class MemoReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("备忘录执行了");
        String str=intent.getStringExtra("content");

        Toast.makeText(context,str, Toast.LENGTH_LONG).show();

        //弹出备忘录
        Intent i=new Intent(context,PlayMemo.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("content",str);
        context.startActivity(i);
    }
}
