package team23.lecompteurdetartot.java_object;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Hugo Selle on 24/09/2018.
 */

public class Party {
    private long id;
    private int playersAmount;
    private ArrayList<Player> playerArrayList;
    private ArrayList<Game> gameArrayList;
    private Date creationDate;
    private String partyName;
    private GameType gameType;

    public Party(GameType gameType, String partyName, ArrayList<Player> playerArrayList, Date creationDate) {
        this.gameType = gameType;
        this.partyName = partyName;
        this.playerArrayList = playerArrayList;
        this.playersAmount = playerArrayList.size();
        this.creationDate = creationDate;

    }

    public Party(GameType gameType, ArrayList<Player> playerArrayList, Date creationDate) {
        String defaultName = "Partie du " + creationDate.toString();
        new Party(gameType, defaultName, playerArrayList, creationDate);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPlayersAmount() {
        return playersAmount;
    }

    public void setPlayersAmount(int playersAmount) {
        this.playersAmount = playersAmount;
    }

    public ArrayList<Player> getPlayerArrayList() {
        return playerArrayList;
    }

    public void setPlayerArrayList(ArrayList<Player> playerArrayList) {
        this.playerArrayList = playerArrayList;
    }

    public ArrayList<Game> getGameArrayList() {
        return gameArrayList;
    }

    public void setGameArrayList(ArrayList<Game> gameArrayList) {
        this.gameArrayList = gameArrayList;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }


    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }
}