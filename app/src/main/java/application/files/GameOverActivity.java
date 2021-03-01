package application.files;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class GameOverActivity extends AppCompatActivity {
    //create instance of database
    private DatabaseHelper db;
    private EditText usernameInput;
    private int score;
    private MediaPlayer gameOverSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        score = Objects.requireNonNull(getIntent().getExtras()).getInt("final score");
        usernameInput = findViewById(R.id.addUsername);
        db = new DatabaseHelper(this);
        //easier way of doing it
        gameOverSound = MediaPlayer.create(this, R.raw.gameoversound);
        gameOverSound.setVolume(0.1f,0.1f);
        gameOverSound.start();

        String yourFinalScore = "Your final score: " + score;
        TextView finalScore = findViewById(R.id.finalScore);
        finalScore.setText(yourFinalScore);

    }

    public void restartGame(View v){
        MainActivity.setHaveBeenGameOver(true);
        Intent restartIntent = new Intent(this, MainActivity.class);
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //so we can't go back to game over
        startActivity(restartIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); //transition between activities
        finish(); //end this activity, MainActivity is already ended so can't only call on finish here to go back
    }

    public void backToStartMenu(@SuppressWarnings("unused") View view) {
        Intent startMenuIntent = new Intent(this, MenuActivity.class);
        startActivity(startMenuIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish(); //end this activity
    }

    public void addHighscore(View view) {
        String writtenUsername = usernameInput.getText().toString();
        if (!writtenUsername.equals("") && score != 0){
            //insert writtenUsername and score into database
            boolean insertedData = db.insertData(writtenUsername, score);
            if (insertedData){
                Toast.makeText(this, "Highscore was added", Toast.LENGTH_SHORT).show();
                Intent startMenuIntent = new Intent(this, MenuActivity.class);
                startActivity(startMenuIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();}
            else{
                Toast.makeText(this, "Highscore couldn't be added", Toast.LENGTH_SHORT).show();
            }
        }
        else if (writtenUsername.equals("")){
        Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();}
        else if (score == 0){
            Toast.makeText(this, "Score cannot be 0", Toast.LENGTH_SHORT).show();}
    }

    //this runs whenever the app is closed, mobile arrow is pressed or we switch activity
    @Override
    protected void onStop(){
        super.onStop();
        gameOverSound.stop();
        gameOverSound.release(); //solve error: if run twice the app will close because we cant release it twice
    }

    //if pressed mobile back button go back to start menu
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        View v = new View(this);
        backToStartMenu(v);
    }
}
