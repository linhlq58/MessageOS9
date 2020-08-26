package com.linhleeproject.mymessage.messengeros10.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.linhleeproject.mymessage.messengeros10.R;
import com.linhleeproject.mymessage.messengeros10.models.SettingsObject;

import java.util.ArrayList;

/**
 * Created by Linh Lee on 12/5/2016.
 */
public class ListSettingsAdapter extends RecyclerView.Adapter<ListSettingsAdapter.RecyclerViewHolder> {
    private Activity context;
    private ArrayList<SettingsObject> listSettings;

    private int selectedPos = 0;
    private PositionClickListener listener;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_COMMON = 1;

    public ListSettingsAdapter(Activity context, ArrayList<SettingsObject> listSettings, PositionClickListener listener) {
        this.context = context;
        this.listSettings = listSettings;
        this.listener = listener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View itemView = context.getLayoutInflater().inflate(R.layout.header_settings, parent, false);
            return new RecyclerViewHolder(itemView);
        }
        View itemView = context.getLayoutInflater().inflate(R.layout.item_settings, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        if (position == 0 || position == 6) {
            holder.settingsName.setText(listSettings.get(position).getName());
        } else {
            holder.settingsImg.setImageResource(listSettings.get(position).getImgRes());
            holder.settingsName.setText(listSettings.get(position).getName());
        }

        if (position == 7 || position == 8 || position == 9) {
            holder.arrowImage.setVisibility(View.GONE);
            if (position == 7 || position == 8) {
                holder.switchButton.setVisibility(View.VISIBLE);
                holder.itemView.setClickable(false);
            }
        }

        holder.itemView.setSelected(selectedPos == position);
    }

    @Override
    public int getItemCount() {
        return listSettings.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == 6) {
            return TYPE_HEADER;
        }
        return TYPE_COMMON;
    }

    public interface PositionClickListener {
        void itemClicked(int position);
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView settingsImg;
        private TextView settingsName;
        private ImageView arrowImage;
        private SwitchButton switchButton;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            settingsImg = (ImageView) itemView.findViewById(R.id.settings_img);
            settingsName = (TextView) itemView.findViewById(R.id.settings_name);
            arrowImage = (ImageView) itemView.findViewById(R.id.arrow_img);
            switchButton = (SwitchButton) itemView.findViewById(R.id.switch_btn);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.itemClicked(getLayoutPosition());
            notifyItemChanged(selectedPos);
            selectedPos = getLayoutPosition();
            notifyItemChanged(selectedPos);
        }
    }
}
