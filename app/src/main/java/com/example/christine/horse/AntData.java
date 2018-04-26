//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.livelife.motolibrary;

public class AntData {
    public static final int LED_COLOR_OFF = 0;
    public static final int LED_COLOR_RED = 1;
    public static final int LED_COLOR_BLUE = 2;
    public static final int LED_COLOR_GREEN = 3;
    public static final int LED_COLOR_INDIGO = 4;
    public static final int LED_COLOR_ORANGE = 5;
    public static final int LED_COLOR_WHITE = 6;
    public static final int LED_COLOR_VIOLET = 7;
    public static final byte CMD_INIT = 25;
    public static final byte CMD_SETUP = 31;
    public static final byte CMD_START_BL = 35;
    public static final byte CMD_SET_COLOR = 1;
    public static final byte CMD_SET_IDLE = 23;
    public static final byte CMD_SINGLE_LED = 39;
    public static final byte CMD_FADE_BREATH = 42;
    public static final byte CMD_FADE_OUT = 43;
    public static final byte EVENT_PRESS = 22;
    public static final byte EVENT_RELEASE = 36;
    private byte[] payload = new byte[8];

    public AntData(byte targetId) {
        this.payload[0] = targetId;
        this.payload[1] = 0;
        this.payload[2] = 0;
        this.payload[3] = 0;
        this.payload[4] = 0;
        this.payload[5] = 0;
        this.payload[6] = 0;
        this.payload[7] = 0;
    }

    public byte[] getPayload() {
        return this.payload;
    }

    // Fade breath code, frequency is in ms and could for example be 10
    public void setFadeBreath(int color, int freq) {
        this.payload[1] = 42;
        this.payload[2] = (byte)color;
        this.payload[3] = (byte)freq;
        this.payload[4] = 0;
        this.payload[5] = 0;
        this.payload[6] = 0;
        this.payload[7] = 0;
    }

    // Fade out code
    public void setFadeBreath(int color, int tileIndex, int speed) {
        this.payload[1] = 43;
        this.payload[2] = (byte)color;
        this.payload[3] = (byte)tileIndex;
        this.payload[4] = (byte)speed;
        this.payload[5] = 0;
        this.payload[6] = 0;
        this.payload[7] = 0;
    }

    public void setColor(int color) {
        this.payload[1] = 1;
        this.payload[2] = (byte)color;
        this.payload[3] = 8;
        this.payload[4] = 0;
        this.payload[5] = 1;
        this.payload[6] = 0;
        this.payload[7] = (byte)MotoConnection.getInstance().getDeviceId();
    }

    public void setColor(int color, int numLeds) {
        this.payload[1] = 1;
        this.payload[2] = (byte)color;
        this.payload[3] = (byte)numLeds;
        this.payload[4] = 0;
        this.payload[5] = 1;
        this.payload[6] = 0;
        this.payload[7] = (byte)MotoConnection.getInstance().getDeviceId();
    }

    public void setSingleLed(int color, int ledIndex) {
        this.payload[1] = 39;
        this.payload[2] = (byte)color;
        this.payload[3] = (byte)ledIndex;
        this.payload[4] = 0;
        this.payload[5] = 0;
        this.payload[6] = 0;
        this.payload[7] = (byte)MotoConnection.getInstance().getDeviceId();
    }

    public void setBroadcastData(byte[] data) {
        this.payload = data;
    }

    public void setColorNoRelease(int color) {
        this.payload[1] = 1;
        this.payload[2] = (byte)color;
        this.payload[3] = 8;
        this.payload[4] = 0;
        this.payload[5] = 0;
        this.payload[6] = 0;
        this.payload[7] = (byte)MotoConnection.getInstance().getDeviceId();
    }

    public void setInit() {
        this.payload[1] = 25;
        this.payload[2] = 0;
        this.payload[3] = 0;
        this.payload[4] = 0;
        this.payload[5] = 0;
        this.payload[6] = 0;
        this.payload[7] = (byte)MotoConnection.getInstance().getDeviceId();
    }

    public void setIdle(int color) {
        this.payload[1] = 23;
        this.payload[2] = (byte)color;
        this.payload[3] = 8;
        this.payload[4] = 0;
        this.payload[5] = 0;
        this.payload[6] = 0;
        this.payload[7] = (byte)MotoConnection.getInstance().getDeviceId();
    }

    public void setSetup(byte scanId) {
        this.payload[1] = 31;
        this.payload[2] = scanId;
        this.payload[3] = 0;
        this.payload[4] = 0;
        this.payload[5] = 0;
        this.payload[6] = 0;
        this.payload[7] = (byte)MotoConnection.getInstance().getDeviceId();
    }

    public void setUpdateFirmware() {
        this.payload[1] = 35;
        this.payload[2] = 0;
        this.payload[3] = 0;
        this.payload[4] = 0;
        this.payload[5] = 0;
        this.payload[6] = 0;
        this.payload[7] = (byte)MotoConnection.getInstance().getDeviceId();
    }

    public static int getId(byte[] message) {
        return message[0];
    }

    public static int getCommand(byte[] message) {
        return message[1];
    }

    public static int getColorFromPress(byte[] message) {
        return message[3];
    }
}
