package com.realm.utils.bluetooth.adapters;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import com.realm.R;

@SuppressLint("MissingPermission")
public class bt_devicelist_adapter extends BaseAdapter {
ArrayList<BluetoothDevice> devices= new ArrayList<>();
    Context contx;

public bt_devicelist_adapter(Context contx, ArrayList<BluetoothDevice> devices)
{
    this.contx=contx;
    this.devices=devices;


}

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);

    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
String memberdata=devices.get(position).getName();
      if(convertView==null)
      {convertView= LayoutInflater.from(contx).inflate(R.layout.item_bt_device,null);}
        TextView devicename=(TextView)convertView.findViewById(R.id.devicename);
        ImageView bticon=(ImageView)convertView.findViewById(R.id.bticon);
if(devices.get(position).getBondState() == BluetoothDevice.BOND_BONDED)
{
   // try{  bticon.setImageDrawable(contx.getResources().getDrawable(R.drawable.bt));}catch (Exception ex){}
   try{


      //  bticon.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(contx.getResources(), R.drawable.bt), 40, 40, false));
    Glide.with(contx)
            .load(contx.getResources().getDrawable(R.drawable.ic_bluetooth_searching))
            .into(bticon);
   }catch (Throwable t){}
    }else{
//try{    bticon.setImageDrawable(contx.getResources().getDrawable(R.drawable.bluetoothplainl));}catch (Exception ex){}
    try{
       // bticon.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(contx.getResources(), R.drawable.bluetoothplainl), 40, 40, false));
        Glide.with(contx)
                .load(contx.getResources().getDrawable(R.drawable.bluetoothplainl))
                .into(bticon);
    }catch (Throwable e){}

}
        devicename.setText(memberdata==null?devices.get(position).getAddress():memberdata);
        Log.e("Bt devicelist adapter"," "+devices.get(position).toString());


        return convertView;
    }
}
