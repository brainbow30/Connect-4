package application.game;

import application.ImmutablePosition;
import application.game.verifiers.OthelloVerifier;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OthelloVerifierTest {

    private OthelloVerifier verifier;

    private Board board;
    private Counter counter;
    private application.ImmutablePosition position;


    @Test
    public void validRightMoveTest() {
        board = new Board(8, verifier, 0.01, 10.0, 1.0);
        counter = new Counter(COLOUR.WHITE);
        verifier = new OthelloVerifier();

        application.ImmutablePosition.Builder builder = application.ImmutablePosition.builder();
        builder.x(5);
        builder.y(3);
        position = builder.build();
        assertEquals(true, verifier.validMove(board, counter.getColour(), position));


    }

    @Test
    public void validLeftMoveTest() {
        board = new Board(8, verifier, 0.01, 10.0, 1.0);
        counter = new Counter(COLOUR.WHITE);
        verifier = new OthelloVerifier();

        application.ImmutablePosition.Builder builder = application.ImmutablePosition.builder();
        builder.x(2);
        builder.y(4);
        position = builder.build();
        assertEquals(true, verifier.validMove(board, counter.getColour(), position));


    }

    @Test
    public void validDiagonalMoveTest() {
        verifier = new OthelloVerifier();
        board = new Board(8, verifier, 0.01, 10.0, 1.0);
        counter = new Counter(COLOUR.WHITE);


        application.ImmutablePosition.Builder builder = application.ImmutablePosition.builder();
        builder.x(5);
        builder.y(3);
        position = builder.build();
        assertEquals(true, verifier.validMove(board, counter.getColour(), position));
        board.addCounter(counter, position);

        builder.y(2);
        position = builder.build();
        counter = new Counter(COLOUR.BLACK);
        assertEquals(true, verifier.validMove(board, counter.getColour(), position));


    }

    @Test
    public void invalidColourTest() {
        board = new Board(8, verifier, 0.01, 10.0, 1.0);
        counter = new Counter(COLOUR.WHITE);
        verifier = new OthelloVerifier();

        application.ImmutablePosition.Builder builder = application.ImmutablePosition.builder();
        builder.y(3);
        builder.x(2);
        position = builder.build();
        assertEquals(false, verifier.validMove(board, counter.getColour(), position));

    }

    @Test
    public void noNeighboursTest() {
        board = new Board(8, verifier, 0.01, 10.0, 1.0);
        counter = new Counter(COLOUR.WHITE);
        verifier = new OthelloVerifier();

        application.ImmutablePosition.Builder builder = application.ImmutablePosition.builder();
        builder.y(3);
        builder.x(1);
        position = builder.build();
        assertEquals(false, verifier.validMove(board, counter.getColour(), position));

    }

    @Test
    public void occupiedSpaceTest() {
        board = new Board(8, verifier, 0.01, 10.0, 1.0);
        counter = new Counter(COLOUR.WHITE);
        verifier = new OthelloVerifier();

        application.ImmutablePosition.Builder builder = ImmutablePosition.builder();
        builder.y(3);
        builder.x(3);
        position = builder.build();
        assertEquals(false, verifier.validMove(board, counter.getColour(), position));

    }

}
