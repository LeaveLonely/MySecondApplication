package com.example.jiaw2.mysecondapplication.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.jiaw2.mysecondapplication.R;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jiaw2 on 2016/10/25.
 */
public class MarqueeTestActivity extends Activity{
    private static final String TAG = "thm";
    private static final byte MARQUEE_STOPPED = 0x0;
    private TextView mTextView;
    private Field fieldStatus = null;
    private Object mMarquee = null;

    private Timer mTimer;
    private int CHECK_MARQUE_STOPED = 10201;

    final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == CHECK_MARQUE_STOPED) {
                checkStop();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marquee_test);

        mTextView = (TextView) findViewById(R.id.textview);
        mTextView.setSelected(true);

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(CHECK_MARQUE_STOPED);
            }
        }, 2000);
    }

    private void getFiled(){
        Class<?> mTextViewClass = mTextView.getClass();

        if(mTextViewClass != TextView.class){
            mTextViewClass = mTextViewClass.getSuperclass();
        }

        Field filedMarquee = null;
        try {
            filedMarquee = mTextViewClass.getDeclaredField("mMarquee");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        if(filedMarquee == null){
            return;
        }
        filedMarquee.setAccessible(true);

        try {
            mMarquee = filedMarquee.get(mTextView);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if(mMarquee == null){
            return;
        }

        Class<?> mMarqueeClass = mMarquee.getClass();
        try {
            fieldStatus = mMarqueeClass.getDeclaredField("mStatus");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        if(fieldStatus == null){
            return;
        }
        fieldStatus.setAccessible(true);
    }

    public void checkStop(){
        if(fieldStatus == null){
            getFiled();
        }
        if(fieldStatus == null){
            Log.d(TAG, "fieldStatus == null");
            return;
        }

        Byte mStatus = -1;
        try {
            mStatus = (Byte) fieldStatus.get(mMarquee);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "mStatus = "+mStatus);
        if(mStatus == MARQUEE_STOPPED){
            mTextView.setVisibility(View.GONE);
            return;
        }


        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(CHECK_MARQUE_STOPED);
            }
        }, 1000);
    }
}
