package com.example.christine.horse;

import android.os.Handler;
import android.util.Log;

import com.livelife.motolibrary.AntData;
import com.livelife.motolibrary.MotoConnection;
import java.util.ArrayList;
import android.os.SystemClock;

public class HorseKnockout extends com.example.christine.horse.Game {
    MotoConnection connection = MotoConnection.getInstance();
    Handler handler = new Handler();

    //This is the Horse Knockout Game Class

    private class TilePress {
        private int tileId;
        private long time; //In ms

        //TilePress constructor
        private TilePress(int tId, long t){
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

    private ArrayList<TilePress> sequence = new ArrayList<>(); //This is the stepping sequence
    private ArrayList<TilePress> seqToCompare = new ArrayList<>();
    ArrayList<Integer> players = new ArrayList<>();
    private int numPlayers, seqSize, step; //Class control variables
    private boolean timing;
    private long delta; //Timing variable in ms


    public HorseKnockout(boolean timing, long delta){
        if(timing == true){
            this.timing = true;
            this.delta = delta;
        } else {
            timing = false;
        }
    }
    public HorseKnockout(boolean timing) {
        if (timing == true) {
            this.timing = true;
            this.delta = 300; //Default delta
        } else {
            this.timing = false;
        }
    }

    public HorseKnockout(){this(false);}

    @Override
    public void onGameStart() {
        super.onGameStart();
        numPlayers = this.getNumPlayers();

        //Fill player array
        for (int i=1; i<=numPlayers; i++) {
            players.add(i);
        }

        seqSize = 0; step = 0;
        connection.setAllTilesColor(players.get(0)); //Set color to be color idx player 1
        Log.v("","Game started: Number of Players:" + numPlayers + "Player 1 begin.");
    }

    @Override
    public void onGameUpdate(byte[] message) {
        super.onGameUpdate(message);
        int cmd = AntData.getCommand(message);
        int tId = AntData.getId(message);

        if (cmd == AntData.EVENT_PRESS ) {
            if (step==0)connection.setAllTilesColor(0); //Turn off all tiles

            //Compare Case
            if (step < seqSize) {
                if (tId == sequence.get(step).getTileId()) { //Correct tile id
                    connection.setTileColor(players.get(0), tId);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            connection.setAllTilesColor(0);
                        }
                    }, 700);

                    if (timing == true) {
                        long curTime = SystemClock.uptimeMillis();

                        if (step == 0) {
                            TilePress myTile = new TilePress(tId, curTime);
                            seqToCompare.add(myTile);
                            step++;
                        } else {
                            long timeDelta = timeInterval(seqToCompare.get(step - 1).getTime(), curTime);
                            if (java.lang.Math.abs(timeDelta - sequence.get(step).getTime()) <= delta) {
                                //Correct timing
                                TilePress myTile = new TilePress(tId, curTime);
                                seqToCompare.add(myTile);
                                step++;
                            } else {
                                //Timing does not match
                                //Knockout
                                knockout();
                            }
                        }
                    }

                } else { //If tileid does not match
                    knockout();
                }
            } else { // Add to sequence
                long curTime = SystemClock.uptimeMillis();

                if(step == 0){
                    TilePress myTile = new TilePress(tId, curTime);
                    sequence.add(myTile);
                } else {
                    long tInt = timeInterval(seqToCompare.get(step-1).getTime(), curTime);
                    TilePress myTile = new TilePress(tId, curTime);
                    sequence.add(myTile);
                }
                step++;

                connection.setTileColor(players.get(0),tId);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {connection.setAllTilesColor(0);}
                },700);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nextRound(false);
                    }
                }, 700);
            }
        }
    }

    @Override
    public void onGameEnd() {
        super.onGameEnd();
        sequence.clear();
        seqToCompare.clear();
        connection.setAllTilesToInit();
        handler.removeCallbacksAndMessages(null);
        Log.v("","GAME ENDED");
    }

    private void nextRound(boolean knockout) {
        handler.removeCallbacksAndMessages(null);
        step = 0;
        int t = players.remove(0);
        if (!knockout) {players.add(t); seqSize++;}
        if (players.size()==1){
            // WE HAVE A WINNER!!!
            // run win animation
            // End game
            winAnimation(players.get(0));
        }
        connection.setAllTilesColor(players.get(0));
    }

    //Creates animation when player is knocked out
    private void knockout() {
        connection.setAllTilesColor(players.get(0));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                connection.setAllTilesColor(0);
            }
        }, 200);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {connection.setAllTilesColor(players.get(0));
            }
        }, 400);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                connection.setAllTilesColor(0);
            }
        }, 600);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {connection.setAllTilesColor(players.get(0));
            }
        }, 800);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                connection.setAllTilesColor(0);
            }
        }, 1000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {connection.setAllTilesColor(players.get(0));
            }
        }, 1200);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {nextRound(true);
            }
        }, 1500);
    }

    private void winAnimation(final int winner) {
        connection.setAllTilesColor(players.get(0));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                connection.setAllTilesColor(0);
            }
        }, 100);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {connection.setAllTilesColor(winner);
            }
        }, 200);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                connection.setAllTilesColor(0);
            }
        }, 300);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {connection.setAllTilesColor(winner);
            }
        }, 400);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                connection.setAllTilesColor(0);
            }
        }, 500);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {connection.setAllTilesColor(winner);
            }
        }, 600);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                connection.setAllTilesColor(0);
            }
        }, 1100);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {connection.setAllTilesColor(winner);
            }
        }, 1600);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                connection.setAllTilesColor(0);
            }
        }, 2100);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {connection.setAllTilesColor(winner);
            }
        }, 2600);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                connection.setAllTilesColor(0);
            }
        }, 3100);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {connection.setAllTilesColor(winner);
            }
        }, 3600);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {endGame();
            }
        }, 5000);
    }

    private void endGame(){
        this.stopGame();
    }

    private long timeInterval(long t1, long t2){return t2-t1;}
}
