package application;

import com.google.common.collect.ImmutableList;

import java.io.*;
import java.util.Random;


public class TreeNode implements Serializable {
    private TreeNode parent;
    private Integer numberOfWins = 0;
    private Integer numberOfSimulations = 0;
    private Boolean visited = false;
    private final Board currentBoard;
    private final Counter.COLOUR colour;
    private final Counter.COLOUR rootColour;
    private ImmutableList<TreeNode> children;
    private Boolean terminalNode = false;
    private ImmutablePosition positionToCreateBoard;


    public TreeNode(TreeNode parent, Board currentBoard, Counter.COLOUR colour, Counter.COLOUR rootColour, ImmutablePosition position) {
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
        Counter.COLOUR newColour;
        if (counter.getColour().equals(Counter.COLOUR.WHITE)) {
            newColour = Counter.COLOUR.BLACK;
        } else {
            newColour = Counter.COLOUR.WHITE;
        }

        if (validMoves.size() == 0) {

            if (currentBoard.getValidMoves(newColour).size() == 0) {
                terminalNode = true;
            }
            if (newColour.equals(Counter.COLOUR.WHITE)) {
                newColour = Counter.COLOUR.BLACK;
            } else {
                newColour = Counter.COLOUR.WHITE;
            }
            validMoves = currentBoard.getValidMoves(newColour);
            counter.flip();

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

    public Integer simulateGame() {
        if (this.isTerminalNode()) {
            Counter.COLOUR winner = this.getCurrentBoard().getWinner(false);

            if (winner == null) {
                return 0;
            } else if (winner.equals(rootColour)) {

                return 1;

            } else {
                return 0;
            }
        } else {
            Integer result = this.selectMove().simulateGame();
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

    public void addResult(Integer result) {
        this.numberOfSimulations++;
        numberOfWins += result;
    }

    private Board getCurrentBoard() {
        return currentBoard;
    }

    public TreeNode findChildBoardMatch(Board board) {
        if (this.getCurrentBoard().getCountersPlayed().equals(board.getCountersPlayed() - 1)) {
            for (TreeNode child : this.getChildren()) {

                //todo override equals method in board
                if (child.getCurrentBoard().printBoard().equals(board.printBoard())) {
                    return child.clone();
                }
            }
        } else if (this.getCurrentBoard().getCountersPlayed() < board.getCountersPlayed()) {
            for (TreeNode child : this.getChildren()) {
                TreeNode childBoardMatch = child.clone().findChildBoardMatch(board);
                if (childBoardMatch != null) {
                    return childBoardMatch;
                }
            }
        }
        //todo issue with generate children such that children arent correct and it cant find board after invalid move
        System.out.println("created new root node");
        return new TreeNode(null, board, rootColour, rootColour, null);
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
}
