package application;

import com.google.common.collect.ImmutableList;

import java.util.Random;


public class ComputerPlayer implements Player {

    private final COLOUR counterColour;
    private final MessageProducer producer;
    private final Integer waitTime;
    private TreeNode previousNode;

    private final Integer moveFunction;


    public ComputerPlayer(COLOUR counterColour, MessageProducer producer, Integer moveFunction, Integer waitTime) {
        this.counterColour = counterColour;
        this.producer = producer;
        this.moveFunction = moveFunction;
        this.waitTime = waitTime;
        this.previousNode = null;
    }

    public Board playTurn(Board board) {
        System.out.println(counterColour + "'s Turn");
        ImmutablePosition position;
        if (this.moveFunction.equals(1)) {
            position = getNextPositionHuerstic(board);
        } else if (this.moveFunction.equals(2)) {
            position = getNextPositionMCTS(board);
        } else {
            System.out.println("random move");
            Random random = new Random();
            ImmutableList<ImmutablePosition> validMoves = board.getValidMoves(this.counterColour);
            position = validMoves.get(random.nextInt(validMoves.size()));
        }
        Counter counter = new Counter(counterColour);
        board.addCounter(counter, position);


        return board;

    }

    public void playTurnKafka(Board board) {
        boolean invalidMove = true;
        while (invalidMove) {
            ImmutablePosition position = getNextPositionHuerstic(board);
            Counter counter = new Counter(counterColour);
            if (board.addCounter(counter, position)) {
                invalidMove = false;
            }

        }
        if (counterColour.equals(COLOUR.WHITE)) {
            producer.sendMessage2(0, java.time.LocalDateTime.now().toString(), board);
        } else {
            producer.sendMessage1(0, java.time.LocalDateTime.now().toString(), board);
        }

    }

    @Override
    public COLOUR getCounterColour() {
        return counterColour;
    }

    private ImmutablePosition getNextPositionHuerstic(Board board) {
        ImmutableList<ImmutablePosition> validMoves = board.getValidMoves(this.counterColour);
        Counter counter = new Counter(this.counterColour);
        Double bestBoardHeurstic = Double.MIN_VALUE;
        ImmutablePosition bestMove = null;
        for (ImmutablePosition position : validMoves) {
            Board futureBoard = board.clone();
            futureBoard.addCounter(counter, position);
            Double boardHeurstic = futureBoard.getBoardHeurstic(this.counterColour, 1);
            if (boardHeurstic > bestBoardHeurstic) {
                bestBoardHeurstic = boardHeurstic;
                bestMove = position;
            }
        }
        return bestMove;


    }

    private ImmutablePosition getNextPositionMCTS(Board board) {
        MonteCarloTreeSearch monteCarloTreeSearch;
        TreeNode currentNode;
        //todo find replacement for previous node
        if (previousNode != null && !previousNode.isTerminalNode()) {

            currentNode = previousNode.findChildBoardMatch(board);
            monteCarloTreeSearch = new MonteCarloTreeSearch(currentNode.clone(), waitTime);

        } else {
            monteCarloTreeSearch = new MonteCarloTreeSearch(board, this.counterColour, waitTime);
        }
        currentNode = monteCarloTreeSearch.run();
        this.previousNode = currentNode;
        return currentNode.getPositionToCreateBoard();

    }

    public void reset() {
        previousNode = null;
    }

}
