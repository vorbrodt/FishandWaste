package application.files;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.view.View;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class BigGarbage extends View implements GarbageInterface{
    private int bigX;
    private int bigY;
    private final Bitmap toxicWaste;
    private final static long bigGarbagePeriod = 33; //how often to call on onDraw() once the timerTask has been initiated
    private final Handler handler = new Handler();
    private GarbageListener listener;
    private boolean garbageLanded = false;
    private Timer fallingGarbageTimer;



    public BigGarbage(Context context) {
        super(context);
        toxicWaste = BitmapFactory.decodeResource(getResources(),R.drawable.toxicwaste);
        resetGarbage(); //here we set x and y
        startFallingGarbage();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int fallingSpeed = 4; //how much further down the garbage with fall per onDraw() update
        bigY = bigY + fallingSpeed;

        //this should change
        if (bigY > getHeight()){
            //resetGarbage(); //don't want big garbage to respawn so cancel timer here
            garbageLanded = true;
        }

        canvas.drawBitmap(toxicWaste,bigX,bigY,null);

    }

    @Override
    public void createNewFallingGarbageTask() {
        TimerTask bigGarbageTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //update small garbage movement
                        invalidate();

                        //if some garbage hit the ground, then give player points
                        if (isGarbageLanded()){
                            listener.emptyBigGarbageList(); //have to remove object from pause list in MainActivity, because this does not respawn like SmallGarbage
                            listener.handleAvoidedGarbage("bigGarbage"); //should give 50 points for this
                            setGarbageLandedToFalse();
                            //don't want big garbage to respawn so cancel timer here
                            disableGarbageTimer();
                            setWillNotDraw(true);

                        }
                        //check if player hit big garbage
                        if (listener.handleHitPlayer(bigX,bigY, "bigGarbage")){
                            listener.emptyBigGarbageList(); //have to remove object from pause list in MainActivity
                            listener.handleLoseLife(); //lose 2 lives for this
                            listener.handleLoseLife();
                            disableGarbageTimer();
                            setWillNotDraw(true);
                        }
                    }
                });
            }
        };
        fallingGarbageTimer.scheduleAtFixedRate(bigGarbageTask, 0, bigGarbagePeriod);

    }

    @Override
    public void disableGarbageTimer() {
        fallingGarbageTimer.cancel();
    }

    @Override
    public void startFallingGarbage() {
        fallingGarbageTimer = new Timer();
        createNewFallingGarbageTask();
    }

    @Override
    public void resetGarbage() {
        Random randomGenerator = new Random();
        bigX = randomGenerator.nextInt(MainActivity.width-toxicWaste.getWidth()); //random X coordinate between 0 and 870
        bigY = -toxicWaste.getHeight();
    }

    @Override
    public void setGarbageLandedToFalse() {
        garbageLanded = false;
    }

    @Override
    public boolean isGarbageLanded() {
        return garbageLanded;
    }

    @Override
    public void setListener(GarbageListener listener) {
        this.listener = listener;
    }
}
