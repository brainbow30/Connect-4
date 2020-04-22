package application.game;

import application.ImmutablePosition;
import application.Position;
import application.game.verifiers.Verifier;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

@SuppressWarnings("Guava")
@Component
public class Board implements Serializable {
    private ImmutableList<ImmutableList<Optional<Counter>>> board;
    private final Verifier verifier;
    private Integer countersPlayed;
    private Integer numberOfWhiteCounters;
    private final Integer boardSize;

    //todo separate boardsize into width and height
    @Autowired
    public Board(@Value("${board.size}") Integer boardSize, @Qualifier("connect4") Verifier verifier) {
        this.boardSize = boardSize;
        this.verifier = verifier;
        setup();
    }

    private void setup() {
        board = verifier.setupBoard(boardSize);
        int[] stats = verifier.setupStats(boardSize);
        numberOfWhiteCounters = stats[1];
        countersPlayed = stats[0] + stats[1];
    }

    public void reset() {
        setup();
    }


    public Integer getBoardSize() {
        return boardSize;
    }

    public Integer getCountersPlayed() {
        return countersPlayed;
    }

    public Integer getNumberOfWhiteCounters() {
        return numberOfWhiteCounters;
    }

    public Integer getNumberOfBlackCounters() {
        return countersPlayed - numberOfWhiteCounters;
    }

    public Optional<Counter> getCounter(Position position) {
        int x = position.x();
        int y = position.y();
        try {

            if (board.get(y).get(x).isPresent()) {
                return board.get(y).get(x);
            } else {
                return Optional.absent();
            }
        } catch (IndexOutOfBoundsException e) {
            return Optional.absent();
        }
    }


