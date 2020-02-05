package application.mcts;

import application.game.COLOUR;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateNNData {
    private BufferedWriter outputWriter;
    private String filename;
    private StringBuilder builder;

    public GenerateNNData(String filename) {
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

    static ImmutableList<Integer> canonicalBoard(TreeNode node) {
        ImmutableList<Integer> intBoard = node.getCurrentBoard().asIntArray();
        if (node.getColour().equals(COLOUR.YELLOW)) {
            return changeBoardPerspective(intBoard);
        }
        return intBoard;
    }


    void write(ImmutableList<Integer> intBoard, ImmutableList<Double> policyBoard, Double result) {
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

    static ImmutableList<Integer> changeBoardPerspective(ImmutableList<Integer> intBoard) {
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
        return ImmutableList.copyOf(builder.build());
    }

    public void save(TreeNode terminalNode) {
        TreeNode node = terminalNode;
        builder = new StringBuilder();
        builder.append("[");
        Optional<COLOUR> winner = node.getCurrentBoard().getWinner();
        int result = 0;
        int oppResult = 0;

        if (winner.isPresent()) {
            if (winner.get().equals(COLOUR.RED)) {
                result = 1;
                oppResult = -1;
            } else if (winner.get().equals(COLOUR.YELLOW)) {
                result = -1;
                oppResult = 1;
            }
        }
        while (node.getParent() != null) {
            ImmutableList<Integer> intBoard = canonicalBoard(node);
            if (node.getColour().equals(COLOUR.RED)) {
                write(intBoard, node.getTrainingPolicy(), ((node.getNumberOfWins() / node.getNumberOfSimulations()) + result) / 2.0);
                builder.append(",");
            } else if (node.getColour().equals(COLOUR.YELLOW)) {
                write(intBoard, node.getTrainingPolicy(), ((node.getNumberOfWins() / node.getNumberOfSimulations()) + oppResult) / 2.0);
                builder.append(",");
            }


            node = node.getParent();
        }
        ImmutableList<Integer> intBoard = canonicalBoard(node);
        if (node.getColour().equals(COLOUR.RED)) {
            write(intBoard, node.getTrainingPolicy(), ((node.getNumberOfWins() / node.getNumberOfSimulations()) + result) / 2.0);
        } else if (node.getColour().equals(COLOUR.YELLOW)) {
            write(intBoard, node.getTrainingPolicy(), ((node.getNumberOfWins() / node.getNumberOfSimulations()) + oppResult) / 2.0);
        }
        builder.append("]");
        try {
            outputWriter.write(builder.toString());
            outputWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
