package com.washer.sdk.flutter.card.serial.command.receive;


public class ReceiveCardIdCommand extends ReceiveBase {

    private final String result;

    public ReceiveCardIdCommand(byte[] allPack, int cmd, byte[] data) {
        super(allPack, cmd, data);

        String strData = "";
        for (int i = 0; i < data.length; i++) {
            strData += String.format("%02X ", data[i]);
        }
        result = strData;
    }

    public String getResult() {
        return result;
    }
}
