package application;

import com.google.common.collect.ImmutableList;

import java.util.Random;


public class TreeNode {
    private TreeNode parent;
    private Integer numberOfWins = 0;
    private Integer numberOfSimulations = 0;
    private Boolean visited = false;
    private Board currentBoard;
    private Counter.COLOUR colour;
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

    public Integer getNumberOfWins() {
        return numberOfWins;
    }

    public Integer getNumberOfSimulations() {
        return numberOfSimulations;
    }

    public Boolean isVisited() {
        return visited;
    }

    public ImmutableList<TreeNode> getChildren() {
        if (children == null) {
            children = generateChildren();
        }
        return children;

    }

    public void setTerminalNode() {
        terminalNode = true;
    }

    public Boolean isTerminalNode() {
        return terminalNode;
    }

    public Integer simulateGame() {
        return simulateGame(this.currentBoard, this.colour);
    }

    public Integer simulateGame(Board board, Counter.COLOUR colour) {

        Counter.COLOUR newColour;
        if (colour.equals(Counter.COLOUR.WHITE)) {
            newColour = Counter.COLOUR.BLACK;
        } else {
            newColour = Counter.COLOUR.WHITE;
        }

        ImmutableList<ImmutablePosition> validMoves = board.getValidMoves(colour);
        if (validMoves.size() == 0) {
            validMoves = board.getValidMoves(newColour);
            if (validMoves.size() == 0) {

                Counter.COLOUR winner = board.getWinner(false);

                if (winner == null) {
                    return 0;
                } else if (winner.equals(rootColour)) {

                    return 1;

                } else {
                    return 0;
                }

            } else {
                Counter.COLOUR temp = colour;
                //this.colour=newColour;
                colour = newColour;
                newColour = temp;
            }

        }
        Counter counter = new Counter(colour);
        ImmutablePosition nextMove = pickNextMove(board, validMoves);

        board.addCounter(counter, nextMove);

        return simulateGame(board.clone(), newColour);


    }

    private ImmutablePosition pickNextMove(Board board, ImmutableList<ImmutablePosition> validMoves) {
        //todo change to uct
        //todo change so that children nodes have simulation number and wins and are not recalculated every turn
        Random random = new Random();
        return validMoves.get(random.nextInt(validMoves.size()));

    }

    public void addResult(Integer result) {
        numberOfSimulations++;
        if (result.equals(1)) {
            numberOfWins++;
        }


    }

}
