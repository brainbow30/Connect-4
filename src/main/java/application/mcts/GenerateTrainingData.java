package application.mcts;

import application.game.COLOUR;
import com.google.common.collect.ImmutableList;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateTrainingData {
    private BufferedWriter outputWriter;

    public GenerateTrainingData(String filename) {
        try {
            outputWriter = new BufferedWriter(new FileWriter("intBoards/" + filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(TreeNode terminalNode) {
        while (terminalNode.getParent() != null) {
            ImmutableList<Integer> intBoard = terminalNode.canonicalBoard();
            COLOUR winner = terminalNode.getCurrentBoard().getWinner(false);
            ImmutableList<Double> policyVector = terminalNode.getPolicyVector();
            int result = 0;
            if (winner != null && winner.equals(terminalNode.getColour())) {
                result = 1;
            } else if (winner != null && !winner.equals(terminalNode.getColour())) {
                result = -1;
            }
            try {
                write(intBoard, policyVector, result);
            } catch (IOException e) {
                e.printStackTrace();
            }
            terminalNode = terminalNode.getParent();
        }
    }

    void write(ImmutableList<Integer> intBoard, ImmutableList<Double> policy, Integer result) throws IOException {
        for (int pos = 0; pos < intBoard.size(); pos++) {
            outputWriter.write(intBoard.get(pos).toString());
            if (pos + 1 != intBoard.size()) {
                outputWriter.write(",");
            }
        }
        outputWriter.write(":");
        StringBuilder policyVector = new StringBuilder();
        for (Double value : policy) {
            policyVector.append(value).append(",");
        }
        outputWriter.write(policyVector.substring(0, policyVector.length() - 1));
        if (result == 0) {
            result = -1;
        }
        outputWriter.write(":" + result);
        outputWriter.newLine();
        outputWriter.flush();

    }


}
