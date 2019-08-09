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
    private double heursticWeighting;
    private double randomWeighting;

    public TreeNode(TreeNode parent, Board currentBoard, Counter.COLOUR colour, Counter.COLOUR rootColour, ImmutablePosition position, double heursticWeighting, double randomWeighting) {
        this.parent = parent;
        this.currentBoard = currentBoard;
        this.colour = colour;
        this.rootColour = rootColour;
        this.positionToCreateBoard = position;

        this.heursticWeighting = heursticWeighting;
        this.randomWeighting = randomWeighting;


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
            TreeNode childNode = new TreeNode(this, clone, newColour, this.rootColour, move, heursticWeighting, randomWeighting);
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
        return simulateGame(this.currentBoard.clone(), this.colour);
    }

    public Integer simulateGame(Board board, Counter.COLOUR colour) {

        Counter.COLOUR newColour;
        if (colour.equals(Counter.COLOUR.WHITE)) {
            newColour = Counter.COLOUR.BLACK;
        } else {
            newColour = Counter.COLOUR.WHITE;
        }

        ImmutableList<ImmutablePosition> validMoves = ImmutableList.copyOf(board.getValidMoves(colour));
        if (validMoves.size() == 0) {
            validMoves = ImmutableList.copyOf(board.getValidMoves(newColour));
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
        ImmutablePosition nextMove = pickNextMove(board.clone(), validMoves, colour);

        board.addCounter(counter, nextMove);

        return simulateGame(board.clone(), newColour);


    }

    private ImmutablePosition pickNextMove(Board board, ImmutableList<ImmutablePosition> validMoves, Counter.COLOUR colour) {
        Random random = new Random();

        int bestIndex = 0;
        double bestValue = 0;
        int i = 0;
        for (ImmutablePosition pos : validMoves) {
            Board newBoard = board.clone();
            Counter counter = new Counter(colour);
            newBoard.addCounter(counter, pos);
            //double heursticValue = newBoard.getBoardHeurstic(this.rootColour);
            double heursticValue = 10 * validMoves.size();
            double randomValue = random.nextDouble();

            double combinedValue = (heursticValue * heursticWeighting) + (randomValue * randomWeighting);

            if (combinedValue > bestValue) {
                bestValue = combinedValue;
                bestIndex = i;
            }
            i++;
        }
        return validMoves.get(random.nextInt(validMoves.size()));


    }

    public void addResult(Integer result) {
        numberOfSimulations++;
        if (result.equals(1)) {
            numberOfWins++;
        }


    }

    public Board getCurrentBoard() {
        return currentBoard;
    }

    public TreeNode findChildBoardMatch(Board board) {
        for (TreeNode child : this.getChildren()) {
            //todo override equals method in board
            if (child.getCurrentBoard().printBoard().equals(board.printBoard())) {
                System.out.println("equals");
                return child;
            }
        }
        //todo replace null
        return null;
    }
}
