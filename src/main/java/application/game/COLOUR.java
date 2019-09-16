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
}
