package application;

import org.junit.Test;

public class MonteCarloTreeSearchTest {
    private Board board = new Board(8, new Verifier(), 0.01, 10.0, 1.0);
    private MonteCarloTreeSearch monteCarloTreeSearch;


    @Test
    public void test() {
        monteCarloTreeSearch = new MonteCarloTreeSearch(board, Counter.COLOUR.WHITE, 3000, 0.0005, 0.5);
        ImmutablePosition immutablePosition = monteCarloTreeSearch.run().getPositionToCreateBoard();
        System.out.println("immutablePosition = " + immutablePosition);


    }
}
