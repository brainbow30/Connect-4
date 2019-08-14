package application;

interface Player {

    Board playTurn(Board board);

    void playTurnKafka(Board board);

    Counter.COLOUR getCounterColour();

    void reset();


}
