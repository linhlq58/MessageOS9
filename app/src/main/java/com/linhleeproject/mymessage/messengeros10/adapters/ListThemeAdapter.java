package com.linhleeproject.mymessage.messengeros10.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.linhleeproject.mymessage.messengeros10.R;
import com.linhleeproject.mymessage.messengeros10.models.ThemeObject;

import java.util.ArrayList;

/**
 * Created by Linh Lee on 12/6/2016.
 */
public class ListThemeAdapter extends RecyclerView.Adapter<ListThemeAdapter.RecyclerViewHolder> {
    private Activity context;
    private int layout;
    private ArrayList<ThemeObject> listTheme;

    private int selectedPos = 0;
    private PositionClickListener listener;

    public ListThemeAdapter(Activity context, int layout, ArrayList<ThemeObject> listTheme, PositionClickListener listener) {
        this.context = context;
        this.layout = layout;
        this.listTheme = listTheme;
        this.listener = listener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = context.getLayoutInflater().inflate(layout, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.themeImg.setImageResource(listTheme.get(position).getBgPreview());

        holder.itemView.setSelected(selectedPos == position);
    }

    @Override
    public int getItemCount() {
        return listTheme.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public  interface PositionClickListener {
        void itemClicked(int position);
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView themeImg;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            themeImg = (ImageView) itemView.findViewById(R.id.theme_img);

            itemView.setMinimumHeight(themeImg.getHeight());
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
