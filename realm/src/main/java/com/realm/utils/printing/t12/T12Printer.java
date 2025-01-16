package com.realm.utils.printing.t12;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;

import com.realm.utils.printing.Printer;


public class T12Printer extends Printer {


    private PrinterInstance mPrinter;

    public T12Printer(Activity context, Printer.PrintingInterface printingInterface) {
        super(context, printingInterface);
        logTag = "BTPrinter";
    }

    @Override
    public void print() {
        super.print();
        if (deviceMacAddress != null) {
            mPrinter = new PrinterInstance(context, getDevice(deviceMacAddress), mHandler);
            mPrinter.openConnection();
        }
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PrinterConstants.Connect.SUCCESS:

//                    Toast.makeText(context, "Impression...", Toast.LENGTH_SHORT).show();
                    printingInterface.onPrinterConnected(deviceMacAddress);
                    printingInterface.onReadyToPrint(mPrinter);
                    printingInterface.onPrintComplete();
                    mPrinter.closeConnection();
                    break;
                case PrinterConstants.Connect.FAILED:

                    printingInterface.onPrintError("Connection failed");

                    break;
                case PrinterConstants.Connect.CLOSED:

//                    Toast.makeText(context, "connect close...", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }


        }

    };



}