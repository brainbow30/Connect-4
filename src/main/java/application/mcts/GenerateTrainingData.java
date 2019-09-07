package application.mcts;

import application.game.Board;
import application.game.COLOUR;
import com.google.common.collect.ImmutableList;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateTrainingData {
    public static void save(TreeNode terminalNode) {
        while (terminalNode.getParent() != null) {
            ImmutableList<Integer> intBoard = forNeuralNet(terminalNode.getCurrentBoard(), terminalNode.getColour());
            COLOUR winner = terminalNode.getCurrentBoard().getWinner(false);
            Integer result = -1;
            if (winner != null && winner.equals(terminalNode.getColour())) {
                result = 1;
            }
            try {
                write("training.txt", intBoard, result);
            } catch (IOException e) {
                e.printStackTrace();
            }
            terminalNode = terminalNode.getParent();
        }
    }

    static void write(String filename, ImmutableList<Integer> intBoard, Integer result) throws IOException {
        BufferedWriter outputWriter = null;
        outputWriter = new BufferedWriter(new FileWriter("intBoards/" + filename, true));
        outputWriter.write("[");
        for (int pos = 0; pos < intBoard.size(); pos++) {
            outputWriter.write(intBoard.get(pos).toString());
            if (pos + 1 != intBoard.size()) {
                outputWriter.write(",");
            } else {
                outputWriter.write("]");
            }
        }
        outputWriter.write(":" + result);
        outputWriter.newLine();
        outputWriter.flush();
        outputWriter.close();
    }

    static ImmutableList<Integer> forNeuralNet(Board board, COLOUR colour) {
        ImmutableList<Integer> intBoard = board.asIntArray();
        if (colour.equals(COLOUR.BLACK)) {
            ImmutableList.Builder<Integer> builder = ImmutableList.builder();
            for (Integer pos : intBoard) {
                if (pos == 1) {
                    builder.add(-1);
                } else if (pos == -1) {
                    builder.add(1);
                } else {
                    builder.add(0);
                }
            }
            intBoard = ImmutableList.copyOf(builder.build());
        }
        return intBoard;
    }
}
