package team23.lecompteurdetartot.database;

/**
 * Created by Hugo Selle on 25/09/2018.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteParty extends SQLiteOpenHelper {

    public static final String TABLE_PARTY = "parties";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_NAME = "party_name";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_PLAYER_ID_1 = "player_id_1";
    public static final String COLUMN_PLAYER_ID_2 = "player_id_2";
    public static final String COLUMN_PLAYER_ID_3 = "player_id_3";
    public static final String COLUMN_PLAYER_ID_4 = "player_id_4";
    public static final String COLUMN_PLAYER_ID_5 = "player_id_5";

    private static final String DATABASE_NAME = "parties.db";
    private static final int DATABASE_VERSION = 1;

    // Commande sql pour la création de la base de données
    private static final String DATABASE_CREATE = "create table "
            + TABLE_PARTY + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TYPE + " int not null, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_DATE + " text not null, "
            + COLUMN_PLAYER_ID_1 + " integer, "
            + COLUMN_PLAYER_ID_2 + " integer, "
            + COLUMN_PLAYER_ID_3 + " integer, "
            + COLUMN_PLAYER_ID_4 + " integer, "
            + COLUMN_PLAYER_ID_5 + " integer)";

    public MySQLiteParty(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteParty.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTY);
        onCreate(db);
    }
}