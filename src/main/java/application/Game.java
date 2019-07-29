package application;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.List;

@Component
class Game {
    private Board board;
    private Player player1;
    private Player player2;
    private Player currentTurnsPlayer;
    private final Gson gson;
    private final MessageProducer messageProducer;
    private List<Board> previousBoards = new LinkedList<>();




    @Autowired
    public Game(Board board, @Value("${player1.human}") Boolean humanPlayer1, @Value("${player2.human}") Boolean humanPlayer2, MessageProducer messageProducer, Gson gson,
                @Value("${computer1.moveFunction}") Integer computer1MoveFunction, @Value("${computer2.moveFunction}") Integer computer2MoveFunction, @Value("${mcts.waitTime}") Integer mctsWaitTime) {

        this.board = board;
        this.messageProducer = messageProducer;
        this.gson = gson;
        if (humanPlayer1) {
            this.player1 = new HumanPlayer(Counter.COLOUR.WHITE, messageProducer);
        } else {
            this.player1 = new ComputerPlayer(Counter.COLOUR.WHITE, messageProducer, computer1MoveFunction, mctsWaitTime);
        }
        if (humanPlayer2) {
            this.player2 = new HumanPlayer(Counter.COLOUR.BLACK, messageProducer);
        } else {

            this.player2 = new ComputerPlayer(Counter.COLOUR.BLACK, messageProducer, computer2MoveFunction, mctsWaitTime);
        }
        currentTurnsPlayer = player1;
    }

    public Player play() {
        int numberOfConcecutivePasses = 0;
        while (Math.pow(board.getBoardSize(), 2) > board.getCountersPlayed() && numberOfConcecutivePasses < 2) {
            System.out.println("board = " + board.printBoard());
            if (board.numberOfValidMoves(currentTurnsPlayer.getCounterColour()) > 0) {
                board = currentTurnsPlayer.playTurn(board);
                numberOfConcecutivePasses = 0;
            } else {
                System.out.println("No valid moves, turn passes");
                numberOfConcecutivePasses += 1;


            }
            if (currentTurnsPlayer.equals(player1)) {
                currentTurnsPlayer = player2;
            } else {
                currentTurnsPlayer = player1;
            }

        }
        return endGame(board);


    }

    public void playKafka() {

        messageProducer.sendMessage1(0, java.time.LocalDateTime.now().toString(), board);

    }


    //@KafkaListener(topics = "${player1.topic}", groupId = "foo")
    public void player1Turn(String data) {
        try {
            //System.out.println("player1 received board");

            Board newBoard;
            ByteArrayInputStream bis;
            ObjectInputStream ois;
            byte[] boardBytes = gson.fromJson(data, byte[].class);
            bis = new ByteArrayInputStream(boardBytes);
            ois = new ObjectInputStream(bis);
            newBoard = (Board) ois.readObject();

            this.board = newBoard;
            previousBoards.add(newBoard);
            if (previousBoards.size() > 3 && previousBoards.get(previousBoards.size() - 3).equals(newBoard)) {
                endGame(board);
            } else {
                this.currentTurnsPlayer = player1;
                System.out.println("board = " + board.printBoard());

                if (Math.pow(board.getBoardSize(), 2) > board.getCountersPlayed()) {
                    if (board.numberOfValidMoves(currentTurnsPlayer.getCounterColour()) > 0) {
                        currentTurnsPlayer.playTurnKafka(board);
                    } else {
                        System.out.println("No valid moves, turn passes");
                        messageProducer.sendMessage2(0, java.time.LocalDateTime.now().toString(), board);
                    }

                } else {
                    endGame(board);
                }
            }


            //System.out.println("obj = " + obj.printBoard());
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }


    }

    //@KafkaListener(topics = "${player2.topic}", groupId = "bar")
    public void player2Turn(String data) {

        try {
            //System.out.println("player2 received board");

            Board newBoard;
            ByteArrayInputStream bis;
            ObjectInputStream ois;
            byte[] boardBytes = gson.fromJson(data, byte[].class);
            bis = new ByteArrayInputStream(boardBytes);
            ois = new ObjectInputStream(bis);
            newBoard = (Board) ois.readObject();

            this.board = newBoard;
            previousBoards.add(newBoard);
            if (previousBoards.size() > 3 && previousBoards.get(previousBoards.size() - 3).equals(newBoard)) {
                endGame(board);
            } else {
                this.currentTurnsPlayer = player2;
                System.out.println("board = " + board.printBoard());

                if (Math.pow(board.getBoardSize(), 2) > board.getCountersPlayed()) {
                    if (board.numberOfValidMoves(currentTurnsPlayer.getCounterColour()) > 0) {
                        currentTurnsPlayer.playTurnKafka(board);
                    } else {
                        System.out.println("No valid moves, turn passes");
                        messageProducer.sendMessage1(0, java.time.LocalDateTime.now().toString(), board);
                    }
                } else {
                    endGame(board);
                }
            }


        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }


    }

    private Player endGame(Board board) {
        System.out.println("final board = " + board.printBoard());
        Counter.COLOUR winner = board.getWinner(true);
        if (winner.equals(Counter.COLOUR.WHITE)) {
            System.out.println("White wins");
            if (player1.getCounterColour().equals(Counter.COLOUR.WHITE)) {
                return player1;
            } else {
                return player2;
            }
        } else if (winner.equals(Counter.COLOUR.BLACK)) {
            System.out.println("Black wins");
            if (player1.getCounterColour().equals(Counter.COLOUR.WHITE)) {
                return player1;
            } else {
                return player2;
            }
        } else {
            System.out.println("Draw");
            return null;
        }

    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void reset() {
        board.reset();
    }
}
