package application.files;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import static application.files.MenuActivity.menuSound;

public class HighscoreActivity extends AppCompatActivity {

    private boolean backButtonPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        DatabaseHelper db = new DatabaseHelper(this);
        Cursor allHighscores = db.queryByScore();

        LinearLayout nameList = findViewById(R.id.NameList);
        LinearLayout scoreList = findViewById(R.id.scoreList);
        LinearLayout rankList = findViewById(R.id.rankList);

        if (allHighscores.getCount()==0){
            return;
        }

        int ranking = 0;
        while (allHighscores.moveToNext()) {
            ranking ++;

            String rank = Integer.toString(ranking);
            String username = allHighscores.getString(1);
            String score = allHighscores.getString(2);

            TextView usernameView = new TextView(this);
            TextView scoreView = new TextView(this);
            TextView rankView = new TextView(this);

            //set username in list
            makeHighscoreFormat(usernameView, username, nameList);
            //set score in list
            makeHighscoreFormat(scoreView, score, scoreList);
            //set rank in list
            makeHighscoreFormat(rankView, rank, rankList);
            }
        }

    private void makeHighscoreFormat(TextView highscore, String inputString, LinearLayout highscoreList){
        highscore.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        highscore.setTextSize(22);
        highscore.setTextColor(Color.WHITE);
        highscore.setBackgroundResource(R.drawable.borderbuttonblack);
        String inputStringWithSpace = " " + inputString;
        highscore.setText(inputStringWithSpace);
        highscore.setTypeface(Typeface.SERIF);
        highscoreList.addView(highscore);
    }

    public void backToStartMenu(View view) {
        backButtonPressed = true;
        //startService(menuSound);
        finish(); //end this activity and go back to last one
    }

    //when we get back from having pressed menu button while being in Highscore
    @Override
    protected void onResume() {
        super.onResume();
        startService(menuSound);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!backButtonPressed){
            //only if mobile menu button was pressed
            stopService(menuSound);
        }
        backButtonPressed = false; //reset pressed mobile back button
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backButtonPressed = true;
    }

}
