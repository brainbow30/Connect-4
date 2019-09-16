package application.mcts;

import application.ImmutablePosition;
import application.game.Board;
import application.game.COLOUR;
import application.game.Counter;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.*;
import java.util.Random;


public final class TreeNode implements Serializable {
    private final Board currentBoard;
    private final COLOUR colour;
    private final COLOUR rootColour;
    private final TreeNode parent;
    private Double numberOfWins = 0.0;
    private Integer numberOfSimulations = 0;
    private Boolean visited = false;
    private ImmutableList<TreeNode> children;
    private Boolean terminalNode = false;
    private final ImmutablePosition positionToCreateBoard;
    private Boolean isRoot = false;


    private TreeNode(Builder builder) {
        parent = builder.parent;
        currentBoard = builder.currentBoard;
        colour = builder.colour;
        rootColour = builder.rootColour;
        if (builder.positionToCreateBoard == null) {
            isRoot = true;
        }
        positionToCreateBoard = builder.positionToCreateBoard;
        children = ImmutableList.of();
    }

    public static Builder builder() {
        return new Builder();
    }

    private ImmutableList<TreeNode> generateChildren() {
        Counter counter = new Counter(colour);
        ImmutableList.Builder<TreeNode> builder = ImmutableList.builder();
        ImmutableList<ImmutablePosition> validMoves = currentBoard.getValidMoves(colour);
        COLOUR newColour = COLOUR.opposite(colour);

        if (validMoves.size() == 0) {
            validMoves = currentBoard.getValidMoves(newColour);
            if (validMoves.size() == 0) {
                setTerminalNode();
            }
            counter.flip();
            newColour = COLOUR.opposite(newColour);
        }

        for (ImmutablePosition move : validMoves) {
            Board clone = currentBoard.clone();
            clone.addCounter(counter, move);
            TreeNode childNode = TreeNode.builder()
                    .parent(this)
                    .currentBoard(clone)
                    .colour(newColour)
                    .rootColour(rootColour)
                    .positionToCreateBoard(move)
                    .build();
            builder.add(childNode);
        }
        return builder.build();
    }

    private Optional<TreeNode> isBoardPositionAValidChild(Integer i) {
        for (TreeNode child : getChildren()) {
            ImmutablePosition positionToCreateBoard = child.positionToCreateBoard;
            if (positionToCreateBoard != null) {
                Integer pos = positionToCreateBoard.x() * currentBoard.getBoardSize() + positionToCreateBoard.y();
                if (pos.equals(i)) {
                    return Optional.of(child);
                }
            }
        }
        return Optional.absent();
    }

    public COLOUR getColour() {
        return colour;
    }

    public ImmutablePosition getPositionToCreateBoard() {
        return positionToCreateBoard;
    }

    public void visited() {
        visited = true;
    }

    public TreeNode getParent() {
        return parent;
    }

    public static Double getWinnerValue(COLOUR rootColour, COLOUR actualColour) {
        if (actualColour != null) {
            if (rootColour.equals(actualColour)) {
                return 1.0;
            } else {
                return 0.0;
            }
        } else {
            return 0.0;
        }
    }

    Integer getNumberOfSimulations() {
        return numberOfSimulations;
    }

    public ImmutableList<Double> getPolicyVector() {
        ImmutableList<Integer> canonicalBoard = canonicalBoard();
        ImmutableList.Builder<Double> builder = ImmutableList.builder();
        Double total = 0.0;
        for (int i = 0; i < canonicalBoard.size(); i++) {
            Optional<TreeNode> posValidChild = isBoardPositionAValidChild(i);
            if (posValidChild.isPresent()) {
                Double value = posValidChild.get().numberOfSimulations.doubleValue();
                total += value;
                builder.add(value);
            } else {
                builder.add(0.0);
            }
        }
        ImmutableList<Double> boardOfChildSimulations = builder.build();
        ImmutableList.Builder<Double> boardOfMoveProbabilities = ImmutableList.builder();
        if (total > 0) {
            for (Double value : boardOfChildSimulations) {
                boardOfMoveProbabilities.add(value / total);
            }
        } else {
            boardOfMoveProbabilities.addAll(boardOfChildSimulations.asList());
        }
        return boardOfMoveProbabilities.build();
    }

    Double getNumberOfWins() {
        return numberOfWins;
    }

    public Boolean isVisited() {
        return visited;
    }

    ImmutableList<TreeNode> getChildren() {
        if (children.isEmpty()) {
            children = ImmutableList.copyOf(generateChildren());
        }
        return children;
    }

    private void setTerminalNode() {
        terminalNode = true;
    }

    public Boolean isTerminalNode() {
        return terminalNode;
    }

