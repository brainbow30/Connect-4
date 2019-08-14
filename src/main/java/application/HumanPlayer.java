package application;

import java.util.InputMismatchException;
import java.util.Scanner;


public class HumanPlayer implements Player {
    private final Counter.COLOUR counterColour;
    private final MessageProducer producer;


    public HumanPlayer(Counter.COLOUR counterColour, MessageProducer producer) {
        this.counterColour = counterColour;
        this.producer = producer;
    }

    public Board playTurn(Board board) {
        boolean invalidMove = true;
        while (invalidMove) {
            ImmutablePosition position = getUserInput(board.getBoardSize());
            Counter counter = new Counter(counterColour);
            if (board.addCounter(counter, position)) {
                invalidMove = false;
            }

        }
        return board;

    }

    public void playTurnKafka(Board board) {
        boolean invalidMove = true;
        while (invalidMove) {
            ImmutablePosition position = getUserInput(board.getBoardSize());
            Counter counter = new Counter(counterColour);
            if (board.addCounter(counter, position)) {
                invalidMove = false;
            }

        }
        if (counterColour.equals(Counter.COLOUR.WHITE)) {
            producer.sendMessage2(0, java.time.LocalDateTime.now().toString(), board);
        } else {
            producer.sendMessage1(0, java.time.LocalDateTime.now().toString(), board);
        }

    }

    private ImmutablePosition getUserInput(Integer boardSize) {
        System.out.println(counterColour + "'s Turn");
        Scanner input = new Scanner(System.in);
        boolean invalid = true;
        int x = 0;
        int y = 0;
        while (invalid) {
            try {
                System.out.print("Enter x coordinate: ");
                x = input.nextInt();
                System.out.print("Enter y coordinate: ");
                y = input.nextInt();
                if (x > boardSize || y > boardSize || x <= 0 || y <= 0) {
                    throw new IndexOutOfBoundsException();
                }
                invalid = false;

            } catch (IndexOutOfBoundsException | InputMismatchException e) {
                System.out.println("Invalid Input");
                input = new Scanner(System.in);

            }

        }
        return ImmutablePosition.builder().x(x - 1).y(y - 1).build();

    }

    public Counter.COLOUR getCounterColour() {
        return counterColour;
    }


    public void reset() {

    }
}
