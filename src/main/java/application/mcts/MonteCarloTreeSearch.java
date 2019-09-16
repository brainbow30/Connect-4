package application.mcts;


import application.game.Board;
import application.game.COLOUR;
import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;


public class MonteCarloTreeSearch {

    private final TreeNode root;
    private final Integer waitTime;
    private final Boolean useNN;


    public MonteCarloTreeSearch(Board board, COLOUR colour, Integer waitTime, Boolean useNN, String hostname) {
        root = TreeNode.builder()
                .parent(null)
                .currentBoard(board)
                .colour(colour)
                .rootColour(colour)
                .positionToCreateBoard(null)
                .hostname(hostname)
                .build();
        root.visited();
        this.waitTime = waitTime;
        this.useNN = useNN;
    }

    public MonteCarloTreeSearch(TreeNode node, Integer waitTime, Boolean useNN) {
        root = node;
        root.visited();
        root.setRoot();
        this.waitTime = waitTime;
        this.useNN = useNN;
    }

    public TreeNode run() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        while (stopwatch.elapsed(TimeUnit.MILLISECONDS) < waitTime) {

            TreeNode selectedNode = root.selectRandomMove();
            while (selectedNode.isVisited() && !selectedNode.isTerminalNode()) {
                selectedNode = selectedNode.selectRandomMove();
            }

            Double result = selectedNode.simulateGame(useNN);
            propagateResult(selectedNode, result);
            selectedNode.visited();
        }

        TreeNode selectMove = root.selectUCTMove();
        System.out.println("selectMove value= " + selectMove.getNumberOfWins());
        System.out.println("selectMove sims = " + selectMove.getNumberOfSimulations());
        return selectMove;
    }

    private void propagateResult(TreeNode node, Double result) {

        while (!node.getRoot()) {
            node.addResult(result);
            node = node.getParent();
        }
        node.addResult(result);

    }

}