    public TreeNode selectUCTMove() {
        Random random = new Random();
        ImmutableList<TreeNode> children = getChildren();
        double bestValue = Double.MIN_VALUE;
        TreeNode selected = null;
        for (TreeNode child : children) {
            double epsilon = 1e-6;
            double uctValue = child.numberOfWins / (child.numberOfSimulations + epsilon) +
                    Math.sqrt(Math.log(numberOfSimulations + 1) / (child.numberOfSimulations + epsilon)) +
                    random.nextDouble() * epsilon;
            //System.out.println("uctValue = " + uctValue);
            if (uctValue > bestValue) {
                selected = child;
                bestValue = uctValue;
            }
        }
        if (selected == null) {
            return this;
        }
        return selected;
    }

    double getNNPrediction() {
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        WebTarget target = client.target(UriBuilder.fromUri(
                "http://127.0.0.1:5000").build());

        StringBuilder stringBoard = new StringBuilder();
        ImmutableList<Integer> intBoard = canonicalBoard();
        for (int pos = 0; pos < intBoard.size(); pos++) {
            stringBoard.append(intBoard.get(pos));
            if (pos + 1 != intBoard.size()) {
                stringBoard.append(",");
            }
        }
        // Get JSON for application
        String jsonResponse = target.path("predict")
                .path(currentBoard.getBoardSize().toString())
                .path(stringBoard.toString()).request()
                .accept(MediaType.APPLICATION_JSON).get(String.class);


        return Double.parseDouble(jsonResponse);
    }

    public TreeNode findChildBoardMatch(Board board) {
        if (currentBoard.equals(board)) {
            return this;
        } else if (currentBoard.getCountersPlayed().equals(board.getCountersPlayed() - 1)) {
            for (TreeNode child : getChildren()) {
                if (child.currentBoard.equals(board)) {
                    return child;
                }
            }
        } else if (currentBoard.getCountersPlayed() < board.getCountersPlayed()) {
            System.out.println("recursive board search");
            for (TreeNode child : getChildren()) {
                TreeNode childBoardMatch = child.findChildBoardMatch(board);
                if (childBoardMatch != null) {
                    return childBoardMatch;
                }
            }
        }
        return null;
    }

    public TreeNode selectRandomMove() {
        ImmutableList<TreeNode> children = getChildren();
        if (children.size() > 0) {
            Random random = new Random();
            return this.children.get(random.nextInt(children.size()));
        } else {
            return this;
        }
    }

    Double simulateGame(Boolean useNN) {
        if (useNN) {
            double nnPrediction = getNNPrediction();
            addResult(nnPrediction);
            return nnPrediction;
        } else {
            Double result;
            if (isTerminalNode()) {
                result = getWinnerValue(rootColour, currentBoard.getWinner(false));
            } else {
                result = selectRandomMove().simulateGame(false);
            }
            addResult(result);
            return result;
        }
    }

    Board getCurrentBoard() {
        return currentBoard;
    }

    @Override
    public boolean equals(Object object) {
        try {
            TreeNode node = (TreeNode) object;
            return node.currentBoard.equals(currentBoard)
                    && positionToCreateBoard.equals(node.positionToCreateBoard)
                    && colour.equals(node.colour)
                    && rootColour.equals(node.rootColour);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setRoot() {
        isRoot = true;
    }

    public Boolean getRoot() {
        return isRoot;
    }

    @Override
    public TreeNode clone() {
        //noinspection DuplicatedCode
        try {
            TreeNode newTreeNode;
            ByteArrayInputStream bis;
            ObjectInputStream ois;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.flush();
            byte[] data = bos.toByteArray();
            bis = new ByteArrayInputStream(data);
            ois = new ObjectInputStream(bis);
            newTreeNode = (TreeNode) ois.readObject();
            return newTreeNode;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("ReturnOfThis")
    public static class Builder {
        private Board currentBoard;
        private COLOUR colour;
        private COLOUR rootColour;
        private TreeNode parent;
        private ImmutablePosition positionToCreateBoard;


        public Builder currentBoard(Board currentBoard) {
            this.currentBoard = currentBoard;
            return this;
        }

        public Builder colour(COLOUR colour) {
            this.colour = colour;
            return this;
        }

        public Builder rootColour(COLOUR rootColour) {
            this.rootColour = rootColour;
            return this;
        }

        public Builder parent(TreeNode parent) {
            this.parent = parent;
            return this;
        }

        public Builder positionToCreateBoard(ImmutablePosition positionToCreateBoard) {
            this.positionToCreateBoard = positionToCreateBoard;
            return this;
        }

        public TreeNode build() {
            return new TreeNode(this);
        }
    }

    ImmutableList<Integer> canonicalBoard() {
        ImmutableList<Integer> intBoard = currentBoard.asIntArray();
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

    public void addResult(Double result) {
        numberOfSimulations++;
        numberOfWins += result;
    }
}
