package application.files;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.view.View;

import java.util.concurrent.TimeUnit;


public class FishView extends View {

    private final Bitmap[] fish = new Bitmap[3];
    //private final Bitmap gameBackground;
    private final Bitmap[] lifePoints = new Bitmap[2];

    private int selectedFish;

    private final Paint scorePaint = new Paint();
    private int score, fishLives;

    //booleans for making sure player cannot be hit twice by the same object
    private boolean notPickingUpHeart = true;
    private boolean notPickingUpBigGarbage = true;


    private int fishY = -1000; //for slow phones, because when FishView(Context context) is run it will place fish at these coordinates then after onWindowFocusChanged is run the fishY will be correct
    private int fishX = 400;
    private int speedX = 0;

    private boolean leftPressed = false;
    private boolean rightPressed = false;

    public FishView(Context context) {
        super(context);

        //set default/start fish animations
        leftFishAnimation();

        //set selected fish animation to default start on 0
        selectedFish = 0;

        //set life points
        lifePoints[1] = BitmapFactory.decodeResource(getResources(),R.drawable.lifepoint);
        lifePoints[0] = BitmapFactory.decodeResource(getResources(),R.drawable.deadlife);

        //set score
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(80);
       // scorePaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD)); //??
        scorePaint.setAntiAlias(true); //(graphic improvement) this removes the staircase effect aka make smoother
        scorePaint.setTypeface(Typeface.SERIF);
        score = 0;

        //set fish lives
        fishLives = 3;
    }

