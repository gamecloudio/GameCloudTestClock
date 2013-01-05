package io.gamecloud.tiktok.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.gamecloud.model.game.GameSaveInfo;
import io.gamecloud.tiktok.R;

import java.util.ArrayList;

public class SaveGameAdapter extends ArrayAdapter<GameSaveInfo> {

    private final Activity activity;
    private final ArrayList<GameSaveInfo> savedGames;

    public SaveGameAdapter(Activity activity, ArrayList<GameSaveInfo> elements) {
        super(activity, R.layout.item_save_game_adapter, elements);
        this.activity = activity;
        this.savedGames = elements;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        SaveGameItemHolder holder;

        if (rowView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_save_game_adapter, null);

            holder = new SaveGameItemHolder();
            holder.saveName = (TextView) rowView.findViewById(R.id.tv_save_game_name);
            holder.saveDate = (TextView) rowView.findViewById(R.id.tv_save_game_time);
            holder.saveThumb = (ImageView) rowView.findViewById(R.id.iv_screenshot);

            rowView.setTag(holder);
        } else {
            holder = (SaveGameItemHolder) rowView.getTag();
        }

        GameSaveInfo oItem = savedGames.get(position);

        holder.saveName.setText(oItem.getSaveName());
        holder.saveDate.setText(oItem.getSaveDate());

        return rowView;
    }

    protected static class SaveGameItemHolder {
        protected TextView saveName;
        protected TextView saveDate;
        protected ImageView saveThumb;
    }
}
