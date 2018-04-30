package com.example.christine.horse;

/**
 * Created by sindribergsson on 30/04/2018.
 */

public class Players {
    private int round;
    private int current;
    private int[] playerLives = new int[6];
    private int stillAlive;

    private int startLives = 2;

    Players(int numPlayers) {
        this.stillAlive = numPlayers;
        this.round = 1;
        this.current = 1;

        for (int i=1;  i<=6; i++) {
            if (i<stillAlive) playerLives[i] = startLives;
            else playerLives[i] = 0;
        }
    }

    public int nextPlayer(boolean knockout) {

        /**
         * FIX THIS LOGIC
         */

        if (knockout) this.playerLives[this.current]--;
        if (this.playerLives[this.current]==0) stillAlive--;
        if (stillAlive == 1) return 0;

        while (true) { // Find next player
            if (this.current==this.stillAlive) this.current = 1;
            else this.current++;

            if (this.playerLives[this.current]>0) break;
        }

        if (this.current == this.round) {
            this.round = this.current;
        }

        return current;
    }

    public int getCurrent() {return this.current;}
    public int getRound() {return this.round;}

}
