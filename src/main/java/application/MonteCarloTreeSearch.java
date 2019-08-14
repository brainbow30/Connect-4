package application;


import com.google.common.collect.ImmutableList;

import java.util.Random;


public class MonteCarloTreeSearch {

    private TreeNode root;
    private Integer waitTime;
    private static double epsilon = 1e-6;
    private Counter.COLOUR colour;
    private final double heursticWeighting;
    private final double randomWeighting;


    public MonteCarloTreeSearch(Board board, Counter.COLOUR colour, Integer waitTime,
                                double heursticWeighting, double randomWeighting) {
        this.root = new TreeNode(null, board, colour, colour, null, heursticWeighting, randomWeighting);
        root.visited();
        this.colour = colour;
        this.waitTime = waitTime;
        this.heursticWeighting = heursticWeighting;
        this.randomWeighting = randomWeighting;


    }

    public MonteCarloTreeSearch(TreeNode node, Counter.COLOUR colour, Integer waitTime,
                                double heursticWeighting, double randomWeighting) {
        this.root = node;
        root.visited();
        root.setRoot();
        this.colour = colour;
        this.waitTime = waitTime;
        this.heursticWeighting = heursticWeighting;
        this.randomWeighting = randomWeighting;


    }

    public TreeNode run() {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + waitTime;

        while (System.currentTimeMillis() < endTime) {

            TreeNode selectedNode = selectNode(root);
            while (selectedNode.isVisited() && !selectedNode.isTerminalNode()) {
                    selectedNode = selectNode(selectedNode);
            }

            Integer result = selectedNode.simulateGame();
            selectedNode.addResult(result);
            propagateResult(selectedNode, result);
            selectedNode.visited();
        }

        TreeNode newNode = root.selectMove();
        return newNode;
    }

    private TreeNode selectNode(TreeNode node) {
        Random random = new Random();
        ImmutableList<TreeNode> children = node.getChildren();
        if (node.getChildren().size() == 0) {
            node.setTerminalNode();
            return node;
        }
        int bestIndex = 0;
        double bestValue = 0;
        int i = 0;
        for (TreeNode child : children) {
            double heursticValue = child.getCurrentBoard().getBoardHeurstic(colour, 1);

            double randomValue = random.nextDouble();

            double combinedValue = (heursticValue * heursticWeighting) + (randomValue * randomWeighting);

            if (combinedValue > bestValue) {
                bestValue = combinedValue;
                bestIndex = i;
            }
            i++;
        }

        return children.get(bestIndex);

    }


    private void propagateResult(TreeNode node, Integer result) {

        while (node.getParent() != null) {
            node.addResult(result);
            node = node.getParent();
        }
        node.addResult(result);

    }

}
