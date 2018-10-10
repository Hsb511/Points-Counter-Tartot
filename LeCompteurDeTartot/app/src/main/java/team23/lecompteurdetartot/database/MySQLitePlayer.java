package team23.lecompteurdetartot.database;

/**
 * Created by Hugo Selle on 25/09/2018.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLitePlayer extends SQLiteOpenHelper {

    public static final String TABLE_PLAYER = "players";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "player";
    public static final String COLUMN_VICTORY = "victory";
    public static final String COLUMN_LOSE = "lose";

    private static final String DATABASE_NAME = "players.db";
    private static final int DATABASE_VERSION = 1;

    // Commande sql pour la création de la base de données
    private static final String DATABASE_CREATE = "create table "
            + TABLE_PLAYER + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_VICTORY + " integer, "
            + COLUMN_LOSE + " integer);";

    public MySQLitePlayer(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLitePlayer.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYER);
        onCreate(db);
    }
}