package com.example.christine.horse;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.livelife.motolibrary.AntData;
import com.livelife.motolibrary.MotoConnection;
import java.util.ArrayList;
import java.lang.Object;
import java.lang.Thread;

public class HorseHighScore extends com.example.christine.horse.Game {
    MotoConnection connection = MotoConnection.getInstance();

    // This is the horse game class
    ArrayList<Integer> sequence = new ArrayList<>();
    ArrayList<Integer> players = new ArrayList<>();
    int numPlayers;
    int round;
    int maxRound = 10;
    int step;
    //final int base = 4;

    @Override
    public void onGameStart() {
        super.onGameStart();
        numPlayers = this.getNumPlayers();
        for (int i=1; i<=numPlayers; i++) {
            players.add(i);
        }
        round = 1;
        step = 1;
        connection.setAllTilesColor(players.get(0));
        Log.v("","Game started: Number of Players:" + numPlayers);
    }

    @Override
    public void onGameUpdate(byte[] message) {
        super.onGameUpdate(message);
        int cmd = AntData.getCommand(message);
        int tile = AntData.getId(message);

        if (cmd == AntData.EVENT_PRESS && step <= round) {
            if (step==1)connection.setAllTilesColor(0);

            if (step < round) { // Compare to sequence
                if (tile == sequence.get(step-1)){ //Correct
                    connection.setTileColor(players.get(0),tile);
                    handler.postDelayed(createRunnable(tile),700);
                    step++;
                } else {
                    wrongMove(tile);
                }
            } else if (step == round){ // Add to sequence
                sequence.add(tile);
                connection.setTileColor(players.get(0),tile);
                handler.postDelayed(createRunnable(tile),700);
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
        connection.setAllTilesToInit();
        handler.removeCallbacksAndMessages(null);
        Log.v("","GAME ENDED");
    }

    public void wrongMove(final int tile) {
        connection.setTileColor(players.get(0),tile);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                connection.setTileColor(0,tile);
            }
        }, 200);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {connection.setTileColor(players.get(0),tile);
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
            public void run() {connection.setTileColor(players.get(0),tile);
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
            public void run() {connection.setTileColor(players.get(0),tile);
            }
        }, 1200);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {nextRound(true);
            }
        }, 1500);
    }

    public void nextRound(boolean failedStep) {
        handler.removeCallbacksAndMessages(null);
        this.incrementScore(step, players.get(0));
        step = 1;
        int t = players.remove(0);
        players.add(t);
        if (!failedStep) round++;
        if (round >= maxRound) {
            winAnimation(this.getWinner());
        }
        connection.setAllTilesColor(players.get(0));
    }

    public void winAnimation(final int winner) {
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

    Handler handler = new Handler();
    private Runnable createRunnable(final int tile){
        Runnable lightDelay = new Runnable() {
            @Override
            public void run() {
                connection.setTileColor(0,tile);
            }
        };
        return lightDelay;
    }
}