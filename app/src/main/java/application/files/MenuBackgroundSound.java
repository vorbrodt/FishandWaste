package application.files;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

//creating a service for playing background sound
public class MenuBackgroundSound extends Service {
    private MediaPlayer player;

    public IBinder onBind(Intent arg0) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.menustream);
        player.setLooping(true); // Set looping
        player.setVolume(100,100);


    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        //if checking highscore save the time played in song an resume that time in highscore
        if (MenuActivity.isCheckingHighscores()){
            savePlayedTime();
        }
        player.seekTo(MenuActivity.getLengthOfSong());
        player.start();
        return startId;
    }

    @Override
    public void onDestroy() {
        savePlayedTime();
        player.stop();
        player.release();
    }

    //uses MusicListener so it can be reached from MenuActivity
    private void savePlayedTime(){
        int lengthOfsong = player.getCurrentPosition(); //save how far we got in the song
        MenuActivity.setLengthOfSong(lengthOfsong);
    }

}