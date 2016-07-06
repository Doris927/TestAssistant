package com.example.administrator.testassistant;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;


/**
 * 闹钟
 */
public class PlayAlarmAty extends Activity {

    private MediaPlayer mp;//音乐播放器
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_play_alarm_aty);
        button=(Button)findViewById(R.id.closeAlarm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mp=MediaPlayer.create(this,R.raw.lastchristmas);
        mp.start();//播放音乐

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    /*
    退出时停止播放音乐
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.stop();
        mp.release();

    }
}
