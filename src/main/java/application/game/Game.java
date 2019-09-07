package application.game;

import application.players.ComputerPlayer;
import application.players.HumanPlayer;
import application.players.Player;
import application.utils.MessageProducer;
import com.google.common.base.Optional;
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
public
class Game {
    private final Player player1;
    private final Player player2;
    private final Gson gson;
    private final MessageProducer messageProducer;
    private final List<Board> previousBoards = new LinkedList<>();
    private Board board;
    private Player currentTurnsPlayer;


    @Autowired
    public Game(Board board, @Value("${player1.human}") Boolean humanPlayer1, @Value("${player2.human}") Boolean humanPlayer2, MessageProducer messageProducer, Gson gson,
                @Value("${computer1.moveFunction}") Integer computer1MoveFunction, @Value("${computer2.moveFunction}") Integer computer2MoveFunction, @Value("${mcts.waitTime}") Integer mctsWaitTime) {

        this.board = board;
        this.messageProducer = messageProducer;
        this.gson = gson;
        if (humanPlayer1) {
            player1 = new HumanPlayer(COLOUR.WHITE, messageProducer);
        } else {
            player1 = new ComputerPlayer(COLOUR.WHITE, messageProducer, computer1MoveFunction, mctsWaitTime);
        }
        if (humanPlayer2) {
            player2 = new HumanPlayer(COLOUR.BLACK, messageProducer);
        } else {

            player2 = new ComputerPlayer(COLOUR.BLACK, messageProducer, computer2MoveFunction, mctsWaitTime);
        }
        currentTurnsPlayer = player1;
    }

    public Optional<Player> play() {
        int numberOfConsecutivePasses = 0;
        while (Math.pow(board.getBoardSize(), 2) > board.getCountersPlayed() && numberOfConsecutivePasses < 2) {
            System.out.println(board);
            if (board.numberOfValidMoves(currentTurnsPlayer.getCounterColour()) > 0) {
                board = currentTurnsPlayer.playTurn(board);
                numberOfConsecutivePasses = 0;
            } else {
                System.out.println("No valid moves, turn passes");
                numberOfConsecutivePasses += 1;
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
    @SuppressWarnings("unused")
    public void player1Turn(String data) {
        try {

            Board newBoard;
            ByteArrayInputStream bis;
            ObjectInputStream ois;
            byte[] boardBytes = gson.fromJson(data, byte[].class);
            bis = new ByteArrayInputStream(boardBytes);
            ois = new ObjectInputStream(bis);
            newBoard = (Board) ois.readObject();

            board = newBoard;
            previousBoards.add(newBoard);
            if (previousBoards.size() > 3 && previousBoards.get(previousBoards.size() - 3).equals(newBoard)) {
                endGame(board);
            } else {
                currentTurnsPlayer = player1;
                System.out.println(board);

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

            board = newBoard;
            previousBoards.add(newBoard);
            if (previousBoards.size() > 3 && previousBoards.get(previousBoards.size() - 3).equals(newBoard)) {
                endGame(board);
            } else {
                currentTurnsPlayer = player2;
                System.out.println(board);

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

    private Optional<Player> endGame(Board board) {
        System.out.println("final board = " + board.printBoard());
        COLOUR winner = board.getWinner(true);
        if (winner == null) {
            System.out.println("Draw");
            Optional.absent();
        } else if (winner.equals(COLOUR.WHITE)) {
            System.out.println("White wins");
            if (player1.getCounterColour().equals(COLOUR.WHITE)) {
                return Optional.of(player1);
            } else {
                return Optional.of(player2);
            }
        } else if (winner.equals(COLOUR.BLACK)) {
            System.out.println("Black wins");
            if (player1.getCounterColour().equals(COLOUR.BLACK)) {
                return Optional.of(player1);
            } else {
                return Optional.of(player2);
            }
        }
        return Optional.absent();
    }


    public void reset() {
        board.reset();
        player1.reset();
        player2.reset();
    }
}
