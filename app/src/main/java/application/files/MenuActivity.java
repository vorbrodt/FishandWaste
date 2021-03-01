package application.files;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MenuActivity extends AppCompatActivity {

    public static Intent menuSound;

    //boolean to not quit sound while checking highscores
    private static boolean checkingHighscores = false;

    //keep track of how far we are in the song, serviceStop() deletes everything in service so variable must be saved elsewhere like for instance here
    private static int lengthOfSong = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        menuSound=new Intent(this, MenuBackgroundSound.class);
        startService(menuSound); // initialize sound
    }


    public void startGame(View view) {
        stopService(menuSound);
        Intent mainIntent = new Intent(MenuActivity.this, MainActivity.class);
        startActivity(mainIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void viewHighscore(View v){
        checkingHighscores=true;
        Intent highscoreIntent = new Intent(this, HighscoreActivity.class);
        startActivity(highscoreIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void showGameInfo(View view) {
        InfoPopUp infoPopUp = new InfoPopUp();
        infoPopUp.show(getSupportFragmentManager(), "Game info");
    }

    //this runs whenever the app is closed
    @Override
    protected void onStop(){
        super.onStop();
        if(!checkingHighscores)
        { stopService(menuSound);} //stop sound
        checkingHighscores = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(menuSound);
    }

    public static int getLengthOfSong() {
        return lengthOfSong;
    }

    public static void setLengthOfSong(int lengthOfSong) {
        MenuActivity.lengthOfSong = lengthOfSong;
    }

    public static boolean isCheckingHighscores() {
        return checkingHighscores;
    }

}
