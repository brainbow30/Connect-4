package application.game;

public enum COLOUR {
    YELLOW,
    RED;

    public static COLOUR opposite(COLOUR colour) {
        if (colour.equals(RED)) {
            return YELLOW;
        } else {
            return RED;
        }
    }

    public static Double getColourValue(COLOUR colour) {
        if (colour.equals(RED)) {
            return 1.0;
        } else if (colour.equals(YELLOW)) {
            return -1.0;
        } else {
            return 0.0;
        }
    }
}
