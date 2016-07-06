package com.example.administrator.testassistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/22.
 * 评论列表的适配器
 */
public class CommentAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String, Object>> m_Data;//数据源
    private LinearLayout layout;
    public ImageLoader imageLoader;
    public CommentAdapter(Context context,List<Map<String, Object>> m_Data){
        this.context=context;
        this.m_Data=m_Data;
        imageLoader=new ImageLoader(context.getApplicationContext());

    }

    @Override
    public int getCount() {
        return m_Data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //设置界面和数据
        LayoutInflater inflater=LayoutInflater.from(context);
        layout=(LinearLayout)inflater.inflate(R.layout.friend_content_item,null);
        TextView contentTv=(TextView)layout.findViewById(R.id.friendItemContentTv);
        TextView timeTv=(TextView)layout.findViewById(R.id.friendItemTimeTv);
        TextView nameTv=(TextView)layout.findViewById(R.id.friendNameTv);
        ImageView avatarIv=(ImageView)layout.findViewById(R.id.itemAvatarImageView);

        contentTv.setText((String)m_Data.get(position).get("comment"));
        timeTv.setText(CommonFunctions.getTime(Integer.parseInt((String)m_Data.get(position).get("time"))*(long)1000));
        nameTv.setText((String)m_Data.get(position).get("name"));

        String path="http://greenteabitch.coding.io/"+(String)m_Data.get(position).get("avatar");
        if(!path.isEmpty()){//加载头像图片
            imageLoader.DisplayImage(path,avatarIv);
        }
        return layout;
    }



}
