package com.realm.utils.bluetooth;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;


import com.realm.R;
import com.realm.utils.bluetooth.adapters.bt_devicelist_adapter;
import com.realm.utils.svars;


/**
 * Created by Thompsons on 9/1/2017.
 */

public class bt_device_connector {

    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;

    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Member fields
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    Set<BluetoothDevice> pairedDevices;
    ArrayList<BluetoothDevice> paired_devices = new ArrayList<>();
    ArrayList<BluetoothDevice> discovered_devices = new ArrayList<>();
    Context act;
    View main;
    AlertDialog ald;
    GridView discovered_list;
    TextView current_device, connection_status, title;
    ImageView bt_search;
    ObjectAnimator bt_search_anime;
    Button dismis;
    bt_device_type device_type = bt_device_type.fp_device;

    public enum bt_device_type {
        fp_device,
        printer,
        weighbridge_model,
        lb_access_point
    }

    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH})
    public bt_device_connector(final Context act, bt_device_type device_type) {
        this.act = act;
        this.device_type = device_type;
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (ActivityCompat.checkSelfPermission(act, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return ;
        }

        if (!mBtAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            act.startActivity(enableIntent);

    }
    if(device_type== bt_device_type.lb_access_point){
        if (!act.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(act, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    pairedDevices =mBtAdapter.getBondedDevices();
//    pairedDevices =device_type==bt_device_type.lb_access_point?new Set<BluetoothDevice>(): mBtAdapter.getBondedDevices();
//paired_devices=(ArrayList<BluetoothDevice>) pairedDevices;



}



    public interface device_selection_handler{

        void on_device_paired_and_selected(BluetoothDevice device);
        void on_device_slected(BluetoothDevice device);
        void on_device_paired(BluetoothDevice device);


    }
    device_selection_handler dh=null;
    public void show(final device_selection_handler dhm)
    {
        dh=dhm;
        paired_devices.clear();
        if (mBtAdapter.isEnabled()) {

            main= LayoutInflater.from(act).inflate(R.layout.dialog_bt_device_select,null);
            ald=new AlertDialog.Builder(act)
                    .setView(main)
                    .show();
            ald.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            discovered_list=(GridView)main.findViewById(R.id.device_list);
            bt_search=(ImageView)main.findViewById(R.id.status_image);
            connection_status=(TextView)main.findViewById(R.id.value1);
            current_device=(TextView)main.findViewById(R.id.current_device);
            dismis=(Button) main.findViewById(R.id.dismis);
            title=(TextView)main.findViewById(R.id.title);

            if(device_type== bt_device_type.printer)
            {
                title.setText("BT Printer Selection");
            }else if(device_type== bt_device_type.fp_device)
            {
                title.setText("BT FP device Selection");

            }else if(device_type== bt_device_type.weighbridge_model)
            {
                title.setText("BT Weighbridge Selection");

            }else if(device_type== bt_device_type.lb_access_point)
            {
                title.setText("BT Access point Selection");

            }

             ald.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mBtAdapter.cancelDiscovery();
                }
            });

            Thread thread_s=new Thread(new Runnable(){

                public void run(){
                    connection_status.post(new Runnable() {
                        @Override
                        public void run() {
                            bt_search_anime = ObjectAnimator.ofFloat(bt_search,"alpha",0,1f,0);
                            bt_search_anime.setDuration(1000);
                            bt_search_anime.setRepeatMode(ValueAnimator.RESTART);
                            bt_search_anime.setRepeatCount(ValueAnimator.INFINITE);
                            connection_status.setText("Status : Searching ... ");
                            bt_search_anime.start();

                        }
                    });

                }

            });
            thread_s.start();


            discovered_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    BluetoothDevice btdv=paired_devices.get(position);
                    current_device.setText("Current device : "+btdv.getName());

                     svars.set_bt_device_address(act,device_type, btdv.getAddress());
                    dhm.on_device_slected(btdv);
//                    if(device_type== bt_device_type.fp_device) {
//                        svars.set_bt_device_address(act, paired_devices.get(position).getAddress());
//
//                    }else if(device_type== bt_device_type.printer) {
//
//                        svars.set_bt_printer_address(act, paired_devices.get(position).getAddress());
//
//                    }
//                   else if(device_type== bt_device_type.weighbridge_model) {
//
//                        svars.set_bt_weighbridge_address(act, paired_devices.get(position).getAddress());
//
//                    }
//                    if(paired_devices.get(position).getBondState() != BluetoothDevice.BOND_BONDED)
//                    {
//                        dh.on_device_slected(paired_devices.get(position));
//                        pairDevice(paired_devices.get(position));
//                    }else{
//                        dh.on_device_paired_and_slected(paired_devices.get(position));
//                    }
                }
            });
            dismis.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ald.dismiss();

                }
            });
            ald.show();
            Thread bt_search_thread=new Thread(new Runnable(){

                public void run(){
                    IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                    act.registerReceiver(mReceiver, intent);
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    act.registerReceiver(mReceiver, filter);
                    filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                    act.registerReceiver(mReceiver, filter);
                    if (pairedDevices.size() > 0) {
                        for (final BluetoothDevice device : pairedDevices) {
                            paired_devices.add(device);
                            current_device.post(new Runnable(){

                                @Override
                                public void run() {
                                    if(device_type== bt_device_type.fp_device) {
                                        if (device.getAddress().equalsIgnoreCase(svars.bt_device_address(act,device_type))) {
                                            current_device.setText("Current FP device : " + device.getName());
                                        }
                                    }else if(device_type== bt_device_type.printer) {
                                        if (device.getAddress().equalsIgnoreCase(svars.bt_device_address(act,device_type))) {
                                            current_device.setText("Current Printer : " + device.getName());
                                        }
                                    }else if(device_type== bt_device_type.weighbridge_model) {
                                        if (device.getAddress().equalsIgnoreCase(svars.bt_device_address(act,device_type))) {
                                            current_device.setText("Current Weighbridge : " + device.getName());
                                        }
                                    }




                                    discovered_list.setAdapter(new bt_devicelist_adapter(act, paired_devices));

                                }

                            });

                        }
                    }

                    mBtAdapter.startDiscovery();

                }

            });

            if(device_type== bt_device_type.lb_access_point) {
                Thread ble_search_thread=new Thread(new Runnable(){

                    public void run(){
                        paired_devices.clear();
                        scanLeDevice(10000);
                    }

                });
                ble_search_thread.start();

            }else {
                bt_search_thread.start();

            }

        }else {
            Toast.makeText(act,"Bluetooth not available ...!!!", Toast.LENGTH_LONG).show();//00:14:03:05:EC:D0
        }
    }
    private android.os.Handler mHandler=new android.os.Handler();
    private  void scanLeDevice(long SCAN_PERIOD) {

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                //    mScanning = false;
                    mBtAdapter.stopLeScan(mLeScanCallback);
                    bt_search_anime.cancel();
                    connection_status.setText("Status : Search complete");


                }
            }, SCAN_PERIOD);

           // mScanning = true;
            mBtAdapter.startLeScan(mLeScanCallback);


    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    current_device.post(new Runnable() {
                        @Override
                        public void run() {

                            if (!paired_devices.contains(device))
                            {
                                paired_devices.add(device);
                                if(device_type== bt_device_type.fp_device)
                                {
                                    if(device.getAddress().equalsIgnoreCase(svars.bt_device_address(act,device_type)))
                                    {
                                        current_device.setText("Set device : "+device.getName());
                                    }

                                }else if(device_type== bt_device_type.lb_access_point){
                                    if(device.getAddress().equalsIgnoreCase(svars.bt_device_address(act,device_type)))
                                    {
                                        current_device.setText("Set Access Point : "+device.getName());
                                    }

                                }else if(device_type== bt_device_type.printer){
                                    if(device.getAddress().equalsIgnoreCase(svars.bt_device_address(act,device_type)))
                                    {
                                        current_device.setText("Set printer : "+device.getName());
                                    }

                                }else if(device_type== bt_device_type.weighbridge_model){
                                    if(device.getAddress().equalsIgnoreCase(svars.bt_device_address(act,device_type)))
                                    {
                                        current_device.setText("Set WB Access point : "+device.getName());
                                    }

                                }

                                discovered_list.setAdapter(new bt_devicelist_adapter(act,paired_devices));
                            }


                        }
                    });
                }
            };
    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device.getBondState() != BluetoothDevice.BOND_BONDED&&!paired_devices.contains(device))
                {
                    paired_devices.add(device);
                    if(device_type== bt_device_type.fp_device)
                    {
                        if (device.getAddress().equalsIgnoreCase(svars.bt_device_address(act,device_type))) {

                            current_device.setText("Set device : "+device.getName());
                        }

                    }else if(device_type== bt_device_type.lb_access_point){
                        if (device.getAddress().equalsIgnoreCase(svars.bt_device_address(act,device_type))) {

                            current_device.setText("Set Access Point : "+device.getName());
                        }

                    }else if(device_type== bt_device_type.printer){
                        if (device.getAddress().equalsIgnoreCase(svars.bt_device_address(act,device_type))) {

                            current_device.setText("Set printer : "+device.getName());
                        }

                    }else if(device_type== bt_device_type.weighbridge_model){
                        if (device.getAddress().equalsIgnoreCase(svars.bt_device_address(act,device_type))) {

                            current_device.setText("Set WB Access point : "+device.getName());
                        }

                    }

                    discovered_list.setAdapter(new bt_devicelist_adapter(act,paired_devices));
                }
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                bt_search_anime.cancel();
                connection_status.setText("Status : Search complete");

