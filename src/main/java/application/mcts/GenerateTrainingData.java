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
    private StringBuilder builder;

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
        builder = new StringBuilder();
        builder.append("[");
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
            write(intBoard, terminalNode.getTrainingPolicy(), result);
            builder.append(",");
            write(oppIntBoard, terminalNode.rotateBoard(terminalNode.getTrainingPolicy()), oppResult);
            builder.append(",");

            terminalNode = terminalNode.getParent();
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append("]");
        try {
            outputWriter.write(builder.toString());
            outputWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void write(ImmutableList<Integer> intBoard, ImmutableList<Double> policyBoard, Integer result) {
        builder.append("[[");
        for (int pos = 0; pos < intBoard.size(); pos++) {
            builder.append(intBoard.get(pos));
            if (pos + 1 != intBoard.size()) {
                builder.append(",");
            }
        }

        builder.append("],[");
        for (int pos = 0; pos < policyBoard.size(); pos++) {
            builder.append(String.format("%.8f", policyBoard.get(pos)));
            if (pos + 1 != policyBoard.size()) {
                builder.append(",");
            }
        }

        builder.append("]," + result);
        builder.append("]");


    }

    public void close() {
        try {
            outputWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
