package team23.lecompteurdetartot.database;

/**
 * Created by Hugo Selle on 25/09/2018.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import team23.lecompteurdetartot.java_object.Player;

public class PlayerDAO {
    // for BDD
    private SQLiteDatabase database;
    private MySQLitePlayer dbHelper;
    private String[] allColumns = { MySQLitePlayer.COLUMN_ID,
            MySQLitePlayer.COLUMN_NAME, MySQLitePlayer.COLUMN_VICTORY, MySQLitePlayer.COLUMN_LOSE };

    public PlayerDAO(Context context) {
        dbHelper = new MySQLitePlayer(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    //for creation of a Player in the bdd
    public Player createPlayer(String playerName, int victoryNumber, int loseNumber) {
        ContentValues values = new ContentValues();
        values.put(MySQLitePlayer.COLUMN_NAME, playerName);
        values.put(MySQLitePlayer.COLUMN_VICTORY, victoryNumber);
        values.put(MySQLitePlayer.COLUMN_LOSE, loseNumber);
        long insertId = database.insert(MySQLitePlayer.TABLE_PLAYER, null, values);
        Cursor cursor = database.query(MySQLitePlayer.TABLE_PLAYER, allColumns,
                MySQLitePlayer.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Player newPlayer = cursorToPlayer(cursor);
        cursor.close();
        return newPlayer;
    }

    public void deletePlayer(Player player) {
        long id = player.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(MySQLitePlayer.TABLE_PLAYER, MySQLitePlayer.COLUMN_ID
                + " = " + id, null);
    }

    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<Player>();

        Cursor cursor = database.query(MySQLitePlayer.TABLE_PLAYER,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Player player = cursorToPlayer(cursor);
            players.add(player);
            cursor.moveToNext();
        }
        // we close the cursor
        cursor.close();
        return players;
    }

    public boolean findPlayerByName(String name) {
        String columns[] = new String[]{MySQLitePlayer.COLUMN_NAME};
        String whereClause = MySQLitePlayer.COLUMN_NAME + "=?";
        String whereArgs[] =  new String[]{name};
        Cursor cursor = database.query(MySQLitePlayer.TABLE_PLAYER, columns, whereClause, whereArgs,null, null, null );
        Log.i("player", name + " founded in : " + String.valueOf(cursor.getCount()));
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return true;
        } else {
            cursor.moveToFirst();
            return false;
        }
    }

    public Player getPlayerByName(String name) {
        String whereClause = MySQLitePlayer.COLUMN_NAME + "=?";
        String whereArgs[] =  new String[]{name};
        Cursor cursor = database.query(MySQLitePlayer.TABLE_PLAYER, allColumns, whereClause, whereArgs,null, null, null );
        //Cursor cursor = database.query(MySQLitePlayer.TABLE_PLAYER, allColumns, MySQLitePlayer.COLUMN_NAME + " = " + name, null, null, null, null);
        //Log.i("player", String.valueOf(cursor.getCount()));
        cursor.moveToFirst();
        return cursorToPlayer(cursor);
    }

    public Player getPlayerById(long id) {
        String whereClause = MySQLitePlayer.COLUMN_ID + "=?";
        String whereArgs[] = new String[]{String.valueOf(id)};
        Cursor cursor = database.query(MySQLitePlayer.TABLE_PLAYER, allColumns, whereClause, whereArgs,null, null, null );
        cursor.moveToFirst();
        return cursorToPlayer(cursor);
    }

    private Player cursorToPlayer(Cursor cursor) {
        long id = cursor.getLong(0);
        String name = cursor.getString(1);
        int victoryAmount = cursor.getInt(2);
        int loseAmount = cursor.getInt(3);

        Player player = new Player(name);
        player.setId(id);
        player.setVictoryNumber(victoryAmount);
        player.setLoseNumber(loseAmount);
        return player;
    }
}