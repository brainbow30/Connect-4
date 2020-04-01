package application;

import application.game.COLOUR;
import application.game.Game;
import application.gui.GUI;
import application.mcts.GenerateNNData;
import application.players.ComputerPlayer;
import application.players.Player;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.util.concurrent.TimeUnit;


@SpringBootApplication
class Application {

    private final Game game;
    private final Integer numberOfGames;
    private final Integer boardSize;
    private final Boolean train;
    private final Boolean useGUI;
    private final boolean humanPlayer1;
    private final boolean humanPlayer2;
    private final boolean eval;
    private final Integer evalMode;
    private final Integer evalGames;
    private final Double evalIncrease;
    private final String hostname;

    @Autowired
    public Application(Game game, @Value("${numberOfGames}") Integer numberOfGames, @Value("${board.size}") Integer boardSize,
                       @Value("${nn.train}") Boolean train, @Value("${useGUI}") Boolean useGUI,
                       @Value("${player1.human}") Boolean humanPlayer1, @Value("${player2.human}") Boolean humanPlayer2, @Value("${eval}") Boolean eval, @Value("${evalMode}") Integer evalMode, @Value("${evalGames}") Integer evalGames, @Value("${evalIncrease}") Double evalIncrease, @Value("${hostname}") String hostname) {
        this.game = game;
        this.numberOfGames = numberOfGames;
        this.boardSize = boardSize;
        this.train = train;
        this.useGUI = useGUI;
        this.humanPlayer1 = humanPlayer1;
        this.humanPlayer2 = humanPlayer2;
        this.eval = eval;
        this.evalMode = evalMode;
        this.evalGames = evalGames;
        this.evalIncrease = evalIncrease;
        this.hostname = hostname;
    }


    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(Application.class).headless(false).run(args);
        GUI frame = context.getBean(GUI.class);


    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            if (!eval) {
                Integer player1Wins, draws;
                Integer[] stats = play(numberOfGames);
                player1Wins = stats[0];
                draws = stats[1];
                System.out.println("\n\nFinal Score after " + numberOfGames + " games\n" + player1Wins + ":" + ((numberOfGames - player1Wins) - draws + " with " + draws + " draws"));
            } else {
                ImmutableList<ImmutableList<Double>> evaluationResults;
                String evaluation;
                if (evalMode == 0) {
                    evaluationResults = evalCpuct(numberOfGames);
                    evaluation = "Cpuct";
                } else if (evalMode == 1) {
                    evaluationResults = evalTemp(numberOfGames);
                    evaluation = "Temperature Threshold";
                } else {
                    System.out.println("No associated evaluation mode");
                    evaluationResults = ImmutableList.of();
                    evaluation = "";
                }

                System.out.println("evaluationResults = " + evaluationResults);
                StringBuilder resultString = new StringBuilder();

                for (ImmutableList<Double> entry : evaluationResults) {
                    resultString.append(entry.get(0) + ":" + entry.get(1) + ",");
                }
                resultString.deleteCharAt(resultString.length() - 1);
                ClientConfig config = new ClientConfig();
                Client client = ClientBuilder.newClient(config);

                WebTarget target = client.target(UriBuilder.fromUri(
                        hostname).build());
                String jsonResponse = target.path("plot")
                        .path(resultString.toString()).path(evaluation).request()
                        .accept(MediaType.APPLICATION_JSON).get(String.class);
                System.out.println("jsonResponse = " + jsonResponse);

            }
        };
    }

    private Integer[] play(Integer numberOfGames) {
        Integer player1Wins = 0;
        Integer draws = 0;
        //play locally
        for (int i = 0; i < numberOfGames; i++) {
            game.reset();
            System.out.println("Game Number: " + i);
            Optional<Player> winner = game.play(useGUI);
            if (winner.isPresent()) {
                if (winner.get().getCounterColour().equals(COLOUR.RED)) {
                    player1Wins++;
                }
            } else {
                draws++;
            }
            //rest time before next game
            if (humanPlayer1 || humanPlayer2) {
                try {
                    TimeUnit.MILLISECONDS.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (train) {
                trainNN();
            }
        }
        Integer[] stats = {player1Wins, draws};
        return stats;
    }

    private ImmutableList<ImmutableList<Double>> evalTemp(Integer numberOfGames) {
        ImmutableList<ImmutableList<Double>> results = ImmutableList.of();
        Player player1 = game.getPlayer1();
        Player player2 = game.getPlayer2();

        for (int i = 0; i < numberOfGames; i++) {
            System.out.println("Eval Number: " + i);
            Integer player1Wins, draws;
            Integer[] stats = play(evalGames);
            player1Wins = stats[0];
            draws = stats[1];
            Double player1WinRatio = (player1Wins * 1.0 / (evalGames - draws));
            if (player2 instanceof ComputerPlayer) {
                Double player2WinRatio = 1 - player1WinRatio;
                Double tempThreshold = Double.valueOf(((ComputerPlayer) player2).getTempThreshold());
                ImmutableList<Double> result = ImmutableList.of(tempThreshold, player2WinRatio);
                ImmutableList.Builder<ImmutableList<Double>> builder = ImmutableList.builder();
                results = builder.addAll(results).add(result).build();
                ((ComputerPlayer) player2).setTempThreshold((int) (tempThreshold + evalIncrease));
            } else if (player1 instanceof ComputerPlayer) {
                Double tempThreshold = Double.valueOf(((ComputerPlayer) player1).getTempThreshold());
                ImmutableList<Double> result = ImmutableList.of(tempThreshold, player1WinRatio);
                ImmutableList.Builder<ImmutableList<Double>> builder = ImmutableList.builder();
                results = builder.addAll(results).add(result).build();
                ((ComputerPlayer) player1).setTempThreshold((int) (tempThreshold + evalIncrease));
            }
        }
        return results;
    }

    private ImmutableList<ImmutableList<Double>> evalCpuct(Integer numberOfGames) {
        ImmutableList<ImmutableList<Double>> results = ImmutableList.of();
        Player player1 = game.getPlayer1();
        Player player2 = game.getPlayer2();

        for (int i = 0; i < numberOfGames; i++) {
            System.out.println("Eval Number: " + i);
            Integer player1Wins, draws;
            Integer[] stats = play(evalGames);
            player1Wins = stats[0];
            draws = stats[1];
            Double player1WinRatio = (player1Wins * 1.0 / (evalGames - draws));
            if (player2 instanceof ComputerPlayer) {
                Double player2WinRatio = 1 - player1WinRatio;
                Double cpuct = (((ComputerPlayer) player2).getCpuct());
                ImmutableList<Double> result = ImmutableList.of(cpuct, player2WinRatio);
                ImmutableList.Builder<ImmutableList<Double>> builder = ImmutableList.builder();
                results = builder.addAll(results).add(result).build();
                ((ComputerPlayer) player2).setCpuct(cpuct + evalIncrease);
            } else if (player1 instanceof ComputerPlayer) {
                Double cpuct = Double.valueOf(((ComputerPlayer) player1).getCpuct());
                ImmutableList<Double> result = ImmutableList.of(cpuct, player1WinRatio);
                ImmutableList.Builder<ImmutableList<Double>> builder = ImmutableList.builder();
                results = builder.addAll(results).add(result).build();
                ((ComputerPlayer) player1).setCpuct(cpuct + evalIncrease);
            }
        }
        return results;
    }

    void trainNN() {
        System.out.println("Training Neural Network...");

        Player player1 = game.getPlayer1();
        Player player2 = game.getPlayer2();
        String trainingData = "";
        if (player1 instanceof ComputerPlayer && ((ComputerPlayer) player1).getPreviousNode() != null && ((ComputerPlayer) player1).getPreviousNode().isTerminalNode()) {
            trainingData += GenerateNNData.save(((ComputerPlayer) player1).getPreviousNode()) + ",";
        }
        if (player2 instanceof ComputerPlayer && ((ComputerPlayer) player2).getPreviousNode() != null && ((ComputerPlayer) player2).getPreviousNode().isTerminalNode()) {
            trainingData += GenerateNNData.save(((ComputerPlayer) player2).getPreviousNode());
        } else {
            trainingData = trainingData.substring(0, trainingData.length() - 1);
        }
        trainingData = "[" + trainingData + "]";

        String trainingDataJSON = "{\"data\":\"" + trainingData + "\"}";
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        WebTarget target = client.target(UriBuilder.fromUri(
                "http://127.0.0.1:5000").build());

        // Get JSON for application
        String jsonResponse = target.path("train").path(boardSize.toString()).request().put(Entity.json(trainingDataJSON)).toString();


        System.out.println(jsonResponse);

    }

}
