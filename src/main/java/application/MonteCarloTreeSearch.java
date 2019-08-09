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


    public MonteCarloTreeSearch(Board board, Counter.COLOUR colour, Integer waitTime, double heursticWeighting, double randomWeighting) {
        this.root = new TreeNode(null, board, colour, colour, null, heursticWeighting, randomWeighting);
        root.visited();
        this.colour = colour;
        this.waitTime = waitTime;
        this.heursticWeighting = heursticWeighting;
        this.randomWeighting = randomWeighting;


    }

    public MonteCarloTreeSearch(TreeNode node, Counter.COLOUR colour, Integer waitTime, double heursticWeighting, double randomWeighting) {
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

        System.out.println("\nbefore");
        for (TreeNode node : root.getChildren()) {
            System.out.println();
            System.out.println("wins = " + node.getNumberOfWins());
            System.out.println("played = " + node.getNumberOfSimulations());
        }
        while (System.currentTimeMillis() < endTime) {

            TreeNode selectedNode = selectNode(root);
            while (selectedNode.isVisited() && !selectedNode.isTerminalNode()) {
                    selectedNode = selectNode(selectedNode);
            }

            Integer result = selectedNode.simulateGame();
            propagateResult(selectedNode, result);
            selectedNode.visited();
        }
        System.out.println("\nafter");
        for (TreeNode node : root.getChildren()) {
            System.out.println();
            System.out.println("wins = " + node.getNumberOfWins());
            System.out.println("played = " + node.getNumberOfSimulations());
        }

        TreeNode newNode = selectMove(root);
        System.out.println("\nselected");
        System.out.println("wins = " + newNode.getNumberOfWins());
        System.out.println("played = " + newNode.getNumberOfSimulations());

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

    private TreeNode selectMove(TreeNode node) {
        Random random = new Random();
        ImmutableList<TreeNode> children = node.getChildren();
        Double bestValue = Double.MIN_VALUE;
        TreeNode selected = null;
        for (TreeNode child : children) {
            double uctValue = child.getNumberOfWins() / (child.getNumberOfSimulations() + epsilon) +
                    Math.sqrt(Math.log(node.getNumberOfSimulations() + 1) / (child.getNumberOfSimulations() + epsilon)) +
                    random.nextDouble() * epsilon;
            //System.out.println("uctValue = " + uctValue);
            if (uctValue > bestValue) {
                selected = child;
                bestValue = uctValue;
            }

        }
        if (selected == null) {
            node.setTerminalNode();
            return node;
        }

        return selected;


    }

    private void propagateResult(TreeNode node, Integer result) {
        while (node.getParent() != null) {
            node.addResult(result);
            node = node.getParent();
        }
    }

}
