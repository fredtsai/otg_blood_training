package com.otg_blood_pressure;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.otg_blood_pressure.login.login_activity;

/**
 * 本APP所有容之智慧財權，均為蝸牛手建館所有，包括文字、照片、圖片、軟體程式碼，皆受中華民國著作權法、商標法、
 * 公平交易法與其他相關法令之保障，請尊重智慧財權並遵守法令規範，請勿自行使用、轉載、修改、重製、發行、公開發表、教學、散佈，
 * 若需此方面的使用或服務，請務必事先與蝸牛手建館人員接洽，並且取得授權。
 */

public class WelcomeActivity extends Activity {

    private Button button;
    private static final int GOTO_SELECT_ACTIVITY = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_layout);
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHandler.sendEmptyMessageDelayed(GOTO_SELECT_ACTIVITY, 200);
            }
        });
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Intent intent = new Intent();
            switch (msg.what) {
                case GOTO_SELECT_ACTIVITY:
                    intent = new Intent();
                    //將原本Activity的換成MainActivity
                    intent.setClass(WelcomeActivity.this, login_activity.class);

                    startActivity(intent);
                    WelcomeActivity.this.finish();
                    break;
                default:
                    break;
            }
        }

    };
}