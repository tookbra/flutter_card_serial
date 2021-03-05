package com.washer.sdk.flutter.card.serial.command;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 协议
 */
public interface Protocol {



    /**
     * 发送数据后，接收数据超时时间，毫秒
     */
    long RECEIVE_TIME_OUT = 3000;

    /**
     * 帧头
     */
    byte FRAME_HEAD = (byte) 0xAA;

    /**
     * 地址
     */
    byte ADDRESS = (byte) 0x00;

    /**
     * 终止字节
     */
    byte END = (byte) 0xBB;

    /**
     * 最小数据包的长度(除开数据的N个字节）
     * 数据长度=命令码+数据N
     * 帧头   数据长度  命令码     数据    校验和
     * 2       2         1        N       1
     */
    int MIN_PACK_LEN = 1 + 1 + 1 + 1 + 1 + 1;

    /**
     * 数据：0-N字节，N小于等于123
     */
    int MAX_N = 123;

    /**
     * 自动读
     */
    int CMD_AUTO_ANTICOLL = 0x90;

    /**
     * @hide
     */
    @IntDef({
        CMD_AUTO_ANTICOLL
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Cmd {
    }
}
