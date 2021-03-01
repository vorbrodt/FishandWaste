package application.files;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_HIGHSCORE = "Highscore.db";
    private static final String TABLE_NAME = "highscore_table"; //specific way it should look
    private static final String PLAYER_NAME = "PLAYERNAME";
    private static final String SCORE = "SCORE";

    DatabaseHelper(Context context) {
        super(context, DATABASE_HIGHSCORE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create the table with all columns, need to call on this the first time to create the table
        System.out.println("In onCreate()");
        db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,PLAYERNAME TEXT,SCORE INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("In onUpgrade()");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    boolean insertData(String playerName, Integer score){
        //this will get the database
        SQLiteDatabase db = this.getWritableDatabase();
        //put all values in object ContentValues
        ContentValues contentValues = new ContentValues();
        contentValues.put(PLAYER_NAME, playerName);
        contentValues.put(SCORE, score);
        //insert data into db
        System.out.println("content stuff: " + contentValues.toString());
        long resultStatus = db.insert(TABLE_NAME, null, contentValues); //return -1 if not successful in inserting data to db
        return resultStatus != -1;
    }

    Cursor queryByScore(){
        SQLiteDatabase db = this.getWritableDatabase();
        //default order is ASC(small to big for numbers), reverse that is DESC
        return db.rawQuery("select * from " + TABLE_NAME + " ORDER BY " + SCORE + " DESC", null);
    }

}
