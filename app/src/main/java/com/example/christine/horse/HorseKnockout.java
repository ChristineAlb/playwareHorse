package com.example.christine.horse;

import android.os.Handler;
import android.util.Log;

import com.livelife.motolibrary.AntData;
import com.livelife.motolibrary.MotoConnection;
import java.util.ArrayList;

public class HorseKnockout extends com.example.christine.horse.Game {
    MotoConnection connection = MotoConnection.getInstance();

    // This is the horse game class
    ArrayList<Integer> sequence = new ArrayList<>();
    ArrayList<Integer> players = new ArrayList<>();
    private int numPlayers;
    private int round;
    private int step;
    //final int base = 4;

    // Hello there!

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
                if (tile == sequence.get(step-1)){ // Correct move
                    connection.setTileColor(players.get(0),tile);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {connection.setAllTilesColor(0);}
                    },700);
                    step++;
                } else { // Wrong move
                    knockout();
                }
            } else if (step == round){ // Add to sequence
                sequence.add(tile);
                step++;
                connection.setTileColor(players.get(0),tile);
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
        connection.setAllTilesToInit();
        handler.removeCallbacksAndMessages(null);
        Log.v("","GAME ENDED");
    }

    private void nextRound(boolean knockout) {
        handler.removeCallbacksAndMessages(null);
        step = 1;
        int t = players.remove(0);
        if (!knockout) {players.add(t); round++;}
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

    Handler handler = new Handler();
}