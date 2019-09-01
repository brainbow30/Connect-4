package application.game;

import application.ImmutablePosition;
import application.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public
class Verifier implements Serializable {
    @Autowired
    public Verifier() {

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
