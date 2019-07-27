package application;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CounterTest {

    @Test
    public void FlipTest() {
        Counter counter = new Counter(Counter.COLOUR.BLACK);
        counter.flip();
        assertEquals(counter.getColour(), Counter.COLOUR.WHITE);
        counter.flip();
        assertEquals(counter.getColour(), Counter.COLOUR.BLACK);


    }


}
