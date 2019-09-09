package application;

import application.game.COLOUR;
import application.game.Game;
import application.players.Player;
import com.google.common.base.Optional;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.util.Properties;
import java.util.UUID;

@SpringBootApplication
class Application {

    private final Game game;
    private final Boolean localGame;
    private final Integer numberOfGames;
    private final String nnPath;
    private final Integer boardSize;

    @Autowired
    public Application(Game game, @Value("${localGame}") Boolean localGame, @Value("${numberOfGames}") Integer numberOfGames, @Value("${nn.path}") String nnPath, @Value("${board.size}") Integer boardSize) {
        this.game = game;
        this.localGame = localGame;
        this.numberOfGames = numberOfGames;
        this.nnPath = nnPath;
        this.boardSize = boardSize;
    }


    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);

        Properties properties = new Properties();
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
                Integer draws = 0;
                //play locally
                for (int i = 0; i < numberOfGames; i++) {
                    System.out.println("Local Game");
                    game.reset();

                    Optional<Player> winner = game.play();
                    if (winner.isPresent()) {
                        if (winner.get().getCounterColour().equals(COLOUR.WHITE)) {
                            player1Wins++;
                        }
                    } else {
                        draws++;
                    }
                    trainNN();
                }
                System.out.println("\n\nFinal Score after " + numberOfGames + " games\n" + player1Wins + ":" + ((numberOfGames - player1Wins) - draws + " with " + draws + " draws"));


                //play over kafka
            } else {
                System.out.println("Kafka Game");
                game.playKafka();
            }

        };
    }

    private void trainNN() {
        System.out.println("Training Neural Network...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        WebTarget target = client.target(UriBuilder.fromUri(
                "http://127.0.0.1:5000").build());

        // Get JSON for application
        String jsonResponse = target.path("train").path(boardSize.toString()).request()
                .accept(MediaType.APPLICATION_JSON).get(String.class);

        System.out.println(jsonResponse);
    }

}
