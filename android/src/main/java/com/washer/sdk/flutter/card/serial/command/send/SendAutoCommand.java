package com.washer.sdk.flutter.card.serial.command.send;


import com.washer.sdk.flutter.card.serial.command.Protocol;

public class SendAutoCommand extends SendBase {

    private final byte[] mData;

    public SendAutoCommand(byte [] data) {
        super(Protocol.CMD_AUTO_ANTICOLL);

        mData = data;
    }

    @Override
    protected byte[] getDataN() {
        return mData;
    }
}
