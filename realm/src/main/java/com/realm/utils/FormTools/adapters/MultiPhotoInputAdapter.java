package com.realm.utils.FormTools.adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import com.realm.activities.SpartaAppCompactActivity;
import com.realm.R;
import com.realm.Realm;
import com.realm.utils.svars;
import com.realm.utils.FormTools.models.AppData;


public class MultiPhotoInputAdapter extends RecyclerView.Adapter<MultiPhotoInputAdapter.view> {
    SpartaAppCompactActivity activity;
    Context context;
    public ArrayList<AppData> items = new ArrayList<>();
    public int maxItems = 0;
    public int minItems = 0;
    InputListener listener;
    ViewGroup parent;
    String addImageTransactionNo="AddImage";

    public interface InputListener {
        void onPhotoTaken();

        void onPhotoRequested();

        default void onImageClicked(AppData image) {


        }
default void onImageDeleted(AppData image) {


        }
default void onImagesDeleted() {


        }

        default void onMaxItemsReached() {


        }

    }

    public void setActivity(SpartaAppCompactActivity activity) {
        this.activity = activity;
    }

    public void setMaxImages(int maxItems) {
        this.maxItems = maxItems;
    }

    public void setMinItems(int minItems) {
        this.minItems = minItems;
    }


    public MultiPhotoInputAdapter(@NonNull InputListener listener) {

        this.listener = listener;
        AppData memberImage = new AppData();
        memberImage.transaction_no = addImageTransactionNo;
        items.add(memberImage);

    }

    public MultiPhotoInputAdapter(int maxItems, @NonNull InputListener listener) {
        this.maxItems = maxItems;

        this.listener = listener;
        AppData memberImage = new AppData();
        memberImage.transaction_no = addImageTransactionNo;
        items.add(memberImage);

    }

    public void addImage(AppData memberImage) {
        ArrayList<AppData> result = new ArrayList<>(items);
        result.removeIf(x -> (x.transaction_no != null && x.transaction_no.equals(addImageTransactionNo)));
        if(maxItems!=-1&&result.size()>=maxItems){
            listener.onMaxItemsReached();
            return;
        }
        if (memberImage == null || memberImage.data == null) {
//            inputField.inputValid=false;
            return;
        } else {
            File file = new File(svars.current_app_config(Realm.context).appDataFolder, memberImage.data);
            if (!file.exists() || file.length() < 500) {
//                inputField.inputValid=false;
                return;
            }

        }
        items.add(0, memberImage);
        notifyItemInserted(0);
        notifyDataSetChanged();
        result = new ArrayList<>(items);
        result.removeIf(x -> (x.transaction_no != null && x.transaction_no.equals(addImageTransactionNo)));
        if(maxItems!=-1&&result.size()>=maxItems){
            items.removeIf(x -> (x.transaction_no != null && x.transaction_no.equals(addImageTransactionNo)));
            listener.onMaxItemsReached();
        }
    }

    public ArrayList<AppData> getItems() {
        ArrayList<AppData> result = new ArrayList<>(items);
//        result.remove(result.size() - 1);
        result.removeIf(x -> (x.transaction_no != null && x.transaction_no.equals(addImageTransactionNo)));
        return result;
    }
    public int getImageCount() {
        ArrayList<AppData> result = new ArrayList<>(items);
        result.removeIf(x -> (x.transaction_no != null && x.transaction_no.equals(addImageTransactionNo)));
        return result.size();
    }

    public void clearItems() {
        int currentSize = items.size();
//     items.clear();
//        for(    AppData appData:items){
//            if(appData.transaction_no == null || !appData.transaction_no.equals(addImageTransactionNo)){
//                listener.onImageDeleted(appData);
//
//            }
//
//        }
        items.removeIf(x -> (x.transaction_no == null || !x.transaction_no.equals(addImageTransactionNo)));
        notifyItemRangeRemoved(0, currentSize - 1);

        ArrayList<AppData>   result = new ArrayList<>(items);
        result.removeIf(x -> (x.transaction_no != null && x.transaction_no.equals(addImageTransactionNo)));
        if(result.size()==items.size()&&maxItems!=-1&&result.size()<maxItems){
            AppData addImage = new AppData();
            addImage.transaction_no = addImageTransactionNo;
            items.add(addImage);
            notifyItemInserted(items.indexOf(addImage));

        }
        listener.onImagesDeleted();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                notifyDataSetChanged();
            }
        }, 500);


    }

    @NonNull
    @Override
    public view onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multi_photo_capture, parent, false);

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
        public ImageView image;
        public ImageView clearButton;
        public TextView title;

        public int position;


        view(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            clearButton = itemView.findViewById(R.id.delete_icon);
            clearButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppData appData=items.get(position);
                    items.remove(position);
                    notifyItemRemoved(position);
                    ArrayList<AppData>  result = new ArrayList<>(items);
                    result.removeIf(x -> (x.transaction_no != null && x.transaction_no.equals(addImageTransactionNo)));
                    if(result.size()==items.size()&&maxItems!=-1&&result.size()<maxItems){
                        AppData addImage = new AppData();
                        addImage.transaction_no = addImageTransactionNo;
                        items.add(addImage);
                        notifyItemInserted(items.indexOf(addImage));

                    }
                    listener.onImageDeleted(appData);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();

                        }
                    }, 500);
                }
            });

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int items_size = items.size() - 1;
                    if (position == items_size&&items.get(position).transaction_no!=null&&items.get(position).transaction_no.equals(addImageTransactionNo)) {
                        if (maxItems == -1 || items_size < maxItems) {
//                        svars.setImageCameraType(context,"2",5);
//                        activity.takePhoto("2");
                            listener.onPhotoRequested();
                        } else if (items_size == maxItems) {
                            listener.onMaxItemsReached();
                        }
                    } else {

                        listener.onImageClicked(items.get(position));

                    }

                }
            });


        }

        void populate(int position) {
            this.position = position;
            AppData memberImage = items.get(position);
            if (memberImage.transaction_no != null && memberImage.transaction_no.equals(addImageTransactionNo)) {
                image.setImageDrawable(context.getDrawable(new Random().nextInt() % 2 == 0 ? R.drawable.ic_add : R.drawable.ic_add_photo));
                image.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));
                clearButton.setVisibility(View.GONE);
                title.setText("Add photo");

            } else {
                image.setPadding(0, 0, 0, 0);
                clearButton.setVisibility(View.VISIBLE);
                if (memberImage != null && memberImage.data != null) {
                    image.setImageDrawable(null);
                    image.setImageURI(null);
//                    image.setImageURI(Uri.parse(Uri.parse(svars.current_app_config(Realm.context).appDataFolder) + memberImage.data));
                    title.setText(null);
                    Uri uri = Uri.fromFile(new File(svars.current_app_config(Realm.context).appDataFolder + memberImage.data));
                    Glide.with(itemView.getContext()).
                            load(uri).
                            thumbnail(0.1f).
                            into(image);
                } else {
                    image.setImageURI(null);
                    title.setText("Error loading");

                }


            }


        }

        int dpToPx(int dp) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dp * scale + 0.5f);
        }

        @Override
        public void onClick(View view) {

        }
    }

}
