package com.realm.utils.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;



import com.realm.utils.bluetooth.services.BluetoothLeService;

import static android.content.Context.BIND_AUTO_CREATE;

public class ble_probe {
    
    String TAG ="BLE_PROBE :";
    private static BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private static BluetoothGattCharacteristic mNotifyCharacteristic;
    private static BluetoothGattCharacteristic command_characteristic;
    String device_address;
    String characteristic_uuid;


    static bt_probe.data_interface bti;
Context act;

    public  ble_probe (Context act, String device_address,String characteristic_uuid)
    {
        this.act=act;
        this.device_address=device_address;
        this.characteristic_uuid=characteristic_uuid;
        Intent gattServiceIntent = new Intent(act, BluetoothLeService.class);
        act.bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        Log.e(TAG, "Service binding ... " );

    }


    public static void setup_bt(bt_probe.data_interface bti2)
    {

        bti=bti2;
    }
    public  void send_gate_command(boolean open)
    {
        if (command_characteristic != null) {
            final BluetoothGattCharacteristic characteristic =command_characteristic;
            final int charaProp = characteristic.getProperties();
            characteristic.setValue(open?"e":"o");
Log.e(TAG,"SENT");

            mBluetoothLeService.write_xtics(characteristic);
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService.setCharacteristicNotification(
                            mNotifyCharacteristic, false);
                    mNotifyCharacteristic = null;
                }
                mBluetoothLeService.readCharacteristic(characteristic);
            }
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                mNotifyCharacteristic = characteristic;
                mBluetoothLeService.setCharacteristicNotification(
                        characteristic, true);
            }


        }
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                //finish();
            }else{
                Log.e(TAG, "Service bound " );

            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(device_address);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };


    public void Resume() {

        act.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(device_address);
            Log.e(TAG, "Connect request result=" + result);
        }
    }


    public void Pause() {

        act.unregisterReceiver(mGattUpdateReceiver);
    }



    public void Destroy() {

        act.unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;


            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;



            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.e("BLE :",""+intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };


    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;

        for (BluetoothGattService gattService : gattServices) {
Log.e(TAG,"Service :"+gattService.getUuid());
            for (BluetoothGattCharacteristic gattCharacteristic : gattService.getCharacteristics()) {
                Log.e(TAG,"Xtics :"+gattCharacteristic.getUuid());
                if(gattCharacteristic.getUuid().toString().equalsIgnoreCase(characteristic_uuid))
                {
                    Log.e(TAG,"Command Xtics :"+gattService.getUuid());
                    command_characteristic=gattCharacteristic;
                }
            }
        }
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
