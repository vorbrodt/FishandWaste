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

public class LifePoint extends View implements GarbageInterface{
    private int heartX;
    private int heartY;
    private final static long lifePointPeriod = 29; //basically fps
    private final Handler handler = new Handler();
    private GarbageListener listener;
    private boolean heartLanded = false;
    private Timer fallingHeartTimer;
    private final Bitmap heart;

    public LifePoint(Context context) {
        super(context);
        heart = BitmapFactory.decodeResource(getResources(),R.drawable.lifepoint);
        resetGarbage(); //here we set x and y
        startFallingGarbage();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int fallingSpeed = 10; //how much further down the garbage with fall per onDraw() update
        heartY = heartY + fallingSpeed;

        //this should change
        if (heartY > getHeight()){
            //resetGarbage(); //don't want big garbage to respawn so cancel timer here
            heartLanded = true;
        }
        canvas.drawBitmap(heart,heartX,heartY,null);
    }

    @Override
    public void createNewFallingGarbageTask() {
        TimerTask lifePointTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //update small garbage movement
                        invalidate();

                        //if some garbage hit the ground, then give player points
                        if (isGarbageLanded()){
                            listener.emptyLifePointList(); //have to remove object from pause list in MainActivity, because this does not respawn like SmallGarbage
                            setGarbageLandedToFalse();
                            //don't want big garbage to respawn so cancel timer here
                            disableGarbageTimer();
                            setWillNotDraw(true);
                        }
                        //check if player hit small garbage
                        if (listener.handleHitPlayer(heartX,heartY, "lifePoint")){
                            listener.emptyLifePointList(); //have to remove object from pause list in MainActivity
                            setGarbageLandedToFalse();
                            disableGarbageTimer();
                            setWillNotDraw(true);
                        }
                    }
                });
            }
        };
        fallingHeartTimer.scheduleAtFixedRate(lifePointTask, 0, lifePointPeriod);
    }

    @Override
    public void disableGarbageTimer() {
        fallingHeartTimer.cancel();
    }

    @Override
    public void startFallingGarbage() {
        fallingHeartTimer = new Timer();
        createNewFallingGarbageTask();
    }

    //set objects position
    @Override
    public void resetGarbage() {
        Random randomGenerator = new Random();
        heartX = randomGenerator.nextInt(MainActivity.width-heart.getWidth()); //random X coordinate between 0 and 940
        heartY = -heart.getHeight();
    }

    @Override
    public void setGarbageLandedToFalse() {
        heartLanded = false;
    }

    @Override
    public void setListener(GarbageListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean isGarbageLanded() {
        return heartLanded;
    }
}
