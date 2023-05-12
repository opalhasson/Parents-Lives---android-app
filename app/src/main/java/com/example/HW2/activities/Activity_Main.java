package com.example.HW2.activities;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.HW2.GameManager;
import com.example.HW2.LocationManager;
import com.example.HW2.MSPV3;
import com.example.HW2.R;
import com.example.HW2.Sensors;
import com.example.HW2.SoundManager;
import com.example.HW2.objects.MyDB;
import com.example.HW2.objects.Record;
import com.example.HW2.objects.StepDetector;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

public class Activity_Main extends AppCompatActivity {

    private MaterialTextView main_LBL_time;
    private ImageView[] main_IMG_hearts;
    private ImageView[][] main_IMG_route;
    private ImageButton main_BTN_left;
    private ImageButton main_BTN_right;
    private MaterialButton main_BTN_FAST;
    private MaterialButton main_BTN_SLOW;
    private GameManager gameManager;
    private StepDetector stepDetector;
    private Bundle bundle;
    private String game;
    private Sensors sensors;
    private SensorManager sensorManager;
    private int sensorGame = 0;
    private SoundManager soundManager;
    private boolean isGotCoin = false;
    private final MyDB myDB = MyDB.initMyDB();
    LocationManager locationManager;
    private static int DELAY;
    private final int FAST_DELAY = 500;
    private final int SLOW_DELAY = 1000;
    private float previousX = 0.0f;
    private float previousY = 0.0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameManager = new GameManager();
        stepDetector = new StepDetector();
        soundManager = new SoundManager();
        DELAY = SLOW_DELAY;
        locationManager = new LocationManager(this);

        stepDetector.start();
        if (getIntent().getBundleExtra("Bundle") != null) {
            this.bundle = getIntent().getBundleExtra("Bundle");
            gameManager.getPlayer().setPlayerName(bundle.getString("playerName"));
        } else this.bundle = new Bundle();

        game = bundle.getString("game");
        if (game.equals("buttons")) {
            setContentView(R.layout.activity_main);
            findViews();
            initPlayerButtons();
        } else {
            setContentView(R.layout.activity_sensors);
            sensorGame = 1;
            findViews();
            sensors = new Sensors();
            initSensors();
        }

        // Location permission
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findViews() {
        main_LBL_time = findViewById(R.id.main_LBL_time);
        main_IMG_hearts = new ImageView[]{
                findViewById(R.id.main_IMG_heart1),
                findViewById(R.id.main_IMG_heart2),
                findViewById(R.id.main_IMG_heart3)
        };

        main_IMG_route = new ImageView[][]{
                {findViewById(R.id.main_IMG_00), findViewById(R.id.main_IMG_01), findViewById(R.id.main_IMG_02), findViewById(R.id.main_IMG_03), findViewById(R.id.main_IMG_04)},
                {findViewById(R.id.main_IMG_10), findViewById(R.id.main_IMG_11), findViewById(R.id.main_IMG_12), findViewById(R.id.main_IMG_13), findViewById(R.id.main_IMG_14)},
                {findViewById(R.id.main_IMG_20), findViewById(R.id.main_IMG_21), findViewById(R.id.main_IMG_22), findViewById(R.id.main_IMG_23), findViewById(R.id.main_IMG_24)},
                {findViewById(R.id.main_IMG_30), findViewById(R.id.main_IMG_31), findViewById(R.id.main_IMG_32), findViewById(R.id.main_IMG_33), findViewById(R.id.main_IMG_34)},
                {findViewById(R.id.main_IMG_40), findViewById(R.id.main_IMG_41), findViewById(R.id.main_IMG_42), findViewById(R.id.main_IMG_43), findViewById(R.id.main_IMG_44)},
                {findViewById(R.id.main_IMG_50), findViewById(R.id.main_IMG_51), findViewById(R.id.main_IMG_52), findViewById(R.id.main_IMG_53), findViewById(R.id.main_IMG_54)},
                {findViewById(R.id.main_IMG_60), findViewById(R.id.main_IMG_61), findViewById(R.id.main_IMG_62), findViewById(R.id.main_IMG_63), findViewById(R.id.main_IMG_64)},
                {findViewById(R.id.main_IMG_70), findViewById(R.id.main_IMG_71), findViewById(R.id.main_IMG_72), findViewById(R.id.main_IMG_73), findViewById(R.id.main_IMG_74)}
        };

        if (game.equals("buttons")) {
            main_BTN_left = findViewById(R.id.main_BTN_left);
            main_BTN_right = findViewById(R.id.main_BTN_right);
            main_BTN_FAST = findViewById(R.id.main_BTN_FAST);
            main_BTN_SLOW = findViewById(R.id.main_BTN_SLOW);
        }
    }

