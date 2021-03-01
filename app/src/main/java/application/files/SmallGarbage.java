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


public class SmallGarbage extends View implements GarbageInterface {
    private int smallX;
    private int smallY;
    private final Bitmap sodaCan;
    private final static long smallGarbagePeriod = 30; //basically fps
    private final Handler handler = new Handler();
    private GarbageListener listener;
    private boolean garbageLanded = false;
    private Timer fallingGarbageTimer;

    public SmallGarbage(Context context) {
        super(context);
        sodaCan = BitmapFactory.decodeResource(getResources(),R.drawable.sodacan);
        resetGarbage();
        startFallingGarbage();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int smallSpeed = 5; // how far to jump per frame
        smallY = smallY + smallSpeed;

        //respawn garbage if it hits bottom
        if (smallY > getHeight()){
            resetGarbage();
            garbageLanded = true;
        }

        canvas.drawBitmap(sodaCan,smallX,smallY,null);
    }

    @Override
    public void resetGarbage(){
        Random randomGenerator = new Random();
        smallX = randomGenerator.nextInt(MainActivity.width-sodaCan.getWidth()); //random X coordinate between 0 and 990
        smallY = -sodaCan.getHeight();
    }

    @Override
    public void createNewFallingGarbageTask(){
        TimerTask smallGarbageTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //update small garbage movement
                        invalidate();
                        //if some garbage hit the ground, then give player points
                        if (isGarbageLanded()){
                            listener.handleAvoidedGarbage("smallGarbage");
                            setGarbageLandedToFalse();
                        }
                        //check if player hit small garbage
                        if (listener.handleHitPlayer(smallX,smallY, "smallGarbage")){
                            listener.handleLoseLife();
                            resetGarbage();
                        }
                    }
                });
            }
        };
        fallingGarbageTimer.scheduleAtFixedRate(smallGarbageTask, 0, smallGarbagePeriod);
    }

    @Override
    public void disableGarbageTimer(){
        fallingGarbageTimer.cancel();
    }

    @Override
    public void startFallingGarbage(){
        fallingGarbageTimer = new Timer();
        createNewFallingGarbageTask();
    }


    //set listener so that we can reach activity functions
    @Override //overrides interface
    public void setListener(GarbageListener listener){
        this.listener = listener;
    }

    @Override
    public boolean isGarbageLanded() {
        return garbageLanded;
    }

    @Override
    public void setGarbageLandedToFalse() {
        this.garbageLanded = false;
    }


}
