package application.players;

import application.ImmutablePosition;
import application.game.Board;
import application.game.COLOUR;
import application.game.Counter;

import java.util.InputMismatchException;
import java.util.Scanner;


public class HumanPlayer implements Player {
    private final COLOUR counterColour;


    public HumanPlayer(COLOUR counterColour) {
        this.counterColour = counterColour;
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

    public COLOUR getCounterColour() {
        return counterColour;
    }


    public void reset() {

    }
}
