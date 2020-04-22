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


public final class TreeNode {
    private final Board currentBoard;
    private final COLOUR colour;
    private final COLOUR rootColour;
    private final TreeNode parent;
    private final ImmutablePosition positionToCreateBoard;
    private final String hostname;
    private Double numberOfWins = 0.0;
    private Double currentSimulations = 0.0;
    private Double prevSimulations = 0.0;
    private Boolean visited = false;
    private ImmutableList<TreeNode> children;
    private Boolean terminalNode = false;
    private Boolean isRoot = false;
    private ImmutableList<Double> policy;


    private TreeNode(Builder builder) {
        parent = builder.parent;
        currentBoard = builder.currentBoard;
        colour = builder.colour;
        rootColour = builder.rootColour;
        if (builder.positionToCreateBoard == null) {
            setRoot();
        }
        if (Math.pow(currentBoard.getBoardSize(), 2) == currentBoard.getCountersPlayed() || currentBoard.isWinner()) {
            setTerminalNode();
        }
        positionToCreateBoard = builder.positionToCreateBoard;
        children = ImmutableList.of();
        hostname = builder.hostname;
    }

    public static Builder builder() {
        return new Builder();
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
            return 0.0;
        }
    }

    static double getQ(TreeNode node) {
        double q = 0.0;
        if (node.currentSimulations > 0) {
            q = node.numberOfWins / node.currentSimulations;
        }
        return q;
    }

    private ImmutableList<TreeNode> generateChildren() {
        Counter counter = new Counter(colour);
        ImmutableList.Builder<TreeNode> builder = ImmutableList.builder();
        ImmutableList<ImmutablePosition> validMoves = currentBoard.getValidMoves(colour);
        COLOUR newColour = COLOUR.opposite(colour);
        if (!isTerminalNode()) {
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
        }
        return builder.build();
    }

    public Double getPrevSimulations() {
        return prevSimulations;
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

    public ImmutableList<Double> getTrainingPolicy() {
        ImmutableList.Builder<Double> builder = ImmutableList.builder();
        for (int y = 0; y < currentBoard.getBoardSize(); y++) {
            for (int x = 0; x < currentBoard.getBoardSize(); x++) {
                ImmutablePosition position = ImmutablePosition.builder().x(x).y(y).build();
                Boolean contains = false;
                for (TreeNode child : getChildren()) {
                    if (!child.getRoot()) {
                        child.prevSimulations = child.currentSimulations;
                    }
                    //if visited then currentsimulation>1 else >0
                    if (child.getPositionToCreateBoard().equals(position) && (currentSimulations > (0 + visited.compareTo(false)))) {
                        builder.add((child.getPrevSimulations() / (currentSimulations - visited.compareTo(false))));
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

    Double getCurrentSimulations() {
        return currentSimulations;
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
            double bestValue = Double.MAX_VALUE * -1.0;
            TreeNode selected = children.get(random.nextInt(children.size()));
            for (TreeNode child : children) {
                double epsilon = 1e-6;
                Double q = getQ(child);
                //if opponent's turn in game then best move for opponent is worst move for player
                if (child.getRootColour().equals(child.getColour())) {
                    q *= -1;
                }
                double uctValue = q +
                        Math.sqrt(Math.log(currentSimulations + 1) / (child.currentSimulations + epsilon)) +
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

    public TreeNode selectAlphaZeroMove(Double cpuct, Boolean test, Double temp) {
        Random random = new Random();
        ImmutableList<TreeNode> children = getChildren();
        if (children.size() > 0) {

            double bestValue = Double.MAX_VALUE * -1.0;
            TreeNode selected = children.get(random.nextInt(children.size()));
            for (TreeNode child : children) {
                if (child.isTerminalNode() && getWinnerValue(COLOUR.opposite(child.getColour()), child.getCurrentBoard().getWinner()) == 1.0) {
                    return child;
                }
                ImmutablePosition position = child.positionToCreateBoard;
                int integerPosition = position.x() + position.y() * currentBoard.getBoardSize();
                if (policy == null) {
                    getNNPrediction(test);
                }
                Double q = getQ(child);
                //if opponent's turn in game then best move for opponent is worst move for player
                if (child.getRootColour().equals(child.getColour())) {
                    q *= -1;
                }
                double epsilon = 1e-6;
                double uctValue = q +
                        (temp * cpuct * policy.get(integerPosition)) * (Math.sqrt(currentSimulations) / (child.currentSimulations + 1)) +
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
        } catch (ArrayIndexOutOfBoundsException e) {
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
                    child.visited();
                    return child;
                }
            }
        } else if (currentBoard.getCountersPlayed() < board.getCountersPlayed()) {
            System.out.println("recursive board search");
            for (TreeNode child : getChildren()) {
                TreeNode childBoardMatch = child.findChildBoardMatch(board);
                if (childBoardMatch != null) {
                    if (!childBoardMatch.isVisited()) {
                        childBoardMatch.visited();
                    }
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
        prevSimulations = currentSimulations;
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

    public synchronized void addResult(Double result) {
        currentSimulations++;
        numberOfWins += result;
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


}
