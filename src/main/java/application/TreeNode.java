package application;

import com.google.common.collect.ImmutableList;

import java.io.*;
import java.util.Random;


public class TreeNode implements Serializable {
    private final Board currentBoard;
    private final COLOUR colour;
    private final COLOUR rootColour;
    private TreeNode parent;
    private Integer numberOfWins = 0;
    private Integer numberOfSimulations = 0;
    private Boolean visited = false;
    private ImmutableList<TreeNode> children;
    private Boolean terminalNode = false;
    private ImmutablePosition positionToCreateBoard;


    public TreeNode(TreeNode parent, Board currentBoard, COLOUR colour, COLOUR rootColour, ImmutablePosition position) {
        this.parent = parent;
        this.currentBoard = currentBoard;
        this.colour = colour;
        this.rootColour = rootColour;
        this.positionToCreateBoard = position;
        this.children = ImmutableList.of();


    }

    private ImmutableList<TreeNode> generateChildren() {

        Counter counter = new Counter(colour);
        ImmutableList.Builder<TreeNode> builder = ImmutableList.builder();
        ImmutableList<ImmutablePosition> validMoves = currentBoard.getValidMoves(colour);
        COLOUR newColour;
        if (counter.getColour().equals(COLOUR.WHITE)) {
            newColour = COLOUR.BLACK;
        } else {
            newColour = COLOUR.WHITE;
        }

        if (validMoves.size() == 0) {


            validMoves = currentBoard.getValidMoves(newColour);
            if (validMoves.size() == 0) {
                terminalNode = true;
            }
            counter.flip();
            if (newColour.equals(COLOUR.WHITE)) {
                newColour = COLOUR.BLACK;
            } else {
                newColour = COLOUR.WHITE;
            }


        }
        for (ImmutablePosition move : validMoves) {
            Board clone = currentBoard.clone();
            clone.addCounter(counter, move);
            TreeNode childNode = new TreeNode(this, clone, newColour, this.rootColour, move);
            builder.add(childNode);
        }
        return builder.build();
    }

    public ImmutablePosition getPositionToCreateBoard() {
        return positionToCreateBoard;
    }

    public void visited() {
        visited = true;
    }

    public TreeNode getParent() {
        return parent;
    }

    private Integer getNumberOfWins() {
        return numberOfWins;
    }

    private Integer getNumberOfSimulations() {
        return numberOfSimulations;
    }

    public Boolean isVisited() {
        return visited;
    }

    private ImmutableList<TreeNode> getChildren() {
        if (children.isEmpty()) {
            children = ImmutableList.copyOf(generateChildren());
        }
        return children;

    }

    private void setTerminalNode() {
        terminalNode = true;
    }

    public Boolean isTerminalNode() {
        return terminalNode;
    }

    public COLOUR simulateGame() {
        if (this.isTerminalNode()) {
            return this.getCurrentBoard().getWinner(false);
        } else {
            COLOUR result = this.selectMove().simulateGame();
            addResult(result);
            return result;
        }


    }

    public TreeNode selectMove() {
        Random random = new Random();
        ImmutableList<TreeNode> children = this.getChildren();
        double bestValue = Double.MIN_VALUE;
        TreeNode selected = null;
        for (TreeNode child : children) {
            double epsilon = 1e-6;
            double uctValue = child.getNumberOfWins() / (child.getNumberOfSimulations() + epsilon) +
                    Math.sqrt(Math.log(this.getNumberOfSimulations() + 1) / (child.getNumberOfSimulations() + epsilon)) +
                    random.nextDouble() * epsilon;
            //System.out.println("uctValue = " + uctValue);
            if (uctValue > bestValue) {
                selected = child;
                bestValue = uctValue;
            }

        }
        if (selected == null) {
            this.setTerminalNode();
            return this;
        }

        return selected;


    }

    public void addResult(COLOUR result) {
        this.numberOfSimulations++;
        if (result != null && result.equals(rootColour)) {
            numberOfWins++;
        }
    }

    private Board getCurrentBoard() {
        return currentBoard;
    }

    public TreeNode findChildBoardMatch(Board board) {
        if (this.getCurrentBoard().equals(board)) {
            return this;
        } else if (this.getCurrentBoard().getCountersPlayed().equals(board.getCountersPlayed() - 1)) {
            for (TreeNode child : this.getChildren()) {
                if (child.getCurrentBoard().equals(board)) {
                    return child.clone();
                }
            }
        } else if (this.getCurrentBoard().getCountersPlayed() < board.getCountersPlayed()) {
            System.out.println("recursive board search");
            for (TreeNode child : this.getChildren()) {
                TreeNode childBoardMatch = child.clone().findChildBoardMatch(board);
                if (childBoardMatch != null) {
                    return childBoardMatch;
                }
            }
        }
        return null;
    }


    public void setRoot() {
        this.parent = null;
        this.positionToCreateBoard = null;
    }

    @Override
    public TreeNode clone() {
        //noinspection DuplicatedCode
        try {
            TreeNode newTreeNode;
            ByteArrayInputStream bis;
            ObjectInputStream ois;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.flush();
            byte[] data = bos.toByteArray();
            bis = new ByteArrayInputStream(data);
            ois = new ObjectInputStream(bis);
            newTreeNode = (TreeNode) ois.readObject();
            return newTreeNode;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;

        }
    }

    @Override
    public boolean equals(Object object) {
        try {
            TreeNode node = (TreeNode) object;
            return node.getCurrentBoard().equals(this.getCurrentBoard())
                    && this.positionToCreateBoard.equals(node.positionToCreateBoard)
                    && this.colour.equals(node.colour)
                    && this.rootColour.equals(node.rootColour);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
