package application.mcts;


import application.game.Board;
import application.game.COLOUR;
import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;


public class MonteCarloTreeSearch {

    private final TreeNode root;
    private final Integer waitTime;


    public MonteCarloTreeSearch(Board board, COLOUR colour, Integer waitTime) {
        root = TreeNode.builder()
                .parent(null)
                .currentBoard(board)
                .colour(colour)
                .rootColour(colour)
                .positionToCreateBoard(null)
                .build();
        root.visited();
        this.waitTime = waitTime;
    }

    public MonteCarloTreeSearch(TreeNode node, Integer waitTime) {
        root = node;
        root.visited();
        root.setRoot();
        this.waitTime = waitTime;
    }

    public TreeNode run() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        while (stopwatch.elapsed(TimeUnit.MILLISECONDS) < waitTime) {

            TreeNode selectedNode = root.selectRandomMove();
            while (selectedNode.isVisited() && !selectedNode.isTerminalNode()) {
                selectedNode = selectedNode.selectRandomMove();
            }

            COLOUR result = selectedNode.simulateGame();
            propagateResult(selectedNode, result);
            selectedNode.visited();
        }

        TreeNode selectMove = root.selectUCTMove();
        System.out.println("selectMove = " + selectMove.getNumberOfWins());
        System.out.println("selectMove sims = " + selectMove.getNumberOfSimulations());
        return selectMove;
    }

    private void propagateResult(TreeNode node, COLOUR result) {

        while (node.getPositionToCreateBoard() != null) {
            node.addResult(result);
            node = node.getParent();
        }
        node.addResult(result);

    }

}
