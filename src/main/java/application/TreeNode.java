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

    public TreeNode(TreeNode parent, Board currentBoard, Counter.COLOUR colour, Counter.COLOUR rootColour) {
        this.parent = parent;
        this.currentBoard = currentBoard;
        this.colour = colour;
        this.rootColour = colour;


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
            TreeNode childNode = new TreeNode(this, clone, newColour, this.rootColour);
            builder.add(childNode);
        }
        return builder.build();
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

    public Boolean isTerminalNode() {
        return terminalNode;
    }

    public Integer simulateGame() {
        return simulateGame(this.currentBoard, this.colour);
    }

    public Integer simulateGame(Board board, Counter.COLOUR colour) {
        Counter counter = new Counter(colour);

        ImmutableList<ImmutablePosition> validMoves = board.getValidMoves(colour);
        if (validMoves.size() == 0) {
            counter.flip();
            validMoves = board.getValidMoves(counter.getColour());
            if (validMoves.size() == 0) {

                Counter.COLOUR winner = board.getWinner(false);

                if (winner == null) {
                    return 0;
                } else if (winner.equals(rootColour)) {

                    return 0;

                } else {
                    return 1;
                }

            }
        }
        ImmutablePosition nextMove = pickNextMove(board, validMoves);

        board.addCounter(counter, nextMove);

        Counter.COLOUR newColour;
        if (counter.getColour().equals(Counter.COLOUR.WHITE)) {
            newColour = Counter.COLOUR.BLACK;
        } else {
            newColour = Counter.COLOUR.WHITE;
        }

        return simulateGame(board.clone(), newColour);


    }

    private ImmutablePosition pickNextMove(Board board, ImmutableList<ImmutablePosition> validMoves) {
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
