package com.washer.sdk.flutter.card.serial.command.receive;


import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Arrays;
import java.util.Collections;
import java.util.StringJoiner;

public class ReceiveCardIdCommand extends ReceiveBase {

    private final String result;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ReceiveCardIdCommand(byte[] allPack, int cmd, byte[] data) {
        super(allPack, cmd, data);

        String strData = "";
        for (int i = 0; i < data.length; i++) {
            strData += String.format("%02X ", data[i]);
        }

        if(!strData.equals("")) {
            String [] arr = strData.split(" ");
            Collections.reverse(Arrays.asList(arr));
            StringJoiner joiner = new StringJoiner("");
            for (String v: arr) {
                joiner.add(v);
            }
            strData = joiner.toString();
        }

        result = strData;
    }

    public String getResult() {
        return result;
    }
}
