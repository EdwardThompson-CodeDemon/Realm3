package com.realm.utils.FormTools.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.realm.R;
import com.realm.utils.FormTools.models.SelectionData;


public class SelectionDataRecyclerViewAdapter extends RecyclerView.Adapter<SelectionDataRecyclerViewAdapter.view>  implements Filterable {

    Context cntxt;
    public ArrayList<? extends SelectionData> items;
    ArrayList<? extends SelectionData> originalList;
    onItemClickListener listener;

    public interface onItemClickListener {

        void onItemClick(SelectionData selectionData, View view);
    }


    public SelectionDataRecyclerViewAdapter(ArrayList<? extends SelectionData> items, onItemClickListener listener) {
//        this.items = items!=null?items:new ArrayList<>();
//        originalList= new ArrayList<>(this.items);
        originalList = items!=null?items:new ArrayList<>();
        this.items = new ArrayList<>(this.originalList);
        this.listener = listener;


    }
public void setupLists()
{
    this.items = new ArrayList<>(this.originalList);
    notifyDataSetChanged();

}
    @NonNull
    @Override
    public view onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.cntxt = parent.getContext();
        View view = LayoutInflater.from(cntxt).inflate(R.layout.item_selection_data, parent, false);

        return new view(view);
    }

    @Override
    public void onBindViewHolder(@NonNull view holder, int position) {
        holder.populate(position);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class view extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, code;
        public String sid;
        public ImageView icon;
        public int position;

        view(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            code = itemView.findViewById(R.id.info1);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(items.get(position), itemView);

                }
            });

        }

        @Override
        public void onClick(View view) {

        }

        public void populate(int position) {
            this.position = position;
            SelectionData selectionData = items.get(position);
            title.setText(selectionData.name);
            if (selectionData.code != null && selectionData.code.length() < 1) {
                code.setText(selectionData.code);
                code.setVisibility(View.VISIBLE);
            } else {
                code.setVisibility(View.INVISIBLE);
            }


        }
    }

    DataFilter dataFilter;

    @Override
    public Filter getFilter() {
        if (dataFilter == null) {
            dataFilter = new DataFilter();
        }
        return dataFilter;
    }

    public class DataFilter extends Filter {


        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            FilterResults results = new FilterResults();

            ArrayList<SelectionData> filtered_list =charSequence==null?new ArrayList<>(originalList):new ArrayList<>();
            if(charSequence!=null){

                int tot=originalList.size();
                for (int i = 0; i < tot; i++) {
                    SelectionData selectionData = originalList.get(i);
                    if ((selectionData.name!=null&&selectionData.name.toLowerCase().contains(charSequence.toString().toLowerCase()) )|| (selectionData.code!=null&&selectionData.code.toLowerCase().contains(charSequence.toString().toLowerCase()))) {

                        filtered_list.add(selectionData);

                    }
                }
            }

            results.count = filtered_list.size();
            results.values = filtered_list;


            return results;

        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            items.clear();
            if(filterResults.values!=null){
                items.addAll((ArrayList) filterResults.values);

            }
            notifyDataSetChanged();
        }
    }
}
