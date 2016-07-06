package com.example.administrator.testassistant;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2015/4/13.
 */
public class SessionProcess {

    public final String KEY_SESSION_ID="sessionID";
    public final String KEY_USER_ID="userID";
    public SessionProcess(Context context){
        this.context=context;
    }
    private Context context;
    public void saveSessionID(String sessionID){
        SharedPreferences.Editor editor=context.getSharedPreferences(settings.class.getName(), Context.MODE_PRIVATE).edit();
        editor.putString(KEY_SESSION_ID,sessionID);
        editor.commit();
    }

    public String getSessionID(){
        SharedPreferences sp = context.getSharedPreferences(settings.class.getName(), Context.MODE_PRIVATE);
        String sessionID = sp.getString(KEY_SESSION_ID, null);

        return sessionID;
    }

    public void saveUserID(int userID){
        SharedPreferences.Editor editor=context.getSharedPreferences(settings.class.getName(), Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_USER_ID,userID);
        editor.commit();
    }

    public int getUserID(){
        SharedPreferences sp = context.getSharedPreferences(settings.class.getName(), Context.MODE_PRIVATE);
        int userID = sp.getInt(KEY_USER_ID, -1);
        return userID;
    }

}
