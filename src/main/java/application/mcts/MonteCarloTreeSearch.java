package application.mcts;


import application.game.Board;
import application.game.COLOUR;
import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;


public class MonteCarloTreeSearch {

    private final TreeNode root;
    private final Integer waitTime;
    private final Boolean useNN;
    private final Double cpuct;


    public MonteCarloTreeSearch(Board board, COLOUR colour, Integer waitTime, Boolean useNN, String hostname, Double cpuct) {
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
        this.cpuct = cpuct;

        //initialize policy vector
        if (useNN) {
            root.getNNPrediction();
        }
    }

    public MonteCarloTreeSearch(TreeNode node, Integer waitTime, Boolean useNN, Double cpuct) {
        root = node;
        root.visited();
        root.setRoot();
        this.waitTime = waitTime;
        this.useNN = useNN;
        this.cpuct = cpuct;
    }

    public TreeNode run() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        //todo if all nodes visited stop
        while (stopwatch.elapsed(TimeUnit.MILLISECONDS) < waitTime) {

            TreeNode selectedNode = root.selectRandomMove();
            while (selectedNode.isVisited() && !selectedNode.isTerminalNode()) {
                selectedNode = selectedNode.selectRandomMove();
            }

            Double result = selectedNode.simulateGame(useNN);
            propagateResult(selectedNode, result);
            selectedNode.visited();
        }
        TreeNode selectMove;
        if (useNN) {
            selectMove = root.selectAlphaZeroMove(cpuct);
        } else {
            selectMove = root.selectUCTMove();
        }
        System.out.println("selectMove value= " + selectMove.getNumberOfWins());
        System.out.println("selectMove sims = " + selectMove.getNumberOfSimulations());
        return selectMove;
    }

    private void propagateResult(TreeNode node, Double result) {
        node = node.getParent();
        while (node.getParent() != null) {
            node.addResult(result);
            node = node.getParent();
        }
        node.addResult(result);

    }

}
