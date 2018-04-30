package com.example.christine.horse;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.livelife.motolibrary.AntData;
import com.livelife.motolibrary.MotoConnection;

import java.util.ArrayList;

/**
 * Created by sindribergsson on 29/04/2018.
 */

public class HorseTiming extends com.example.christine.horse.Game {

    /** tilePress class for grouping tileId and pressTime
     *
     */


    /**
     * The following private variables and methods are used internally in the class
     */
    private ArrayList<TilePress> sequence = new ArrayList<>(); //This is the stepping sequence
    private ArrayList<TilePress> seqToCompare = new ArrayList<>();
    private Players players; //Players
    private int delta; //Timing delta in ms
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
    public HorseTiming(int delta) {this.delta = delta;} //One parameter Constructor
    public HorseTiming() {this(100);} //Default constructor overload. Init delta to 100 ms

    /**
     * Public get-methods for data access
     */
    public void setTimingDelta(int delta){this.delta = delta;}

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

            if(step < seqSize) {

                //CASE: Build Sequence
                if (currentPlayer == players.getRound()) {
                    TilePress myTile = new TilePress(tId, SystemClock.uptimeMillis());
                    sequence.add(myTile);
                } else { //CASE: Compare to Sequence
                    if (tId == sequence.get(step).getTileId()) {
                        //Check if timing is correct
                        long curTime = SystemClock.uptimeMillis();
                        long timeDelta = timeInterval(seqToCompare.get(step - 1).getTime(), curTime);
                        if (java.lang.Math.abs(timeDelta - sequence.get(step).getTime()) <= delta) {

                            //Time is within margin
                            /**
                             * FIX THIS LOGIC
                             */
                            TilePress myTile = new TilePress(tId, curTime);
                            seqToCompare.add(myTile);

                            //Increment Score or whatever

                        } else {

                            //Time is not within margin. Die, lose point?
                        }
                        step++;
                    } else {

                        //What should happen if tile id does not match
                        //Die Lose Point whatever

                    }
                }

                step++;

                connection.setTileColor(currentPlayer, tId);
                handler.postDelayed(tileColorRunnable(tId), colorOffDelay);
                if (step == seqSize) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            nextPlayer(knockout);
                        }
                    }, 700);
                }
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

    private void nextPlayer(boolean knockout) {

        /**
         * FIX THIS LOGIC
         */

        handler.removeCallbacksAndMessages(null);
        step = 0;
        seqToCompare.clear();
        int t = players.nextPlayer(knockout);
        if (t == 0) gameOver();
        else {
            currentPlayer = t;
            if (currentPlayer==players.getRound()) round
            connection.setAllTilesColor(currentPlayer);
        }
    }

    private void gameOver(){
        /**
         * FIX THIS LOGIC
         */
    }

    private void setupNewGame(){
        sequence.clear();
        seqToCompare.clear();
        numPlayers = this.getNumPlayers();
        players = new Players(numPlayers);
        step=0; seqSize=0;
        connection.setAllTilesColor(players.getCurrent()); //What if this is out of bound?
    }

    private long timeInterval(long t1, long t2){return t2-t1;}
}
