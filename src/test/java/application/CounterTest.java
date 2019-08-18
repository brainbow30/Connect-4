package application;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CounterTest {

    @Test
    public void FlipTest() {
        Counter counter = new Counter(COLOUR.BLACK);
        counter.flip();
        assertEquals(counter.getColour(), COLOUR.WHITE);
        counter.flip();
        assertEquals(counter.getColour(), COLOUR.BLACK);


    }


}
