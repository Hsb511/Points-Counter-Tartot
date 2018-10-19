package team23.lecompteurdetartot.java_object;

import java.util.ArrayList;

/**
 * Created by Hugo Selle on 24/09/2018.
 */

public class Game {
    private long id;
    private long partyId;
    private int pointsAttack;
    private int oudlersNumberAttack;
    private ArrayList<Float> scoreArrayList;
    private Player dealer;
    private Bid bid;
    private Player attacker;
    private Player called;
    private Chelem chelem;
    private int chelemTeam; //0: none, 1: Attack; 2: Defense
    private int oneAtEnd; //0: none, 1: Attack; 2: Defense
    private Handful handfulAttack;
    private Handful handfulDefense;
    private ArrayList<Player> miseryPlayersList;


    /**
     * constructor used for a PASS
     * @param bid the biggest bid chosen (by the attacker if not a PASS)
     * @param dealer the Player that deals (will be automatically incremented by the Activity)
     */
    public Game(long partyId, Bid bid, Player dealer) {
        this.bid = bid;
        this.dealer = dealer;
    }


    public int calculateBaseScore() {
        int pointsOverContract;
        switch (oudlersNumberAttack) {
            case 0: pointsOverContract = pointsAttack - 56; break;
            case 1: pointsOverContract = pointsAttack - 51; break;
            case 2: pointsOverContract = pointsAttack - 41; break;
            case 3: pointsOverContract = pointsAttack - 36; break;
            default: throw new IllegalArgumentException();
        }
        int scoreBeforeMultiplicator = 25 + Math.abs(pointsOverContract);
        int points = scoreBeforeMultiplicator * bid.getMultiplicant();
        if (handfulAttack != null) {
            points += handfulAttack.getPoints();
        }
        if (pointsOverContract < 0) {
            points = -points;
        }
        if (oneAtEnd == 1) {
            points += 10*bid.getMultiplicant();
        } else if (oneAtEnd == 2) {
            points -= 10*bid.getMultiplicant();
        }
        if (handfulDefense != null) {
            points -= handfulDefense.getPoints();
        }
        if (chelemTeam == 1) {
            points += chelem.getPoints();
        } else if (chelemTeam == 2) {
            points += chelem.getPoints();
        }

        return points;
    }


    public ArrayList<Float> getScoreArrayList() {
        return scoreArrayList;
    }

    public void setScoreArrayList(ArrayList<Float> scoreArrayList) {
        this.scoreArrayList = scoreArrayList;
    }

    public Player getDealer() {
        return dealer;
    }

    public void setDealer(Player dealer) {
        this.dealer = dealer;
    }

    public Bid getBid() {
        return bid;
    }

    public void setBid(Bid bid) {
        this.bid = bid;
    }

    public Player getAttacker() {
        return attacker;
    }

    public void setAttacker(Player attacker) {
        this.attacker = attacker;
    }

    public Player getCalled() {
        return called;
    }

    public void setCalled(Player called) {
        this.called = called;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPointsAttack() {
        return pointsAttack;
    }

    public void setPointsAttack(int pointsAttack) {
        this.pointsAttack = pointsAttack;
    }

    public int getOudlersNumberAttack() {
        return oudlersNumberAttack;
    }

    public void setOudlersNumberAttack(int oudlersNumberAttack) {
        this.oudlersNumberAttack = oudlersNumberAttack;
    }

    public Chelem getChelem() {
        return chelem;
    }

    public void setChelem(Chelem chelem) {
        this.chelem = chelem;
    }

    public int getChelemTeam() {
        return chelemTeam;
    }

    public void setChelemTeam(int chelemTeam) {
        this.chelemTeam = chelemTeam;
    }

    public int getOneAtEnd() {
        return oneAtEnd;
    }

    public void setOneAtEnd(int oneAtEnd) {
        this.oneAtEnd = oneAtEnd;
    }

    public Handful getHandfulAttack() {
        return handfulAttack;
    }

    public void setHandfulAttack(Handful handfulAttack) {
        this.handfulAttack = handfulAttack;
    }

    public Handful getHandfulDefense() {
        return handfulDefense;
    }

    public void setHandfulDefense(Handful handfulDefense) {
        this.handfulDefense = handfulDefense;
    }

    public ArrayList<Player> getMiseryPlayersList() {
        return miseryPlayersList;
    }

    public void setMiseryPlayersList(ArrayList<Player> miseryPlayersList) {
        this.miseryPlayersList = miseryPlayersList;
    }

    public long getPartyId() {
        return partyId;
    }

    public void setPartyId(long partyId) {
        this.partyId = partyId;
    }


}
