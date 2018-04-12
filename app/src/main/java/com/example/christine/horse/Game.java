//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.livelife.motolibrary;

import android.os.Handler;

public class Game {
    private Game.OnGameEventListener onGameEventListener;
    static final int STATE_WAITING = 0;
    static final int STATE_SETUP = 1;
    static final int STATE_PLAYING = 2;
    private int currentState = 0;
    private String name;
    private String description;
    private int duration = 0;
    private int remaining = 0;
    private int score = 0;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        public void run() {
            int time = Game.this.remaining * 1000;
            Game.this.remaining = Game.this.remaining - 1000;
            if(Game.this.remaining <= 0) {
                Game.this.stopGame();
            } else {
                Game.this.timerHandler.postDelayed(this, 1000L);
            }

            if(Game.this.onGameEventListener != null) {
                Game.this.onGameEventListener.onGameTimerEvent(Game.this.remaining / 1000);
            }

        }
    };
    private boolean isSetup;
    private boolean isPlaying;

    public Game() {
    }

    public void setOnGameEventListener(Game.OnGameEventListener onGameEventListener) {
        this.onGameEventListener = onGameEventListener;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDuration(int duration) {
        this.duration = duration;
        this.remaining = duration;
    }

    public void incrementScore(int scoreToAdd) {
        this.score += scoreToAdd;
        if(this.onGameEventListener != null) {
            this.onGameEventListener.onGameScoreEvent(this.score);
        }

    }

    public void addEvent(byte[] message) {
        switch(this.currentState) {
            case 1:
                this.onSetupUpdate(message);
                break;
            case 2:
                this.onGameUpdate(message);
        }

    }

    public void startSetup() {
        if(!this.isSetup) {
            this.currentState = 1;
            this.isSetup = true;
            this.onSetupStart();
        }

    }

    public void stopSetup() {
        if(this.isSetup) {
            this.currentState = 0;
            this.isSetup = false;
            this.onSetupEnd();
        }

    }

    public void startGame() {
        if(!this.isPlaying) {
            this.isPlaying = true;
            this.currentState = 2;
            this.remaining = this.duration;
            this.score = 0;
            this.onGameStart();
            if(this.duration != 0) {
                this.timerHandler.postDelayed(this.timerRunnable, 1000L);
            }
        }

    }

    public void stopGame() {
        if(this.isPlaying) {
            this.isPlaying = false;
            this.currentState = 0;
            this.timerHandler.removeCallbacksAndMessages((Object)null);
            this.onGameEnd();
        }

    }

    public void onSetupStart() {
    }

    public void onSetupUpdate(byte[] message) {
    }

    public void onSetupEnd() {
    }

    public void onGameStart() {
    }

    public void onGameUpdate(byte[] message) {
    }

    public void onGameEnd() {
    }

    public interface OnGameEventListener {
        void onGameTimerEvent(int var1);

        void onGameScoreEvent(int var1);
    }
}
