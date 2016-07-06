package com.example.administrator.testassistant;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/4/10.
 * 记事本列表适配器
 */
public class MyAdapter extends BaseAdapter {

    private Context context;
    private Cursor cursor;
    private LinearLayout layout;
    public MyAdapter(Context context,Cursor cursor){
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
        //绑定数据
        LayoutInflater inflater=LayoutInflater.from(context);
        layout=(LinearLayout)inflater.inflate(R.layout.notescell,null);
        TextView contentTv=(TextView)layout.findViewById(R.id.listContent);
        TextView timeTv=(TextView)layout.findViewById(R.id.listTime);
        ImageView imageIv=(ImageView)layout.findViewById(R.id.listImg);
        ImageView video=(ImageView)layout.findViewById(R.id.listVideo);
        cursor.moveToPosition(position);
        String content=cursor.getString(cursor.getColumnIndex("content"));
        String time=cursor.getString(cursor.getColumnIndex("time"));
        String url=cursor.getString(cursor.getColumnIndex("path"));
        contentTv.setText(content);
        timeTv.setText(time);
        imageIv.setImageBitmap(getImgThumbnail(url,200,200));
        return layout;
    }

    //获得图片的缩略图
    public Bitmap getImgThumbnail(String uri,int width,int height){////获取图片缩略图
        Bitmap bitmap=null;
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        bitmap=BitmapFactory.decodeFile(uri,options);
        options.inJustDecodeBounds=false;
        int beWidth=options.outWidth/width;
        int beHeight=options.outHeight/height;
        int be=1;
        if(beWidth>beHeight){
            be=beWidth;
        }else {
            be=beHeight;
        }
        if (be<=0){
            be=1;
        }
        options.inSampleSize=be;
        bitmap=BitmapFactory.decodeFile(uri,options);
        bitmap= ThumbnailUtils.extractThumbnail(bitmap,width,height,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
}
