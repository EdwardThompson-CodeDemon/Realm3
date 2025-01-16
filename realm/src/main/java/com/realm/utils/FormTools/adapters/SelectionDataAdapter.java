package com.realm.utils.FormTools.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import com.realm.R;
import com.realm.utils.FormTools.models.SelectionData;


/**
 * Created by Thompson on 04-Feb-2023.
 */

public class SelectionDataAdapter extends BaseAdapter implements Filterable {
    ArrayList<? extends SelectionData> objs=new ArrayList<>();
    ArrayList<? extends SelectionData> originalList=new ArrayList<>();
    Context act;
    public SelectionDataAdapter(Context act, ArrayList<? extends SelectionData> objs)
    {
        this.act=act;
        this.objs=objs;
        originalList= new ArrayList<>(objs);

    }
    @Override
    public int getCount() {
        return objs.size();
    }

    @Override
    public Object getItem(int position) {
        return objs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView=null;
        SelectionData obj= objs.get(position);
        convertView= LayoutInflater.from(act).inflate(R.layout.item_selection_data,null,false);
        TextView name=(TextView)convertView.findViewById(R.id.title);

//        TextView code=(TextView)convertView.findViewById(R.id.index_field);
        name.setText(obj.name);
        if(obj.sid==null)
        {
            name.setTypeface(null, Typeface.BOLD);
        }else{
            name.setTypeface(null, Typeface.NORMAL);

        }

        return convertView;
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
            objs.clear();
            if(filterResults.values!=null){
                objs.addAll((ArrayList) filterResults.values);

            }
            notifyDataSetChanged();
        }
    }
}
