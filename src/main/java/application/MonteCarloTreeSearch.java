package application;


import com.google.common.collect.ImmutableList;

import java.util.Random;


public class MonteCarloTreeSearch {

    private TreeNode root;
    private Integer waitTime;

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
                try {
                    selectedNode = selectNode(selectedNode);
                } catch (IllegalArgumentException e) {
                    //todo fix, should already exit loop on terminal
                    selectedNode = selectedNode.getParent();
                    break;
//                    System.out.println("selectedNodeTerminal = " + selectedNode.isTerminalNode());
//                    System.out.println("parent = " + selectedNode.getParent().getCurrentBoard().printBoard());
//                    System.out.println("selectedNode = " + selectedNode.getCurrentBoard().printBoard());
                }
            }

            Integer result = selectedNode.simulateGame();
            propagateResult(selectedNode, result);
            selectedNode.visited();


        }
//        for (TreeNode node : root.getChildren()) {
//            System.out.println();
//            System.out.println("wins = " + node.getNumberOfWins());
//            System.out.println("runs = " + node.getNumberOfSimulations());
//        }
        TreeNode newNode = selectNode(root);
        return newNode.getPositionToCreateBoard();




    }

    private TreeNode selectNode(TreeNode node) {

        Random random = new Random();
        ImmutableList<TreeNode> children = node.getChildren();
        return children.get(random.nextInt(children.size()));

    }

    private void propagateResult(TreeNode node, Integer result) {
        while (node.getParent() != null) {
            node.addResult(result);
            node = node.getParent();
        }
    }

}
