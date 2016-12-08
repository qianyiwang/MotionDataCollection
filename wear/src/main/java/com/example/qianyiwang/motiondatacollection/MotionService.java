package com.example.qianyiwang.motiondatacollection;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by wangqianyi on 2016-11-21.
 */
public class MotionService extends Service implements SensorEventListener {

    //Sensor variable
    Sensor senAccelerometer, senGyroscope;
    SensorManager mSensorManager;
    float acc_x, acc_y, acc_z, gry_x, gry_y, gry_z, acc_y_lowpass;
    private float mGry; // acceleration apart from gravity
    private float mGryCurrent; // current acceleration including gravity
    private float mGryLast; // last acceleration including gravity
    private float yAcc; // acceleration apart from gravity
    private float xAcc;
    private float zAcc;
    private float yAccCurrent; // current acceleration including gravity
    private float xAccCurrent;
    private float zAccCurrent;
    private float yAccLast; // last acceleration including gravity
    private float xAccLast;
    private float zAccLast;
    boolean trigger = false;
    ArrayList<Float> dataArray_acc_y = new ArrayList();
    ArrayList<Float> dataGry = new ArrayList();
    TextToSpeech t1;

    BroadcastReceiver broadcastReceiver;
    boolean recordToggle;

    @Override
    public void onCreate() {
        super.onCreate();

        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        senAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        senGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_FASTEST);//adjust the frequency
        mSensorManager.registerListener(this, senGyroscope , SensorManager.SENSOR_DELAY_FASTEST);//adjust the frequency

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }
        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                recordToggle = intent.getBooleanExtra("record_toggle",false);
                Log.v("record_toggle", String.valueOf(recordToggle));
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(MainApp.BROADCAST_ACTION));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
        unregisterReceiver(broadcastReceiver);
        Toast.makeText(this,"stop motion service",0).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gry_x = event.values[0];
            gry_y = event.values[1];
            gry_z = event.values[2];
            mGryLast = mGryCurrent;
            float omegaMagnitude = (float) Math.sqrt(gry_x * gry_x + gry_y * gry_y + gry_z * gry_z);
            mGryCurrent = omegaMagnitude;
            float delta = mGryCurrent - mGryLast;
            mGry = mGry * 0.9f + delta; // perform low-cut filter

//            if(mGry>=8) {
//                if (!trigger) {
//                    trigger = true;
//                    excute();
//                }
//            }

//            if(trigger){
//                dataGry.add(mGry);
//            }
        }

        if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            acc_x = event.values[0];
            acc_y = event.values[1];
            acc_z= event.values[2];
            xAccLast = xAccCurrent;
            yAccLast = yAccCurrent;
            zAccLast = zAccCurrent;
            yAccCurrent = acc_y;//(float) Math.sqrt(acc_x * acc_x + acc_y * acc_y + acc_z * acc_z);
            xAccCurrent = acc_x;
            zAccCurrent = acc_z;
            float delta = yAccCurrent - yAccLast;
            yAcc = yAcc * 0.9f + delta; // perform low-cut filter

            float delta1 = xAccCurrent - xAccLast;
            xAcc = xAcc * 0.9f + delta1; // perform low-cut filter
            float delta2 = zAccCurrent - zAccLast;
            zAcc = zAcc * 0.9f + delta2; // perform low-cut filter

//            if(trigger){
//                dataArray_acc_y.add(yAcc);
//            }

            if(recordToggle){
                dispData();
            }

        }
    }

    private void dispData() {
        Log.v("gry_m", String.valueOf(mGry));
        Log.v("acc_x", String.valueOf(xAcc));
        Log.v("acc_y", String.valueOf(yAcc));
        Log.v("acc_z", String.valueOf(zAcc));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this,"start motion service",0).show();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private int findPeaks(ArrayList<Float> dataArr){
        ArrayList<Float> bigVal = new ArrayList();
        ArrayList<Float> peakNum = new ArrayList();

        for (int i=1; i<dataArr.size(); i++){

            if(i<dataArr.size()-1){
                if((float)dataArr.get(i)>(float)dataArr.get(i-1)&&(float)dataArr.get(i)>(float)dataArr.get(i+1)){
                    peakNum.add((float)dataArr.get(i));
                }
            }
        }

        for(float f: peakNum){
            if(f>10){
                bigVal.add(f);
            }
        }
        return bigVal.size();
    }

    private float findMax(ArrayList<Float> dataArr){
        float bigVal = dataArr.get(0);
        for(float f: dataArr){
            if(f>bigVal){
                bigVal = f;
            }
        }

        return bigVal;
    }


    public void excute(){
        // Execute some code after 2 seconds have passed
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                trigger = false;

                dataArray_acc_y.clear();
                dataGry.clear();
            }
        }, 1000);
    }
}
