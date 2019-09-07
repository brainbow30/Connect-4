package application.players;

import application.ImmutablePosition;
import application.game.Board;
import application.game.COLOUR;
import application.game.Counter;
import application.mcts.GenerateTrainingData;
import application.mcts.MonteCarloTreeSearch;
import application.mcts.TreeNode;
import application.utils.MessageProducer;
import com.google.common.collect.ImmutableList;

import java.util.Random;


public class ComputerPlayer implements Player {

    private final COLOUR counterColour;
    private final MessageProducer producer;
    private final Integer waitTime;
    private TreeNode previousNode;

    private final Integer moveFunction;
    private final GenerateTrainingData generateTrainingData;


    public ComputerPlayer(COLOUR counterColour, MessageProducer producer, Integer moveFunction, Integer waitTime) {
        this.counterColour = counterColour;
        this.producer = producer;
        this.moveFunction = moveFunction;
        this.waitTime = waitTime;
        previousNode = null;
        generateTrainingData = new GenerateTrainingData("training.txt");
    }

    public Board playTurn(Board board) {
        System.out.println(counterColour + "'s Turn");
        ImmutablePosition position;
        if (moveFunction.equals(1)) {
            position = getNextPositionHeuristic(board);
        } else if (moveFunction.equals(2)) {
            position = getNextPositionMCTS(board);
        } else {
            System.out.println("random move");
            Random random = new Random();
            ImmutableList<ImmutablePosition> validMoves = board.getValidMoves(counterColour);
            position = validMoves.get(random.nextInt(validMoves.size()));
        }
        Counter counter = new Counter(counterColour);
        board.addCounter(counter, position);


        return board;

    }

    public void playTurnKafka(Board board) {
        boolean invalidMove = true;
        while (invalidMove) {
            ImmutablePosition position = getNextPositionHeuristic(board);
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

    private ImmutablePosition getNextPositionHeuristic(Board board) {
        ImmutableList<ImmutablePosition> validMoves = board.getValidMoves(counterColour);
        Counter counter = new Counter(counterColour);
        Double bestBoardHeuristic = Double.MIN_VALUE;
        ImmutablePosition bestMove = null;
        for (ImmutablePosition position : validMoves) {
            Board futureBoard = board.clone();
            futureBoard.addCounter(counter, position);
            Double boardHeuristic = futureBoard.getBoardHeuristic(counterColour, 1);
            if (boardHeuristic > bestBoardHeuristic) {
                bestBoardHeuristic = boardHeuristic;
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
            monteCarloTreeSearch = new MonteCarloTreeSearch(board, counterColour, waitTime);
        }
        currentNode = monteCarloTreeSearch.run();
        previousNode = currentNode;
        final TreeNode trainingNode = currentNode;
        if (currentNode.isTerminalNode()) {
            Thread t1 = new Thread(new Runnable() {
                public void run() {
                    generateTrainingData.save(trainingNode);
                }
            });
            t1.start();

        }
        return currentNode.getPositionToCreateBoard();

    }

    public void reset() {
        previousNode = null;
    }

}
