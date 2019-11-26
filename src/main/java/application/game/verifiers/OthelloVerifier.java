package application.game.verifiers;

import application.ImmutablePosition;
import application.Position;
import application.game.Board;
import application.game.COLOUR;
import application.game.Counter;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Qualifier("othello")
@Primary
public class OthelloVerifier implements Serializable, Verifier {
    @Autowired

    public OthelloVerifier() {

    }

    public ImmutableList<ImmutableList<Optional<Counter>>> setupBoard(int boardSize) {
        ImmutableList.Builder<ImmutableList<Optional<Counter>>> boardBuilder = ImmutableList.builder();

        //create initial counter set up in centre of board
        for (int y = 0; y < boardSize; y++) {
            ImmutableList.Builder<Optional<Counter>> rowBuilder = ImmutableList.builder();
            for (int x = 0; x < boardSize; x++) {

                if (y == (boardSize / 2) - 1) {
                    if (x == (boardSize / 2) - 1) {
                        rowBuilder.add(Optional.of(new Counter(COLOUR.WHITE)));
                    } else if (x == (boardSize / 2)) {
                        rowBuilder.add(Optional.of(new Counter(COLOUR.BLACK)));
                    } else {
                        rowBuilder.add(Optional.absent());
                    }
                } else if (y == (boardSize / 2)) {
                    if (x == (boardSize / 2) - 1) {
                        rowBuilder.add(Optional.of(new Counter(COLOUR.BLACK)));
                    } else if (x == (boardSize / 2)) {
                        rowBuilder.add(Optional.of(new Counter(COLOUR.WHITE)));
                    } else {
                        rowBuilder.add(Optional.absent());
                    }
                } else {
                    rowBuilder.add(Optional.absent());
                }
            }
            boardBuilder.add(rowBuilder.build());
        }
        return boardBuilder.build();
    }

    public int[] setupStats(int boardSize) {
        int countersPlayed = 4;
        int numberOfWhiteCounters = 2;
        int[] stats = {countersPlayed, numberOfWhiteCounters};
        return stats;

    }


    public Boolean validMove(Board board, COLOUR colour, Position position) {
        if (position.x() >= board.getBoardSize() || position.x() < 0) {
            throw new IndexOutOfBoundsException();
        } else if (position.y() >= board.getBoardSize() || position.y() < 0) {
            throw new IndexOutOfBoundsException();
        }

        if (board.getCounter(position).isPresent()) {
            return false;
        }
        if (validMoveRowChecker(board, colour, position)) {
            return true;
        } else if (validMoveColumnChecker(board, colour, position)) {
            return true;
        } else return validMoveDiagonalChecker(board, colour, position);


    }

    private Boolean validMoveRowChecker(Board board, COLOUR colour, Position position) {
        return checkDirectionForValidMove(board, colour, position, 1, 0) ||
                checkDirectionForValidMove(board, colour, position, -1, 0);

    }

    private Boolean validMoveColumnChecker(Board board, COLOUR colour, Position position) {
        return checkDirectionForValidMove(board, colour, position, 0, 1) ||
                checkDirectionForValidMove(board, colour, position, 0, -1);

    }

    private Boolean validMoveDiagonalChecker(Board board, COLOUR colour, Position position) {

        return checkDirectionForValidMove(board, colour, position, 1, 1) ||
                checkDirectionForValidMove(board, colour, position, 1, -1) ||
                checkDirectionForValidMove(board, colour, position, -1, 1) ||
                checkDirectionForValidMove(board, colour, position, -1, -1);

    }

    private Boolean checkDirectionForValidMove(Board board, COLOUR colour, Position position, Integer xIncrease, Integer yIncrease) {
        ImmutablePosition.Builder tempPosition = ImmutablePosition.builder();
        tempPosition.from(position);
        int numberOfCounters = 0;

        tempPosition.x(position.x() + xIncrease);
        tempPosition.y(position.y() + yIncrease);

        //while COLOUR colour present and of opposite colour

        while (board.getCounter(tempPosition.build()).isPresent() && !(board.getCounter(tempPosition.build()).get().getColour().equals(colour))) {
            numberOfCounters++;
            tempPosition.x(tempPosition.build().x() + xIncrease);
            tempPosition.y(tempPosition.build().y() + yIncrease);

        }

        return numberOfCounters > 0 && board.getCounter(tempPosition.build()).isPresent() && board.getCounter(tempPosition.build()).get().getColour().equals(colour);
    }
}