    private void initPlayerButtons() {
        main_BTN_left.setOnClickListener(view -> {
            gameManager.playerMove("LEFT");
        });

        main_BTN_right.setOnClickListener(view -> {
            gameManager.playerMove("RIGHT");
        });

        main_BTN_FAST.setOnClickListener(view -> {
            DELAY = FAST_DELAY;
            stopTimer();
            startTimer();
        });

        main_BTN_SLOW.setOnClickListener(view -> {
            DELAY = SLOW_DELAY;
            stopTimer();
            startTimer();
        });
    }


    //-------timer--------

    private Timer timer = new Timer();
    private int counter = 0;

    private enum TIMER_STATUS {
        OFF,
        RUNNING,
        PAUSE
    }

    private TIMER_STATUS timerStatus = TIMER_STATUS.OFF;


    private void tick() {
        ++counter;
        main_LBL_time.setText("" + counter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timerStatus == TIMER_STATUS.RUNNING) {
            stopTimer();
            timerStatus = TIMER_STATUS.PAUSE;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (timerStatus == TIMER_STATUS.OFF) {
            startTimer();
        } else if (timerStatus == TIMER_STATUS.RUNNING) {
            stopTimer();
        } else startTimer();
    }

    private void startTimer() {
        timerStatus = TIMER_STATUS.RUNNING;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> startGame());
            }
        }, 0, DELAY);
    }

    private void stopTimer() {
        timerStatus = TIMER_STATUS.OFF;
        timer.cancel();
    }

    private void startGame() {
        tick();
        runLogic();
        updateUI();
    }

    public void runLogic() {
        CheckCrashWithCoin();
        updateScore();
        gameManager.randomObjectDirectionMove();
        gameManager.randomCoinDirectionMove();
        gameManager.checkCrash();
    }

    //sensors
    protected void onResume() {
        super.onResume();
        if (sensorGame == 1) {
            sensorManager.registerListener(accSensorEventListener, sensors.getAccSensor(), sensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorGame == 1)
            sensorManager.unregisterListener(accSensorEventListener);
    }

    private void updateUI() {
        int playerX = gameManager.getPlayer().getLocationX();
        int playerY = gameManager.getPlayer().getLocationY();

        for (int i = 0; i < main_IMG_route.length; i++) {
            for (int j = 0; j < main_IMG_route[0].length; j++) {
                if (playerX == i && playerY == j) {
                    main_IMG_route[playerX][playerY].setImageResource(R.drawable.ic_parents);
                    main_IMG_route[playerX][playerY].setVisibility(View.VISIBLE);
                }
                else main_IMG_route[i][j].setVisibility(View.INVISIBLE);
            }
        }

        for (int i = 0; i < gameManager.getObject().length; i++) {
            if (gameManager.getObject()[i].getLocationX() != -1) {
                int objectX = gameManager.getObject()[i].getLocationX();
                int objectY = gameManager.getObject()[i].getLocationY();
                main_IMG_route[objectX][objectY].setImageResource(R.drawable.ic_baby);
                main_IMG_route[objectX][objectY].setVisibility(View.VISIBLE);
            }
        }

        if (stepDetector.getStepCount() % 10 == 0) {
            isGotCoin = false;
            // Random Coin
            gameManager.getCoin().setCoinX(0);
            gameManager.getCoin().setCoinY((int) (Math.random() * 5));
        }

        if (!isGotCoin) {
            if (gameManager.getCoin().getCoinX() != -1) {
                int coinX = gameManager.getCoin().getCoinX();
                int coinY = gameManager.getCoin().getCoinY();
                main_IMG_route[coinX][coinY].setImageResource(R.drawable.ic_bottle);
                main_IMG_route[coinX][coinY].setVisibility(View.VISIBLE);
            }
            main_IMG_route[playerX][playerY].setImageResource(R.drawable.ic_parents);
            for (int i = 0; i < gameManager.getObject().length; i++) {
                if (gameManager.getObject()[i].getLocationX() != -1) {
                    int objectX = gameManager.getObject()[i].getLocationX();
                    int objectY = gameManager.getObject()[i].getLocationY();
                    main_IMG_route[objectX][objectY].setImageResource(R.drawable.ic_baby);
                }
            }
        }


        if (gameManager.getCrash()) {
            if (gameManager.getLives() > 0) {
                soundManager.setMpAndPlay((ContextWrapper) getApplicationContext(), R.raw.crash_sound);
                Toast.makeText(this, "BOOM", Toast.LENGTH_LONG).show();
                main_IMG_hearts[gameManager.getLives()].setVisibility(View.INVISIBLE);
                updateUIAfterCrash();
            } else {
                Toast.makeText(this, "Game Over", Toast.LENGTH_LONG).show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Record rec = new Record()
                                .setName(gameManager.getPlayer().getPlayerName())
                                .setScore(counter)
                                .setLon(locationManager.getLon())
                                .setLat(locationManager.getLat());

                        myDB.addRecord(rec);
                        String json = new Gson().toJson(myDB);
                        MSPV3.getMe().putString("MY_DB", json);
                        replaceActivity();
                        finish();
                    }
                }, 500);
                gameManager.getPlayer().setScore(counter);
                stopTimer();
                replaceActivity();
                finish();
            }
        }

    }

    private void replaceActivity() {
        Intent intent = new Intent(this, Activity_GameOver.class);
        bundle.putInt("PlayerScore", gameManager.getPlayer().getScore());
        intent.putExtra("Bundle", bundle);
        startActivity(intent);
    }

    private void updateUIAfterCrash() {
        int playerX = gameManager.getPlayer().getLocationX();
        int playerY = gameManager.getPlayer().getLocationY();
        main_IMG_route[playerX][playerY].setVisibility(View.INVISIBLE);
        gameManager.getPlayer().setLocationY(gameManager.getPlayer().getStartObjectLocationY());
        main_IMG_route[playerX][playerY].setImageResource(R.drawable.ic_parents);
        gameManager.setCrash(false);
    }


    //sensors
    public void initSensors() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensors.setSensorManager(sensorManager);
        sensors.initSensor();
    }

    private SensorEventListener accSensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];

            // Check if values have changed since the last time
            if (x != previousX || y != previousY) {
                previousX = x;
                previousY = y;

                if (x < -5) { // Move right
                    gameManager.playerMove("RIGHT");
                } else if (x > 5) { // Move left
                    gameManager.playerMove("LEFT");
                } else if (y < -3) { // Fast Mode
                    DELAY = FAST_DELAY;
                    stopTimer();
                    startTimer();
                } else if (y > 3) { // Slow Mode
                    DELAY = SLOW_DELAY;
                    stopTimer();
                    startTimer();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };


    //coin
    private void updateScore() {
        int coinX = gameManager.getCoin().getCoinX();
        int coinY = gameManager.getCoin().getCoinY();
        int playerX = gameManager.getPlayer().getLocationX();
        int playerY = gameManager.getPlayer().getLocationY();
        if (coinX == playerX && coinY == playerY) {
            counter += 10;
            main_LBL_time.setText("" + counter);
            Toast.makeText(this, "+10 coins", Toast.LENGTH_SHORT).show();
            stepDetector.setStepCount(0);
        }
    }

    public void CheckCrashWithCoin() {
        int playerX = gameManager.getPlayer().getLocationX();
        int playerY = gameManager.getPlayer().getLocationY();
        if (gameManager.coinAndPlayerInTheSamePlace()) {
            main_IMG_route[playerX][playerY].setImageResource(R.drawable.ic_parents);
            soundManager.setMpAndPlay((ContextWrapper) getApplicationContext(), R.raw.bite_sound);
            isGotCoin = true;
        }
    }

}


