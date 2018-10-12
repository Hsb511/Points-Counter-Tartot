package team23.lecompteurdetartot.database;

/**
 * Created by Hugo Selle on 26/09/2018.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteGame extends SQLiteOpenHelper {

    public static final String TABLE_GAMES = "games";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PARTY_ID = "party_id";
    public static final String COLUMN_DEALER = "dealer";                        //id of the Player
    public static final String COLUMN_MULTIPLICATOR = "multiplicator";          //0: PASS, 1: SMALL, 2: GUARD, 4: GUARD_WITHOUT, 6: GUARD_AGAINST
    public static final String COLUMN_OUDLERS_NUMBER = "oudler_amount";         //0, 1, 2, 3
    public static final String COLUMN_POINTS = "points";                        //[0, 91]
    public static final String COLUMN_ATTACKER = "attacker";                    //id of the Player
    public static final String COLUMN_CALLED = "called";                        //id of the Player
    public static final String COLUMN_HANDFUL_ATTACK = "handful_attack";        //20: simple, 30: double, 40: triple, null: none
    public static final String COLUMN_HANDFUL_DEFENSE = "handful_defense";      //same as above
    public static final String COLUMN_CHELEM_POINTS = "chelem_points";          // 400, 200, -200
    public static final String COLUMN_CHELEM_TEAM = "chelem_team";              //0: defense, 1: attack, -1:none
    public static final String COLUMN_ONE_AT_END = "one_at_end";                //same as above
    public static final String COLUMN_MISERY_1 = "misery_1";                    //id of the Player
    public static final String COLUMN_MISERY_2 = "misery_2";                    //id of the Player
    public static final String COLUMN_MISERY_3 = "misery_3";                    //id of the Player
    public static final String COLUMN_MISERY_4 = "misery_4";                    //id of the Player
    public static final String COLUMN_MISERY_5 = "misery_5";                    //id of the Player



    private static final String DATABASE_NAME = "games.db";
    private static final int DATABASE_VERSION = 1;

    // Commande sql pour la création de la base de données
    private static final String DATABASE_CREATE = "create table "
            + TABLE_GAMES + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_PARTY_ID + "integer not null, "
            + COLUMN_DEALER + " integer not null, "
            + COLUMN_MULTIPLICATOR + " smallint, "
            + COLUMN_OUDLERS_NUMBER + " smallint, "
            + COLUMN_POINTS + " decimal (2,1), "
            + COLUMN_ATTACKER + " integer, "
            + COLUMN_CALLED + " integer, "
            + COLUMN_HANDFUL_ATTACK + " smallint, "
            + COLUMN_HANDFUL_DEFENSE + " smallint, "
            + COLUMN_CHELEM_POINTS + " smallint, "
            + COLUMN_CHELEM_TEAM + " smallint, "
            + COLUMN_ONE_AT_END + " smallint, "
            + COLUMN_MISERY_1 + " integer, "
            + COLUMN_MISERY_2 + " integer, "
            + COLUMN_MISERY_3 + " integer, "
            + COLUMN_MISERY_4 + " integer, "
            + COLUMN_MISERY_5 + " integer)";

    public MySQLiteGame(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteGame.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES);
        onCreate(db);
    }
}