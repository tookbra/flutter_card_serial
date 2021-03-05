package com.washer.sdk.flutter.card.serial.command.receive;

public class AutoReadCommand extends ReceiveBase {
    private final int mResult;

    public AutoReadCommand(byte[] allPack, int cmd, byte[] data) {
        super(allPack, cmd, data);
        mResult = 0xff & data[0];
    }

    public int getResult() {
        return mResult;
    }
}
