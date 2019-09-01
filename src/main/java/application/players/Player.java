package application.players;

import application.game.Board;
import application.game.COLOUR;

public interface Player {

    Board playTurn(Board board);

    void playTurnKafka(Board board);

    COLOUR getCounterColour();

    void reset();


}
