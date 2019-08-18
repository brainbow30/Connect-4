package application;

public enum COLOUR {
    BLACK,
    WHITE;

    static COLOUR opposite(COLOUR colour) {
        if (colour.equals(WHITE)) {
            return BLACK;
        } else {
            return WHITE;
        }
    }


}
