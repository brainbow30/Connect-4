package application.game;

import application.ImmutablePosition;
import application.game.verifiers.Connect4Verifier;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Connect4VerifierTest {

    private Connect4Verifier verifier;

    private Board board;
    private Counter counter;
    private application.ImmutablePosition position;

    @Before
    private void setup() {
        verifier = new Connect4Verifier();
        board = new Board(8, verifier);
    }

    @Test
    public void validRightMoveTest() {
        verifier = new Connect4Verifier();
        board = new Board(8, verifier);
        counter = new Counter(COLOUR.RED);


        application.ImmutablePosition.Builder builder = application.ImmutablePosition.builder();
        builder.x(5);
        builder.y(3);
        position = builder.build();
        assertEquals(true, verifier.validMove(board, counter.getColour(), position));


    }

    @Test
    public void validLeftMoveTest() {

        counter = new Counter(COLOUR.RED);


        application.ImmutablePosition.Builder builder = application.ImmutablePosition.builder();
        builder.x(2);
        builder.y(4);
        position = builder.build();
        assertEquals(true, verifier.validMove(board, counter.getColour(), position));


    }

    @Test
    public void validDiagonalMoveTest() {
        counter = new Counter(COLOUR.RED);


        application.ImmutablePosition.Builder builder = application.ImmutablePosition.builder();
        builder.x(5);
        builder.y(3);
        position = builder.build();
        assertEquals(true, verifier.validMove(board, counter.getColour(), position));
        board.addCounter(counter, position);

        builder.y(2);
        position = builder.build();
        counter = new Counter(COLOUR.YELLOW);
        assertEquals(true, verifier.validMove(board, counter.getColour(), position));


    }

    @Test
    public void invalidColourTest() {
        counter = new Counter(COLOUR.RED);


        application.ImmutablePosition.Builder builder = application.ImmutablePosition.builder();
        builder.y(3);
        builder.x(2);
        position = builder.build();
        assertEquals(false, verifier.validMove(board, counter.getColour(), position));

    }

    @Test
    public void noNeighboursTest() {
        counter = new Counter(COLOUR.RED);


        application.ImmutablePosition.Builder builder = application.ImmutablePosition.builder();
        builder.y(3);
        builder.x(1);
        position = builder.build();
        assertEquals(false, verifier.validMove(board, counter.getColour(), position));

    }

    @Test
    public void occupiedSpaceTest() {
        counter = new Counter(COLOUR.RED);


        application.ImmutablePosition.Builder builder = ImmutablePosition.builder();
        builder.y(3);
        builder.x(3);
        position = builder.build();
        assertEquals(false, verifier.validMove(board, counter.getColour(), position));

    }

}
