package io.gamecloud.tiktok.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;
import io.gamecloud.tiktok.R;

import java.util.concurrent.atomic.AtomicBoolean;

public class SplashActivity extends BaseActivity {

    protected boolean active = true;
    protected int splashTime = 2000;

    private RelativeLayout splash;
    private AtomicBoolean started;
    private TextView splashText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setupContentLayout());
        started = new AtomicBoolean();
        setupViews();
        setupEvents();
    }

    @Override
    protected int setupContentLayout() {
        return R.layout.splash_activity;
    }

    @Override
    protected void setupViews() {
        splash = (RelativeLayout) findViewById(R.id.rl_splash);
        splashText = (TextView) findViewById(R.id.tv_app_title);
    }

    @Override
    protected void setupEvents() {
        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (active && (waited < splashTime)) {
                        sleep(100);
                        if (active) {
                            waited += 100;
                        }
                    }
                } catch (InterruptedException e) {

                } finally {
                    finish();
                    startActivity(new Intent(SplashActivity.this, UserLoginActivity.class));
                }
            }
        };
        splashTread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            active = false;
        }
        return true;
    }

    @Override
    protected void setupResume() {

    }

    @Override
    protected void setupPause() {

    }

    @Override
    protected void setupDestroy() {

    }
}
