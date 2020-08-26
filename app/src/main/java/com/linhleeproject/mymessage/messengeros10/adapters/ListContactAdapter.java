package com.linhleeproject.mymessage.messengeros10.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.linhleeproject.mymessage.messengeros10.R;
import com.linhleeproject.mymessage.messengeros10.models.ContactObject;
import com.linhleeproject.mymessage.messengeros10.utils.RemoveAccent;

import java.util.ArrayList;

/**
 * Created by Linh Lee on 12/2/2016.
 */
public class ListContactAdapter extends RecyclerView.Adapter<ListContactAdapter.RecyclerViewHolder> implements Filterable {
    private Activity context;
    private int layout;
    private ArrayList<ContactObject> listContactOriginal;
    private ArrayList<ContactObject> listContactDisplayed;

    private int selectedPos = 0;
    private PositionClickListener listener;

    public ListContactAdapter(Activity context, int layout, ArrayList<ContactObject> listContact, PositionClickListener listener) {
        this.context = context;
        this.layout = layout;
        this.listContactOriginal = listContact;
        this.listContactDisplayed = listContact;
        this.listener = listener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = context.getLayoutInflater().inflate(layout, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.name.setText(listContactDisplayed.get(position).getName());
        holder.number.setText(listContactDisplayed.get(position).getPhoneNumber());

        holder.itemView.setSelected(selectedPos == position);
    }

    @Override
    public int getItemCount() {
        return listContactDisplayed.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ContactObject getItem(int position) {
        return listContactDisplayed.get(position);
    }

    public interface PositionClickListener {
        void itemClicked(int position);
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name;
        public TextView number;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            number = (TextView) itemView.findViewById(R.id.number);

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
                ArrayList<ContactObject> filteredArrList = new ArrayList<>();

                if (constraint == null) {
                    filteredArrList.addAll(listContactOriginal);
                } else {
                    constraint = RemoveAccent.removeAccent(constraint.toString().toLowerCase());
                    for (int i = 0; i < listContactOriginal.size(); i++) {
                        String name = listContactOriginal.get(i).getName();
                        name = RemoveAccent.removeAccent(name.toString().toLowerCase());
                        if (name.indexOf(constraint.toString()) > -1) {
                            filteredArrList.add(listContactOriginal.get(i));
                        }
                    }
                }

                results.values = filteredArrList;
                results.count = filteredArrList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listContactDisplayed = (ArrayList<ContactObject>) results.values;
                notifyDataSetChanged();
            }
        };

        return filter;
    }
}
