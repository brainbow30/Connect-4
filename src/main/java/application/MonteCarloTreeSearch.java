package application;


import java.util.Random;


public class MonteCarloTreeSearch {

    private TreeNode root;

    public MonteCarloTreeSearch(Board board, Counter.COLOUR colour, Double waitTime) {
        this.root = new TreeNode(null, board, colour, colour);
        root.visited();

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
        for (TreeNode node : root.getChildren()) {
            System.out.println();
            System.out.println("wins = " + node.getNumberOfWins());
            System.out.println("runs = " + node.getNumberOfSimulations());
        }
    }

    private TreeNode selectNode(TreeNode node) {
        Random random = new Random();
        return node.getChildren().get(random.nextInt(node.getChildren().size()));
    }

    private void propagateResult(TreeNode node, Integer result) {
        while (node.getParent() != null) {
            node.addResult(result);
            node = node.getParent();
        }
    }

}
