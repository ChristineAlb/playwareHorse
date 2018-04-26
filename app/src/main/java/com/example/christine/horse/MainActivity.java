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
    TextView tilesConnectedLabel;

    TextView teamRedScore,teamBlueScore,teamGreenScore,teamVioletScore,teamYellowScore,teamWhiteScore;

    Boolean isConnected = false;
    Boolean pairing = false;
    Boolean updating = false;

    HorseKnockout knockoutGame = new HorseKnockout();
    HorseHighScore highscoreGame = new HorseHighScore();
    Button startGameButton;
    boolean playing = false;
    int gameMode;

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
                AntData data = new AntData((byte) 39);
                data.setColor(3, 4);
                data.setColor(4);
                data.setSingleLed(1, 1);
            }
        });

        tilesConnectedLabel = (TextView) findViewById(R.id.tilesConnectedLabel);

        teamRedScore = (TextView) findViewById(R.id.team_red_score);
        teamBlueScore = (TextView) findViewById(R.id.team_blue_score);
        teamGreenScore = (TextView) findViewById(R.id.team_green_score);
        teamVioletScore = (TextView) findViewById(R.id.team_violet_score);
        teamYellowScore = (TextView) findViewById(R.id.team_yellow_score);
        teamWhiteScore = (TextView) findViewById(R.id.team_white_score);


        // Game stuff goes here
        startGameButton = (Button) findViewById(R.id.playGame);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameMode = gameModeSpinner.getSelectedItemPosition();
                if(!playing) {
                    switch (gameMode) {
                        case 0: knockoutGame.setNumPlayers(numPlayersSpinner.getSelectedItemPosition());
                                knockoutGame.setIfTiming(false);
                                knockoutGame.startGame();
                        case 1: knockoutGame.setNumPlayers(numPlayersSpinner.getSelectedItemPosition());
                                knockoutGame.setIfTiming(true);
                                knockoutGame.startGame();
                        case 2: highscoreGame.setNumPlayers(numPlayersSpinner.getSelectedItemPosition());
                                highscoreGame.setIfTiming(false);
                                highscoreGame.startGame();
                        case 3: highscoreGame.setNumPlayers(numPlayersSpinner.getSelectedItemPosition());
                                highscoreGame.setIfTiming(true);
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

        knockoutGame.setOnGameEventListener(new Game.OnGameEventListener() {
            @Override
            public void onGameTimerEvent(int var1) {
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
        });
        numPlayersSpinner = (Spinner) findViewById(R.id.numPlayersSpinner);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.spinner_item, getResources().getStringArray(R.array.players));
        numPlayersSpinner.setAdapter(adapter2);

        gameModeSpinner = (Spinner) findViewById(R.id.gameModeSpinner);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,R.layout.spinner_item,getResources().getStringArray(R.array.gamemode));
        gameModeSpinner.setAdapter(adapter3);
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

}
