package com.washer.sdk.flutter.card.serial.command;

import com.licheedev.serialworker.core.RecvData;

/**
 * 接收到的数据（空白接口）
 */
public interface RecvCommand extends RecvData {

    int getCmd();
}
