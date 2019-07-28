package application;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
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


    //heuristic values
    @Value("${evaluationValue.discNum}")
    private Double evaluationDiscValue;
    @Value("${evaluationValue.mobility}")
    private Double evaluationMobilityValue;
    @Value("${evaluationValue.stableNum}")
    private Double evaluationStableDiscValue;

    @Value("${board.evaluationFunction}")
    private Integer evaluationFunction;


    @Autowired
    public Board(@Value("${board.size}") Integer boardSize, Verifier verifier) {
        this.boardSize = boardSize;
        this.board = setupBoard();
        this.verifier = verifier;
    }

    private ImmutableList<ImmutableList<Optional<Counter>>> setupBoard() {
        ImmutableList.Builder<ImmutableList<Optional<Counter>>> boardBuilder = ImmutableList.builder();

        //create initial counter set up in centre of board
        for (int y = 0; y < boardSize; y++) {
            ImmutableList.Builder<Optional<Counter>> rowBuilder = ImmutableList.builder();
            for (int x = 0; x < boardSize; x++) {

                if (y == (boardSize / 2) - 1) {
                    if (x == (boardSize / 2) - 1) {
                        rowBuilder.add(Optional.of(new Counter(Counter.COLOUR.WHITE)));
                    } else if (x == (boardSize / 2)) {
                        rowBuilder.add(Optional.of(new Counter(Counter.COLOUR.BLACK)));
                    } else {
                        rowBuilder.add(Optional.absent());
                    }
                } else if (y == (boardSize / 2)) {
                    if (x == (boardSize / 2) - 1) {
                        rowBuilder.add(Optional.of(new Counter(Counter.COLOUR.BLACK)));
                    } else if (x == (boardSize / 2)) {
                        rowBuilder.add(Optional.of(new Counter(Counter.COLOUR.WHITE)));
                    } else {
                        rowBuilder.add(Optional.absent());
                    }
                } else {
                    rowBuilder.add(Optional.absent());
                }
            }
            boardBuilder.add(rowBuilder.build());

        }
        countersPlayed = 4;
        numberOfWhiteCounters = 2;

        return boardBuilder.build();


    }



    public Integer getBoardSize() {
        return boardSize;
    }

    public Integer getCountersPlayed() {
        return countersPlayed;
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
                //System.out.println("valid move");
                //flip counter
                //horizontal flips
                flipCounters(newCounter.getColour(), position, 1, 0);
                flipCounters(newCounter.getColour(), position, -1, 0);
                //vertical flips
                flipCounters(newCounter.getColour(), position, 0, -1);
                flipCounters(newCounter.getColour(), position, 0, 1);

                //diagonal flips
                flipCounters(newCounter.getColour(), position, 1, 1);
                flipCounters(newCounter.getColour(), position, -1, 1);
                flipCounters(newCounter.getColour(), position, 1, -1);
                flipCounters(newCounter.getColour(), position, -1, -1);


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
                if (newCounter.getColour().equals(Counter.COLOUR.WHITE)) {
                    numberOfWhiteCounters++;
                }

                return true;
            } else {
                System.out.println("invalid move");
                return false;
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("exception");
            return false;
        }
    }


    public String printBoard() {
        //System.out.println("numberOfWhiteCounters = " + numberOfWhiteCounters);
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
                    if (counterOptional.get().getColour().equals(Counter.COLOUR.WHITE)) {
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

    private void flipCounters(Counter.COLOUR colour, Position position, Integer xDirection, Integer yDirection) {

        ImmutablePosition.Builder tempPosition = ImmutablePosition.builder();
        tempPosition.from(position);
        int numberOfCounters = 0;


        tempPosition.x(position.x() + xDirection);
        tempPosition.y(position.y() + yDirection);

        while (getCounter(tempPosition.build()).isPresent() && !(getCounter(tempPosition.build()).get().getColour().equals(colour))) {
            numberOfCounters++;
            tempPosition.x(tempPosition.build().x() + xDirection);
            tempPosition.y(tempPosition.build().y() + yDirection);

        }


        if (numberOfCounters > 0 && getCounter(tempPosition.build()).isPresent() && getCounter(tempPosition.build()).get().getColour().equals(colour)) {
            while (numberOfCounters > 0) {
                tempPosition.x(tempPosition.build().x() - xDirection);
                tempPosition.y(tempPosition.build().y() - yDirection);


                getCounter(tempPosition.build()).get().flip();
                if (colour.equals(Counter.COLOUR.WHITE)) {
                    numberOfWhiteCounters++;
                } else {
                    numberOfWhiteCounters--;
                }
                numberOfCounters--;
            }
        }


    }

    public ImmutableList<ImmutablePosition> getValidMoves(Counter.COLOUR colour) {
        ImmutableList.Builder<ImmutablePosition> validMoves = ImmutableList.builder();
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

    public Integer numberOfValidMoves(Counter.COLOUR colour) {


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

    public Integer numberOfStableDiscs(Counter.COLOUR colour) {
        int stableCounters = 0;

        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                if (board.get(y).get(x).isPresent() && board.get(y).get(x).get().getColour().equals(colour)) {
                    //divide board into 4 regions with the intersection of the 4 regions being the current counter location
                    //then check if any of the regions are of the same colour
                    //if so then counter stable
                    ImmutablePosition counterPosition = ImmutablePosition.builder().x(x).y(y).build();
                    ImmutablePosition topLeft = ImmutablePosition.builder().x(0).y(0).build();
                    ImmutablePosition bottomRight = ImmutablePosition.builder().x(boardSize - 1).y(boardSize - 1).build();
                    ImmutablePosition midLeft = ImmutablePosition.builder().x(0).y(counterPosition.y()).build();
                    ImmutablePosition bottomMid = ImmutablePosition.builder().x(counterPosition.x()).y(boardSize - 1).build();
                    ImmutablePosition topMid = ImmutablePosition.builder().x(counterPosition.x()).y(0).build();
                    ImmutablePosition midRight = ImmutablePosition.builder().x(boardSize - 1).y(counterPosition.y()).build();

                    if (areaSameColour(colour, topLeft, counterPosition) || areaSameColour(colour, counterPosition, bottomRight) || areaSameColour(colour, midLeft, bottomMid) || areaSameColour(colour, topMid, midRight)) {
                        stableCounters++;
                    }
                }
            }

        }


        return stableCounters;

    }

    private Boolean areaSameColour(Counter.COLOUR colour, ImmutablePosition startPosition, ImmutablePosition endPosition) {

        for (int y = startPosition.y(); y <= endPosition.y(); y++) {
            for (int x = startPosition.x(); x <= endPosition.x(); x++) {
                ImmutablePosition.Builder currentPosition = ImmutablePosition.builder();
                currentPosition.x(x).y(y);
                if (!getCounter(currentPosition.build()).isPresent() || !getCounter(currentPosition.build()).get().getColour().equals(colour)) {
                    return false;
                }
            }
        }
        return true;
    }

    public Double getBoardHeurstic(Counter.COLOUR colour) {
        Double huersticValue = 0.0;
        if (evaluationFunction == 1) {
            huersticValue = numberOfValidMoves(colour) * evaluationMobilityValue
                    + numberOfStableDiscs(colour) * evaluationStableDiscValue;

            if (colour.equals(Counter.COLOUR.WHITE)) {
                huersticValue += numberOfWhiteCounters * evaluationDiscValue;
            } else {
                huersticValue += (countersPlayed - numberOfWhiteCounters) * evaluationDiscValue;
            }

        } else {
            System.out.println("Using basic huerstic function");
            if (colour.equals(Counter.COLOUR.WHITE)) {
                huersticValue = numberOfWhiteCounters * evaluationDiscValue;
            } else {
                huersticValue = (countersPlayed - numberOfWhiteCounters) * evaluationDiscValue;
            }
        }
        return huersticValue;
    }


    public Counter.COLOUR getWinner(Boolean printScore) {
        int whiteCounters = 0;
        for (ImmutableList<Optional<Counter>> row : board) {
            for (Optional<Counter> counter : row) {

                if (counter.isPresent() && counter.get().getColour().equals(Counter.COLOUR.WHITE)) {
                    whiteCounters++;
                }
            }
        }
        if (printScore) {
            System.out.println("Score: " + whiteCounters + ":" + (int) (Math.pow(boardSize, 2) - whiteCounters));
        }
        if (whiteCounters > Math.ceil(Math.pow(boardSize, 2) / 2.0)) {
            return Counter.COLOUR.WHITE;
        } else if (whiteCounters < Math.ceil(Math.pow(boardSize, 2) / 2.0)) {
            return Counter.COLOUR.BLACK;
        } else {
            return null;
        }
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
        } catch (IOException e) {
            e.printStackTrace();
            return null;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
