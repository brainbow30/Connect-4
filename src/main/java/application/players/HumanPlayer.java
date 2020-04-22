package application.players;

import application.ImmutablePosition;
import application.game.Board;
import application.game.COLOUR;
import application.game.Counter;
import application.gui.GUI;
import com.google.common.base.Optional;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


public class HumanPlayer implements Player {
    private final COLOUR counterColour;
    private final GUI gui;


    public HumanPlayer(COLOUR counterColour, GUI gui) {
        this.counterColour = counterColour;
        this.gui = gui;
    }

    public HumanPlayer(COLOUR counterColour) {
        this.counterColour = counterColour;
        gui = null;
    }

    public Board playTurn(Board board) {
        boolean invalidMove = true;
        Counter counter = new Counter(counterColour);
        while (invalidMove) {
            ImmutablePosition position;
            if (gui != null) {
                gui.getFocus();
                Optional<ImmutablePosition> clickedPos = Optional.absent();
                while (!clickedPos.isPresent()) {
                    clickedPos = gui.getClickedPos();
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                position = clickedPos.get();


            } else {
                position = getUserInput(board.getBoardSize());


            }
            invalidMove = !board.addCounter(counter, position);
            position = ImmutablePosition.builder().x(position.x()).y(position.y() + 1).build();
            while (invalidMove && position.y() < board.getBoardSize()) {
                invalidMove = !board.addCounter(counter, position);
                position = ImmutablePosition.builder().x(position.x()).y(position.y() + 1).build();
            }
            if (invalidMove) {
                System.out.println("invalid move");
            }
        }
        return board;

    }


    private ImmutablePosition getUserInput(Integer boardSize) {
        System.out.println(counterColour + "'s Turn");
        Scanner input = new Scanner(System.in);
        boolean invalid = true;
        int x = 0;
        while (invalid) {
            try {
                System.out.print("Enter x coordinate: ");
                x = input.nextInt();

                if (x > boardSize || x <= 0) {
                    throw new IndexOutOfBoundsException();
                }
                invalid = false;

            } catch (IndexOutOfBoundsException | InputMismatchException e) {
                System.out.println("Invalid Input");
                input = new Scanner(System.in);

            }

        }
        return ImmutablePosition.builder().x(x - 1).y(0).build();

    }

    public COLOUR getCounterColour() {
        return counterColour;
    }


    public void reset() {

    }
}
