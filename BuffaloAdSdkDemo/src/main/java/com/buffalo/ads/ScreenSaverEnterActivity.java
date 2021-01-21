package com.buffalo.ads;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.buffalo.ads.R;

/**
 * Created by chenhao on 2016/1/6.
 */
public class ScreenSaverEnterActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screensave_enter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mMasterResetReciever, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMasterResetReciever);
    }


    BroadcastReceiver mMasterResetReciever= new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent){
            try{
                Intent i = new Intent();
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setClass(context, ScreenSaverActivity.class);
                context.startActivity(i);
            }catch(Exception e){
                Log.i("Output:", e.toString());
            }
        }
    };
}