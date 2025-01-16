package com.realm.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.realm.annotations.RealmModel;

import java.util.ArrayList;


public class GeneralDataAdapter<RM extends RealmModel, L extends GeneralDataAdapterListener<RM>, VH extends GeneralDataAdapterView<RM, L>> extends RecyclerView.Adapter<VH> {
    Context context;
    public ArrayList<RM> items = new ArrayList<>();
    Class<RM> realmModel;
    L listener;
    Class<VH> viewHolder;
    ViewGroup parent;
    String logTag = "GeneralDataAdapter";


    public GeneralDataAdapter(ArrayList<RM> items, Class<VH> viewHolder, L listener) {
        this.items = items;
        Log.e(logTag, "Items: " + items.size());

        this.listener = listener;
        this.viewHolder = viewHolder;


    }

    public void setItems(ArrayList<RM> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void addItem(RM item) {
        if (item == null) {
            return;
        }
        items.add(0, item);
        notifyItemInserted(0);
        notifyDataSetChanged();
    }

    public ArrayList<RM> getItems() {
        ArrayList<RM> result = new ArrayList<>(items);
//        result.removeIf(x->(x.transaction_no!=null&&!x.transaction_no.equals("Add")));
        return result;
    }

    public void clear() {
//        items.removeIf(x->(x.transaction_no==null||!x.transaction_no.equals("Add")));
        items.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        try {
            VH vh = viewHolder.newInstance();
            vh.setListener(listener);
            vh.setContext(parent.getContext());
            return vh;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.onBind(items.get(position), items);

    }


    @Override
    public int getItemCount() {
        return items.size();
    }


}
