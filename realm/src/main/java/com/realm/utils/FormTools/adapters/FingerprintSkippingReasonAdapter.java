package com.realm.utils.FormTools.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.realm.annotations.RealmModel;

import java.util.List;

import com.realm.R;
import com.realm.utils.FormTools.models.FingerprintSkippingReason;


/**
 * Created by Thompson on 13-May-2022.
 */

public class FingerprintSkippingReasonAdapter extends BaseAdapter {
    List<? extends RealmModel> objs;
    Context context;
    public FingerprintSkippingReasonAdapter(Context context, List<? extends RealmModel> objs)
    {
        this.context=context;
        this.objs=objs;

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
        FingerprintSkippingReason obj= (FingerprintSkippingReason)objs.get(position);
        convertView= LayoutInflater.from(context).inflate(R.layout.item_selection_data,null,false);
        TextView name=(TextView)convertView.findViewById(R.id.title);


        name.setText(obj.name);




        return convertView;
    }
}
