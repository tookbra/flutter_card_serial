package com.washer.sdk.flutter.card.serial.command.send;

import android.os.SystemClock;

import com.licheedev.hwutils.ByteUtil;
import com.washer.sdk.flutter.card.serial.command.Protocol;
import com.washer.sdk.flutter.card.serial.command.SendCommand;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 刷卡发送的数据
 */
abstract class SendBase implements SendCommand {

    protected byte[] mBytes;
    protected int cmd;
    private long mSendTime;

    public SendBase(@Protocol.Cmd int cmd) {
        this.cmd = cmd;
    }

    @Protocol.Cmd
    public int getCmd() {
        return cmd;
    }

    /**
     * 获取数据，即协议中的数据N
     *
     * @return
     */
    protected abstract byte[] getDataN();

    /**
     * 帧头	 设备地址  数据长度 命令码	 数据   校验和 终止位
     * 1字节  1字节	  1字节	  1字节	 1字节  1字节  N字节
     */
    public synchronized byte[] toBytes() {

        byte[] bytes = mBytes;

        if (bytes == null) {

            byte[] dataN = getDataN();
            if (dataN == null) {
                dataN = new byte[0];
            }

            int packLen = Protocol.MIN_PACK_LEN + dataN.length;
            bytes = new byte[packLen];

            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            byteBuffer.clear();
            // 填充START FLAG1	START FLAG2
            byteBuffer.put(Protocol.FRAME_HEAD);
            // 地址
            byteBuffer.put(Protocol.ADDRESS);
            //数据长度：1字节，包括命令码、协议版本和数据域的长度
            byteBuffer.put((byte)(dataN.length + 1));
            //cmd
            byteBuffer.put((byte) cmd);
            // 填充数据N
            byteBuffer.put(dataN);
            // 计算校验和
            byte xor = ByteUtil.getXOR(bytes, 1, 4 + dataN.length);
            byteBuffer.put(xor);

            byteBuffer.put(Protocol.END);
            mBytes = bytes;
        }
        return bytes;
    }

    @Override
    public long getSendTime() {
        return mSendTime;
    }

    @Override
    public void updateSendTime() {
        mSendTime = SystemClock.elapsedRealtime();
    }

    @Override
    public long timeout() {
        return 0;
    }
}
