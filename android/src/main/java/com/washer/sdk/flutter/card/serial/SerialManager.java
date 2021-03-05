package com.washer.sdk.flutter.card.serial;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.serialport.SerialPort;

import com.licheedev.myutils.LogPlus;
import com.licheedev.serialworker.core.Callback;
import com.washer.sdk.flutter.card.serial.command.Protocol;
import com.washer.sdk.flutter.card.serial.command.RecvCommand;
import com.washer.sdk.flutter.card.serial.command.receive.AutoReadCommand;
import com.washer.sdk.flutter.card.serial.command.receive.ReceiveCardIdCommand;
import com.washer.sdk.flutter.card.serial.command.send.SendAutoCommand;

import org.greenrobot.eventbus.EventBus;

/**
 * 串口管理器
 */
public class SerialManager {

    private static final String TAG = "SerialManager";

    private static String CARD_SERIAL = "/dev/ttyXRM0";
    private static int CARD_BAUD_RATE = 9600;


    private final HandlerThread dispatchThread;
    private final Handler dispatchThreadHandler;
    private static volatile SerialManager sManager = null;
    private final CardSerialWorker cardSerialWorker;

    /**
     * [单例]获取串口管理器
     *
     * @return
     */
    public static SerialManager get() {

        SerialManager manager = sManager;
        if (manager == null) {
            synchronized (SerialManager.class) {
                manager = sManager;
                if (manager == null) {
                    manager = new SerialManager();
                    sManager = manager;
                }
            }
        }
        return manager;
    }

    private SerialManager() {

        dispatchThread = new HandlerThread("serial-dispatch-thread");
        dispatchThread.start();
        dispatchThreadHandler = new Handler(dispatchThread.getLooper());

        // 
        cardSerialWorker = new CardSerialWorker(dispatchThreadHandler);
        cardSerialWorker.setDevice(CARD_SERIAL, CARD_BAUD_RATE); // 串口地址，波特率
        cardSerialWorker.setParams(8, 0, 1); // 8数据位，无校验，1停止位
        // 设置超时
        cardSerialWorker.setTimeout(Protocol.RECEIVE_TIME_OUT);
        // 开启打印日志
        cardSerialWorker.enableLog(true, true);
        // 设置回调
        cardSerialWorker.setReceiveCallback(new CardSerialWorker.ReceiveCallback() {
            @Override
            public void onReceive(RecvCommand recvCommand) {
                if(recvCommand.getCmd() != 1) {
                    ReceiveCardIdCommand receive = (ReceiveCardIdCommand) recvCommand;
                    EventBus.getDefault().post(receive.getResult());
                }
            }
        });
    }


    public void initDevice() throws Exception {
        cardSerialWorker.setDevice(CARD_SERIAL, CARD_BAUD_RATE);
        cardSerialWorker.openSerial();
    }


    /**
     * 开启自动读
     */
    public void setAutoRead(SendAutoCommand command, Callback<AutoReadCommand> callback) throws Exception {
        cardSerialWorker.send(command, AutoReadCommand.class, callback);
    }


    /**
     * 释放资源
     */
    public synchronized void release() {

        cardSerialWorker.release();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            dispatchThread.quitSafely();
        } else {
            dispatchThread.quit();
        }
        sManager = null;
    }
}
