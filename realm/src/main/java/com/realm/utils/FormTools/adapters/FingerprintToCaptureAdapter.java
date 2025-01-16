package com.realm.utils.FormTools.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import com.realm.R;
import com.realm.utils.FormTools.models.FingerprintToCapture;


public class FingerprintToCaptureAdapter extends RecyclerView.Adapter<FingerprintToCaptureAdapter.view> {
    List<FingerprintToCapture> items;
    Context context;
    public interface OnItemClickListener {

        void onItemClick(FingerprintToCapture fingerprint, View view,int position);
    }
    OnItemClickListener onItemClickListener;
    public FingerprintToCaptureAdapter(List<FingerprintToCapture> items, OnItemClickListener onItemClickListener)
    {
        this.items=items;
        this.onItemClickListener = onItemClickListener;
    }
    @NonNull
    @Override
    public view onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_fingerprint, parent, false);

        return new view(view);
    }

    @Override
    public void onBindViewHolder(@NonNull view holder, int position) {
holder.populate(position);
    }


    @Override
    public int getItemCount() {
        return items.size();    }



    public class view extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CircleImageView icon;
        public TextView name;
        public String sid;
        public View fpCapturedCheck,loadingBar;
        int position;

        view(View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.title_icon1);
            name = itemView.findViewById(R.id.name);
            fpCapturedCheck = itemView.findViewById(R.id.fp_check_pic);
            loadingBar = itemView.findViewById(R.id.loading_bar);
itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        onItemClickListener.onItemClick(items.get(position),itemView,position);
    }
});

        }
        public void populate(int position) {
            this.position = position;
            FingerprintToCapture item = items.get(position);
            if(item.skipped)
            {
                itemView.setBackground(context.getDrawable(R.drawable.button_negative));
                itemView.setAlpha(0.6f);
            }else{
                itemView.setAlpha(1f);
                itemView.setBackground(context.getDrawable(item.wsq==null||item.wsq.length()<10?R.drawable.button_negative:R.drawable.button_positive));

            }



            name.setText(item.name);
            icon.setImageDrawable(context.getDrawable(item.drawable_resource));
            fpCapturedCheck.setVisibility(item.wsq==null||item.wsq.length()<10?View.GONE:View.VISIBLE);
            loadingBar.setVisibility(item.capturing?View.VISIBLE:View.GONE);


        }

        @Override
        public void onClick(View view) {

        }
    }

}
