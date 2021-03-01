package application.files;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StartPageActivity extends AppCompatActivity {

    //How to track memory, network and CPU usage: Help -> Find Action -> profiler -> choose Profile 'app' (which is you phone)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swim);

        //thread that will wait 5 seconds before we start the game
        Thread thread = new Thread() {
            @Override
            public void run() {
                //super.run(); can't do this as that would run in AppCompatActivity which we don't want
                try {
                    sleep(3000);
                } catch (Exception e) {
                    System.out.println("Start Page Error: " + e);
                } finally {
                    Intent menuIntent = new Intent(StartPageActivity.this, MenuActivity.class);
                    startActivity(menuIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }};
        thread.start();
    }

    //When Activity is in background then onPause() method will execute
    //and when we go back to an activity onStart() and onResume() will execute
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
