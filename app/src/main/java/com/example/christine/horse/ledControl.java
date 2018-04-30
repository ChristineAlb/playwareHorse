package com.example.christine.horse;

import com.livelife.motolibrary.AntData;
import com.livelife.motolibrary.MotoConnection;

/**
 * Created by sindribergsson on 30/04/2018.
 */

public class ledControl {

    public static void setSingleLed(MotoConnection connection, int color, int ledID, int tileID) {
        AntData data = new AntData((byte) tileID);
        data.setSingleLed(color, ledID);
        connection.update(data);
    }

    public static void setFadeBreathing(MotoConnection connection, int color, int frequency, int tileID) {
        AntData data = new AntData((byte) tileID);
        byte col = (byte) color;
        byte freq = (byte) frequency; // A frequency of 10 ms is nice
        byte tile = (byte) tileID;
        byte connect = (byte) MotoConnection.getInstance().getDeviceId();
        byte[] communication = {tile, 42, col, freq, 0, 0, 0, connect};
        data.setBroadcastData(communication);
        connection.update(data);
    }

    public static void setAllFadeBreathing(MotoConnection connection, int color, int frequency){
        for(int i=0; i<10; i++){
            setFadeBreathing(connection,color,frequency,i);
        }
    }

    public static void setFadeOut(MotoConnection connection, int color, int frequency, int tileID) {
        AntData data = new AntData((byte) tileID);
        byte col = (byte) color;
        byte freq = (byte) frequency; // A frequency of 20 ms is nice
        byte tile = (byte) tileID;
        byte connect = (byte)MotoConnection.getInstance().getDeviceId();
        byte[] communication = {tile, 43, col, freq, 0, 0, 0, connect};
        data.setBroadcastData(communication);
        connection.update(data);
    }
}
