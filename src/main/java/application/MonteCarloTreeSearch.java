package application;


import java.util.Random;


public class MonteCarloTreeSearch {

    private TreeNode root;
    private static double epsilon = 1e-6;

    public MonteCarloTreeSearch(Board board, Counter.COLOUR colour, Double waitTime) {
        this.root = new TreeNode(null, board, colour, colour, null);
        root.visited();
    }

    public ImmutablePosition run() {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + 5000;

        while (System.currentTimeMillis() < endTime) {

            TreeNode selectedNode = selectNode(root);
            while (selectedNode.isVisited() && !selectedNode.isTerminalNode()) {

                selectedNode = selectNode(selectedNode);
            }

            Integer result = selectedNode.simulateGame();
            propagateResult(selectedNode, result);
            selectedNode.visited();


        }
        return selectNode(root).getPositionToCreate();
    }

    private TreeNode selectNode(TreeNode node) {
        Random random = new Random();
        TreeNode selected = null;
        double bestValue = Double.MIN_VALUE;
        for (TreeNode child : node.getChildren()) {
            double uctValue = child.getNumberOfWins() / (child.getNumberOfSimulations() + epsilon) +
                    Math.sqrt(Math.log(node.getNumberOfSimulations() + 1) / (child.getNumberOfSimulations() + epsilon)) +
                    random.nextDouble() * epsilon;
            // small random number to break ties randomly in unexpanded nodes
            if (uctValue > bestValue) {
                selected = child;
                bestValue = uctValue;
            }
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
