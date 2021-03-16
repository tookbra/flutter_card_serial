package com.washer.sdk.flutter.card.serial;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;

import com.licheedev.serialworker.core.DataReceiver;
import com.licheedev.serialworker.worker.RxRs232SerialWorkerX;
import com.washer.sdk.flutter.card.serial.command.RecvCommand;
import com.washer.sdk.flutter.card.serial.command.SendCommand;


/**
 * 刷卡串口操作
 */
public class CardSerialWorker extends RxRs232SerialWorkerX<SendCommand, RecvCommand> {

    private final Handler mRecvHandler;

    public CardSerialWorker(@Nullable Handler recvHandler) {
        mRecvHandler = recvHandler;
    }

    @Override
    public boolean isMyResponse(SendCommand sendData, RecvCommand recvData) {
        // 如果收到的命令跟发送的命令是同类型
//        return sendData.getCmd() == recvData.getCmd();
        return true;
    }

    @Override
    public DataReceiver<RecvCommand> newReceiver() {
        return new CardDataReceiver();
    }

    @Override
    public void onReceiveData(final RecvCommand recvData) {
        // 把数据暴露出去
        if (mReceiveCallback != null) {
            if (mRecvHandler != null) {
                mRecvHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mReceiveCallback.onReceive(recvData);
                    }
                });
            } else {
                mReceiveCallback.onReceive(recvData);
            }
        }
    }

    private ReceiveCallback mReceiveCallback;

    public interface ReceiveCallback {

        void onReceive(RecvCommand recvCommand);
    }

    public void setReceiveCallback(ReceiveCallback receiveCallback) {
        mReceiveCallback = receiveCallback;
    }
}