package com.realm.utils.printing;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;

import java.util.Set;

import com.realm.utils.bluetooth.bt_device_connector;
import com.realm.utils.svars;

public class Printer {
    public PrintingInterface printingInterface;
    protected String logTag = "BTPrinter";
    protected Activity context;
    protected String deviceMacAddress;
    BluetoothAdapter mBluetoothAdapter;
    public Printer(Activity context, PrintingInterface printingInterface) {
        this.context = context;
        this.printingInterface = printingInterface;
    }

    public void print() {
        deviceMacAddress = svars.bt_device_address(context, bt_device_connector.bt_device_type.printer);
        if (deviceMacAddress == null) {
            new bt_device_connector(context, bt_device_connector.bt_device_type.printer).show(new bt_device_connector.device_selection_handler() {

                @Override
                public void on_device_paired_and_selected(BluetoothDevice device) {

                }

                @Override
                public void on_device_slected(BluetoothDevice device) {

                }

                @Override
                public void on_device_paired(BluetoothDevice device) {

                }
            });
            return;
        }

    }

    protected BluetoothDevice getDevice(String deviceMacAddress) {
        if (deviceMacAddress == null) {
            return null;
        } else {

            try {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                if (mBluetoothAdapter == null) {
                    Log.e("Bluetooth class =>", "No bluetooth adapter available");


                }

                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    context.startActivityForResult(enableBluetooth, 0);
                    return null;
                }

                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        Log.e("Printer =>", "device : " + device.getName());

                        if (device.getAddress().toString().equalsIgnoreCase(deviceMacAddress)) {
                            Log.e("Bluetooth class =>", "Bluetooth device found.");
                            return device;

                        }

                    }


                } else {
                    return null;

                }


            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public interface PrintingInterface {
        default void onPrinterConnected(String deviceAddress) {


        }

      default   void onPrintBegun(){


      }

       default void onPrintComplete(){


        }

        default void onPrintError(String error) {

        }

         void onReadyToPrint(Object... printingObjects);

    }
}
