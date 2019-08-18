package application;


import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;


class MonteCarloTreeSearch {

    private final TreeNode root;
    private final Integer waitTime;


    public MonteCarloTreeSearch(Board board, COLOUR colour, Integer waitTime) {
        this.root = new TreeNode(null, board, colour, colour, null);
        root.visited();
        this.waitTime = waitTime;



    }

    public MonteCarloTreeSearch(TreeNode node, Integer waitTime) {
        this.root = node;
        root.visited();
        root.setRoot();
        this.waitTime = waitTime;


    }

    public TreeNode run() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        while (stopwatch.elapsed(TimeUnit.MILLISECONDS) < waitTime) {

            TreeNode selectedNode = root.selectMove();
            while (selectedNode.isVisited() && !selectedNode.isTerminalNode()) {
                selectedNode = selectedNode.selectMove();
            }

            COLOUR result = selectedNode.simulateGame();
            propagateResult(selectedNode, result);
            selectedNode.visited();
        }

        return root.selectMove();
    }

    private void propagateResult(TreeNode node, COLOUR result) {

        while (node.getParent() != null) {
            node.addResult(result);
            node = node.getParent();
        }
        node.addResult(result);

    }

}
