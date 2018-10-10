package team23.lecompteurdetartot.java_object;

/**
 * Created by Hugo Selle on 26/09/2018.
 */

public enum  Handful {
    SIMPLE_HANDFUL(20),
    DOUBLE_HANDFUL(30),
    TRIPLE_HANDFUL(40);

    private int points;

    Handful(int points) {
        this.points = points;
    }

    public int getPoints() {
        return this.points;
    }

    public Handful intToHandful (int points) {
        switch (points) {
            case 20: return SIMPLE_HANDFUL;
            case 30: return DOUBLE_HANDFUL;
            case 40: return TRIPLE_HANDFUL;
            default: throw new IllegalArgumentException();
        }
    }
}
