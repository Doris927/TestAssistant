package com.example.administrator.testassistant;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;


public class PlayMemo extends ActionBarActivity {


    TextView textView;
    Button button;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
       setContentView(R.layout.activity_play_memo);
        String content=getIntent().getStringExtra("content");
        if (content.isEmpty()){
            content="没有提醒内容";
        }
        textView=(TextView)findViewById(R.id.playContentTextView);
        textView.setText(content);
        button=(Button)findViewById(R.id.closeMemo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        actionBar=getSupportActionBar();
        actionBar.setTitle("备忘录");

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
