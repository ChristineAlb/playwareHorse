package com.example.christine.horse;

import com.livelife.motolibrary.Game;

import java.util.ArrayList;

public class Horse extends Game{

    // This is the horse game class

    // Initialize Horse variables
    private int player = 1;
    private int move = 1;
    private int totalMoves = 4;
    private int round = 1;
    private int numPlayers = getNumPlayers();

    public void addToPattern (ArrayList sequence, int numberOfNew) {
        for (int i = 0; i < numberOfNew; i++) {
            sequence.append(input[i]);
        }
    }

    public boolean comparePattern (ArrayList sequence) {
        for (int i = 0; i < sequence.length; i++) {
            if (input[i] != sequence[i]) {
                return false;
            }
        }
        return true;
    }

    public void play () {
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

}

