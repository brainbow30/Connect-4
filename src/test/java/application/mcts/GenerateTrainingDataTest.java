package application.mcts;

import application.game.Board;
import application.game.COLOUR;
import application.game.verifiers.Connect4Verifier;
import com.google.common.collect.ImmutableList;

import java.io.*;

import static org.junit.Assert.assertEquals;


public class GenerateTrainingDataTest {
    private Board board;
    private TreeNode treeNode;

    private GenerateTrainingData generateTrainingData;

    public void setup() {
        generateTrainingData = new GenerateTrainingData("testWrite.txt");
        board = new Board(4, new Connect4Verifier());
        treeNode = TreeNode.builder().colour(COLOUR.WHITE).currentBoard(board).positionToCreateBoard(null).parent(null).rootColour(COLOUR.WHITE).hostname("127.0.0.1:5000").build();
    }

    public void writeSingleBoardTest() {
        File file = new File("intBoards/testWrite.txt");
        file.delete();
        try {
            ImmutableList.Builder<Double> builder = ImmutableList.builder();
            builder.add(0.0);
            generateTrainingData.write(board.asIntArray(), builder.build(), 1);
            String expected = "[[0,0,0,0,0,1,-1,0,0,-1,1,0,0,0,0,0],[0.0],1]";
            assertEquals(expected, readFile().get(0));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        generateTrainingData.open();
        generateTrainingData.save(treeNode);
        generateTrainingData.close();
    }

    private ImmutableList<String> readFile() throws IOException {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        File file = new File("intBoards/" + "testWrite.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            builder.add(line);
        }
        return builder.build();
    }


}




