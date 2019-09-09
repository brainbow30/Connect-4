package application.mcts;

import application.game.Board;
import application.game.Verifier;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class GenerateTrainingDataTest {
    private Board board;

    private GenerateTrainingData generateTrainingData;
    @Before
    public void setup() {
        generateTrainingData = new GenerateTrainingData("testWrite.txt");
        board = new Board(4, new Verifier(), 0.0, 0.0, 0.0);
    }

    @Test
    public void writeSingleBoardTest() {
        File file = new File("intBoards/testWrite.txt");
        file.delete();
        try {
            generateTrainingData.write(board.asIntArray(), ImmutableList.of(), null);
            String expected = "[0,0,0,0,0,1,-1,0,0,-1,1,0,0,0,0,0]";
            assertEquals(expected, readFile("testWrite.txt").get(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ImmutableList<String> readFile(String filename) throws IOException {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        File file = new File("intBoards/" + filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            builder.add(line);
        }
        return builder.build();
    }


}




