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
@Qualifier("connect4")
@Primary
public class Connect4Verifier implements Serializable, Verifier {
    @Autowired

    public Connect4Verifier() {

    }

    public ImmutableList<ImmutableList<Optional<Counter>>> setupBoard(int boardSize) {
        ImmutableList.Builder<ImmutableList<Optional<Counter>>> boardBuilder = ImmutableList.builder();

        //create initial counter set up in centre of board
        for (int y = 0; y < boardSize; y++) {
            ImmutableList.Builder<Optional<Counter>> rowBuilder = ImmutableList.builder();
            for (int x = 0; x < boardSize; x++) {
                rowBuilder.add(Optional.absent());
            }
            boardBuilder.add(rowBuilder.build());
        }
        return boardBuilder.build();
    }

    public int[] setupStats(int boardSize) {
        int countersPlayed = 0;
        int numberOfWhiteCounters = 0;
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
        ImmutablePosition positionBelow = ImmutablePosition.builder().x(position.x()).y(position.y() + 1).build();
        if (position.y() == board.getBoardSize() - 1) {
            return true;
        } else if (board.getCounter(positionBelow).isPresent()) {
            return true;
        } else return false;


    }
}
