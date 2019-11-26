package application.gui;

import application.game.Board;
import application.game.verifiers.OthelloVerifier;
import org.junit.Before;
import org.junit.Test;

public class GUITest {

    private Board board;
    private GUI gui;

    @Before
    public void setup() {
        board = new Board(4, new OthelloVerifier(), 0.0, 0.0, 0.0);
        gui = new GUI(board);

    }

    @Test
    public void showGui() {
        gui.show();
    }

}
