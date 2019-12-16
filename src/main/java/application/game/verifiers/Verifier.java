package application.game.verifiers;

import application.Position;
import application.game.Board;
import application.game.COLOUR;
import application.game.Counter;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

public interface Verifier {
    ImmutableList<ImmutableList<Optional<Counter>>> setupBoard(int boardSize);

    int[] setupStats(int boardSize);

    Boolean validMove(Board board, COLOUR colour, Position position);

}
