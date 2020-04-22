package application.mcts;

import application.game.COLOUR;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import static application.mcts.TreeNode.getQ;

public class GenerateNNData {

    static ImmutableList<Integer> canonicalBoard(TreeNode node) {
        ImmutableList<Integer> intBoard = node.getCurrentBoard().asIntArray();
        if (node.getRootColour().equals(COLOUR.YELLOW)) {
            return changeBoardPerspective(intBoard);
        }
        return intBoard;
    }


    static String write(ImmutableList<Integer> intBoard, ImmutableList<Double> policyBoard, Double result) {
        StringBuilder builder = new StringBuilder();
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
        return builder.toString();
    }


    static ImmutableList<Integer> changeBoardPerspective(ImmutableList<Integer> intBoard) {
        ImmutableList.Builder<Integer> builder = ImmutableList.builder();
        for (Integer pos : intBoard) {
            builder.add(pos * -1);
        }
        return ImmutableList.copyOf(builder.build());
    }


    public static String save(TreeNode terminalNode, Optional<COLOUR> winner) {
        TreeNode node = terminalNode;
        COLOUR rootColour = terminalNode.getRootColour();
        StringBuilder builder = new StringBuilder();
        double result = 0.0;
        double oppResult = 0.0;

        if (winner.isPresent()) {
            if (winner.get().equals(COLOUR.RED)) {
                result = 1.0;
                oppResult = -1.0;
            } else if (winner.get().equals(COLOUR.YELLOW)) {
                result = -1.0;
                oppResult = 1.0;
            }
        }
        node = node.getParent();
        while (node != null && node.getParent() != null) {
            ImmutableList<Integer> intBoard = node.getCurrentBoard().asIntArray();
            if (rootColour.equals(COLOUR.RED)) {
                if (node.getColour().equals(rootColour)) {
                    builder.append(write(intBoard, node.getTrainingPolicy(), (result + getQ(node)) / 2.0));
                    builder.append(",");
                } else if (!node.getColour().equals(rootColour)) {
                    builder.append(write(changeBoardPerspective(intBoard), node.getTrainingPolicy(), (oppResult + getQ(node) * -1) / 2.0));
                    builder.append(",");
                }
            } else if (rootColour.equals(COLOUR.YELLOW)) {
                if (node.getColour().equals(rootColour)) {
                    builder.append(write(changeBoardPerspective(intBoard), node.getTrainingPolicy(), (oppResult + getQ(node)) / 2.0));
                    builder.append(",");
                } else if (!node.getColour().equals(rootColour)) {
                    builder.append(write(intBoard, node.getTrainingPolicy(), (result + getQ(node) * -1) / 2.0));
                    builder.append(",");
                }
            }


            node = node.getParent();
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

}
