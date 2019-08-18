package application;

import org.junit.Test;

public class MonteCarloTreeSearchTest {
    private final Board board = new Board(8, new Verifier(), 0.01, 10.0, 1.0);


    @Test
    public void test() {
        MonteCarloTreeSearch monteCarloTreeSearch = new MonteCarloTreeSearch(board, COLOUR.WHITE, 3000);
        ImmutablePosition immutablePosition = monteCarloTreeSearch.run().getPositionToCreateBoard();
        System.out.println("immutablePosition = " + immutablePosition);


    }
}