    public Boolean addCounter(Counter newCounter, Position position) {
        try {
            if (verifier.validMove(this, newCounter.getColour(), position)) {

                ImmutableList.Builder<Optional<Counter>> rowBuilder = ImmutableList.builder();
                Integer x = 0;
                for (Optional<Counter> counter : board.get(position.y())) {
                    if (x.equals(position.x())) {
                        rowBuilder.add(Optional.of(newCounter));
                    } else {
                        rowBuilder.add(counter);
                    }
                    x++;
                }
                Integer y = 0;
                ImmutableList.Builder<ImmutableList<Optional<Counter>>> boardBuilder = ImmutableList.builder();
                for (ImmutableList<Optional<Counter>> row : board) {
                    if (y.equals(position.y())) {
                        boardBuilder.add(rowBuilder.build());
                    } else {
                        boardBuilder.add(row);
                    }
                    y++;
                }
                board = boardBuilder.build();
                countersPlayed++;
                if (newCounter.getColour().equals(COLOUR.RED)) {
                    numberOfWhiteCounters++;
                }

                return true;
            } else {
                return false;
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("exception");
            return false;
        }
    }


    public String printBoard() {
        StringBuilder boardString = new StringBuilder("\n  ");
        for (int x = 0; x < boardSize; x++) {
            boardString.append(" ").append(x + 1);
        }
        boardString.append("\n  __");
        for (int i = 0; i < boardSize * 2 - 1; i++) {
            boardString.append("_");
        }
        boardString.append("\n");

        int y = 1;
        for (ImmutableList<Optional<Counter>> row : board) {
            boardString.append(y).append(" |");
            for (Optional<Counter> counterOptional : row) {
                if (counterOptional.isPresent()) {
                    if (counterOptional.get().getColour().equals(COLOUR.RED)) {
                        boardString.append("O|");
                    } else {
                        boardString.append("X|");
                    }
                } else {
                    boardString.append(" |");
                }
            }
            boardString.append("\n  __");
            for (int i = 0; i < boardSize * 2 - 1; i++) {
                boardString.append("_");
            }
            boardString.append("\n");
            y++;
        }
        return boardString.toString();
    }


    @Override
    public String toString() {
        return printBoard();
    }

    public ImmutableList<ImmutablePosition> getValidMoves(COLOUR colour) {
        ImmutableList.Builder<ImmutablePosition> validMoves = ImmutableList.builder();
        if (isWinner()) {
            return validMoves.build();
        }
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                ImmutablePosition immutablePosition = ImmutablePosition.builder().x(x).y(y).build();
                if (verifier.validMove(this, colour, immutablePosition)) {
                    validMoves.add(immutablePosition);
                }
            }
        }
        return validMoves.build();
    }

    public Integer numberOfValidMoves(COLOUR colour) {
        Integer count = 0;
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                ImmutablePosition immutablePosition = ImmutablePosition.builder().x(x).y(y).build();
                if (verifier.validMove(this, colour, immutablePosition)) {
                    count++;
                }
            }
        }

        return count;
    }

    public Boolean isWinner() {
        if (getWinner().isPresent()) {
            return true;
        } else {
            return false;
        }
    }

    public Optional<COLOUR> getWinner() {
        for (int y = boardSize; y >= 0; y--) {
            for (int x = 0; x < boardSize; x++) {
                ImmutablePosition position = ImmutablePosition.builder().x(x).y(y).build();
                Optional<Counter> startCounter = getCounter(position);
                if (startCounter.isPresent()) {
                    Boolean vertical = fourVerticle(position);
                    Boolean horizontal = fourHorizontal(position);
                    Boolean right = fourDiagonalRight(position);
                    Boolean left = fourDiagonalLeft(position);
                    if (vertical || horizontal || right || left) {
                        return Optional.of(startCounter.get().getColour());
                    }
                }

            }
        }
        return Optional.absent();
    }

    private Boolean fourVerticle(ImmutablePosition startPosition) {
        Optional<Counter> startCounter = getCounter(startPosition);

            ImmutablePosition.Builder position = ImmutablePosition.builder().x(startPosition.x());
            for (int y = startPosition.y(); y < startPosition.y() + 4; y++) {
                position.y(y);
                Optional<Counter> currentCounter = getCounter(position.build());
                if (!(currentCounter.isPresent() && currentCounter.get().getColour().equals(startCounter.get().getColour()))) {
                    return false;
                }
            }
            return true;
    }

    private Boolean fourHorizontal(ImmutablePosition startPosition) {
        Optional<Counter> startCounter = getCounter(startPosition);
            ImmutablePosition.Builder position = ImmutablePosition.builder().y(startPosition.y());
            for (int x = startPosition.x(); x < startPosition.x() + 4; x++) {
                if (x >= boardSize) {
                    return false;
                }
                position.x(x);
                Optional<Counter> currentCounter = getCounter(position.build());
                if (!(currentCounter.isPresent() && currentCounter.get().getColour().equals(startCounter.get().getColour()))) {
                    return false;
                }
            }
            return true;
    }

    private Boolean fourDiagonalRight(ImmutablePosition startPosition) {
        Optional<Counter> startCounter = getCounter(startPosition);
            ImmutablePosition.Builder position = ImmutablePosition.builder().y(startPosition.y());
            for (int i = 0; i < 4; i++) {
                int x = startPosition.x() + i;
                int y = startPosition.y() + i;
                if (x >= boardSize) {
                    return false;
                }
                if (y >= boardSize) {
                    return false;
                }
                position.x(x);
                position.y(y);
                Optional<Counter> currentCounter = getCounter(position.build());
                if (!(currentCounter.isPresent() && currentCounter.get().getColour().equals(startCounter.get().getColour()))) {
                    return false;
                }
            }
            return true;
    }

    private Boolean fourDiagonalLeft(ImmutablePosition startPosition) {
        Optional<Counter> startCounter = getCounter(startPosition);
            ImmutablePosition.Builder position = ImmutablePosition.builder().y(startPosition.y());
            for (int i = 0; i < 4; i++) {
                int x = startPosition.x() - i;
                int y = startPosition.y() + i;
                if (x < 0) {
                    return false;
                }
                if (y >= boardSize) {
                    return false;
                }
                position.x(x);
                position.y(y);
                Optional<Counter> currentCounter = getCounter(position.build());
                if (!(currentCounter.isPresent() && currentCounter.get().getColour().equals(startCounter.get().getColour()))) {
                    return false;
                }
            }
            return true;
    }


    @Override
    public Board clone() {
        try {
            Board newBoard;
            ByteArrayInputStream bis;
            ObjectInputStream ois;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.flush();
            byte[] data = bos.toByteArray();
            bis = new ByteArrayInputStream(data);
            ois = new ObjectInputStream(bis);
            newBoard = (Board) ois.readObject();
            return newBoard;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;

        }
    }

    @Override
    public boolean equals(Object object) {
        try {
            Board board = (Board) object;
            return board.printBoard().equals(printBoard());
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }

    }

    public ImmutableList<Integer> asIntArray() {
        ImmutableList.Builder<Integer> builder = ImmutableList.builder();
        for (ImmutableList<Optional<Counter>> row : board) {
            for (Optional<Counter> counterOptional : row) {
                if (counterOptional.isPresent()) {
                    Counter counter = counterOptional.get();
                    if (counter.getColour().equals(COLOUR.RED)) {
                        builder.add(1);
                    } else {
                        builder.add(-1);
                    }
                } else {
                    builder.add(0);
                }
            }
        }
        return builder.build();
    }

}
