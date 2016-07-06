package com.example.administrator.testassistant;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/4/12.
 * 备忘录列表的适配器
 */
public class MyAdapter2 extends BaseAdapter {

    private Context context;
    private Cursor cursor;
    private LinearLayout layout;
    public MyAdapter2(Context context,Cursor cursor){
        this.context=context;
        this.cursor=cursor;

    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return cursor.getPosition();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=LayoutInflater.from(context);
        layout=(LinearLayout)inflater.inflate(R.layout.memo_item,null);
        TextView contentTv=(TextView)layout.findViewById(R.id.memoContent);
        TextView timeTv=(TextView)layout.findViewById(R.id.memoTime);

        cursor.moveToPosition(position);
        String content=cursor.getString(cursor.getColumnIndex("content"));
        String time=cursor.getString(cursor.getColumnIndex("time"));

        contentTv.setText(content);
        timeTv.setText(time);

        return layout;
    }

}
