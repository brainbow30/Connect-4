package application.mcts;


import application.game.Board;
import application.game.COLOUR;
import com.google.common.base.Stopwatch;

import java.util.concurrent.*;


public class MonteCarloTreeSearch {

    private final TreeNode root;
    private final Integer waitTime;
    private final Integer nnFunction;
    private final Double cpuct;


    public MonteCarloTreeSearch(Board board, COLOUR colour, Integer waitTime, Integer nnFunction, String hostname, Double cpuct) {
        root = TreeNode.builder()
                .parent(null)
                .currentBoard(board)
                .colour(colour)
                .rootColour(colour)
                .positionToCreateBoard(null)
                .hostname(hostname)
                .build();
        root.visited();
        this.waitTime = waitTime;
        this.nnFunction = nnFunction;
        this.cpuct = cpuct;

        //initialize policy vector
        if (this.nnFunction.equals(1)) {
            root.getNNPrediction(false);
        } else if (this.nnFunction.equals(2)) {
            root.getNNPrediction(true);
        }
    }

    public MonteCarloTreeSearch(TreeNode node, Integer waitTime, Integer nnFunction, Double cpuct) {
        root = node;
        root.visited();
        root.setRoot();
        this.waitTime = waitTime;
        this.nnFunction = nnFunction;
        this.cpuct = cpuct;
    }

    public TreeNode run() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        //todo if all nodes visited stop
        while (stopwatch.elapsed(TimeUnit.MILLISECONDS) < waitTime) {
            final ExecutorService service = Executors.newSingleThreadExecutor();

            try {
                final Future<Object> f = service.submit(() -> {
                    TreeNode selectedNode = root.selectRandomMove();
                    while (selectedNode.isVisited() && !selectedNode.isTerminalNode()) {
                        selectedNode = selectedNode.selectRandomMove();
                    }

                    Double result = selectedNode.simulateGame(nnFunction);
                    propagateResult(selectedNode, result);
                    selectedNode.visited();
                    return true;
                });

                f.get(waitTime - stopwatch.elapsed(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
            } catch (final TimeoutException e) {
                System.err.println("Interupt");
            } catch (final Exception e) {
                throw new RuntimeException(e);
            } finally {
                service.shutdown();
            }
        }
        TreeNode selectMove;
        if (nnFunction.equals(1)) {
            selectMove = root.selectAlphaZeroMove(cpuct, false);
        } else if (nnFunction.equals(2)) {
            selectMove = root.selectAlphaZeroMove(cpuct, true);
        } else {
            selectMove = root.selectUCTMove();
        }
        System.out.println("selectMove value= " + selectMove.getNumberOfWins());
        System.out.println("selectMove sims = " + selectMove.getNumberOfSimulations());
        return selectMove;
    }

    private void propagateResult(TreeNode node, Double result) {
        node = node.getParent();
        while (node.getParent() != null) {
            node.addResult(result);
            node = node.getParent();
        }
        node.addResult(result);

    }

}
