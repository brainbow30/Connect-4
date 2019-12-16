package application.game;

import application.ImmutablePosition;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
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

    @Before
    public void setup() {
        board = new Board(8, new Verifier(), 0.01, 10.0, 1.0);
    }

    @Test
    public void setupTest() {
        application.ImmutablePosition.Builder positionBuilder = application.ImmutablePosition.builder();
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

        Counter counter1 = new Counter();
        application.ImmutablePosition.Builder positionBuilder = application.ImmutablePosition.builder();
        positionBuilder.x(5).y(3);
        board.addCounter(counter1, positionBuilder.build());

        assertTrue(board.getCounter(positionBuilder.build()).isPresent());
        assertEquals(counter1, board.getCounter(positionBuilder.build()).get());


    }

    @Test
    public void addCounterOutOfBoundsTest() {
        application.ImmutablePosition.Builder positionBuilder = application.ImmutablePosition.builder();
        positionBuilder.x(-1).y(-1);
        assertEquals(false, board.addCounter(counter, positionBuilder.build()));


    }

    @Test
    public void addCounterOutOfBoundsTest1() {
        application.ImmutablePosition.Builder positionBuilder = application.ImmutablePosition.builder();

        positionBuilder.x(0).y(-1);
        assertEquals(false, board.addCounter(counter, positionBuilder.build()));


    }

    @Test
    public void addCounterOutOfBoundsTest2() {
        application.ImmutablePosition.Builder positionBuilder = application.ImmutablePosition.builder();

        positionBuilder.x(-1).y(0);
        assertEquals(false, board.addCounter(counter, positionBuilder.build()));


    }

    @Test
    public void addCounterOutOfBoundsTest3() {
        application.ImmutablePosition.Builder positionBuilder = application.ImmutablePosition.builder();

        positionBuilder.x(8).y(8);
        assertEquals(false, board.addCounter(counter, positionBuilder.build()));


    }

    @Test
    public void addCounterOutOfBoundsTest4() {
        application.ImmutablePosition.Builder positionBuilder = application.ImmutablePosition.builder();

        positionBuilder.x(7).y(8);
        assertEquals(false, board.addCounter(counter, positionBuilder.build()));


    }

    @Test
    public void addCounterOutOfBoundsTest5() {
        application.ImmutablePosition.Builder positionBuilder = application.ImmutablePosition.builder();

        positionBuilder.x(8).y(7);
        assertEquals(false, board.addCounter(counter, positionBuilder.build()));


    }


    @Test
    public void getCounterOutOfBounds() {
        application.ImmutablePosition.Builder positionBuilder = application.ImmutablePosition.builder();
        positionBuilder.x(-1).y(-1);

        assertEquals(Optional.absent(), board.getCounter(positionBuilder.build()));
    }

    @Test
    public void printBoard() {
        System.out.println("board = " + board);
    }

    @Test
    public void flipLeftCounterTest() {
        Counter counter1 = new Counter();
        application.ImmutablePosition.Builder positionBuilder = application.ImmutablePosition.builder();
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
        Counter counter1 = new Counter();
        application.ImmutablePosition.Builder positionBuilder = application.ImmutablePosition.builder();
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
        Counter counter1 = new Counter();
        application.ImmutablePosition.Builder positionBuilder = application.ImmutablePosition.builder();
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
        Counter counter1 = new Counter();
        application.ImmutablePosition.Builder positionBuilder = application.ImmutablePosition.builder();
        positionBuilder.x(4).y(2);
        board.addCounter(counter1, positionBuilder.build());
        assertTrue(board.getCounter(positionBuilder.build()).isPresent());
        assertEquals(counter1, board.getCounter(positionBuilder.build()).get());
        positionBuilder.y(3);
        assertEquals(COLOUR.WHITE, board.getCounter(positionBuilder.build()).get().getColour());
    }

    @Test
    public void flipLeftDownCounterTest() {
        Counter counter1 = new Counter();
        application.ImmutablePosition.Builder positionBuilder = application.ImmutablePosition.builder();
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
        Counter counter1 = new Counter();
        application.ImmutablePosition.Builder positionBuilder = application.ImmutablePosition.builder();
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
        Counter counter1 = new Counter(COLOUR.BLACK);
        application.ImmutablePosition.Builder positionBuilder = application.ImmutablePosition.builder();
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
        Counter counter1 = new Counter(COLOUR.BLACK);
        application.ImmutablePosition.Builder positionBuilder = ImmutablePosition.builder();
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

    @Test
    public void asIntArrayTest() {
        ImmutableList<Integer> intBoard = board.asIntArray();
        ImmutableList<Integer> expected = ImmutableList.of(
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 1, -1, 0, 0, 0,
                0, 0, 0, -1, 1, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0
        );
        assertEquals(expected, intBoard);
    }
}
