package team23.lecompteurdetartot.java_object;

/**
 * Created by Hugo Selle on 26/09/2018.
 */

public enum Chelem {
    ANNOUNCED_DONE(400),
    NON_ANNOUNCED_DONE(200),
    ANNOUNCED_FAILED(-200);

    private int points;

    Chelem(int points) {
        this.points = points;
    }

    public int getPoints() {
        return this.points;
    }

    public Chelem intToChelem (int points) {
        switch (points) {
            case 400: return ANNOUNCED_DONE;
            case 200: return NON_ANNOUNCED_DONE;
            case -200: return ANNOUNCED_FAILED;
            default: throw new IllegalArgumentException();
        }
    }
}
