package com.example.qianyiwang.motiondatacollection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.wearable.view.WatchViewStub;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Locale;

public class MainApp extends Activity {

    ToggleButton recordToggle;
    public static final String BROADCAST_ACTION = "net.qianyiw.broadcast.mainapp";
    TextToSpeech t1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_main_app);
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                    t1.speak ("Hello World", TextToSpeech.QUEUE_FLUSH, null);
                    startService(new Intent(getBaseContext(), MotionService.class));
                }
            }
        });
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        recordToggle = (ToggleButton)findViewById(R.id.recordButton);
    }

    @Override
    protected void onResume() {
        super.onResume();

        recordToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            Intent intent = new Intent(BROADCAST_ACTION);
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    intent.putExtra("record_toggle", true);
                    sendBroadcast(intent);
                    t1.speak ("Hello World", TextToSpeech.QUEUE_FLUSH, null);
                } else {
                    intent.putExtra("record_toggle", false);
                    sendBroadcast(intent);
                    t1.speak ("Hello World", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopService(new Intent(getBaseContext(), MotionService.class));
    }
}
