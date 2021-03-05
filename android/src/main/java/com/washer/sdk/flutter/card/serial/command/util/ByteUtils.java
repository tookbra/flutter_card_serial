package com.washer.sdk.flutter.card.serial.command.util;

public class ByteUtils {

    public static byte makeCrc(byte[] data, int startPos, int endPos) {
        byte crc = 0;

        for(int i = startPos; i < endPos; ++i) {
            crc ^= data[i];
        }

        return crc;
    }
}
