package application;

import com.google.common.base.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class BoardTest {

    private Board board;
    @Mock
    private Counter counter;
    @Mock
    private Verifier verifier;

    @Test
    public void setupTest() {
        board = new Board(8, verifier, 0.01, 10.0, 1.0);
        ImmutablePosition.Builder positionBuilder = ImmutablePosition.builder();
        positionBuilder.x(3).y(3);
        //noinspection OptionalGetWithoutIsPresent
        assertEquals(board.getCounter(positionBuilder.build()).get().getColour(), COLOUR.WHITE);
        positionBuilder.x(3).y(4);
        //noinspection OptionalGetWithoutIsPresent
        assertEquals(board.getCounter(positionBuilder.build()).get().getColour(), COLOUR.BLACK);
        positionBuilder.x(4).y(4);
        //noinspection OptionalGetWithoutIsPresent
        assertEquals(board.getCounter(positionBuilder.build()).get().getColour(), COLOUR.WHITE);
        positionBuilder.x(4).y(3);
        //noinspection OptionalGetWithoutIsPresent
        assertEquals(board.getCounter(positionBuilder.build()).get().getColour(), COLOUR.BLACK);

    }

    @Test
    public void addCounterTest() {

        board = new Board(8, new Verifier(), 0.01, 10.0, 1.0);
        Counter counter1 = new Counter();
        ImmutablePosition.Builder positionBuilder = ImmutablePosition.builder();
        positionBuilder.x(5).y(3);
        board.addCounter(counter1, positionBuilder.build());

        assertTrue(board.getCounter(positionBuilder.build()).isPresent());
        assertEquals(counter1, board.getCounter(positionBuilder.build()).get());


    }

    @Test
    public void addCounterOutOfBoundsTest() {
        board = new Board(8, verifier, 0.01, 10.0, 1.0);
        ImmutablePosition.Builder positionBuilder = ImmutablePosition.builder();
        positionBuilder.x(-1).y(-1);
        assertEquals(false, board.addCounter(counter, positionBuilder.build()));


    }

    @Test
    public void addCounterOutOfBoundsTest1() {
        board = new Board(8, verifier, 0.01, 10.0, 1.0);
        ImmutablePosition.Builder positionBuilder = ImmutablePosition.builder();

        positionBuilder.x(0).y(-1);
        assertEquals(false, board.addCounter(counter, positionBuilder.build()));


    }

    @Test
    public void addCounterOutOfBoundsTest2() {
        board = new Board(8, verifier, 0.01, 10.0, 1.0);
        ImmutablePosition.Builder positionBuilder = ImmutablePosition.builder();

        positionBuilder.x(-1).y(0);
        assertEquals(false, board.addCounter(counter, positionBuilder.build()));


    }

    @Test
    public void addCounterOutOfBoundsTest3() {
        board = new Board(8, verifier, 0.01, 10.0, 1.0);
        ImmutablePosition.Builder positionBuilder = ImmutablePosition.builder();

        positionBuilder.x(8).y(8);
        assertEquals(false, board.addCounter(counter, positionBuilder.build()));


    }

    @Test
    public void addCounterOutOfBoundsTest4() {
        board = new Board(8, verifier, 0.01, 10.0, 1.0);
        ImmutablePosition.Builder positionBuilder = ImmutablePosition.builder();

        positionBuilder.x(7).y(8);
        assertEquals(false, board.addCounter(counter, positionBuilder.build()));


    }

    @Test
    public void addCounterOutOfBoundsTest5() {
        board = new Board(8, verifier, 0.01, 10.0, 1.0);
        ImmutablePosition.Builder positionBuilder = ImmutablePosition.builder();

        positionBuilder.x(8).y(7);
        assertEquals(false, board.addCounter(counter, positionBuilder.build()));


    }


    @Test
    public void getCounterOutOfBounds() {
        board = new Board(8, verifier, 0.01, 10.0, 1.0);
        ImmutablePosition.Builder positionBuilder = ImmutablePosition.builder();
        positionBuilder.x(-1).y(-1);

        assertEquals(Optional.absent(), board.getCounter(positionBuilder.build()));
    }

    @Test
    public void printBoard() {
        board = new Board(8, verifier, 0.01, 10.0, 1.0);
        System.out.println("board = " + board);
    }

    @Test
    public void flipLeftCounterTest() {
        board = new Board(8, new Verifier(), 0.01, 10.0, 1.0);
        Counter counter1 = new Counter();
        ImmutablePosition.Builder positionBuilder = ImmutablePosition.builder();
        positionBuilder.x(5).y(3);

        board.addCounter(counter1, positionBuilder.build());
        //System.out.println("board = " + board);
        assertTrue(board.getCounter(positionBuilder.build()).isPresent());
        assertEquals(counter1, board.getCounter(positionBuilder.build()).get());
        positionBuilder.x(4);
        assertEquals(COLOUR.WHITE, board.getCounter(positionBuilder.build()).get().getColour());
    }

    @Test
    public void flipRightCounterTest() {
        board = new Board(8, new Verifier(), 0.01, 10.0, 1.0);
        Counter counter1 = new Counter();
        ImmutablePosition.Builder positionBuilder = ImmutablePosition.builder();
        positionBuilder.x(2).y(4);

        board.addCounter(counter1, positionBuilder.build());
        //System.out.println("board = " + board);
        assertTrue(board.getCounter(positionBuilder.build()).isPresent());
        assertEquals(counter1, board.getCounter(positionBuilder.build()).get());
        positionBuilder.x(3);
        assertEquals(COLOUR.WHITE, board.getCounter(positionBuilder.build()).get().getColour());
    }

    @Test
    public void flipUpCounterTest() {
        board = new Board(8, new Verifier(), 0.01, 10.0, 1.0);
        Counter counter1 = new Counter();
        ImmutablePosition.Builder positionBuilder = ImmutablePosition.builder();
        positionBuilder.x(3).y(5);

        board.addCounter(counter1, positionBuilder.build());
        //System.out.println("board = " + board);
        assertTrue(board.getCounter(positionBuilder.build()).isPresent());
        assertEquals(counter1, board.getCounter(positionBuilder.build()).get());
        positionBuilder.y(4);
        assertEquals(COLOUR.WHITE, board.getCounter(positionBuilder.build()).get().getColour());
    }

    @Test
    public void flipDownCounterTest() {
        board = new Board(8, new Verifier(), 0.01, 10.0, 1.0);
        Counter counter1 = new Counter();
        ImmutablePosition.Builder positionBuilder = ImmutablePosition.builder();
        positionBuilder.x(4).y(2);

        board.addCounter(counter1, positionBuilder.build());
        //System.out.println("board = " + board);
        assertTrue(board.getCounter(positionBuilder.build()).isPresent());
        assertEquals(counter1, board.getCounter(positionBuilder.build()).get());
        positionBuilder.y(3);
        assertEquals(COLOUR.WHITE, board.getCounter(positionBuilder.build()).get().getColour());
    }

    @Test
    public void flipLeftDownCounterTest() {
        board = new Board(8, new Verifier(), 0.01, 10.0, 1.0);
        Counter counter1 = new Counter();
        ImmutablePosition.Builder positionBuilder = ImmutablePosition.builder();
        positionBuilder.x(5).y(3);

        board.addCounter(counter1, positionBuilder.build());

        Counter counter2 = new Counter(COLOUR.BLACK);
        positionBuilder.x(5).y(2);
        board.addCounter(counter2, positionBuilder.build());
        System.out.println("board = " + board);
        assertTrue(board.getCounter(positionBuilder.build()).isPresent());
        assertEquals(counter2, board.getCounter(positionBuilder.build()).get());
        positionBuilder.x(4).y(3);

        assertEquals(COLOUR.BLACK, board.getCounter(positionBuilder.build()).get().getColour());
    }

    @Test
    public void flipRightUpCounterTest() {
        board = new Board(8, new Verifier(), 0.01, 10.0, 1.0);
        Counter counter1 = new Counter();
        ImmutablePosition.Builder positionBuilder = ImmutablePosition.builder();
        positionBuilder.x(5).y(3);

        board.addCounter(counter1, positionBuilder.build());

        Counter counter2 = new Counter(COLOUR.BLACK);
        positionBuilder.x(5).y(2);
        board.addCounter(counter2, positionBuilder.build());
        System.out.println("board = " + board);
        assertTrue(board.getCounter(positionBuilder.build()).isPresent());
        assertEquals(counter2, board.getCounter(positionBuilder.build()).get());
        positionBuilder.x(4).y(3);

        assertEquals(COLOUR.BLACK, board.getCounter(positionBuilder.build()).get().getColour());
    }

    @Test
    public void flipRightDownCounterTest() {
        board = new Board(8, new Verifier(), 0.01, 10.0, 1.0);
        Counter counter1 = new Counter(COLOUR.BLACK);
        ImmutablePosition.Builder positionBuilder = ImmutablePosition.builder();
        positionBuilder.x(2).y(3);

        board.addCounter(counter1, positionBuilder.build());

        Counter counter2 = new Counter(COLOUR.WHITE);
        positionBuilder.x(2).y(2);
        board.addCounter(counter2, positionBuilder.build());
        System.out.println("board = " + board);
        assertTrue(board.getCounter(positionBuilder.build()).isPresent());
        assertEquals(counter2, board.getCounter(positionBuilder.build()).get());
        positionBuilder.x(3).y(3);

        assertEquals(COLOUR.WHITE, board.getCounter(positionBuilder.build()).get().getColour());
    }

    @Test
    public void flipLeftUpCounterTest() {
        board = new Board(8, new Verifier(), 0.01, 10.0, 1.0);
        Counter counter1 = new Counter(COLOUR.BLACK);
        ImmutablePosition.Builder positionBuilder = ImmutablePosition.builder();
        positionBuilder.x(5).y(4);

        board.addCounter(counter1, positionBuilder.build());

        Counter counter2 = new Counter(COLOUR.WHITE);
        positionBuilder.x(5).y(5);
        board.addCounter(counter2, positionBuilder.build());
        System.out.println("board = " + board);
        assertTrue(board.getCounter(positionBuilder.build()).isPresent());
        assertEquals(counter2, board.getCounter(positionBuilder.build()).get());
        positionBuilder.x(4).y(4);

        assertEquals(COLOUR.WHITE, board.getCounter(positionBuilder.build()).get().getColour());
    }
}
