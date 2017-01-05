package com.example.qianyiwang.motiondatacollection;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;

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
    private float mAcc;
    private float yAccCurrent; // current acceleration including gravity
    private float xAccCurrent;
    private float zAccCurrent;
    private float yAccLast; // last acceleration including gravity
    private float xAccLast;
    private float zAccLast;
    private float accLast;
    private float accCurrent;
    boolean recordTrigger = false;
    ArrayList<Float> dataArray_acc_y = new ArrayList();
    ArrayList<Float> dataGry = new ArrayList();
    Vibrator vibrator;

    BroadcastReceiver broadcastReceiver;
    boolean recordToggle = false;

    private static final float NS2S = 1.0f / 1000000000.0f;
    private float timestamp;
    private float angleVal;

    // generate a buffer for 64 data
    private static ArrayBlockingQueue<Double> mGryBuffer;
    private CalculateFFT mAsyncTask;

    @Override
    public void onCreate() {
        super.onCreate();

        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        senAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        senGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_FASTEST);//adjust the frequency
        mSensorManager.registerListener(this, senGyroscope , SensorManager.SENSOR_DELAY_FASTEST);//adjust the frequency

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                recordToggle = intent.getBooleanExtra("record_toggle",false);
                Log.v("record toggle", String.valueOf(recordToggle));
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(MainApp.BROADCAST_ACTION));

        mGryBuffer = new ArrayBlockingQueue<Double>(64);

    }

    @Override
    public void onDestroy() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mAsyncTask.cancel(true);
        mSensorManager.unregisterListener(this);
        unregisterReceiver(broadcastReceiver);
        Toast.makeText(this,"stop motion service",0).show();
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(timestamp!=0){
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                gry_x = event.values[0];
                gry_y = event.values[1];
                gry_z = event.values[2];
//                mGryLast = mGryCurrent;
                float omegaMagnitude = (float) Math.sqrt(gry_x * gry_x + gry_y * gry_y + gry_z * gry_z);
                try {
                    mGryBuffer.add(new Double(omegaMagnitude));
                } catch (IllegalStateException e) {

                    // Exception happens when reach the capacity.
                    // Doubling the buffer. ListBlockingQueue has no such issue,
                    // But generally has worse performance
                    ArrayBlockingQueue<Double> newBuf = new ArrayBlockingQueue<Double>(
                            mGryBuffer.size() * 2);

                    mGryBuffer.drainTo(newBuf);
                    mGryBuffer = newBuf;
                    mGryBuffer.add(new Double(omegaMagnitude));
                }
//                mGryCurrent = omegaMagnitude;
//                float delta = mGryCurrent - mGryLast;
//                mGry = mGry * 0.9f + delta; // perform low-cut filter
//                mGry = mGry + 0.15f * delta;
//                final float dT = (event.timestamp - timestamp) * NS2S;
//                angleVal = mGry*dT;

            }

//            if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
//                acc_x = event.values[0];
//                acc_y = event.values[1];
//                acc_z= event.values[2];
//
//                xAccLast = xAccCurrent;
//                yAccLast = yAccCurrent;
//                zAccLast = zAccCurrent;
//                accLast = accCurrent;
//
//                xAccCurrent = acc_x;
//                yAccCurrent = acc_y;
//                zAccCurrent = acc_z;
//                accCurrent = (float) Math.sqrt(acc_x * acc_x + acc_y * acc_y + acc_z * acc_z);
//
//                float delta_x = xAccCurrent-xAccLast;
//                float delta_y = yAccCurrent-yAccLast;
//                float delta_z = zAccCurrent-yAccLast;
//                float delta = accCurrent - accLast;
////                xAcc = xAccCurrent * 0.9f + delta_x;
////                yAcc = yAccCurrent * 0.9f + delta_y;
////                zAcc = zAccCurrent * 0.9f + delta_z;
////                mAcc = accCurrent * 0.9f + delta; // perform low-cut filter
//                xAcc = xAccCurrent + 0.15f * delta_x;
//                yAcc = yAccCurrent + 0.15f * delta_y;
//                zAcc = zAccCurrent + 0.15f * delta_z;
//                mAcc = accCurrent + 0.15f * delta;
//                if(recordToggle){
//                    dispData();
//                }
//            }

        }
        timestamp = event.timestamp;
    }

    private void dispData() {

//        Log.v("gry_m", String.valueOf(mGry));
//        Log.v("angle", String.valueOf(angleVal));
//        Log.v("acc_x", String.valueOf(xAcc));
//        Log.v("acc_y", String.valueOf(yAcc));
//        Log.v("acc_z", String.valueOf(zAcc));
//        Log.v("acc_m", String.valueOf(mAcc));


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this,"start motion service",0).show();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mAsyncTask = new CalculateFFT();
        mAsyncTask.execute();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // calculate fft in AsyncTask
    private class CalculateFFT extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... arg0) {
            int blockSize = 0;
            FFT fft = new FFT(64);
            double[] gryBuffer = new double[64];
            double[] mArr = new double[64];
            double[] re = gryBuffer;
            double[] im = new double[64];

            double max = Double.MIN_VALUE;

            while (true) {
                try {
                    // need to check if the AsyncTask is cancelled or not in the while loop
                    if (isCancelled () == true)
                    {
                        return null;
                    }

                    // Dumping buffer
                    double a = mGryBuffer.take().doubleValue();
                    gryBuffer[blockSize++] = a;
                    mArr[blockSize-1] = a;
                    if (blockSize == 64) {
                        blockSize = 0;

                        // time = System.currentTimeMillis();
                        max = .0;
//                        for (double val : gryBuffer) {
//                            if (max < val) {
//                                max = val;
//                            }
//                        }
                        fft.fft(re, im);
                        for (int i = 0; i < re.length; i++) {
                            double mag = Math.sqrt(re[i] * re[i] + im[i] * im[i]);
//                            if(recordToggle){
//                                Log.v("fft_v", String.valueOf(mag));
//                                Log.v("gry_m",mArr[i]+"");
//                            }
                            Log.v("fft_v", String.valueOf(mag));
                            if(mag>300){
//                                Activity mActivity = new MainApp();
//                                mActivity.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        t1.speak("rotating", TextToSpeech.QUEUE_FLUSH, null);
//                                    }
//                                });
                                publishProgress();
                            }
                            im[i] = .0; // Clear the field
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

            vibrator.vibrate(100);
        }
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
                recordTrigger = false;

                dataArray_acc_y.clear();
                dataGry.clear();
            }
        }, 1000);
    }
}
