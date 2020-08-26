package com.linhleeproject.mymessage.messengeros10.adapters;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.linhleeproject.mymessage.messengeros10.MyApplication;
import com.linhleeproject.mymessage.messengeros10.R;
import com.linhleeproject.mymessage.messengeros10.database.DatabaseHelper;
import com.linhleeproject.mymessage.messengeros10.dialogs.ConfirmDeleteDialog;
import com.linhleeproject.mymessage.messengeros10.models.MessageObject;
import com.linhleeproject.mymessage.messengeros10.models.ThemeObject;
import com.linhleeproject.mymessage.messengeros10.utils.Constant;

import java.util.ArrayList;

/**
 * Created by Linh Lee on 11/30/2016.
 */
public class ListMessageAdapter extends RecyclerView.Adapter<ListMessageAdapter.RecyclerViewHolder> {
    private Activity context;
    private ArrayList<MessageObject> listMessage;
    private DatabaseHelper db = MyApplication.getDb();

    private static final int TYPE_RECEIVE = 1;
    private static final int TYPE_SENT = 2;

    private SharedPreferences sharedPreferences = MyApplication.getSharedPreferences();
    private Gson gson = new Gson();
    private int textSize = sharedPreferences.getInt("text_size", 2);

    public ListMessageAdapter(Activity context, ArrayList<MessageObject> listMessage) {
        this.context = context;
        this.listMessage = listMessage;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_RECEIVE) {
            View itemView = context.getLayoutInflater().inflate(R.layout.item_message_receive, parent, false);
            return new RecyclerViewHolder(itemView);
        }
        View itemView = context.getLayoutInflater().inflate(R.layout.item_message_sent, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        if (position == 0) {
            holder.date.setVisibility(View.VISIBLE);
            holder.date.setText(Constant.convertTimestamp(listMessage.get(position).getDate()));
        } else {
            String thisDay = Constant.convertTimestamp(listMessage.get(position).getDate());
            String previousDay = Constant.convertTimestamp(listMessage.get(position - 1).getDate());
            if (thisDay.equals(previousDay)) {
                holder.date.setVisibility(View.GONE);
            } else {
                holder.date.setVisibility(View.VISIBLE);
                holder.date.setText(Constant.convertTimestamp(listMessage.get(position).getDate()));
            }
        }

        if (listMessage.get(position).getType() == 2) {
            String themeJson = sharedPreferences.getString("current_theme", "");
            if (themeJson.equals("")) {
                holder.bodyLayout.setBackgroundResource(R.drawable.bg_message_sent);
            } else {
                ThemeObject theme = gson.fromJson(themeJson, ThemeObject.class);
                switch (theme.getId()) {
                    case 1:
                        holder.bodyLayout.setBackgroundResource(R.drawable.bg_message_sent_winter);
                        break;
                    case 2:
                        holder.bodyLayout.setBackgroundResource(R.drawable.bg_message_sent_coffee);
                        break;
                    case 3:
                        holder.bodyLayout.setBackgroundResource(R.drawable.bg_message_sent_autumn);
                        break;
                    case 4:
                        holder.bodyLayout.setBackgroundResource(R.drawable.bg_message_sent_skull);
                        break;
                    case 5:
                        holder.bodyLayout.setBackgroundResource(R.drawable.bg_message_sent_violet);
                        break;
                    case 6:
                        holder.bodyLayout.setBackgroundResource(R.drawable.bg_message_sent_rainbow);
                        break;
                    case 7:
                        holder.bodyLayout.setBackgroundResource(R.drawable.bg_message_sent_cloud);
                        break;
                    case 8:
                        holder.bodyLayout.setBackgroundResource(R.drawable.bg_message_sent_cake);
                        holder.body.setTextColor(context.getResources().getColor(R.color.colorCakeBar));
                        break;
                }
            }
        }

        holder.body.setText(listMessage.get(position).getBody());
        holder.body.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize * 2 + 10);
    }

    @Override
    public int getItemViewType(int position) {
        if (listMessage.get(position).getType() == 1) {
            return TYPE_RECEIVE;
        }
        return TYPE_SENT;
    }

    @Override
    public int getItemCount() {
        return listMessage.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout bodyLayout;
        public TextView date;
        public TextView body;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            bodyLayout = (RelativeLayout) itemView.findViewById(R.id.body_layout);
            date = (TextView) itemView.findViewById(R.id.date);
            body = (TextView) itemView.findViewById(R.id.body);

            body.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View popupView = layoutInflater.inflate(R.layout.popup_layout, null);

                    final PopupWindow popupWindow = new PopupWindow(popupView, Constant.convertDpIntoPixels(160, context), Constant.convertDpIntoPixels(40, context));

                    RelativeLayout copyText = (RelativeLayout) popupView.findViewById(R.id.copy_text);
                    RelativeLayout delete = (RelativeLayout) popupView.findViewById(R.id.delete);

                    copyText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("message", listMessage.get(getLayoutPosition()).getBody());
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(context, "Copied to clipboard.", Toast.LENGTH_SHORT).show();
                            popupWindow.dismiss();
                        }
                    });
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ConfirmDeleteDialog dialog = new ConfirmDeleteDialog(context, new ConfirmDeleteDialog.ButtonClicked() {
                                @Override
                                public void okClicked() {
                                    int id = listMessage.get(getLayoutPosition()).getMessageId();
                                    Constant.deleteSmsByMesId(context, id);
                                    db.deleteMessageById(id);
                                    listMessage.remove(getLayoutPosition());
                                    notifyItemRemoved(getLayoutPosition());
                                    notifyItemRangeChanged(getLayoutPosition(), listMessage.size());
                                    popupWindow.dismiss();
                                }
                            });
                            dialog.show();
                        }
                    });

                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
                    popupWindow.setOutsideTouchable(true);

                    popupWindow.showAsDropDown(bodyLayout, 0, -(bodyLayout.getHeight() + Constant.convertDpIntoPixels(40, context)));
                    return false;
                }
            });
        }
    }
}
