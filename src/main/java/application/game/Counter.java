package application.game;


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
        colour = COLOUR.RED;
    }


    @Bean
    public COLOUR getColour() {
        return colour;
    }
}
