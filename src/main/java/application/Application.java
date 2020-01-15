package application;

import application.game.COLOUR;
import application.game.Game;
import application.gui.GUI;
import application.players.Player;
import com.google.common.base.Optional;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


@SpringBootApplication
class Application {

    private final Game game;
    private final Integer numberOfGames;
    private final Integer boardSize;
    private final Boolean train;
    private final Boolean useGUI;

    @Autowired
    public Application(Game game, @Value("${numberOfGames}") Integer numberOfGames, @Value("${board.size}") Integer boardSize,
                       @Value("${nn.train}") Boolean train, @Value("${useGUI}") Boolean useGUI) {
        this.game = game;
        this.numberOfGames = numberOfGames;
        this.boardSize = boardSize;
        this.train = train;
        this.useGUI = useGUI;
    }


    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(Application.class).headless(false).run(args);
        GUI frame = context.getBean(GUI.class);


    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {


            Integer player1Wins = 0;
            int draws = 0;
            //play locally
            for (int i = 0; i < numberOfGames; i++) {
                game.reset();

                Optional<Player> winner = game.play(useGUI);
                if (winner.isPresent()) {
                    if (winner.get().getCounterColour().equals(COLOUR.RED)) {
                        player1Wins++;
                    }
                } else {
                    draws++;
                }
                //rest time before next game
                try {
                    TimeUnit.MILLISECONDS.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (train) {
                    trainNN();
                }
            }
            System.out.println("\n\nFinal Score after " + numberOfGames + " games\n" + player1Wins + ":" + ((numberOfGames - player1Wins) - draws + " with " + draws + " draws"));


        };
    }

    //todo remove need to save to txt
    void trainNN() {
        System.out.println("Training Neural Network...");
        try {
            Scanner in = new Scanner(new FileReader("intBoards/training" + boardSize + ".txt"));
            StringBuilder sb = new StringBuilder();
            while (in.hasNext()) {
                sb.append(in.next());
            }
            in.close();
            String trainingData = sb.toString();
            String trainingDataJSON = "{\"data\":\"" + trainingData + "\"}";
            Thread.sleep(1000);
            ClientConfig config = new ClientConfig();
            Client client = ClientBuilder.newClient(config);

            WebTarget target = client.target(UriBuilder.fromUri(
                    "http://127.0.0.1:5000").build());

            // Get JSON for application
            String jsonResponse = target.path("train").path(boardSize.toString()).request().put(Entity.json(trainingDataJSON)).toString();


            System.out.println(jsonResponse);
        } catch (InterruptedException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
