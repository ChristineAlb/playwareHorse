package com.example.christine.horse;

import android.os.Handler;
import android.util.Log;

import com.livelife.motolibrary.AntData;
import com.livelife.motolibrary.Game;
import com.livelife.motolibrary.MotoConnection;
import java.util.ArrayList;

public class Horse extends Game {
    MotoConnection connection = MotoConnection.getInstance();

    // This is the horse game class
    ArrayList<Integer> sequence = new ArrayList<>();
    ArrayList<Integer> players = new ArrayList<>();
    int numPlayers;
    int currentPlayer;
    int round;
    int step;
    //final int base = 4;

    // Hello there!

    @Override
    public void onGameStart() {
        sequence.clear();
        super.onGameStart();
        numPlayers = this.getNumPlayers();
        round = 1;
        step = 1;
        currentPlayer = 1;
        connection.setAllTilesColor(currentPlayer);
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
                if (tile == sequence.get(step-1)){
                    connection.setTileColor(currentPlayer,tile);
                    handler.postDelayed(createRunnable(tile),700);
                    step++;
                }
                //IF WRONG LOSE POINTS OR DIE, NOW IT ONLY WORKS IF RIGHT
            } else if (step == round){ // Add to sequence
                sequence.add(tile);
                step++;
                connection.setTileColor(currentPlayer,tile);
                handler.postDelayed(createRunnable(tile),700);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nextRound();
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
    }

    public void nextRound() {
        handler.removeCallbacksAndMessages(null);
        step = 1;
        round++;
        currentPlayer++;
        if (currentPlayer>numPlayers) currentPlayer=1;
        connection.setAllTilesColor(currentPlayer);
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
/*
    // Initialize Horse variables
    private int player = 1;
    private int move = 1;
    private int totalMoves = 4;
    private int round = 1;
    private int numPlayers = getNumPlayers();

    public void addToPattern(ArrayList sequence, int numberOfNew) {
        for (int i = 0; i < numberOfNew; i++) {
            sequence.append(input[i]);
        }
    }

    public boolean comparePattern(ArrayList sequence) {
        for (int i = 0; i < sequence.length; i++) {
            if (input[i] != sequence[i]) {
                return false;
            }
        }
        return true;
    }

    public void play() {
        ArrayList sequence = <>;
        addToPattern(sequence, 4);
        this.round++;
        this.player++;
        boolean playing = comparePattern(sequence);

        while (playing) {
            addToPattern(sequence, 1);
            this.round++;
            this.player++;
            if (this.player > this.numPlayers) {
                this.player = 1;
            }
            playing = comparePattern(sequence);
        }

        // When while loop stops, someone made a wrong step in the sequence

    }*/
}

