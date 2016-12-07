package com.example.qianyiwang.motiondatacollection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainApp extends Activity {

    ToggleButton recordToggle;
    public static final String BROADCAST_ACTION = "net.qianyiw.broadcast.mainapp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_main_app);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        recordToggle = (ToggleButton)findViewById(R.id.togglebutton);

    }

    @Override
    protected void onResume() {
        super.onResume();

        startService(new Intent(getBaseContext(), MotionService.class));

        recordToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            Intent intent = new Intent(BROADCAST_ACTION);
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    intent.putExtra("record_toggle", true);
                    sendBroadcast(intent);
                } else {
                    intent.putExtra("record_toggle", false);
                    sendBroadcast(intent);
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
