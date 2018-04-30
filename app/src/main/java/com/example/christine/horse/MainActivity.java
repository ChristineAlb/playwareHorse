package com.example.christine.horse;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.livelife.motolibrary.AntData;
import com.livelife.motolibrary.MotoConnection;
import com.livelife.motolibrary.MotoConnection;
import com.livelife.motolibrary.MotoGame;
import com.livelife.motolibrary.OnAntEventListener;

import org.w3c.dom.Text;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements OnAntEventListener{

    private static String tag = MainActivity.class.getSimpleName();
    MotoConnection connection;

    Spinner spinner,numPlayersSpinner, gameModeSpinner;
    Button connectButton,pairingButton,updFirmwareButton,testFirmwareButton;
    LinearLayout actionsLayout;
    TextView tilesConnectedLabel, roundText;

    TextView teamRedScore,teamBlueScore,teamGreenScore,teamVioletScore,teamYellowScore,teamWhiteScore;

    Boolean isConnected = false;
    Boolean pairing = false;
    Boolean updating = false;

    HorseKnockout knockoutGame = new HorseKnockout();
    HighScore highscoreGame = new HighScore();
    Button startGameButton;
    boolean playing = false;
    int gameMode;

    int click = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        connection = MotoConnection.getInstance();

        connectButton = (Button) findViewById(R.id.connectButton);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (spinner.getSelectedItemPosition() == 0) return;

                if (!isConnected) {
                    connection.registerListener(MainActivity.this);
                    connection.startMotoConnection(MainActivity.this, spinner.getSelectedItemPosition());
                    connectButton.setEnabled(false);
                    spinner.setEnabled(false);
                } else {
                    connection.unregisterListener(MainActivity.this);
                    connection.stopMotoConnection();
                    connectButton.setText("CONNECT");
                    enableActions(false);
                    connectButton.setEnabled(true);
                    spinner.setEnabled(true);
                    tilesConnectedLabel.setText("Waiting for ANT...");
                }

                isConnected = !isConnected;
            }
        });

        spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, getResources().getStringArray(R.array.channels));
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i!=0) {
                    connectButton.setEnabled(true);
                } else {
                    connectButton.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        actionsLayout = (LinearLayout) findViewById(R.id.actionsLayout);
        enableActions(false);


        pairingButton = (Button) findViewById(R.id.pairingButton);
        pairingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pairing) {
                    connection.pairTilesStart();
                    pairingButton.setText("STOP PAIRING TILES");
                    updateActions(pairingButton.getId());
                } else {
                    connection.pairTilesStop();
                    pairingButton.setText("START PAIRING TILES");
                    enableActions(true);
                }

                pairing = !pairing;
            }
        });

        updFirmwareButton = (Button) findViewById(R.id.updFirmwareButton);
        updFirmwareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!updating) {
                    connection.updateFirmwareStart();
                    updFirmwareButton.setText("STOP UPDATE FIRMWARE");
                    updateActions(updFirmwareButton.getId());
                } else {
                    connection.updateFirmwareStop();
                    updFirmwareButton.setText("START UPDATE FIRMWARE");
                    enableActions(true);
                }

                updating = !updating;
            }
        });

        testFirmwareButton = (Button) findViewById(R.id.testFirmwareButton);
        testFirmwareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //setSingleLed(click, click, 1);
                ledControl.setFadeBreathing(connection,click, 10, 1);
                //setFadeOut(click, 20, 1);
                click++;
            }
        });

        tilesConnectedLabel = (TextView) findViewById(R.id.tilesConnectedLabel);

        teamRedScore = (TextView) findViewById(R.id.team_red_score);
        teamBlueScore = (TextView) findViewById(R.id.team_blue_score);
        teamGreenScore = (TextView) findViewById(R.id.team_green_score);
        teamVioletScore = (TextView) findViewById(R.id.team_violet_score);
        teamYellowScore = (TextView) findViewById(R.id.team_yellow_score);
        teamWhiteScore = (TextView) findViewById(R.id.team_white_score);

        roundText = (TextView) findViewById(R.id.roundText);

        // Game stuff goes here
        startGameButton = (Button) findViewById(R.id.playGame);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameMode = gameModeSpinner.getSelectedItemPosition();
                if(!playing) {
                    switch (gameMode) {
                        case 0: knockoutGame.setNumPlayers(numPlayersSpinner.getSelectedItemPosition()+1);
                                knockoutGame.setIfTiming(false);
                                initScore();
                                knockoutGame.startGame();
                        case 1: knockoutGame.setNumPlayers(numPlayersSpinner.getSelectedItemPosition()+1);
                                knockoutGame.setIfTiming(true);
                                initScore();
                                knockoutGame.startGame();
                        case 2: highscoreGame.setNumPlayers(numPlayersSpinner.getSelectedItemPosition()+1);
                                highscoreGame.setIfTiming(false);
                                highscoreGame.startGame();
                                initScore();
                        case 3: highscoreGame.setNumPlayers(numPlayersSpinner.getSelectedItemPosition()+1);
                                highscoreGame.setIfTiming(true);
                                initScore();
                                highscoreGame.startGame();
                    }
                    startGameButton.setText("STOP GAME");
                    numPlayersSpinner.setEnabled(false);
                    gameModeSpinner.setEnabled(false);
                } else {
                    switch (gameMode) {
                        case 0: knockoutGame.stopGame();
                        case 1: knockoutGame.stopGame();
                        case 2: highscoreGame.stopGame();
                        case 3: highscoreGame.stopGame();
                    }
                    startGameButton.setText("START GAME");
                    numPlayersSpinner.setEnabled(true);
                    gameModeSpinner.setEnabled(true);
                }
                playing = !playing;
            }
        });

        highscoreGame.setOnGameEventListener(new Game.OnGameEventListener() {
            @Override
            public void onGameRoundEvent(int round) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        roundText.setText("Round: "+highscoreGame.getRounds());
                    }
                });
            }

            @Override
            public void onGameScoreEvent(final int score, final int player) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (player) {
                            case 1: teamRedScore.setText(""+score+"");
                            case 2: teamBlueScore.setText(""+score+"");
                            case 3: teamGreenScore.setText(""+score+"");
                            case 4: teamVioletScore.setText(""+score+"");
                            case 5: teamYellowScore.setText(""+score+"");
                            case 6: teamWhiteScore.setText(""+score+"");
                        }
                    }
                });
            }

            @Override
            public void onGameKnockoutEvent(int var1) {

            }
        });

        knockoutGame.setOnGameEventListener(new Game.OnGameEventListener() {
            @Override
            public void onGameRoundEvent(int var1) {
            }
            @Override
            public void onGameScoreEvent(int var1, int var2) {
            }
            @Override
            public void onGameKnockoutEvent(final int player) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (player) {
                            case 1: teamRedScore.setText("X");
                            case 2: teamBlueScore.setText("X");
                            case 3: teamGreenScore.setText("X");
                            case 4: teamVioletScore.setText("X");
                            case 5: teamYellowScore.setText("X");
                            case 6: teamWhiteScore.setText("X");
                        }
                    }
                });
            }
        });



        numPlayersSpinner = (Spinner) findViewById(R.id.numPlayersSpinner);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.spinner_item, getResources().getStringArray(R.array.players));
        numPlayersSpinner.setAdapter(adapter2);

        gameModeSpinner = (Spinner) findViewById(R.id.gameModeSpinner);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,R.layout.spinner_item,getResources().getStringArray(R.array.gamemode));
        gameModeSpinner.setAdapter(adapter3);

        numPlayersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i!=0) {
                    startGameButton.setEnabled(true);
                } else {
                    startGameButton.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //
    }

    public void enableActions(boolean enabled) {
        for (int i = 0; i < actionsLayout.getChildCount(); i++) {
            View child = actionsLayout.getChildAt(i);
            child.setEnabled(enabled);
        }
        connectButton.setEnabled(enabled);
    }

    public void updateActions(int buttonId) {
        for (int i = 0; i < actionsLayout.getChildCount(); i++) {
            View child = actionsLayout.getChildAt(i);
            if (child.getId() != buttonId) {
                child.setEnabled(false);
            }
        }
        connectButton.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connection.unregisterListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        connection.unregisterListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        connection.registerListener(this);
        if (spinner.getSelectedItemPosition() != 0 && !isConnected) {
            connection.startMotoConnection(this, spinner.getSelectedItemPosition());
        }

    }


    @Override
    public void onMessageReceived(byte[] bytes, long l) {

        int tileId = AntData.getId(bytes);
        int command = AntData.getCommand(bytes);

        switch (command) {
            case AntData.EVENT_PRESS:
                Log.v(tag,"Press on tile: "+tileId);
            case AntData.EVENT_RELEASE:
                Log.v(tag,"Release on tile: "+tileId);
            default:
                Log.v(tag,"cmd: "+command+" tile: "+tileId);
                break;
        }
        knockoutGame.addEvent(bytes);
        highscoreGame.addEvent(bytes);
    }

    @Override
    public void onAntServiceConnected() {

        Log.v(tag,"Ant Service connected...");
        enableActions(true);
        connectButton.setText("DISCONNECT");
        connection.setAllTilesToInit();

    }

    @Override
    public void onNumbersOfTilesConnected(int i) {

        tilesConnectedLabel.setText(i+" MOTO found");

    }

    public void initScore() {
        teamRedScore.setText("0");
        teamBlueScore.setText("0");
        teamGreenScore.setText("0");
        teamVioletScore.setText("0");
        teamYellowScore.setText("0");
        teamWhiteScore.setText("0");
    }

//    public void setSingleLed(int color, int tileID, int ledID) {
//        AntData data = new AntData((byte) tileID);
//        data.setSingleLed(color, ledID);
//        connection.update(data);
//    }
//
//    public void setFadeBreathing(int color, int frequency, int tileID) {
//        AntData data = new AntData((byte) tileID);
//        byte col = (byte) color;
//        byte freq = (byte) frequency; // A frequency of 10 ms is nice
//        byte tile = (byte) tileID;
//        byte connect = (byte)MotoConnection.getInstance().getDeviceId();
//        byte[] communication = {tile, 42, col, freq, 0, 0, 0, connect};
//        data.setBroadcastData(communication);
//        connection.update(data);
//    }
//
//    public void setFadeOut(int color, int frequency, int tileID) {
//        AntData data = new AntData((byte) tileID);
//        byte col = (byte) color;
//        byte freq = (byte) frequency; // A frequency of 20 ms is nice
//        byte tile = (byte) tileID;
//        byte connect = (byte)MotoConnection.getInstance().getDeviceId();
//        byte[] communication = {tile, 43, col, freq, 0, 0, 0, connect};
//        data.setBroadcastData(communication);
//        connection.update(data);
//    }

}
