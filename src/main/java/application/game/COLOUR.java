package application.game;

public enum COLOUR {
    BLACK,
    WHITE;

    public static COLOUR opposite(COLOUR colour) {
        if (colour.equals(WHITE)) {
            return BLACK;
        } else {
            return WHITE;
        }
    }

    public static Double getColourValue(COLOUR colour) {
        if (colour.equals(WHITE)) {
            return 1.0;
        } else if (colour.equals(BLACK)) {
            return -1.0;
        } else {
            return 0.0;
        }
    }
}