    //first MainActivity.onCreate(), then FishView(context), then MainActivity.onWindowFocusChanged() (where we set the Y value of LinearLayout), then after that this is run
    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        fishY = MainActivity.buttonsCorY-350;
    }


    //in a View, the onDraw method is called whenever:
    //the view is initially drawn or whenever invalidate() is called on the view
    //in our case we call on the constructor which initially the View
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //should maybe be canvas.getWidth() here
        int canvasWidth=getWidth();

        //set game boundaries
        int minFishX = 0; //should not be able to go of screen (to the left)
        int maxFishX = canvasWidth-fish[0].getWidth(); //furthers you can go to the right (to the right)

        //check boundaries
        if (fishX < minFishX) {
            fishX = minFishX;
        }
        if (fishX > maxFishX) {
            fishX = maxFishX;
        }

        //set position dependent on speed
        fishX += speedX;

        //draw background
        //canvas.drawBitmap(gameBackground, 0, 0, null);

        //this draws the bitmap we decoded from the image
        if (leftPressed){
            speedX -= 15;
        }
        else if (rightPressed){
            speedX += 15;
        }

        if (speedX != 0){
        while (speedX != 0){
            if (leftPressed){
                fishX -= 1;
                speedX += 1;
                canvas.drawBitmap(fish[selectedFish],fishX,fishY,null);
                invalidate();

            }
            else if (rightPressed){
                fishX += 1;
                speedX -= 1;
                canvas.drawBitmap(fish[selectedFish],fishX,fishY,null);
                invalidate();
            }
        }}
        else{ //if nothing happens when we stay here
            canvas.drawBitmap(fish[selectedFish],fishX,fishY, null);
        }

        leftPressed=false;
        rightPressed=false;

        //draw score
        canvas.drawText("Score: " + score, 20 , 90, scorePaint);

        //draw life points and life point we have lost
        for (int lives = 0; lives < 3 ; lives++) {
            int lifeX = getWidth() - lifePoints[1].getWidth()*(lives+1);//650 + 140*lives;
            int lifeY = 10;

            if (lives < fishLives){
                canvas.drawBitmap(lifePoints[1],lifeX,lifeY,null);
            }
            else{
                canvas.drawBitmap(lifePoints[0],lifeX,lifeY,null); //draw deadlife
            }
        }
    }

    public boolean hitWasteChecker(int x, int y, String garbageType){
         switch (garbageType){
             //define hit boxes
             //first check is how far above, second how much underneath, third how much to the left, and fourth how much to the right
            case "smallGarbage":
                if(fishY <= y + 100  && fishY + fish[selectedFish].getHeight() >= y + 75 && fishX <= x + 75 && x + 20 <= (fishX + fish[selectedFish].getWidth()))
                {
                    MediaPlayer takeDamage = MediaPlayer.create(getContext(),R.raw.sodacanhit);
                    handleSoundEffects(takeDamage);
                    return true;
                }
                return false;
            case "bigGarbage":
                if (fishY <= y + 210  && fishY + fish[selectedFish].getHeight() >= y + 75 && fishX <= x + 180 && x + 20 <= (fishX + fish[selectedFish].getWidth()) && notPickingUpBigGarbage){
                    notPickingUpBigGarbage = false; //because no player is picking up big garbage. Now impossible to pick up big garbage twice
                    MediaPlayer takeDamage = MediaPlayer.create(getContext(),R.raw.sodacanhit);
                    handleSoundEffects(takeDamage);

                    return true;
                }
                return false;
             case "lifePoint":
                 if (fishY <= y + 25  && fishY + fish[selectedFish].getHeight() >= y + 60 && fishX <= x + 110 && x + 35 <= (fishX + fish[selectedFish].getWidth()) && notPickingUpHeart){
                     notPickingUpHeart=false;
                     MediaPlayer heartPickUp = MediaPlayer.create(getContext(),R.raw.heartpickup);
                     handleSoundEffects(heartPickUp);

                     if (fishLives<3){fishLives++;
                     return true;} //if not full life gain a life
                     if (fishLives==3){score+=40; //if already full life then gain 40 points
                     return true;}}
                 return false;
            default:
                return false;
        }}


    private void handleSoundEffects(final MediaPlayer soundEffect){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //start the sound effect
                soundEffect.setVolume(0.1f,0.1f);
                soundEffect.setLooping(false);
                soundEffect.start();

                //wait until time effect is finished, after this time the sound is released and it's possible to pick up the item again
                try {
                    TimeUnit.SECONDS.sleep(4);
                    notPickingUpBigGarbage = true;
                    notPickingUpHeart=true;
                    soundEffect.stop();
                    soundEffect.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }).start();
    }


    public void loseLife(){
        if (fishLives > 0){fishLives--;}
        if (fishLives <= 0){
            //stop theme song from playing
            getContext().stopService(MainActivity.themeSong);
            //through these lines a new Activity can be created from a View
            Intent gameOverIntent = new Intent(getContext(), GameOverActivity.class);
            gameOverIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //not possible to go back from game over screen
            gameOverIntent.putExtra("final score", score); // send data to game over activity
            getContext().startActivity(gameOverIntent);
            ((MainActivity) getContext()).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            ((MainActivity) getContext()).finish(); //TIMERS is till running
        }
    }

    public void leftFishAnimation(){
        fish[0] = BitmapFactory.decodeResource(getResources(),R.drawable.leftfish1);
        fish[1] = BitmapFactory.decodeResource(getResources(),R.drawable.leftfish2);

    }

    public void rightFishAnimation(){
        fish[0] = BitmapFactory.decodeResource(getResources(),R.drawable.rightfish1);
        fish[1] = BitmapFactory.decodeResource(getResources(),R.drawable.rightfish2);
    }

    public void setLeftPressed(boolean leftPressed) {
        this.leftPressed = leftPressed;
    }

    public void setRightPressed(boolean rightPressed) {
        this.rightPressed = rightPressed;
    }

    public int getSelectedFish() {
        return selectedFish;
    }

    public void setSelectedFish(int selectedFish) {
        this.selectedFish = selectedFish;
    }

    public void avoidedGarbage(String avoidedGarbage){
        switch (avoidedGarbage){
            case "smallGarbage":
                score += 10;
                break;
            case "bigGarbage":
                score += 25;
                break;
        }
        //check if we should start adjusting spawns
        if (score<500){
            MainActivity.setAdjustSpawns(3); //3 seconds less than original maximum
        }
        if (score<100){
            MainActivity.setAdjustSpawns(6); //6 seconds less than original maximum
        }
        if (score<100){
            MainActivity.setAdjustSpawns(10); //10 seconds less than original maximum
        }
    }

}
