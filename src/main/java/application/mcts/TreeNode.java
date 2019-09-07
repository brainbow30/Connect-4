package application.mcts;

import application.ImmutablePosition;
import application.game.Board;
import application.game.COLOUR;
import application.game.Counter;
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


    private TreeNode(Builder builder) {
        parent = builder.parent;
        currentBoard = builder.currentBoard;
        colour = builder.colour;
        rootColour = builder.rootColour;
        positionToCreateBoard = builder.positionToCreateBoard;
        children = ImmutableList.of();
    }

    public static Builder builder() {
        return new Builder();
    }

    private ImmutableList<TreeNode> generateChildren() {
        Counter counter = new Counter(colour);
        ImmutableList.Builder<TreeNode> builder = ImmutableList.builder();
        ImmutableList<ImmutablePosition> validMoves = currentBoard.getValidMoves(colour);
        COLOUR newColour = COLOUR.opposite(colour);

        if (validMoves.size() == 0) {
            validMoves = currentBoard.getValidMoves(newColour);
            if (validMoves.size() == 0) {
                setTerminalNode();
            }
            counter.flip();
            newColour = COLOUR.opposite(newColour);
        }

        for (ImmutablePosition move : validMoves) {
            Board clone = currentBoard.clone();
            clone.addCounter(counter, move);
            TreeNode childNode = TreeNode.builder()
                    .parent(this)
                    .currentBoard(clone)
                    .colour(newColour)
                    .rootColour(rootColour)
                    .positionToCreateBoard(move)
                    .build();
            builder.add(childNode);
        }
        return builder.build();
    }

    public static class Builder {
        private Board currentBoard;
        private COLOUR colour;
        private COLOUR rootColour;
        private TreeNode parent;
        private ImmutablePosition positionToCreateBoard;


        public Builder currentBoard(Board currentBoard) {
            this.currentBoard = currentBoard;
            return this;
        }

        public Builder colour(COLOUR colour) {
            this.colour = colour;
            return this;
        }

        public Builder rootColour(COLOUR rootColour) {
            this.rootColour = rootColour;
            return this;
        }

        public Builder parent(TreeNode parent) {
            this.parent = parent;
            return this;
        }

        public Builder positionToCreateBoard(ImmutablePosition positionToCreateBoard) {
            this.positionToCreateBoard = positionToCreateBoard;
            return this;
        }

        public TreeNode build() {
            return new TreeNode(this);
        }
    }

    public COLOUR getColour() {
        return colour;
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

    Integer getNumberOfWins() {
        return numberOfWins;
    }

    Integer getNumberOfSimulations() {
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
        COLOUR result;
        if (isTerminalNode()) {
            result = getCurrentBoard().getWinner(false);
        } else {
            result = selectRandomMove().simulateGame();
        }
        addResult(result);
        return result;
    }

    public TreeNode selectUCTMove() {
        Random random = new Random();
        ImmutableList<TreeNode> children = getChildren();
        double bestValue = Double.MIN_VALUE;
        TreeNode selected = null;
        for (TreeNode child : children) {
            double epsilon = 1e-6;
            double uctValue = child.getNumberOfWins() / (child.getNumberOfSimulations() + epsilon) +
                    Math.sqrt(Math.log(getNumberOfSimulations() + 1) / (child.getNumberOfSimulations() + epsilon)) +
                    random.nextDouble() * epsilon;
            //System.out.println("uctValue = " + uctValue);
            if (uctValue > bestValue) {
                selected = child;
                bestValue = uctValue;
            }
        }
        if (selected == null) {
            return this;
        }
        return selected;
    }

    public TreeNode selectRandomMove() {
        ImmutableList<TreeNode> children = getChildren();
        if (children.size() > 0) {
            Random random = new Random();
            return this.children.get(random.nextInt(children.size()));
        } else {
            return this;
        }
    }

    public void addResult(COLOUR result) {
        numberOfSimulations++;
        if (result != null && result.equals(rootColour)) {
            numberOfWins++;
        }
    }

    Board getCurrentBoard() {
        return currentBoard;
    }

    public TreeNode findChildBoardMatch(Board board) {
        if (getCurrentBoard().equals(board)) {
            return this;
        } else if (getCurrentBoard().getCountersPlayed().equals(board.getCountersPlayed() - 1)) {
            for (TreeNode child : getChildren()) {
                if (child.getCurrentBoard().equals(board)) {
                    return child.clone();
                }
            }
        } else if (getCurrentBoard().getCountersPlayed() < board.getCountersPlayed()) {
            System.out.println("recursive board search");
            for (TreeNode child : getChildren()) {
                TreeNode childBoardMatch = child.clone().findChildBoardMatch(board);
                if (childBoardMatch != null) {
                    return childBoardMatch;
                }
            }
        }
        return null;
    }

    public void setRoot() {
        positionToCreateBoard = null;
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
            return node.getCurrentBoard().equals(getCurrentBoard())
                    && positionToCreateBoard.equals(node.positionToCreateBoard)
                    && colour.equals(node.colour)
                    && rootColour.equals(node.rootColour);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
