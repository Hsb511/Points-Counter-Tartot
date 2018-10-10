package team23.lecompteurdetartot.java_object;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Hugo Selle on 24/09/2018.
 */


public class Player implements Parcelable {
    private String name;
    private int victoryNumber;
    private int loseNumber;
    private long id;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeInt(victoryNumber);
        out.writeInt(loseNumber);
    }

    public static final Parcelable.Creator<Player> CREATOR
            = new Parcelable.Creator<Player>() {
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    private Player(Parcel in) {
        name = in.readString();
        victoryNumber = in.readInt();
        loseNumber = in.readInt();
    }

    public float calculateWinLoseRatio() {
        return ((float) victoryNumber/loseNumber);
    }

    public Player(String name) {
        this.name = name;
    }

    public boolean isEqual(Player otherPlayer) {
        return name.equals(otherPlayer.getName());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVictoryNumber() {
        return victoryNumber;
    }

    public void setVictoryNumber(int victoryNumber) {
        this.victoryNumber = victoryNumber;
    }

    public int getLoseNumber() {
        return loseNumber;
    }

    public void setLoseNumber(int loseNumber) {
        this.loseNumber = loseNumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

