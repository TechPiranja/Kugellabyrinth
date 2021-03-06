package com.example.kugellabyrinth;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * The type Sq lite manager.
 */
public class SQLiteManager extends SQLiteOpenHelper {

    private static SQLiteManager sqLiteManager;

    private static final String DATABASE_NAME = "ScoreDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Score";
    private static final String COUNTER = "Counter";

    private static final String ID_FIELD = "id";
    private static final String USERNAME_FIELD = "username";
    private static final String TIMESPENT_FIELD = "timeSpent";
    private static final String LEVEL_FIELD = "level";

    /**
     * Instantiates a new Sq lite manager.
     *
     * @param context the context
     */
    public SQLiteManager(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Instance of database sq lite manager.
     *
     * @param context the context
     * @return the sq lite manager
     */
    public static SQLiteManager instanceOfDatabase(Context context){
        if(sqLiteManager == null)
            sqLiteManager = new SQLiteManager(context);

        return sqLiteManager;
    }

    /**
     * Populate score list array.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void populateScoreListArray(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        try(Cursor result =  sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null)){
            if(result.getCount() != 0){
                while(result.moveToNext()){
                    int id = result.getInt(1);
                    String username = result.getString(2);
                    int level = result.getInt(3);
                    int timeSpent = result.getInt(4);
                    Score score = new Score(id, username, level, timeSpent);
                    Score.scoreArrayList.add(score);
                }
            }
        }
    }

    /**
     * Creates the Score Table if the App was started the very first time (no db exists)
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sql;
        sql = new StringBuilder().append("CREATE TABLE ").append(TABLE_NAME).append("(")
                .append(COUNTER).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ").append(ID_FIELD)
                .append(" INT, ").append(USERNAME_FIELD).append(" TEXT, ").append(LEVEL_FIELD)
                .append(" INT, ").append(TIMESPENT_FIELD).append(" INT)");
        db.execSQL(sql.toString());
    }

    /**
     * Function wich upgrades the db
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This App doesnt need it, but it has to be implemented,
        // so the "extends SQLiteOpenHelper" line throws no error
    }

    /**
     * Add score to db.
     *
     * @param score the score
     */
    public void addScoreToDB(Score score){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_FIELD, score.getId());
        contentValues.put(USERNAME_FIELD, score.getUsername());
        contentValues.put(TIMESPENT_FIELD, score.getTimeSpent());
        contentValues.put(LEVEL_FIELD, score.getLevel());

        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }
}
