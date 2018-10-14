package team23.lecompteurdetartot.database;

/**
 * Created by Hugo Selle on 25/09/2018.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import team23.lecompteurdetartot.java_object.Bid;
import team23.lecompteurdetartot.java_object.Chelem;
import team23.lecompteurdetartot.java_object.Game;
import team23.lecompteurdetartot.java_object.Handful;
import team23.lecompteurdetartot.java_object.Player;

public class GameDAO {
    // for BDD
    private Context context;
    private SQLiteDatabase database;
    private MySQLiteGame dbHelper;
        private String[] allColumns = { MySQLiteGame.COLUMN_ID,
                                        MySQLiteGame.COLUMN_PARTY_ID,
                                        MySQLiteGame.COLUMN_DEALER,
                                        MySQLiteGame.COLUMN_MULTIPLICATOR,
                                        MySQLiteGame.COLUMN_OUDLERS_NUMBER,
                                        MySQLiteGame.COLUMN_POINTS,
                                        MySQLiteGame.COLUMN_ATTACKER,
                                        MySQLiteGame.COLUMN_CALLED,
                                        MySQLiteGame.COLUMN_HANDFUL_ATTACK,
                                        MySQLiteGame.COLUMN_HANDFUL_DEFENSE,
                                        MySQLiteGame.COLUMN_CHELEM_POINTS,
                                        MySQLiteGame.COLUMN_CHELEM_TEAM,
                                        MySQLiteGame.COLUMN_ONE_AT_END,
                                        MySQLiteGame.COLUMN_MISERY_1,
                                        MySQLiteGame.COLUMN_MISERY_2,
                                        MySQLiteGame.COLUMN_MISERY_3,
                                        MySQLiteGame.COLUMN_MISERY_4,
                                        MySQLiteGame.COLUMN_MISERY_5};

    public GameDAO(Context context) {
        this.context = context;
        dbHelper = new MySQLiteGame(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    //for creation of a Player in the bdd

    /**
     * add a Game in the database and return the object Game with id set. Some arguments may be NA (Not Applicable)
     * @param partyId (long) the id of the party that it has been created in. NOT NULL
     * @param dealerId (long) the id of the dealer of the current Game. NOT NULL
     * @param multiplicator (int) the multiplicator that corresponds to the Bid. values cf Bid. NOT NULL (may be 0)
     * @param oudlers_number (int) the amount of oudlers of the Attack ! values in (0, 1, 2, 3) if NA -> -1
     * @param points (int) the points made by the Attack ! values in range (0, 91) if NA -> -1
     * @param attackerId (long) the id of the Attacker. if NA -> 0
     * @param calledId (long) the id of the Player called. If NA -> 0
     * @param handfulAttack (int) the points of the Handful of the Attack. values in (20, 30, 40). If NA -> -1
     * @param handfulDefense (int) the points of the Handful of the Defense. values in (20, 30, 40). If NA -> -1
     * @param chelemPoints (int) the points of the Chelem. values in (-200, 200, 400). If NA -> -1
     * @param chelemTeam (int) 0 = False -> Defense; 1 = True -> Attack; NA -> -1
     * @param oneAtEndTeam (int) 0 = False -> Defense; 1 = True -> Attack; NA -> -1
     * @param misery1 (long) the id of the player with a misery. If NA -> 0
     * @param misery2 (long) the id of the player with a misery. If NA -> 0
     * @return the Game with the arguments set and in particular its id
     */
    public Game createGame(long partyId, long dealerId, int multiplicator, int oudlers_number, int points,
                           long attackerId, long calledId, int handfulAttack, int handfulDefense,
                           int chelemPoints, int chelemTeam, int oneAtEndTeam, long misery1,
                           long misery2, long misery3, long misery4, long misery5) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteGame.COLUMN_PARTY_ID, partyId);
        values.put(MySQLiteGame.COLUMN_DEALER, dealerId);
        values.put(MySQLiteGame.COLUMN_MULTIPLICATOR, multiplicator);
        values.put(MySQLiteGame.COLUMN_OUDLERS_NUMBER, oudlers_number);
        values.put(MySQLiteGame.COLUMN_POINTS, points);
        values.put(MySQLiteGame.COLUMN_ATTACKER, attackerId);
        values.put(MySQLiteGame.COLUMN_CALLED, calledId);
        values.put(MySQLiteGame.COLUMN_HANDFUL_ATTACK, handfulAttack);
        values.put(MySQLiteGame.COLUMN_HANDFUL_DEFENSE, handfulDefense);
        values.put(MySQLiteGame.COLUMN_CHELEM_POINTS, chelemPoints);
        values.put(MySQLiteGame.COLUMN_CHELEM_TEAM, chelemTeam);
        values.put(MySQLiteGame.COLUMN_ONE_AT_END, oneAtEndTeam);
        values.put(MySQLiteGame.COLUMN_MISERY_1, misery1);
        values.put(MySQLiteGame.COLUMN_MISERY_2, misery2);
        values.put(MySQLiteGame.COLUMN_MISERY_3, misery3);
        values.put(MySQLiteGame.COLUMN_MISERY_4, misery4);
        values.put(MySQLiteGame.COLUMN_MISERY_5, misery5);
        Log.i("game_dao", String.valueOf(partyId) + ", " + String.valueOf(dealerId) + ", " + String.valueOf(database.getVersion()));
        Log.i("game_dao", values.toString());
        Log.i("game_dao", Arrays.toString(allColumns));
        long insertId = database.insert(MySQLiteGame.TABLE_GAMES, null, values);
        Cursor cursor = database.query(MySQLiteGame.TABLE_GAMES, allColumns,
                MySQLiteGame.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Game newGame = cursorToGame(cursor);
        cursor.close();
        return newGame;
    }

    public Game createPass(long partyId, long dealerId)  {
        ContentValues values = new ContentValues();
        values.put(MySQLiteGame.COLUMN_PARTY_ID, partyId);
        values.put(MySQLiteGame.COLUMN_DEALER, dealerId);
        values.put(MySQLiteGame.COLUMN_MULTIPLICATOR, 0);
        values.put(MySQLiteGame.COLUMN_OUDLERS_NUMBER, -1);
        values.put(MySQLiteGame.COLUMN_POINTS, -1);
        values.put(MySQLiteGame.COLUMN_ATTACKER, 0);
        values.put(MySQLiteGame.COLUMN_CALLED, 0);
        values.put(MySQLiteGame.COLUMN_HANDFUL_ATTACK, -1);
        values.put(MySQLiteGame.COLUMN_HANDFUL_DEFENSE, -1);
        values.put(MySQLiteGame.COLUMN_CHELEM_POINTS, -1);
        values.put(MySQLiteGame.COLUMN_CHELEM_TEAM, -1);
        values.put(MySQLiteGame.COLUMN_ONE_AT_END, -1);
        values.put(MySQLiteGame.COLUMN_MISERY_1, 0);
        values.put(MySQLiteGame.COLUMN_MISERY_2, 0);
        values.put(MySQLiteGame.COLUMN_MISERY_3, 0);
        values.put(MySQLiteGame.COLUMN_MISERY_4, 0);
        values.put(MySQLiteGame.COLUMN_MISERY_5, 0);
        long insertId = database.insert(MySQLiteGame.TABLE_GAMES, null, values);
        Cursor cursor = database.query(MySQLiteGame.TABLE_GAMES, allColumns,
                        MySQLiteGame.COLUMN_ID + " = " + insertId, null,
                        null, null, null);
        cursor.moveToFirst();
        Game newGame = cursorToGame(cursor);
        cursor.close();
        return newGame;
    }


    public void deleteGame(Game game) {
        long id = game.getId();
        database.delete(MySQLiteGame.TABLE_GAMES, MySQLiteGame.COLUMN_ID
                + " = " + id, null);
    }

    public List<Game> getAllGames() {
        List<Game> games = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteGame.TABLE_GAMES,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Game game = cursorToGame(cursor);
            games.add(game);
            cursor.moveToNext();
        }
        // we close the cursor
        cursor.close();
        return games;
    }

    private Game cursorToGame(Cursor cursor) {
        //we get the id
        long id = cursor.getLong(0);

        long partyId = cursor.getLong(1);
        //we get the dealer
        PlayerDAO playerDAO = new PlayerDAO(context);
        playerDAO.open();
        Player dealer = playerDAO.getPlayerById(cursor.getLong(2));

        //we get the bid
        Bid bid = Bid.PASS;
        bid = bid.intToBid(cursor.getInt(3));

        Game game = new Game(partyId, bid, dealer);
        game.setId(id);

        if (bid.equals(Bid.PASS)) {
            return game;
        } else {
            //we get from the cursor and set the oudlersAmount
            game.setOudlersNumberAttack(cursor.getInt(4));

            //we get from the cursor and set the points
            game.setPointsAttack(cursor.getInt(5));

            //we get from the cursor and set the attacker
            game.setAttacker(playerDAO.getPlayerById(cursor.getLong(6)));

            long calledId = cursor.getLong(7);
            if (calledId != 0) {
                game.setCalled(playerDAO.getPlayerById(calledId));
            }


            int handfulAttackPoints = cursor.getInt(8);
            Handful handfulAttack = Handful.SIMPLE_HANDFUL;
            if (handfulAttackPoints == 20 || handfulAttackPoints == 30 || handfulAttackPoints == 40) {
                game.setHandfulAttack(handfulAttack.intToHandful(handfulAttackPoints));
            }

            int handfulDefensePoints = cursor.getInt(9);
            Handful handfulDefense = Handful.SIMPLE_HANDFUL;
            if (handfulDefensePoints == 20 || handfulDefensePoints == 30 || handfulDefensePoints == 40) {
                game.setHandfulDefense(handfulDefense.intToHandful(handfulDefensePoints));
            }

            int chelemPoints = cursor.getInt(10);
            Chelem chelem = Chelem.NON_ANNOUNCED_DONE;
            if (chelemPoints == -200 || chelemPoints == 200 || chelemPoints == 400) {
                game.setChelem(chelem.intToChelem(chelemPoints));
            }

            int chelemTeam = cursor.getInt(11);
            game.setChelemTeam(chelemTeam);

            int oneAtEnd = cursor.getInt(12);
            game.setOneAtEnd(oneAtEnd);

            for (int i = 13; i < 18; i++) {
                long miseryId = cursor.getLong(i);
                ArrayList<Player> miseryList = new ArrayList<>();
                if (miseryId != 0) {
                    miseryList.add(playerDAO.getPlayerById(miseryId));
                }
                game.setMiseryPlayersList(miseryList);
            }

            return game;
        }
    }
}