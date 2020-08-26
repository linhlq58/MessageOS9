package com.linhleeproject.mymessage.messengeros10.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.linhleeproject.mymessage.messengeros10.MyApplication;
import com.linhleeproject.mymessage.messengeros10.R;
import com.linhleeproject.mymessage.messengeros10.database.DatabaseHelper;
import com.linhleeproject.mymessage.messengeros10.models.MessageObject;
import com.linhleeproject.mymessage.messengeros10.utils.Constant;
import com.linhleeproject.mymessage.messengeros10.utils.RemoveAccent;

import java.util.ArrayList;

/**
 * Created by Linh Lee on 11/29/2016.
 */
public class ListConversationAdapter extends RecyclerView.Adapter<ListConversationAdapter.RecyclerViewHolder> implements Filterable {
    private Activity context;
    private int layout;
    private ArrayList<MessageObject> listConversationOriginal;
    private ArrayList<MessageObject> listConversationDisplayed;
    private DatabaseHelper db = MyApplication.getDb();

    private int selectedPos = 0;
    private PositionClickListener listener;
    private boolean isEdit = false;
    private ArrayList<Boolean> checkList;

    public ListConversationAdapter(Activity context, int layout, ArrayList<MessageObject> listConversation, PositionClickListener listener) {
        this.context = context;
        this.layout = layout;
        this.listConversationOriginal = listConversation;
        this.listConversationDisplayed = listConversation;
        this.listener = listener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = context.getLayoutInflater().inflate(layout, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        if (position % 2 == 0) {
            holder.rowLayout.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        } else {
            holder.rowLayout.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }

        if (listConversationDisplayed.get(position).getRead() == 0) {
            holder.unseenImg.setImageResource(R.drawable.ic_circle);
        } else {
            holder.unseenImg.setImageResource(android.R.color.transparent);
        }

        if (isEdit) {
            if (!checkList.get(position)) {
                holder.unseenImg.setImageResource(R.drawable.ic_circle_empty);
            } else {
                holder.unseenImg.setImageResource(R.drawable.ic_circel_checked);
            }
        }

        if (listConversationDisplayed.get(position).getThumbnailBase64().equals("")) {
            holder.circleAvatar.setVisibility(View.GONE);
            holder.avatar.setVisibility(View.VISIBLE);
            holder.firstLetter.setVisibility(View.VISIBLE);
            String firstLetterPerson = String.valueOf(listConversationDisplayed.get(position).getPerson().charAt(0));
            if (firstLetterPerson.matches("[a-zA-Z]+")) {
                holder.firstLetter.setText(firstLetterPerson.toUpperCase());
            } else {
                holder.firstLetter.setText("#");
            }
        } else {
            holder.circleAvatar.setVisibility(View.VISIBLE);
            holder.avatar.setVisibility(View.GONE);
            holder.firstLetter.setVisibility(View.GONE);
            byte[] imageAsBytes = Base64.decode(listConversationDisplayed.get(position).getThumbnailBase64(), Base64.DEFAULT);
            Bitmap myImage = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            holder.circleAvatar.setImageBitmap(myImage);
        }

        holder.person.setText(listConversationDisplayed.get(position).getPerson());
        holder.date.setText(Constant.convertTimestamp(listConversationDisplayed.get(position).getDate()));
        holder.body.setText(listConversationDisplayed.get(position).getBody());

        holder.itemView.setSelected(selectedPos == position);
    }

    @Override
    public int getItemCount() {
        return listConversationDisplayed.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public MessageObject getItem(int position) {
        return listConversationDisplayed.get(position);
    }

    public void setupCheckList() {
        checkList = new ArrayList<>();
        if (listConversationDisplayed.size() > 0) {
            for (int i = 0; i < listConversationDisplayed.size(); i++) {
                checkList.add(false);
            }
        }
    }

    public void setAllChecked() {
        if (listConversationDisplayed.size() > 0) {
            for (int i = 0; i < listConversationDisplayed.size(); i++) {
                checkList.set(i, true);
            }
        }
    }

    public void setIsEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }

    public void setCheckedPosition(int position) {
        if (checkList.get(position)) {
            checkList.set(position, false);
        } else {
            checkList.set(position, true);
        }
    }

    public ArrayList<Integer> getAllCheckedPosition() {
        ArrayList<Integer> listPosition = new ArrayList<>();
        if (checkList.size() > 0) {
            for (int i = 0; i < checkList.size(); i++) {
                if (checkList.get(i)) {
                    listPosition.add(i);
                }
            }
        }

        return listPosition;
    }

    public interface PositionClickListener {
        void itemClicked(int position);
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public LinearLayout rowLayout;
        public ImageView unseenImg;
        public ImageView avatar;
        public CircularImageView circleAvatar;
        public TextView firstLetter;
        public TextView person;
        public TextView date;
        public TextView body;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            rowLayout = (LinearLayout) itemView.findViewById(R.id.row_layout);
            unseenImg = (ImageView) itemView.findViewById(R.id.unseen_img);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            circleAvatar = (CircularImageView) itemView.findViewById(R.id.circle_avatar);
            firstLetter = (TextView) itemView.findViewById(R.id.first_letter);
            person = (TextView) itemView.findViewById(R.id.person);
            date = (TextView) itemView.findViewById(R.id.date);
            body = (TextView) itemView.findViewById(R.id.body);

            setupCheckList();

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

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<MessageObject> filteredArrList = new ArrayList<>();
                ArrayList<MessageObject> allMessage = db.getAllMessage();

                if (constraint == null || constraint.equals("")) {
                    filteredArrList.addAll(listConversationOriginal);
                } else {
                    constraint = RemoveAccent.removeAccent(constraint.toString().toLowerCase());
                    for (int i = 0; i < allMessage.size(); i++) {
                        String body = allMessage.get(i).getBody();
                        body = RemoveAccent.removeAccent(body.toString().toLowerCase());
                        if (body.indexOf(constraint.toString()) > -1) {
                            filteredArrList.add(allMessage.get(i));
                        }
                    }
                }

                results.values = filteredArrList;
                results.count = filteredArrList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listConversationDisplayed = (ArrayList<MessageObject>) results.values;
                notifyDataSetChanged();
            }
        };

        return filter;
    }
}
