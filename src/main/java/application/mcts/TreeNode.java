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
    private Double numberOfSimulations = 0.0;
    private Boolean visited = false;
    private ImmutableList<TreeNode> children;
    private Boolean terminalNode = false;
    private final ImmutablePosition positionToCreateBoard;
    private Boolean isRoot = false;
    private final String hostname;
    private ImmutableList<Double> policy;


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
        hostname = builder.hostname;
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
                setTerminalNode();
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
                    .hostname(hostname)
                    .build();
            builder.add(childNode);
        }
        return builder.build();
    }

    private static Double getWinnerValue(COLOUR rootColour, Optional<COLOUR> actualColour) {
        if (actualColour.isPresent()) {
            //win
            if (rootColour.equals(actualColour.get())) {
                return 1.0;
                //loss
            } else {
                return -1.0;
            }
            //draw
        } else {
            return -1.0;
        }
    }


    public COLOUR getColour() {
        return colour;
    }

    public COLOUR getRootColour() {
        return rootColour;
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

    ImmutableList<Double> getTrainingPolicy() {
        ImmutableList.Builder<Double> builder = ImmutableList.builder();
        for (int y = 0; y < currentBoard.getBoardSize(); y++) {
            for (int x = 0; x < currentBoard.getBoardSize(); x++) {
                ImmutablePosition position = ImmutablePosition.builder().x(x).y(y).build();
                Boolean contains = false;
                for (TreeNode child : getChildren()) {
                    if (child.getPositionToCreateBoard().equals(position) && child.getNumberOfSimulations() > 0) {
                        //todo find good policy values
                        builder.add((child.getNumberOfSimulations() / getNumberOfSimulations()));
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    builder.add(0.0);
                }
            }
        }
        return builder.build();
    }

    Double getNumberOfSimulations() {
        return numberOfSimulations;
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
        if (children.size() > 0) {
            double bestValue = Double.MIN_VALUE;
            TreeNode selected = children.get(random.nextInt(children.size()));
            for (TreeNode child : children) {
                double epsilon = 1e-6;
                double uctValue = child.numberOfWins / (child.numberOfSimulations + epsilon) +
                        Math.sqrt(Math.log(numberOfSimulations + 1) / (child.numberOfSimulations + epsilon)) +
                        random.nextDouble() * epsilon;
                if (uctValue > bestValue) {
                    selected = child;
                    bestValue = uctValue;
                }
            }
            return selected;
        } else {
            return this;
        }
    }

    public TreeNode selectAlphaZeroMove(Double cpuct, Boolean test) {
        Random random = new Random();
        ImmutableList<TreeNode> children = getChildren();
        double bestValue = Double.MIN_VALUE;
        TreeNode selected = children.get(random.nextInt(children.size()));
        for (TreeNode child : children) {
            ImmutablePosition position = child.positionToCreateBoard;
            int integerPosition = position.x() + position.y() * currentBoard.getBoardSize();
            if (policy == null) {
                getNNPrediction(test);
            }
            double uctValue = child.numberOfWins + (cpuct * policy.get(integerPosition)
                    * (Math.sqrt(numberOfSimulations) / (1 + child.numberOfSimulations)));
            if (uctValue > bestValue) {
                selected = child;
                bestValue = uctValue;
            }
        }
        return selected;
    }

    double getNNPrediction(Boolean test) {
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        WebTarget target = client.target(UriBuilder.fromUri(
                hostname).build());

        StringBuilder stringBoard = new StringBuilder();
        ImmutableList<Integer> intBoard = GenerateNNData.canonicalBoard(this);
        for (int pos = 0; pos < intBoard.size(); pos++) {
            stringBoard.append(intBoard.get(pos));
            if (pos + 1 != intBoard.size()) {
                stringBoard.append(",");
            }
        }
        // Get JSON for application
        String path;
        if (test) {
            path = "testpredict";
        } else {
            path = "predict";
        }
        String jsonResponse = target.path(path)
                .path(currentBoard.getBoardSize().toString())
                .path(stringBoard.toString()).request()
                .accept(MediaType.APPLICATION_JSON).get(String.class);
        try {
            String[] response = jsonResponse.split(":");
            Double v = Double.parseDouble(response[1]);
            String[] stringPolicy = response[0].split(",");
            ImmutableList.Builder<Double> builder = ImmutableList.builder();
            for (String i : stringPolicy
            ) {
                builder.add(Double.parseDouble(i));
            }
            policy = builder.build();
            return v;
        } catch (NumberFormatException e) {
            System.out.println("error");
            System.out.println("jsonResponse = " + jsonResponse);
            e.printStackTrace();
            return 0.0;
        }
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

    Double simulateGame(Integer nnFunction) {
        Double result;
        if (isTerminalNode()) {
            result = getWinnerValue(rootColour, currentBoard.getWinner());
        } else {
            if (nnFunction.equals(1)) {
                result = getNNPrediction(false);
            } else if (nnFunction.equals(2)) {
                result = getNNPrediction(true);
            } else {
                result = selectUCTMove().simulateGame(0);
            }

        }
        addResult(result);
        return result;
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
        private String hostname;


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

        public Builder hostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        public TreeNode build() {
            return new TreeNode(this);
        }
    }



    public void addResult(Double result) {
        numberOfSimulations++;
        numberOfWins += result;
    }


}
