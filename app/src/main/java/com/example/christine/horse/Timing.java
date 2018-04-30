package com.example.christine.horse;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.livelife.motolibrary.AntData;
import com.livelife.motolibrary.MotoConnection;

import java.util.ArrayList;

import static android.os.SystemClock.uptimeMillis;

/**
 * Created by Martin on 4/21/2018.]
 */

public class Timing extends com.example.christine.horse.Game {

    /** tilePress class for grouping tileId and pressTime
     *
     */
    private class TilePress {
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

    /**
     * The following private variables and methods are used internally in the class
     */
    private ArrayList<TilePress> sequence = new ArrayList<>(); //This is the stepping sequence
    private ArrayList<TilePress> seqToCompare = new ArrayList<>();
    private int delta; //Timing delta in ms
    private int stepsToAdd;
    private int numPlayers, currentPlayer, seqSize, step; //Game control variables

    private MotoConnection connection = MotoConnection.getInstance(); //Tiles connection
    Handler handler = new Handler();
    private int colorOffDelay = 700; //In ms

    private Runnable tileColorRunnable(final int tileId){
        Runnable lightDelay = new Runnable() {
            @Override
            public void run() {
                connection.setTileColor(0,tileId);
            }
        };
        return lightDelay;
    }

    /**
     * Class constructors. No destructor, assign, or copy necessary.
     */
    public Timing(int delta, int stepsToAdd) {this.delta = delta; this.stepsToAdd = stepsToAdd;} //One parameter Constructor
    public Timing() {this(100, 1);} //Default constructor overload. Init delta to 100 ms

    /**
     * Public get-methods for data access
     */
    public void setTimingDelta(int delta){this.delta = delta;}
    public void setStepsToAdd(int stepsToAdd){this.stepsToAdd = stepsToAdd;}

    /**
     * Overwritten Game methods. Necessary for Game inheritance
     */
    @Override
    public void onGameStart(){
        super.onGameStart();

        setupNewGame();

        Log.v("START","Timing Game started! Number of Players:" + numPlayers);
    }

    @Override
    public void onGameUpdate(byte[] message){
        super.onGameUpdate(message);
        int cmd = AntData.getCommand(message); //The following command explains what should happen

        if(cmd == AntData.EVENT_PRESS){
            int tId = AntData.getId(message);
            //Case: Compare Sequence
            if(step < seqSize){
                if(tId == sequence.get(step).getTileId()){
                    //If tile Id match, check if timing is correct
                    if(step == 0){
                        //Get the reference time. This ensures time to switch player
                        TilePress myTile = new TilePress(tId, SystemClock.uptimeMillis());
                        seqToCompare.add(myTile);
                    } else {
                        //Check if timing is correct
                        long curTime = SystemClock.uptimeMillis();
                        long timeDelta = timeInterval(seqToCompare.get(step-1).getTime(), curTime);
                        if(java.lang.Math.abs(timeDelta - sequence.get(step).getTime()) <= delta){
                            //Time is within margin
                            TilePress myTile = new TilePress(tId, curTime);
                            seqToCompare.add(myTile);
                            //Increment Score or whatever
                        } else{
                            //Time is not within margin. Die, lose point?
                        }
                    }
                    step++;
                } else {
                    //What should happen if tile id does not match
                    //Die Lose Point whatever
                }
            } else { //If step >= seqSize
                //Case: Add to sequence
                long curTime = SystemClock.uptimeMillis();
                TilePress myTile = new TilePress(tId, curTime);
                seqToCompare.add(myTile);
                if(seqSize == 0){
                    sequence.add(seqToCompare.get(step));
                } else {
                    //Add the timeInterval to the sequence instead of the currentTime.
                    myTile = new TilePress(tId, timeInterval(seqToCompare.get(step-1).getTime(),seqToCompare.get(step).getTime()));
                    sequence.add(myTile);
                }
                Log.v("", "TilePress Object added: " + myTile.convertToString());
                step++;
            }
            connection.setTileColor(currentPlayer, tId);
            handler.postDelayed(tileColorRunnable(tId), colorOffDelay);
            if(step == seqSize+stepsToAdd){
               handler.postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       nextPlayer();
                   }
               }, 700 );
            }
        }
    }

    @Override
    public void onGameEnd(){
        super.onGameEnd();
        connection.setAllTilesToInit();
        handler.removeCallbacksAndMessages(null); //Removes all scheduled messages from queue

        Log.v("END", "Game has ended.");
    }


    /**
     * Helper Methods
     */

    private void nextPlayer() {
        handler.removeCallbacksAndMessages(null);
        step = 0;
        seqSize += stepsToAdd;
        seqToCompare.clear();
        currentPlayer++;
        if (currentPlayer>numPlayers) currentPlayer=1;
        connection.setAllTilesColor(currentPlayer);
    }

    private void setupNewGame(){
        sequence.clear();
        seqToCompare.clear();
        numPlayers = this.getNumPlayers();
        step=0; seqSize=0; currentPlayer=1;
        connection.setAllTilesColor(1); //What if this is out of bound?
    }

    private long timeInterval(long t1, long t2){return t2-t1;}
}