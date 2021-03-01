package application.files;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

//creating a service for playing background sound
public class ThemeSong extends Service {
    private MediaPlayer player;

    public IBinder onBind(Intent arg0) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.themesong);
        player.setLooping(true); // Set looping
        player.setVolume(0.02f,0.02f);

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        player.seekTo(MainActivity.getLengthOfSong()); //set starting position in song
        //if backButton was pressed in MainActivity then we want to restart theme song
        if (MainActivity.isBackButtonPressed()){
            player.seekTo(0);
            MainActivity.setBackButtonPressed(false); //reset backButtonPressed
        }
        player.start();
        return startId;
    }

    @Override
    public void onDestroy() {
        int lengthOfsong = player.getCurrentPosition(); //save how far we got in the song
        MainActivity.setLengthOfSong(lengthOfsong);
        player.stop();
        player.release();
    }


}