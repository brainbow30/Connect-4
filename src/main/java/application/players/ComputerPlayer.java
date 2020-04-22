package application.players;

import application.ImmutablePosition;
import application.game.Board;
import application.game.COLOUR;
import application.game.Counter;
import application.mcts.MonteCarloTreeSearch;
import application.mcts.TreeNode;
import com.google.common.collect.ImmutableList;

import java.util.Random;


public class ComputerPlayer implements Player {

    private final COLOUR counterColour;
    private final Integer waitTime;
    private Double cpuct;
    private final String hostname;
    private TreeNode previousNode;

    private final Integer moveFunction;
    private int tempThreshold;
    private int turns;


    public ComputerPlayer(COLOUR counterColour, Integer moveFunction, Integer waitTime,
                          String hostname, Double cpuct, Integer tempThreshold) {
        this.counterColour = counterColour;
        this.moveFunction = moveFunction;
        this.waitTime = waitTime;
        this.cpuct = cpuct;
        this.tempThreshold = tempThreshold;
        this.hostname = hostname;
        previousNode = null;

    }

    public Board playTurn(Board board) {
        System.out.println(counterColour + "'s Turn");
        ImmutablePosition position;
        if (moveFunction.equals(2)) {
            position = getNextPositionMCTS(board, 0);
        } else if (moveFunction.equals(3)) {
            position = getNextPositionMCTS(board, 1);
        } else if (moveFunction.equals(4)) {
            position = getNextPositionMCTS(board, 2);
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


    private ImmutablePosition getNextPositionMCTS(Board board, Integer nnFunction) {
        turns += 1;
        double temp = 1.0;
        if (turns >= tempThreshold) {
            temp = 0.01;
        }
        MonteCarloTreeSearch monteCarloTreeSearch;
        TreeNode currentNode;
        //todo find replacement for previous node
        if (previousNode != null && !previousNode.isTerminalNode()) {

            currentNode = previousNode.findChildBoardMatch(board);
            monteCarloTreeSearch = new MonteCarloTreeSearch(currentNode, waitTime, nnFunction, cpuct);

        } else {
            monteCarloTreeSearch = new MonteCarloTreeSearch(board, counterColour, waitTime, nnFunction, hostname, cpuct);
        }
        currentNode = monteCarloTreeSearch.run(temp);
        previousNode = currentNode;
        return currentNode.getPositionToCreateBoard();

    }

    public void reset() {
        previousNode = null;
        turns = 0;
    }

    public TreeNode getPreviousNode() {
        return previousNode;
    }

    public Double getCpuct() {
        return cpuct;
    }

    public void setCpuct(Double cpuct) {
        this.cpuct = cpuct;
    }

    public int getTempThreshold() {
        return tempThreshold;
    }

    public void setTempThreshold(int tempThreshold) {
        this.tempThreshold = tempThreshold;
    }
}
