package team23.lecompteurdetartot.database;

/**
 * Created by Hugo Selle on 25/09/2018.
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import team23.lecompteurdetartot.java_object.Party;
import team23.lecompteurdetartot.java_object.Player;
import team23.lecompteurdetartot.java_object.GameType;

public class PartyDAO {
    Context newContext;

    // for BDD
    private SQLiteDatabase database;
    private MySQLiteParty dbHelper;
    private String[] allColumns = {
            MySQLiteParty.COLUMN_ID,
            MySQLiteParty.COLUMN_TYPE,
            MySQLiteParty.COLUMN_NAME,
            MySQLiteParty.COLUMN_DATE,
            MySQLiteParty.COLUMN_PLAYER_ID_1,
            MySQLiteParty.COLUMN_PLAYER_ID_2,
            MySQLiteParty.COLUMN_PLAYER_ID_3,
            MySQLiteParty.COLUMN_PLAYER_ID_4,
            MySQLiteParty.COLUMN_PLAYER_ID_5};

    public PartyDAO(Context context) {
        dbHelper = new MySQLiteParty(context);
        newContext = context;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    //for creation of a Party
    public Party createParty(GameType gameType, String gameName, String dateString, ArrayList<Player> players) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteParty.COLUMN_TYPE, gameType.getIndex());
        values.put(MySQLiteParty.COLUMN_NAME, gameName);
        values.put(MySQLiteParty.COLUMN_DATE, dateString);


        for (int i = 1; i < players.size() + 1; i++) {
            values.put("player_id_"+String.valueOf(i), players.get(i-1).getId());
        }

        for (int i = players.size() + 1; i <= 5; i++) {
            String nullString = null;
            values.put("player_id_"+String.valueOf(i), nullString);
        }

        long insertId = database.insert(MySQLiteParty.TABLE_PARTY, null, values);
        Cursor cursor = database.query(MySQLiteParty.TABLE_PARTY, allColumns,
                MySQLiteParty.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Party newParty = cursorToParty(cursor);
        cursor.close();
        return newParty;
    }

    public void deleteParty(Party party) {
        long id = party.getId();
        System.out.println("Party deleted with id: " + id);
        database.delete(MySQLiteParty.TABLE_PARTY, MySQLiteParty.COLUMN_ID
                + " = " + id, null);
    }

    public List<Party> getAllParties() {
        List<Party> parties = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteParty.TABLE_PARTY,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Party party = cursorToParty(cursor);
            parties.add(party);
            cursor.moveToNext();
        }
        // assurez-vous de la fermeture du curseur
        cursor.close();
        return parties;
    }

    protected Party cursorToParty(Cursor cursor) {
        long partyId = cursor.getLong(0);

        GameType partyType = GameType.TAROT;
        partyType.getTypeFromIndex(cursor.getInt(1));

        String partyName = cursor.getString(2);

        String creationDateString = cursor.getString(3);
        Date creationDate = new Date();
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            creationDate = formatter.parse(creationDateString);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        PlayerDAO playerDAO = new PlayerDAO(newContext);
        playerDAO.open();

        ArrayList<Player> playersList = new ArrayList<>();
        for (int i = 4; i < 9; i++) {
            int playerId = cursor.getInt(i);

            if (playerId != 0) {
                playersList.add(playerDAO.getPlayerById(playerId));
            }
        }

        Party party = new Party(partyType, partyName, playersList, creationDate);
        party.setId(partyId);
        return party;
    }
}