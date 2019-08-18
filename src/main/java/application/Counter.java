package application;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class Counter implements Serializable {

    private COLOUR colour;

    public Counter(COLOUR colour) {
        this.colour = colour;
    }

    @Autowired
    public Counter() {
        this.colour = COLOUR.WHITE;
    }

    public void flip() {
        this.colour = COLOUR.opposite(colour);
    }

    @Bean
    public COLOUR getColour() {
        return colour;
    }
}
