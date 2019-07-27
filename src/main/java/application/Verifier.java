package application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
class Verifier implements Serializable {
    @Autowired
    public Verifier() {

    }


    public Boolean validMove(Board board, Counter counter, Position position) {
        if (position.x() >= board.getBoardSize() || position.x() < 0) {
            throw new IndexOutOfBoundsException();
        } else if (position.y() >= board.getBoardSize() || position.y() < 0) {
            throw new IndexOutOfBoundsException();
        }

        if (board.getCounter(position).isPresent()) {
            return false;
        }
        if (validMoveRowChecker(board, counter, position)) {
            return true;
        } else if (validMoveColumnChecker(board, counter, position)) {
            return true;
        } else return validMoveDiagonalChecker(board, counter, position);


    }

    private Boolean validMoveRowChecker(Board board, Counter counter, Position position) {
        return checkDirectionForValidMove(board, counter, position, 1, 0) ||
                checkDirectionForValidMove(board, counter, position, -1, 0);

    }

    private Boolean validMoveColumnChecker(Board board, Counter counter, Position position) {
        return checkDirectionForValidMove(board, counter, position, 0, 1) ||
                checkDirectionForValidMove(board, counter, position, 0, -1);

    }

    private Boolean validMoveDiagonalChecker(Board board, Counter counter, Position position) {

        return checkDirectionForValidMove(board, counter, position, 1, 1) ||
                checkDirectionForValidMove(board, counter, position, 1, -1) ||
                checkDirectionForValidMove(board, counter, position, -1, 1) ||
                checkDirectionForValidMove(board, counter, position, -1, -1);

    }

    private Boolean checkDirectionForValidMove(Board board, Counter counter, Position position, Integer xIncrease, Integer yIncrease) {
        ImmutablePosition.Builder tempPosition = ImmutablePosition.builder();
        tempPosition.from(position);
        int numberOfCounters = 0;

        tempPosition.x(position.x() + xIncrease);
        tempPosition.y(position.y() + yIncrease);

        //while counter present and of opposite colour

        while (board.getCounter(tempPosition.build()).isPresent() && !(board.getCounter(tempPosition.build()).get().getColour().equals(counter.getColour()))) {
            numberOfCounters++;
            tempPosition.x(tempPosition.build().x() + xIncrease);
            tempPosition.y(tempPosition.build().y() + yIncrease);

        }

        return numberOfCounters > 0 && board.getCounter(tempPosition.build()).isPresent() && board.getCounter(tempPosition.build()).get().getColour().equals(counter.getColour());
    }
}