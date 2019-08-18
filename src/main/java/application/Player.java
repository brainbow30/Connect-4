package application;

interface Player {

    Board playTurn(Board board);

    void playTurnKafka(Board board);

    COLOUR getCounterColour();

    void reset();


}
