package io.gamecloud.tiktok.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import com.gamecloud.exception.GameCloudException;
import com.gamecloud.model.game.GameBinaryContent;
import com.gamecloud.model.game.GameSave;
import com.gamecloud.model.game.GameSaveInfo;
import com.gamecloud.model.player.Player;
import com.gamecloud.service.PlayerService;
import io.gamecloud.tiktok.R;
import io.gamecloud.tiktok.adapters.SaveGameAdapter;
import io.gamecloud.tiktok.models.Clock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class ListOfSaveGames extends BaseActivity {

    private Player player;
    private Button proceed;
    private ListView savedGames;

    private SaveGameAdapter adapterSaveGames;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(setupContentLayout());

        // Get player object passed to start intent
        Bundle b = getIntent().getExtras();
        if (b != null) {
            Object o = b.get("player");
            if (o != null && o instanceof Player) {
                this.player = (Player) o;
            }
        }

        new AsyncTask<String, Integer, ArrayList<GameSaveInfo>>() {
            protected ArrayList<GameSaveInfo> doInBackground(String... parameter) {
                try {
                    return PlayerService.listSaves2(player);
                } catch (GameCloudException e) {
                    throw new RuntimeException(e);
                }
            }

            protected void onPostExecute(ArrayList<GameSaveInfo> savesInfo) {
                adapterSaveGames = new SaveGameAdapter(ListOfSaveGames.this, savesInfo);
                savedGames.setAdapter(adapterSaveGames);
            }
        }.execute();

        setupViews();
        setupEvents();
    }

    @Override
    protected void setupEvents() {

        final Intent iIntent = new Intent(ListOfSaveGames.this, MainActivity.class);
        final Bundle bundle = new Bundle();
        bundle.putSerializable("player", player);

        savedGames.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View _view, int position, long _id) {
                final GameSaveInfo i = (GameSaveInfo) parent.getItemAtPosition(position);
                new AsyncTask<String, Integer, GameSave>() {
                    protected GameSave doInBackground(String... parameter) {
                        try {
                            return PlayerService.readSave1(player, i);
                        } catch (GameCloudException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    protected void onPostExecute(GameSave save) {
                        GameBinaryContent binaryContent = save.getBinaryAttachments().get(0);
                        ByteArrayInputStream bis = new ByteArrayInputStream(binaryContent.getContent());
                        ObjectInput in = null;
                        try {
                            try {
                                in = new ObjectInputStream(bis);
                                Clock i = (Clock) in.readObject();
                                bundle.putSerializable("clock", i);
                                iIntent.putExtras(bundle);
                                startActivity(iIntent);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } catch (ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        } finally {
                            try {
                                bis.close();
                                in.close();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }.execute();
            }
        });
        iIntent.putExtras(bundle);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(iIntent);
            }
        });
    }

    @Override
    protected int setupContentLayout() {
        return R.layout.list_of_saved_games;
    }

    @Override
    protected void setupViews() {
        savedGames = (ListView) findViewById(R.id.lv_saved_games);
        proceed = (Button) findViewById(R.id.but_ignore);
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