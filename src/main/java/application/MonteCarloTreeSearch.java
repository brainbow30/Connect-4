package application;


import com.google.common.collect.ImmutableList;

import java.util.Random;


public class MonteCarloTreeSearch {

    private TreeNode root;
    private Integer waitTime;
    private static double epsilon = 1e-6;

    public MonteCarloTreeSearch(Board board, Counter.COLOUR colour, Integer waitTime) {
        this.root = new TreeNode(null, board, colour, colour, null);
        root.visited();

        this.waitTime = waitTime;


    }

    public ImmutablePosition run() {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + waitTime;

        while (System.currentTimeMillis() < endTime) {

            TreeNode selectedNode = selectNode(root);
            while (selectedNode.isVisited() && !selectedNode.isTerminalNode()) {
                    selectedNode = selectNode(selectedNode);

            }

            Integer result = selectedNode.simulateGame();
            propagateResult(selectedNode, result);
            selectedNode.visited();


        }
        for (TreeNode node : root.getChildren()) {
            System.out.println();
            System.out.println("wins = " + node.getNumberOfWins());
            System.out.println("played = " + node.getNumberOfSimulations());
        }

        TreeNode newNode = selectMove(root);
        System.out.println("selected");
        System.out.println("wins = " + newNode.getNumberOfWins());
        System.out.println("played = " + newNode.getNumberOfSimulations());

        return newNode.getPositionToCreateBoard();




    }

    private TreeNode selectNode(TreeNode node) {
//todo improve select function
        Random random = new Random();
        ImmutableList<TreeNode> children = node.getChildren();
        if (node.getChildren().size() == 0) {
            node.setTerminalNode();
            return node;
        }
        return children.get(random.nextInt(children.size()));

    }

    private TreeNode selectMove(TreeNode node) {
//todo improve select function
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
