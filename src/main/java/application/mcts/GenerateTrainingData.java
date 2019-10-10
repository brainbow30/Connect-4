package application.mcts;

import application.game.COLOUR;
import com.google.common.collect.ImmutableList;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateTrainingData {
    private BufferedWriter outputWriter;
    private String filename;

    public GenerateTrainingData(String filename) {
        this.filename = filename;
        open();
    }

    public void open() {
        try {
            outputWriter = new BufferedWriter(new FileWriter("intBoards/" + filename, false));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(TreeNode terminalNode) {
        COLOUR winner = terminalNode.getCurrentBoard().getWinner(false);
        int result = 0;
        if (winner != null && winner.equals(terminalNode.getRootColour())) {
            result = 1;
        } else if (winner != null && !winner.equals(terminalNode.getRootColour())) {
            result = -1;
        }
        while (terminalNode.getParent() != null) {
            ImmutableList<Integer> intBoard = terminalNode.canonicalBoard();

            try {
                write(intBoard, result);
            } catch (IOException e) {
                e.printStackTrace();
            }
            terminalNode = terminalNode.getParent();
        }
    }

    void write(ImmutableList<Integer> intBoard, Integer result) throws IOException {
        for (int pos = 0; pos < intBoard.size(); pos++) {
            outputWriter.write(intBoard.get(pos).toString());
            if (pos + 1 != intBoard.size()) {
                outputWriter.write(",");
            }
        }
        if (result == 0) {
            result = -1;
        }
        outputWriter.write(":" + result);
        outputWriter.newLine();
        outputWriter.flush();


    }

    public void close() {
        try {
            outputWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
