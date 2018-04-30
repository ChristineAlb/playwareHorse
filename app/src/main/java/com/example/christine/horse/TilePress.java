package com.example.christine.horse;

public class TilePress {
    private int tileId;
    private long time; //In ms

    //TilePress constructor
    TilePress(int tId, long t){
        this.tileId = tId;
        this.time = t;
    }

    //Data access
    public void setTileId(int tId){ this.tileId = tId;}
    public int getTileId() {return this.tileId;}
    public void setTime(long t){ this.time = t;}
    public long getTime() {return this.time;}
    public String convertToString(){return "<TileId: " + this.tileId + ", Time: " + this.time + ">";}
}
