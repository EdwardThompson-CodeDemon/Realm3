package com.realm.utils.bluetooth;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Thompsons on 9/4/2017. (0715300161)
 */

public class bt_probe {
    static BluetoothAdapter mBluetoothAdapter;
    static BluetoothSocket mmSocket1;
    static BluetoothDevice mmDevice;
    static OutputStream mmOutputStream = null;
    static InputStream mmInputStream = null;
    static Thread workerThread;
    static byte[] readBuffer;
    static int readBufferPosition;
      public static volatile boolean stopWorker;
    static Activity act;
   static String device_address="";
   static data_interface bti;
    public interface data_interface
    {
        void on_data_received(BluetoothDevice device, String data);
        void on_device_connection_failed(BluetoothDevice device);
        void on_data_sent(BluetoothDevice device, String data);
        void on_data_sent(BluetoothDevice device, byte[] data);
        void on_data_parsed(BluetoothDevice device, String data);
        void on_device_connection_changed(boolean connected, BluetoothDevice device);
        void on_device_reonnected(BluetoothDevice device);
        void on_device_error(BluetoothDevice device, String error);
    }


public static void setup_bt(Activity acti)
{
   act=acti;
    bti=(data_interface)act;
}
    public static void connect_device(BluetoothDevice device)
    {
        mmDevice=device;
        try{
            closeBT();
        }catch (Exception ex){}
        try {
            openBT();
        }catch (IOException EX)
        {
          //  bti.on_device_connection_changed(false,mmDevice);

        }
     //   bti.on_device_connection_changed(true,mmDevice);
    }
    public void connect_device_from_mac(String device_address)
    {
        this.device_address=device_address;
//        Thread thread = new Thread() {
//            public void run() {
                try {
                    findBT(true);
                    openBT();
                    //bti.on_device_connection_changed(true,mmDevice);
                }catch (IOException EX)
                {
                    bti.on_device_connection_changed(false,mmDevice);

                }
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {



                    }
                });

         /*   }
        };

        thread.start();
*/

    }
    public void connect_device_from_name(String device_address)
    {
        this.device_address=device_address;
        try {
            findBT(false);
            openBT();
            bti.on_device_connection_changed(true,mmDevice);
        }catch (IOException EX)
        {
            bti.on_device_connection_changed(false,mmDevice);

        }

    }

    public static void send_data(String val)
    {
        try {
            mmOutputStream.write(val.getBytes());
            mmOutputStream.flush();
            bti.on_data_sent(mmDevice,val);

        }catch (Exception ex){
            bti.on_device_error(mmDevice,ex.getMessage());
            Log.e("Bluetooth tx error =>"," "+ex);}
    }
 public static void send_data(byte[] val)
    {
        try {
            mmOutputStream.write(val);
            mmOutputStream.flush();
            bti.on_data_sent(mmDevice,val);

        }catch (Exception ex){
            bti.on_device_error(mmDevice,ex.getMessage());
            Log.e("Bluetooth tx error =>"," "+ex);}
    }
    static void findBT(boolean mac) {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                // myLabel.setText("No bluetooth adapter available");
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
if(mac)
{
    if (device.getAddress().equalsIgnoreCase(device_address)) {

        mmDevice = device;
        Log.e("Bluetooth Adapter", "DEVICE FOUND by mac=>"+mmDevice.getName()+" mac => "+mmDevice.getAddress());

        break;
    }
}else {
    if (device.getName().equalsIgnoreCase(device_address)) {

        mmDevice = device;
        Log.e("Bluetooth Adapter", "DEVICE FOUND by name =>"+mmDevice.getName());

        break;
    }
}

                    // openBT();
                }
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
            bti.on_device_error(mmDevice,e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            bti.on_device_error(mmDevice,e.getMessage());
        }
    }
    static void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 13; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            handle_response(data);

                                        }
                                    });
                                }
                                else
                                {
                                    try {
                                        readBuffer[readBufferPosition++] = b;
                                    }catch (Exception ex){}
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
                handler.post(new Runnable()
                {
                    public void run()
                    {
                      //  Bt_status_text.setText("Status :Disconnected");
                    }
                });


            }
        });

        workerThread.start();
    }

    static void openBT() throws IOException {
        try {
            // Standard SerialPortService ID

            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket1 = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket1.connect();
            mmOutputStream = mmSocket1.getOutputStream();
            mmInputStream = mmSocket1.getInputStream();
            //progress.dismiss();
            bti.on_device_connection_changed(true,mmDevice);


            beginListenForData();
        } catch (NullPointerException e) {
            try {

                mmSocket1 = (BluetoothSocket) mmDevice
                        .getClass()
                        .getMethod("createRfcommSocket", new Class[]{int.class}).invoke(mmDevice, 1);
                mmSocket1.connect();
                mmOutputStream = mmSocket1.getOutputStream();
                mmInputStream = mmSocket1.getInputStream();
                Log.e("Connected", "To Bluetooth");
               bti.on_device_connection_changed(true,mmDevice);
                beginListenForData();
            } catch (Exception e2) {
               // bti.on_device_connection_failed(mmDevice);
                try {
                    closeBT();
                } catch (IOException e3) {
                    Log.v("unable to closesocket",
                            e3.getMessage());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
//            Bt_status_text.setText("Status :Unable to connect to :"+mmDevice.getName());
bti.on_device_connection_failed(mmDevice);
            try {
                closeBT();
            } catch (IOException e3) {
                Log.v("unable to close socket",
                        e3.getMessage());
            }
        }
    }

    public static void closeBT() throws IOException {
        try {
            Log.v("Bt closing=>", " ");
            // mmOutputStream.close();
            mmSocket1.close();
            mmSocket1 = null;
            safeClose(mmSocket1);
            bti.on_device_connection_changed(false,mmDevice);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static void handle_response(String response)
    {
        Log.e("DATA IN =>"," "+response);
bti.on_data_received(mmDevice,response);
        try {
            //String parsed=response.split("    ")[1].replace("�","").trim();
            String parsed = response.replaceAll("\\s{2,}", " ").trim().split(" ")[1].replace("�", "").trim();
            bti.on_data_parsed(mmDevice, parsed);
            Log.e("DATA PARSED=>", " " + parsed);
        }catch (Exception ex){}



    }
    private static void safeClose(Closeable c) {
        if (c == null) {
            return;
        }
        for (int retries = 3; retries > 0; retries--) {
            try {
                c.close();
                bti.on_device_connection_changed(false,mmDevice);

            } catch (IOException e) {
                e.printStackTrace();
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}
