package application;

import com.google.common.collect.ImmutableList;

import java.util.Random;


public class ComputerPlayer implements Player {

    private final Counter.COLOUR counterColour;
    private final MessageProducer producer;


    private Integer moveFunction;


    public ComputerPlayer(Counter.COLOUR counterColour, MessageProducer producer, Integer moveFunction) {
        this.counterColour = counterColour;
        this.producer = producer;
        this.moveFunction = moveFunction;

    }

    public Board playTurn(Board board) {
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
        if (counterColour.equals(Counter.COLOUR.WHITE)) {
            producer.sendMessage2(0, java.time.LocalDateTime.now().toString(), board);
        } else {
            producer.sendMessage1(0, java.time.LocalDateTime.now().toString(), board);
        }

    }

    @Override
    public Counter.COLOUR getCounterColour() {
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
            Double boardHeurstic = futureBoard.getBoardHeurstic(this.counterColour);
            if (boardHeurstic > bestBoardHeurstic) {
                bestBoardHeurstic = boardHeurstic;
                bestMove = position;
            }
        }
        return bestMove;


    }

    private ImmutablePosition getNextPositionMCTS(Board board) {
        MonteCarloTreeSearch monteCarloTreeSearch = new MonteCarloTreeSearch(board, this.counterColour, 5.0);
        return monteCarloTreeSearch.run();

    }

//    private Integer simulateGame(Board board, ImmutablePosition position, Counter counter, Integer countersPlayed) {
//        board.addCounter(counter, position);
//        countersPlayed++;
//        //System.out.println("board = " + board.printBoard());
//        if (countersPlayed < Math.pow(board.getBoardSize(), 2)) {
//            Counter.COLOUR newColour;
//            if (counter.getColour().equals(Counter.COLOUR.WHITE)) {
//                newColour = Counter.COLOUR.BLACK;
//            } else {
//                newColour = Counter.COLOUR.WHITE;
//            }
//            Counter newCounter = new Counter(newColour);
//
//            ImmutableList<ImmutablePosition> validMoves = board.getValidMoves(newCounter.getColour());
//
//            if (validMoves.size() == 0) {
//                newCounter.flip();
//                validMoves = board.getValidMoves(newCounter.getColour());
//            }
//            Random random = new Random();
//            if (validMoves.size() == 0) {
//                if (board.getWinner(false) == null) {
//                    return 0;
//                } else if (board.getWinner(false).equals(this.counterColour)) {
//                    return 1;
//                } else {
//                    return 0;
//                }
//            }
//            return simulateGame(board.clone(), validMoves.get(random.nextInt(validMoves.size())), newCounter, countersPlayed);
//
//
//        } else {
//
//            if (board.getWinner(false) == null) {
//                return 0;
//            } else if (board.getWinner(false).equals(this.counterColour)) {
//                return 1;
//            } else {
//                return 0;
//            }
//        }
//
//    }

}
