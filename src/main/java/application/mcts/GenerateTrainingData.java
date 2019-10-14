package application.mcts;

import application.game.COLOUR;
import com.google.common.base.Optional;
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
        Optional<COLOUR> winner = terminalNode.getCurrentBoard().getWinner(false);
        int result = 0;
        int oppResult = 0;
        if (winner.isPresent()) {
            if (winner.get().equals(terminalNode.getRootColour())) {
                result = 1;
                oppResult = -1;
            } else if (!winner.get().equals(terminalNode.getRootColour())) {
                result = -1;
                oppResult = 1;
            }
        }

        while (terminalNode.getParent() != null) {
            ImmutableList<Integer> intBoard = terminalNode.canonicalBoard();
            ImmutableList<Integer> oppIntBoard = terminalNode.changeBoardPerspective(intBoard);

            try {
                //todo write opposite board with opposite result to generate more training data
                write(intBoard, result);
                write(oppIntBoard, oppResult);
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
