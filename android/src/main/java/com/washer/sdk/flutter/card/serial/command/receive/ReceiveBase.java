package com.washer.sdk.flutter.card.serial.command.receive;

import android.os.SystemClock;

import com.licheedev.hwutils.ByteUtil;
import com.washer.sdk.flutter.card.serial.command.RecvCommand;

import java.net.InetSocketAddress;

/**
 * 刷卡接收到的命令封装
 */
public class ReceiveBase implements RecvCommand {

    private final byte[] mAllPack;
    protected final byte[] data;
    protected final int cmd;
    private final long mRecvTime;
    private InetSocketAddress mAddress;

    public ReceiveBase(byte[] allPack, int cmd, byte[] data) {
        mAllPack = allPack;
        this.cmd = cmd;
        this.data = data;
        mRecvTime = SystemClock.uptimeMillis();
    }

    public int getCmd() {
        return cmd;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public long getRecvTime() {
        return mRecvTime;
    }

    @Override
    public byte[] getAllPack() {
        return mAllPack;
    }

    @Override
    public String toString() {
        return "数据=" + ByteUtil.bytes2HexStr(mAllPack);
    }
}
