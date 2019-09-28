package application.game;

import application.players.ComputerPlayer;
import application.players.HumanPlayer;
import application.players.Player;
import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public
class Game {
    private final Player player1;
    private final Player player2;
    private Board board;
    private Player currentTurnsPlayer;


    @Autowired
    public Game(Board board, @Value("${player1.human}") Boolean humanPlayer1, @Value("${player2.human}") Boolean humanPlayer2,
                @Value("${computer1.moveFunction}") Integer computer1MoveFunction, @Value("${computer2.moveFunction}") Integer computer2MoveFunction,
                @Value("${mcts.waitTime}") Integer mctsWaitTime, @Value("${hostname}") String hostname) {
        this.board = board;
        if (humanPlayer1) {
            player1 = new HumanPlayer(COLOUR.WHITE);
        } else {
            player1 = new ComputerPlayer(COLOUR.WHITE, computer1MoveFunction, mctsWaitTime, board.getBoardSize(), hostname);
        }
        if (humanPlayer2) {
            player2 = new HumanPlayer(COLOUR.BLACK);
        } else {

            player2 = new ComputerPlayer(COLOUR.BLACK, computer2MoveFunction, mctsWaitTime, board.getBoardSize(), hostname);
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


    private Optional<Player> endGame(Board board) {
        System.out.println("final board = " + board.printBoard());
        COLOUR winner = board.getWinner(true);
        if (winner == null) {
            System.out.println("Draw");
            return Optional.absent();
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
