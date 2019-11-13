package application.mcts;

import application.ImmutablePosition;
import application.game.Board;
import application.game.COLOUR;
import application.game.Counter;
import application.game.Verifier;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TreeNodeTest {
    private TreeNode whiteNode;
    private TreeNode blackNode;
    private TreeNode nnNode;
    private Board board;

    @Before
    public void setup() {
        board = new Board(4, new Verifier(), 0.0, 0.0, 0.0);
        Board nnboard = new Board(6, new Verifier(), 0.0, 0.0, 0.0);

        TreeNode.Builder builder = TreeNode.builder();
        builder.colour(COLOUR.WHITE);
        builder.currentBoard(board);
        builder.rootColour(COLOUR.WHITE);
        builder.parent(null);
        builder.positionToCreateBoard(null);
        whiteNode = builder.build();
        builder.colour(COLOUR.BLACK);
        blackNode = builder.build();
        builder.currentBoard(nnboard);
        nnNode = builder.build();


    }

    @Test
    public void canonicalBoardTest() {
        ImmutableList<Integer> whiteNNBoard = whiteNode.canonicalBoard();
        ImmutableList<Integer> whiteExpected = ImmutableList.of(
                0, 0, 0, 0,
                0, 1, -1, 0,
                0, -1, 1, 0,
                0, 0, 0, 0);
        assertEquals(whiteExpected, whiteNNBoard);
        ImmutableList<Integer> blackExpected = ImmutableList.of(
                0, 0, 0, 0,
                0, 1, -1, 0,
                0, -1, 1, 0,
                0, 0, 0, 0);
        ImmutableList<Integer> blackNNBoard = blackNode.canonicalBoard();
        assertEquals(blackExpected, blackNNBoard);
    }

    @Test
    public void findChildBoardMatchTest() {
        Counter counter1 = new Counter(COLOUR.WHITE);
        application.ImmutablePosition.Builder positionBuilder = ImmutablePosition.builder();
        positionBuilder.x(2).y(0);
        Board clone = board.clone();
        clone.addCounter(counter1, positionBuilder.build());
        TreeNode childBoardMatch = whiteNode.findChildBoardMatch(clone);
        assertTrue(whiteNode.getChildren().contains(childBoardMatch));


    }

    //@Test
    public void getNNPrediction() {
        System.out.println(nnNode.getNNPrediction(false));
    }
}
