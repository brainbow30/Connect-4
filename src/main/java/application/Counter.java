package application;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class Counter implements Serializable {

    enum COLOUR {
        BLACK,
        WHITE
    }

    private COLOUR colour;

    public Counter(COLOUR colour) {
        this.colour = colour;
    }

    @Autowired
    public Counter() {
        this.colour = COLOUR.WHITE;
    }

    public void flip() {
        if (this.colour.equals(COLOUR.WHITE)) {
            this.colour = COLOUR.BLACK;
        } else {
            this.colour = COLOUR.WHITE;
        }
    }

    @Bean
    public COLOUR getColour() {
        return colour;
    }
}
