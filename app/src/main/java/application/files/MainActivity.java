package application.files;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements GarbageListener {

    //global variable of FishView
    private FishView gameView;

    //handle animation task
    private final Handler handler = new Handler();

    //global variable of screen
    private RelativeLayout screen;

    //time before level update
    private int levelChangeTime = 2; //initialize first small garbage in X seconds
    private int spawnBossGarbage = 45; //initialize big garbage in X seconds
    private int spawnHeart = 40; //initialize heart in X seconds

    //pause variables
    private Button pauseButton;
    private boolean pauseFlag = false;

    //left and right button
    private Button leftButton;
    private Button rightButton;

    //List of small garbage on screen
    private final List<SmallGarbage> smallGarbages = new ArrayList<>();
    //List of big garbage on screen
    private List<BigGarbage> bigGarbages = new ArrayList<>();
    //List of heart on screen
    private List<LifePoint> lifePoints = new ArrayList<>();

    //create timer for animation and level increase
    private Timer mainTimer;

    //create timer fro holding left or right
    private Timer movingLeft;
    private Timer movingRight;
    private final boolean buttonIsPressed = false; //so players can't hold both buttons down
    private final int holdMovementPeriod = 9;

    //keep track of song
    public static Intent themeSong;
    //keep track of how far we are in the song, serviceStop() deletes everything in service ThemeSong so variable must be saved elsewhere
    private static int lengthOfSong = 0;
    private static boolean backButtonPressed = false; //check if backButton was pressed in service ThemeSong oonDestroy() since that's the last thing that is run

    //save LinearLayout for buttons here and have static buttonsCorY (so FishView can reach it) which is the same as the linearLayout's y
    private LinearLayout linearLayoutMoveButtons;
    public static int buttonsCorY = 0;

    //handling events if we pressed restart on game over screen then press back button here it should go to start menu
    private static boolean haveBeenGameOver = false;

    //adjust spawns
    private static int adjustSpawns;

    //screen width
    public static int width = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get screen widht
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;

        themeSong=new Intent(this, ThemeSong.class);
        startService(themeSong); //OR stopService(svc);

        //can't set this above as a field as that would generate NullPointerException
        linearLayoutMoveButtons = findViewById(R.id.buttonsLinearLayout);

        leftButton = findViewById(R.id.leftArrow);
        rightButton = findViewById(R.id.rightArrow);

        screen = findViewById(R.id.gameScreen);
        gameView = new FishView(this);
        screen.addView(gameView);

        pauseButton = findViewById(R.id.pauseButton);

        mainTimer = new Timer();
        createNewAnimationTask();
        createNewLevelTask();

        //start adjustment at 0
        adjustSpawns = 0;

        //create listeners fo holding left or right button
        findViewById(R.id.leftArrow).setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    holdLeft();
                    rightButton.setEnabled(false);}
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    rightButton.setEnabled(true);
                    if (movingLeft!=null){
                    movingLeft.cancel();
                    }}
                return false;}
        });

        findViewById(R.id.rightArrow).setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    holdRight();
                    leftButton.setEnabled(false);}
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    leftButton.setEnabled(true);
                    if (movingRight!=null){
                    movingRight.cancel();}}
                return false;}
        });
        System.out.println("don with this");
    }

    //this is run after onCreate(), have to have this otherwise it won't have time to recognize Y position of LinearLayout
    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        int[] xy = new int[2];
        linearLayoutMoveButtons.getLocationOnScreen(xy);
        buttonsCorY = xy[1];
    }

    public void moveLeft(@SuppressWarnings("unused") View v){
        if (buttonIsPressed){return;}
        gameView.setLeftPressed(true);
        gameView.leftFishAnimation();//before running the animations we first set which fish animations to run (left or right)
        gameView.invalidate();
    }

    public void moveRight(@SuppressWarnings("unused") View view) {
        if (buttonIsPressed){return;}
        gameView.setRightPressed(true);
        gameView.rightFishAnimation();
        gameView.invalidate();
    }

    public void pauseGame(@SuppressWarnings("unused") View v){
        String resume = "Resume";
        String pause = "Pause";
        if (!pauseFlag){
            stopService(themeSong); //turn of music
            pauseFlag = true;
            pauseButton.setText(resume);
            pauseButton.setBackgroundResource(R.drawable.roundbuttonred);

            //disable animation and level tasks
            mainTimer.cancel();
            //disable all falling garbage on screen
            for (SmallGarbage smallGarbage : smallGarbages) {smallGarbage.disableGarbageTimer();}
            for (BigGarbage bigGarbage : bigGarbages) {bigGarbage.disableGarbageTimer();}
            for (LifePoint lifePoint : lifePoints) {lifePoint.disableGarbageTimer();}
            //disable buttons
            leftButton.setEnabled(false);
            rightButton.setEnabled(false);

        }
        else{
            startService(themeSong); //start music
            pauseFlag=false;
            pauseButton.setText(pause);
            leftButton.setEnabled(true);
            rightButton.setEnabled(true);
            pauseButton.setBackgroundResource(R.drawable.roundbuttonblue);
            //resume falling garbage
            for (SmallGarbage smallGarbage : smallGarbages) {smallGarbage.startFallingGarbage();}
            for (BigGarbage bigGarbage : bigGarbages) {bigGarbage.startFallingGarbage();}
            for (LifePoint lifePoint : lifePoints) {lifePoint.startFallingGarbage();}
            //resume animation and level increase
            mainTimer = new Timer();
            createNewAnimationTask();
            createNewLevelTask();
        }

    }

    private void createNewAnimationTask(){
        TimerTask newAnimationTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //here we set the animation
                        int selectedFish = gameView.getSelectedFish();
                        selectedFish ++;
                        if (selectedFish==2){
                            selectedFish = 0;}

                        gameView.setSelectedFish(selectedFish);

                        //update screen
                        gameView.invalidate();
                    }
                });
            }
        };
        long animationPeriod = 600;
        mainTimer.scheduleAtFixedRate(newAnimationTask, 0, animationPeriod);
    }

    private void createNewLevelTask(){
        TimerTask levelCountDown = new TimerTask(){
            @Override
            public void run() {
                levelChangeTime--;
                spawnBossGarbage--;
                spawnHeart--;
                if (levelChangeTime==0 || spawnBossGarbage == 0 || spawnHeart == 0){
                    //move task that updates the UI onto the main thread
                    runOnUiThread(new Runnable() { //this tells the program to run this on the UI(aka main) thread, we could also call on new Thread if wanted to start new thread
                        @Override
                        public void run() {
                            if (levelChangeTime==0){generateNewGarbage("smallGarbage");}
                            if (spawnBossGarbage==0){generateNewGarbage("bigGarbage");}
                            if (spawnHeart==0){generateNewGarbage("lifePoint");}// when this is added we can't lose life?
                        }
                    });
                }
            }
        };
        mainTimer.scheduleAtFixedRate(levelCountDown,0,1000);
    }


    private void holdLeft(){
        movingLeft = new Timer();
        final View v = new View(this); //create view so moveLeft() can called
        TimerTask holdLeftTask = new TimerTask(){
            @Override
            public void run() {
                handler.post(new Runnable() {
            @Override
            public void run() {
                moveLeft(v);
            }
                });
        }};
        movingLeft.scheduleAtFixedRate(holdLeftTask,0,holdMovementPeriod);
    }

    private void holdRight(){
        movingRight = new Timer();
        final View v = new View(this);
        TimerTask holdRightTask = new TimerTask(){
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        moveRight(v);
                    }
                });
            }};
        movingRight.scheduleAtFixedRate(holdRightTask,0,holdMovementPeriod);
    }

    private void generateNewGarbage(String garbage){
        switch (garbage){
            case "bigGarbage":
                //make sound for spawning big garbage
                spawnBossGarbage = new Random().nextInt(45-adjustSpawns)+30; //time to next spawn
                BigGarbage newBigGarbage = new BigGarbage(MainActivity.this);
                newBigGarbage.setListener(MainActivity.this);
                bigGarbages.add(newBigGarbage);
                screen.addView(newBigGarbage);
                break;
            case "smallGarbage":
                //set timer for next object
                levelChangeTime = new Random().nextInt(20)+2; //set seconds between 2 and 20 at random
                //this create SmallGarbage and initialize its task
                SmallGarbage newGarbage = new SmallGarbage(MainActivity.this);
                newGarbage.setListener(MainActivity.this); // set listener for garbage
                smallGarbages.add(newGarbage);
                screen.addView(newGarbage);
                break;
            case "lifePoint":
                spawnHeart= new Random().nextInt(30-adjustSpawns)+20; //time to next life spawn
                //this create SmallGarbage and initialize its task
                LifePoint newLifePoint = new LifePoint(MainActivity.this);
                newLifePoint.setListener(MainActivity.this); // set listener for garbage
                lifePoints.add(newLifePoint);
                screen.addView(newLifePoint);
                break;
        }
    }

    //here starts the GarbageListener
    @Override
    public void handleAvoidedGarbage(String avoidedGarbage) {
        gameView.avoidedGarbage(avoidedGarbage);
    }

    @Override
    public boolean handleHitPlayer(int x, int y, String garbageType) {
        return gameView.hitWasteChecker(x,y, garbageType);
    }

    @Override
    public void handleLoseLife() {
        gameView.loseLife();
    }


    //empty lives on screen, once they have landed or hit player
    @Override
    public void emptyLifePointList(){
        lifePoints.clear();
        lifePoints = new ArrayList<>();
    }

    //empty big garbage on screen, once they have landed or hit player
    @Override
    public void emptyBigGarbageList(){
        bigGarbages.clear();
        bigGarbages = new ArrayList<>();
    }

    //saving and setting length of played song
    public static int getLengthOfSong() {
        return lengthOfSong;
    }

    public static void setLengthOfSong(int lengthOfSong) {
        MainActivity.lengthOfSong = lengthOfSong;
    }

    //onStop runs AFTER onBackPressed(), so lengthOfSong must be reset there
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backButtonPressed = true;
        //if we pressed back after pressed restart go back to start menu
        if (itHasBeenGameOver()){
            setHaveBeenGameOver(false);
            backToStartMenu();
        }
    }

    public static boolean isBackButtonPressed() {
        return backButtonPressed;
    }

    public static void setBackButtonPressed(boolean backButtonPressed) {
        MainActivity.backButtonPressed = backButtonPressed;
    }

    private void backToStartMenu() {
        Intent startMenuIntent = new Intent(this, MenuActivity.class);
        startActivity(startMenuIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish(); //end this activity
    }

    //this runs whenever the app is closed
    @Override
    protected void onStop(){
        super.onStop();
        //stop music
        if(!pauseFlag){
        stopService(themeSong);
        setLengthOfSong(0);
        //pause game, this will also reset sound upon start
        final View v = new View(this);
        pauseFlag = false;
        pauseGame(v);}
    }

    private static boolean itHasBeenGameOver() {
        return haveBeenGameOver;
    }

    public static void setHaveBeenGameOver(boolean haveBeenGameOver) {
       MainActivity.haveBeenGameOver = haveBeenGameOver;
    }

    public static void setAdjustSpawns(int adjustSpawns) {
        MainActivity.adjustSpawns = adjustSpawns;
    }

}
