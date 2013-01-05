package io.gamecloud.tiktok.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.gamecloud.exception.GameCloudException;
import com.gamecloud.model.player.Player;
import com.gamecloud.service.PlayerService;
import io.gamecloud.tiktok.R;
import io.gamecloud.tiktok.utils.Utils;

public class UserLoginActivity extends BaseActivity {

    private Button login;
    private Button loginFacebook;

    private EditText userId;
    private EditText password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setupContentLayout());
        setupViews();
        setupEvents();
    }

    @Override
    protected int setupContentLayout() {
        return R.layout.user_login_activity;
    }

    @Override
    protected void setupViews() {
        login = (Button) findViewById(R.id.but_login);
        loginFacebook = (Button) findViewById(R.id.but_facebook);
        userId = (EditText) findViewById(R.id.et_user_email);
        password = (EditText) findViewById(R.id.et_user_password);
    }

    @Override
    protected void setupEvents() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO Keep authorized and active player somewhere accessible to other activities - service?

                final String id = userId.getText().toString();
                final String pass = password.getText().toString();
                final String gameKey = Utils.TEST_GAME_UUID;
                final Player player = new Player(gameKey, id, pass);

                new AsyncTask<Void, Integer, Boolean>() {
                    protected Boolean doInBackground(Void... params) {
                        try {
                            return PlayerService.authorize(player);
                        } catch (GameCloudException e) {
                            return false;
                        }
                    }

                    protected void onPostExecute(Boolean result) {
                        if (result) {
                            //Start list save games activity with authorized player
                            Toast.makeText(UserLoginActivity.this, "Validation Positive, you may proceed", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(UserLoginActivity.this, ListOfSaveGames.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("player", player);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            Toast.makeText(UserLoginActivity.this, "Unauthorized", Toast.LENGTH_LONG).show();
                        }
                    }
                }.execute();
            }
        });

        loginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserLoginActivity.this, "Facebook integration soon", Toast.LENGTH_LONG).show();
            }
        });
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
