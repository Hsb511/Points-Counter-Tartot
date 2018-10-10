package team23.lecompteurdetartot.java_object;

/**
 * Created by Hugo Selle on 26/09/2018.
 */

public enum GameType {
    TAROT(23),
    BELOTE(42),
    MANILLE(46);

    private int index;

    public int getIndex() {
        return index;
    }

    GameType(int points) {
        this.index = points;
    }

    public GameType getTypeFromIndex(int index) {
        switch (index) {
            case 23: return TAROT;
            case 42: return BELOTE;
            case 46: return MANILLE;
            default: throw new IllegalArgumentException();
        }
    }
}
