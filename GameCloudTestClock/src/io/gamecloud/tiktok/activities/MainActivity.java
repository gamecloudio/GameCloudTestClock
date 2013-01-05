package io.gamecloud.tiktok.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.gamecloud.exception.GameCloudException;
import com.gamecloud.http.property.ByteArrayProperty;
import com.gamecloud.model.player.Player;
import com.gamecloud.service.PlayerService;
import io.gamecloud.tiktok.R;
import io.gamecloud.tiktok.models.Clock;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Calendar;

public class MainActivity extends Activity implements Runnable {

    private Player player;
    private TextView seconds;
    private TextView minutes;
    private TextView hours;
    private Button takeSnapshot;

    private Thread runner;
    private Calendar current;

    final Runnable updater = new Runnable() {
        public void run() {
            updateClockValues();
        }

        ;
    };
    final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        current = Calendar.getInstance();

        // Get player object passed to start intent
        Bundle b = getIntent().getExtras();
        if (b != null) {
            Object o = b.get("player");
            if (o != null && o instanceof Player) {
                this.player = (Player) o;
            }
        }

        // Get Clock object passed to start intent
        if (b != null) {
            Object o = b.get("clock");
            if (o != null && o instanceof Clock) {
                Clock c = (Clock) o;
                current.set(Calendar.HOUR, c.getHH());
                current.set(Calendar.MINUTE, c.getMM());
                current.set(Calendar.SECOND, c.getSS());
            }
        }

        setupViews();
        setupEvents();

        if (runner == null) {
            runner = new Thread(this);
            runner.start();
        }
    }

    private void updateClockValues() {
        current.add(Calendar.SECOND, 1);
        seconds.setText(Integer.toString(current.get(Calendar.SECOND)));
        minutes.setText(Integer.toString(current.get(Calendar.MINUTE)));
        hours.setText(Integer.toString(current.get(Calendar.HOUR)));
    }

    private void setupViews() {
        setContentView(R.layout.layout_main_activtiy);
        seconds = (TextView) findViewById(R.id.tv_current_seconds);
        minutes = (TextView) findViewById(R.id.tv_current_minutes);
        hours = (TextView) findViewById(R.id.tv_current_hours);
        takeSnapshot = (Button) findViewById(R.id.but_take_snapshot);
    }

    private void setupEvents() {
        takeSnapshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutput out = null;
                try {
                    out = new ObjectOutputStream(bos);
                    int hh = current.get(Calendar.HOUR);
                    int mm = current.get(Calendar.MINUTE);
                    int ss = current.get(Calendar.SECOND);
                    Clock c = new Clock(hh, mm, ss);
                    out.writeObject(new Clock(hh, mm, ss));
                    final byte[] bytes = bos.toByteArray();
                    new AsyncTask<String, Integer, String>() {
                        protected String doInBackground(String... parameter) {
                            try {
                                ByteArrayProperty content = new ByteArrayProperty(bytes, "application/octet-stream", "save");
                                return PlayerService.createSave(player, "save", Arrays.asList(new ByteArrayProperty[]{content}));
                            } catch (GameCloudException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        protected void onPostExecute(String uuid) {
                            Toast.makeText(MainActivity.this, "Saved: " + uuid, Toast.LENGTH_LONG).show();
                        }
                    }.execute();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        out.close();
                        bos.close();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.layout_main_activtiy, menu);
        return true;
    }

    @Override
    public void run() {
        while (runner != null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            mHandler.post(updater);
        }
    }
}
