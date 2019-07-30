package application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Properties;
import java.util.UUID;

@SpringBootApplication
class Application {
    @Autowired
    private Game game;
    @Value("${localGame}")
    Boolean localGame;

    @Value("${numberOfGames}")
    Integer numberOfGames;



    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);

        Properties properties = new Properties();
        properties.put("board.size", 4);
        properties.put("player1.human", true);
        properties.put("player2.human", true);
        properties.put("player1.topic", UUID.randomUUID());
        properties.put("player2.topic", UUID.randomUUID());
        application.setDefaultProperties(properties);

        application.run(args);

    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            if (localGame) {
                Integer player1Wins = 0;

                //play locally
                for (int i = 0; i < numberOfGames; i++) {
                    System.out.println("Local Game");
                    game.reset();
                    Player winner = game.play();
                    if (winner.getCounterColour().equals(Counter.COLOUR.WHITE)) {
                        player1Wins++;
                    }
                }
                System.out.println("\n\nFinal Score after " + numberOfGames + " games\n" + player1Wins + ":" + (numberOfGames - player1Wins));


                //play over kafka
            } else {
                System.out.println("Kafka Game");
                game.playKafka();
            }

        };
    }
}
