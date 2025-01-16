package com.realm.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.realm.Realm;

public abstract class GeneralDataAdapterView<RM, L extends GeneralDataAdapterListener<RM>> extends RecyclerView.ViewHolder {
    public L listener;
    private Context context;

    public  RM item;
    public ArrayList<RM> items;

    public GeneralDataAdapterView(@NonNull View itemView) {
        super(itemView);
    }

    public GeneralDataAdapterView(@NonNull View itemView, L listener) {
        super(itemView);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(lp);
        this.listener = listener;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralDataAdapterView.this.listener.onClick(item);
            }
        });
    }


    public static int dpToPx(int dp) {
        final float scale = Realm.context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public abstract void onBind(RM item);

    public void onBind(RM item, ArrayList<RM> items) {
        this.item = item;
        this.items = items;
        onBind(item);
    }

    protected L getListener() {
        return listener;
    }

    public void setListener(L listener) {
        this.listener = listener;
    }

    public void setContext(Context context) {
        this.context = context;

    }
}
