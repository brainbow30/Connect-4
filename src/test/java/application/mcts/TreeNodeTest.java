package application.mcts;

import application.game.Board;
import application.game.COLOUR;
import application.game.verifiers.Connect4Verifier;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TreeNodeTest {
    private TreeNode whiteNode;
    private TreeNode blackNode;
    private TreeNode nnNode;
    private Board board;

    @Before
    public void setup() {
        board = new Board(4, new Connect4Verifier());
        Board nnboard = new Board(6, new Connect4Verifier());

        TreeNode.Builder builder = TreeNode.builder();
        builder.colour(COLOUR.RED);
        builder.currentBoard(board);
        builder.rootColour(COLOUR.RED);
        builder.parent(null);
        builder.positionToCreateBoard(null);
        whiteNode = builder.build();
        builder.colour(COLOUR.YELLOW);
        blackNode = builder.build();
        builder.currentBoard(nnboard);
        nnNode = builder.build();


    }

    @Test
    public void canonicalBoardTest() {
        ImmutableList<Integer> whiteNNBoard = GenerateNNData.canonicalBoard(whiteNode);
        ImmutableList<Integer> whiteExpected = ImmutableList.of(
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0);
        assertEquals(whiteExpected, whiteNNBoard);
        ImmutableList<Integer> blackExpected = ImmutableList.of(
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0);
        ImmutableList<Integer> blackNNBoard = GenerateNNData.canonicalBoard(blackNode);
        assertEquals(blackExpected, blackNNBoard);
    }


    //@Test
    public void getNNPrediction() {
        System.out.println(nnNode.getNNPrediction(false));
    }
}