//                if (mNewDevicesArrayAdapter.getCount() == 0)
//                {
//                    String noDevices ="No devices found";
//                    mNewDevicesArrayAdapter.add(noDevices);
//                }
            }else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                final int state  = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState    = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    Toast.makeText(act,"Paired to :"+device.getName(), Toast.LENGTH_LONG ).show();
                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
                    Toast.makeText(act,"Unpaired to :"+device.getName(), Toast.LENGTH_LONG ).show();
                }


        }
        }
    };


    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void unpairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public BluetoothDevice set_device(bt_device_type device_type)
    {
         for (BluetoothDevice device : pairedDevices) {
            paired_devices.add(device);
            if(device.getAddress().equalsIgnoreCase(svars.bt_device_address(act, bt_device_type.fp_device))&&device_type== bt_device_type.fp_device)
            {
               return device;
            }else if(device.getAddress().equalsIgnoreCase(svars.bt_device_address(act, bt_device_type.printer))&&device_type== bt_device_type.printer)
            {
                return device;

            }else if(device.getAddress().equalsIgnoreCase(svars.bt_device_address(act, bt_device_type.weighbridge_model))&&device_type== bt_device_type.weighbridge_model)
            {
                return device;

            }else if(device.getAddress().equalsIgnoreCase(svars.bt_device_address(act, bt_device_type.lb_access_point))&&device_type== bt_device_type.lb_access_point)
            {
                return device;

            }


            }/*
show(new device_selection_handler() {
    @Override
    public void on_device_paired_and_slected(BluetoothDevice device) {

    }

    @Override
    public void on_device_slected(BluetoothDevice device) {

    }

    @Override
    public void on_device_paired(BluetoothDevice device) {

    }
});

      */
        return null;
    }
}
