package application.players;

import application.ImmutablePosition;
import application.game.Board;
import application.game.COLOUR;
import application.game.Counter;
import application.mcts.GenerateTrainingData;
import application.mcts.MonteCarloTreeSearch;
import application.mcts.TreeNode;
import com.google.common.collect.ImmutableList;

import java.util.Random;


public class ComputerPlayer implements Player {

    private final COLOUR counterColour;
    private final Integer waitTime;
    private final String hostname;
    private TreeNode previousNode;

    private final Integer moveFunction;
    private final GenerateTrainingData generateTrainingData;
    private final Boolean writeTrainingData;


    public ComputerPlayer(COLOUR counterColour, Integer moveFunction, Integer waitTime, Integer boardSize, String hostname, Boolean writeTrainingData) {
        this.counterColour = counterColour;
        this.moveFunction = moveFunction;
        this.waitTime = waitTime;
        this.hostname = hostname;
        previousNode = null;
        generateTrainingData = new GenerateTrainingData("training" + boardSize + ".txt");
        this.writeTrainingData = writeTrainingData;

    }

    public Board playTurn(Board board) {
        System.out.println(counterColour + "'s Turn");
        ImmutablePosition position;
        if (moveFunction.equals(1)) {
            position = getNextPositionHeuristic(board);
        } else if (moveFunction.equals(2)) {
            position = getNextPositionMCTS(board, false);
        } else if (moveFunction.equals(3)) {
            position = getNextPositionMCTS(board, true);
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

    private ImmutablePosition getNextPositionMCTS(Board board, Boolean useNN) {
        MonteCarloTreeSearch monteCarloTreeSearch;
        TreeNode currentNode;
        //todo find replacement for previous node
        if (previousNode != null && !previousNode.isTerminalNode()) {

            currentNode = previousNode.findChildBoardMatch(board);
            monteCarloTreeSearch = new MonteCarloTreeSearch(currentNode.clone(), waitTime, useNN);

        } else {
            monteCarloTreeSearch = new MonteCarloTreeSearch(board, counterColour, waitTime, useNN, hostname);
        }
        currentNode = monteCarloTreeSearch.run();
        previousNode = currentNode;
        final TreeNode trainingNode = currentNode;
        if (currentNode.isTerminalNode() && writeTrainingData) {
            generateTrainingData.open();
            generateTrainingData.save(trainingNode);
            generateTrainingData.close();

        }
        return currentNode.getPositionToCreateBoard();

    }

    public void reset() {
        previousNode = null;
    }

}
