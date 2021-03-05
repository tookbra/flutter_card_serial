package com.washer.sdk.flutter.card.serial;

import android.util.Log;

import com.licheedev.hwutils.ByteUtil;
import com.licheedev.myutils.LogPlus;
import com.licheedev.serialworker.core.DataReceiver;
import com.licheedev.serialworker.core.ValidData;
import com.washer.sdk.flutter.card.serial.command.Protocol;
import com.washer.sdk.flutter.card.serial.command.RecvCommand;
import com.washer.sdk.flutter.card.serial.command.receive.AutoReadCommand;
import com.washer.sdk.flutter.card.serial.command.receive.ReceiveCardIdCommand;
import com.washer.sdk.flutter.card.serial.command.util.ByteUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 数据接收
 */
public class CardDataReceiver implements DataReceiver<RecvCommand> {

    private final ByteBuffer mByteBuffer;

    public CardDataReceiver() {
        mByteBuffer = ByteBuffer.allocate(2048);
        mByteBuffer.clear();
    }

    @Override
    public void onReceive(ValidData validData, byte[] bytes, int offset, int length) {
        try {
//            LogPlus.i("Receiver", "接收数据=" + ByteUtil.bytes2HexStr(bytes, offset, length));

            mByteBuffer.put(bytes, 0, length);
            mByteBuffer.flip();

            byte head = Protocol.FRAME_HEAD;
            byte[] twoBytes = new byte[1];

            byte b;
            int readable;
            out:
            while ((readable = mByteBuffer.remaining()) >= Protocol.MIN_PACK_LEN) {
                mByteBuffer.mark(); // 标记一下开始的位置
                int frameStart = mByteBuffer.position();
                b = mByteBuffer.get();
                if (b != head) { // 不满足包头，就跳到第二位，重新开始
                    mByteBuffer.position(frameStart);
                    continue out;
                }
                // 获取设备地址
                mByteBuffer.get();
                // 数据长度
                mByteBuffer.get(twoBytes);
                final int dataLen = (int) ByteUtil.bytes2long(twoBytes, 0, 1);
                // 如果数据域长度过大，表示数据可能出现异常
                if (dataLen > Protocol.MAX_N) {
                    //回到“第二位”
                    mByteBuffer.position(frameStart);
                    continue;
                }
                // 总数据长度
                final int total = 5 + dataLen;
                // 如果可读数据小于总数据长度，表示不够,还有数据没接收
                if (readable < total) {
                    // 重置一下要处理的位置,并跳出循环
                    mByteBuffer.reset();
                    break;
                }

                // 找到校验位
                mByteBuffer.position(mByteBuffer.position() + dataLen);
                // 回到头
                mByteBuffer.reset();
                // 拿到整个包
                byte[] allPack = new byte[total];
                mByteBuffer.get(allPack);
                byte calXor = ByteUtils.makeCrc(allPack, 1, dataLen + 4);
                //LogPlus.e("xor=" + xor + ",calXor=" + calXor);
                // 校验通过
                if (allPack[0] == -86 && allPack[allPack[2] + 4] == -69 && 0 == calXor) {
                    // 收到有效数据
                    validData.add(allPack);
                } else {
                    // 不一致则回到“第二位”，继续找到下一个3BB3
                    mByteBuffer.position(frameStart);
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            // 最后清掉之前处理过的不合适的数据
            mByteBuffer.compact();
        }
    }

    @Override
    public RecvCommand adaptReceive(byte[] allPack) {
        RecvCommand recvCommand = null;
        try {
            final int dataLen = (int)ByteUtil.bytes2long(allPack, 2, 1);
            final byte[] data = new byte[dataLen - 1];

            if (data.length == 1) {
                System.arraycopy(allPack, 4, data, 0, data.length);
                recvCommand = new AutoReadCommand(allPack, 1, data);
            } else {
                System.arraycopy(allPack, 5, data, 0, data.length);
                recvCommand = new ReceiveCardIdCommand(allPack, 0, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recvCommand;
    }

    @Override
    public void resetCache() {
        mByteBuffer.clear();
    }
}
