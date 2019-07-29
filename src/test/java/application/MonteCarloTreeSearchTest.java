package application;

import org.junit.Test;

public class MonteCarloTreeSearchTest {
    private Board board = new Board(8, new Verifier());
    private MonteCarloTreeSearch monteCarloTreeSearch;


    @Test
    public void test() {
        monteCarloTreeSearch = new MonteCarloTreeSearch(board, Counter.COLOUR.WHITE, 5.0);

    }
}
