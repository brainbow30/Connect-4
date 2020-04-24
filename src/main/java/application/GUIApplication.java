package application;


import application.game.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


@SpringBootApplication
public class GUIApplication {
    private static ConfigurableApplicationContext context;
    private JPanel contentPane;
    private JLabel titleLbl;
    private JButton homeBtn;
    private JLabel boardSizeLbl;
    private JTextField boardSizeTxt;
    private JLabel numGamesLbl;
    private JTextField numGamesTxt;
    private JPanel playerBtns1;
    private JPanel playerBtns2;
    private JPanel extraSettings1;
    private JPanel extraSettings2;
    private Properties properties = new Properties();
    private String fileLocation = "/application.properties";
    private boolean play = false;
    private JFrame frame;


    @Autowired
    public GUIApplication(@Value("${settingsGUI}") Boolean settingsGUI, Application application) {
        if (settingsGUI) {
            frame = new JFrame("Connect-4");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            URL url = getClass().getResource("/icon.jpg");
            ImageIcon img = new ImageIcon(url);
            frame.setIconImage(img.getImage());

            contentPane = new JPanel();

            load();
            setup();
            frame.getContentPane().add(contentPane, BorderLayout.CENTER);
            frame.pack();
            frame.setLocationByPlatform(true);
            frame.setVisible(true);
        } else {
            application.run();
        }
    }

    public static void main(String[] args) {
        context = new SpringApplicationBuilder(GUIApplication.class).headless(false).run(args);
        GUIApplication frame = context.getBean(GUIApplication.class);


    }

    void setup() {

        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 50, 10, 50);
        c.anchor = GridBagConstraints.CENTER;

        c.weightx = 1.0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 1;
        c.gridy = 0;
        titleLbl = new JLabel("Settings", JLabel.CENTER);
        titleLbl.setBorder(new EmptyBorder(10, 0, 10, 0));//top,left,bottom,right
        titleLbl.setFont(new Font(null, Font.PLAIN, 20));
        contentPane.add(titleLbl, c);

        c.gridx = 3;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        homeBtn = new JButton("Play");
        homeBtn.addActionListener(e -> save());
        homeBtn.setPreferredSize(new Dimension(150, 50));
        contentPane.add(homeBtn, c);

        //--------------------row 1-------------------------------------------

        c.insets = new Insets(10, 10, 0, 10);
        c.gridx = 1;
        c.gridy = 2;
        boardSizeLbl = new JLabel("Board Size:", JLabel.LEFT);
        boardSizeLbl.setBorder(new EmptyBorder(10, 0, 10, 0));//top,left,bottom,right
        boardSizeLbl.setFont(new Font(null, Font.PLAIN, 16));
        contentPane.add(boardSizeLbl, c);
        c.insets = new Insets(0, 10, 10, 10);
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 1;
        c.gridheight = 1;
        boardSizeTxt = new JTextField(properties.getProperty("board.size").toString());
        boardSizeTxt.setPreferredSize(new Dimension(100, 30));
        contentPane.add(boardSizeTxt, c);

        c.insets = new Insets(10, 10, 0, 10);
        c.gridx = 2;
        c.gridy = 2;
        numGamesLbl = new JLabel("Number of Games:", JLabel.LEFT);
        numGamesLbl.setBorder(new EmptyBorder(10, 0, 10, 0));//top,left,bottom,right
        numGamesLbl.setFont(new Font(null, Font.PLAIN, 16));
        contentPane.add(numGamesLbl, c);
        c.insets = new Insets(0, 10, 10, 10);
        c.gridx = 2;
        c.gridy = 3;
        c.gridwidth = 1;
        c.gridheight = 1;
        numGamesTxt = new JTextField(properties.getProperty("numberOfGames").toString());
        numGamesTxt.setPreferredSize(new Dimension(100, 30));
        contentPane.add(numGamesTxt, c);


        //-----------------------------row 2-----------------------------------------------
        c.insets = new Insets(10, 10, 0, 10);
        c.gridx = 1;
        c.gridy = 4;
        boardSizeLbl = new JLabel("Select Player 1 Type:", JLabel.LEFT);
        boardSizeLbl.setBorder(new EmptyBorder(10, 0, 10, 0));//top,left,bottom,right
        boardSizeLbl.setFont(new Font(null, Font.PLAIN, 16));
        contentPane.add(boardSizeLbl, c);
        c.insets = new Insets(0, 10, 10, 10);
        c.gridx = 1;
        c.gridy = 5;
        c.gridwidth = 1;
        c.gridheight = 1;
        playerBtns1 = playerBtn(1);
        contentPane.add(playerBtns1, c);

        c.insets = new Insets(10, 10, 0, 10);
        c.gridx = 2;
        c.gridy = 4;
        numGamesLbl = new JLabel("Player Settings:", JLabel.LEFT);
        numGamesLbl.setBorder(new EmptyBorder(10, 0, 10, 0));//top,left,bottom,right
        numGamesLbl.setFont(new Font(null, Font.PLAIN, 16));
        contentPane.add(numGamesLbl, c);
        c.insets = new Insets(0, 10, 10, 10);
        c.gridx = 2;
        c.gridy = 5;
        c.gridwidth = 2;
        c.gridheight = 1;
        extraSettings1 = extraPlayerSettings(1);
        contentPane.add(extraSettings1, c);


        //-----------------------row 3-----------------------------------
        c.insets = new Insets(10, 10, 0, 10);
        c.gridx = 1;
        c.gridy = 6;
        c.gridwidth = 1;
        boardSizeLbl = new JLabel("Select Player 2 Type:", JLabel.LEFT);
        boardSizeLbl.setBorder(new EmptyBorder(10, 0, 10, 0));//top,left,bottom,right
        boardSizeLbl.setFont(new Font(null, Font.PLAIN, 16));
        contentPane.add(boardSizeLbl, c);
        c.insets = new Insets(0, 10, 10, 10);
        c.gridx = 1;
        c.gridy = 7;
        c.gridwidth = 1;
        c.gridheight = 1;
        playerBtns2 = playerBtn(2);
        contentPane.add(playerBtns2, c);

        c.insets = new Insets(10, 10, 10, 10);
        c.gridx = 2;
        c.gridy = 6;
        numGamesLbl = new JLabel("Player Settings:", JLabel.LEFT);
        numGamesLbl.setBorder(new EmptyBorder(10, 0, 0, 0));//top,left,bottom,right
        numGamesLbl.setFont(new Font(null, Font.PLAIN, 16));
        contentPane.add(numGamesLbl, c);
        c.insets = new Insets(0, 0, 0, 0);
        c.gridx = 2;
        c.gridy = 7;
        c.gridwidth = 2;
        c.gridheight = 1;
        extraSettings2 = extraPlayerSettings(2);
        contentPane.add(extraSettings2, c);
        contentPane.repaint();
        contentPane.revalidate();

    }

    private JPanel playerBtn(Integer playerNum) {
        JRadioButton humanBtn = new JRadioButton("Human");
        humanBtn.setActionCommand("Human");


        JRadioButton mctsBtn = new JRadioButton("MCTS");
        mctsBtn.setActionCommand("MCTS");

        JRadioButton alphazeroBtn = new JRadioButton("AlphaZero");
        alphazeroBtn.setActionCommand("AlphaZero");


        //Group the radio buttons.
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(humanBtn);
        buttonGroup.add(mctsBtn);
        buttonGroup.add(alphazeroBtn);


        JPanel radioPanel = new JPanel(new GridLayout(0, 1));
        radioPanel.add(humanBtn);
        radioPanel.add(mctsBtn);
        radioPanel.add(alphazeroBtn);
        int moveFunction = 0;
        switch (playerNum) {
            case 1:
                if (properties.getProperty("player1.human").equals("true")) {
                    moveFunction = 0;
                } else {
                    moveFunction = Integer.parseInt(properties.getProperty("computer1.moveFunction"));

                }
                break;
            case 2:
                if (properties.getProperty("player2.human").equals("true")) {
                    moveFunction = 0;
                } else {
                    moveFunction = Integer.parseInt(properties.getProperty("computer2.moveFunction"));

                }
                break;
        }
        switch (moveFunction) {
            case 0:
                humanBtn.setSelected(true);
                break;
            case 2:
                mctsBtn.setSelected(true);
                break;
            case 3:
            case 4:
                alphazeroBtn.setSelected(true);
                break;
        }


        //Register a listener for the radio buttons.
        humanBtn.addActionListener(e -> {
            switch (playerNum) {
                case 1:
                    properties.setProperty("player1.human", "true");

                    break;
                case 2:
                    properties.setProperty("player2.human", "true");

                    break;
            }
            contentPane.removeAll();
            setup();


        });
        mctsBtn.addActionListener(e -> {
            switch (playerNum) {
                case 1:
                    properties.setProperty("player1.human", "false");
                    properties.setProperty("computer1.moveFunction", "2");
                    break;
                case 2:
                    properties.setProperty("player2.human", "false");
                    properties.setProperty("computer2.moveFunction", "2");
                    break;
            }
            contentPane.removeAll();
            setup();
        });
        alphazeroBtn.addActionListener(e -> {
            switch (playerNum) {
                case 1:
                    properties.setProperty("player1.human", "false");
                    properties.setProperty("computer1.moveFunction", "3");
                    break;
                case 2:
                    properties.setProperty("player2.human", "false");
                    properties.setProperty("computer2.moveFunction", "3");
                    break;
            }
            contentPane.removeAll();
            setup();
        });


        return radioPanel;
    }

    private JPanel extraPlayerSettings(Integer playerNum) {
        JPanel extraSettingsPanel = new JPanel(new GridLayout(4, 2));
        int moveFunction = 0;
        switch (playerNum) {
            case 1:
                if (properties.getProperty("player1.human").equals("true")) {
                    moveFunction = 0;
                } else {
                    moveFunction = Integer.parseInt(properties.getProperty("computer1.moveFunction"));

                }
                break;
            case 2:
                if (properties.getProperty("player2.human").equals("true")) {
                    moveFunction = 0;
                } else {
                    moveFunction = Integer.parseInt(properties.getProperty("computer2.moveFunction"));

                }
                break;
        }
        if (moveFunction >= 2) {
            JLabel waitTimeLbl = new JLabel("Wait Time:", JLabel.LEFT);
            waitTimeLbl.setBorder(new EmptyBorder(10, 0, 10, 0));//top,left,bottom,right
            waitTimeLbl.setFont(new Font(null, Font.PLAIN, 12));
            extraSettingsPanel.add(waitTimeLbl);


            JTextField waitTimeTxt = new JTextField(properties.getProperty("mcts.waitTime" + playerNum.toString()));
            waitTimeTxt.setName("mcts.waitTime" + playerNum.toString());
            waitTimeTxt.setPreferredSize(new Dimension(100, 20));
            extraSettingsPanel.add(waitTimeTxt);
        } else {
            JLabel noneLbl = new JLabel("No Extra Settings", JLabel.LEFT);
            noneLbl.setBorder(new EmptyBorder(10, 0, 10, 0));//top,left,bottom,right
            noneLbl.setFont(new Font(null, Font.PLAIN, 12));
            extraSettingsPanel.add(noneLbl);
        }
        if (moveFunction >= 3) {
            JLabel cpuctLbl = new JLabel("Cpuct:", JLabel.LEFT);
            cpuctLbl.setBorder(new EmptyBorder(10, 0, 0, 0));//top,left,bottom,right
            cpuctLbl.setFont(new Font(null, Font.PLAIN, 12));
            extraSettingsPanel.add(cpuctLbl);

            JTextField cpuctTxt = new JTextField(properties.getProperty("mcts.cpuct" + playerNum.toString()));
            cpuctTxt.setName("mcts.cpuct" + playerNum.toString());
            cpuctTxt.setPreferredSize(new Dimension(100, 20));
            extraSettingsPanel.add(cpuctTxt);


            JLabel tempThresLbl = new JLabel("Temperature Threshold:", JLabel.LEFT);
            tempThresLbl.setBorder(new EmptyBorder(10, 0, 0, 0));//top,left,bottom,right
            tempThresLbl.setFont(new Font(null, Font.PLAIN, 12));
            extraSettingsPanel.add(tempThresLbl);

            JTextField tempThresTxt = new JTextField(properties.getProperty("mcts.tempThreshold" + playerNum.toString()));
            tempThresTxt.setPreferredSize(new Dimension(100, 20));
            tempThresTxt.setName("mcts.tempThreshold" + playerNum.toString());
            extraSettingsPanel.add(tempThresTxt);

            JLabel testModelLbl = new JLabel("Use Test Model:", JLabel.LEFT);
            testModelLbl.setBorder(new EmptyBorder(10, 0, 0, 0));//top,left,bottom,right
            testModelLbl.setFont(new Font(null, Font.PLAIN, 12));
            extraSettingsPanel.add(testModelLbl);


            boolean testModel = false;
            if (moveFunction >= 4) {
                testModel = true;
            }
            JCheckBox testModelChk = new JCheckBox("Test Model:", testModel);
            testModelChk.setBounds(100, 100, 50, 30);
            extraSettingsPanel.add(testModelChk);
            contentPane.revalidate();
            contentPane.repaint();


        }


        return extraSettingsPanel;
    }

    private boolean save() {
            properties.setProperty("board.size", boardSizeTxt.getText());
            properties.setProperty("numberOfGames", numGamesTxt.getText());
            Component[] components1 = extraSettings1.getComponents();
            Component[] components2 = extraSettings2.getComponents();
            for (Component component : components1) {
                if (component.getClass().equals(JTextField.class)) {
                    properties.setProperty(((JTextField) component).getName(), ((JTextField) component).getText());
                } else if (component.getClass().equals(JCheckBox.class)) {
                    if (((JCheckBox) component).isSelected()) {
                        properties.setProperty("computer1.moveFunction", "4");
                    }
                }

            }
            for (Component component : components2) {
                if (component.getClass().equals(JTextField.class)) {
                    properties.setProperty(((JTextField) component).getName(), ((JTextField) component).getText());
                } else if (component.getClass().equals(JCheckBox.class)) {
                    if (((JCheckBox) component).isSelected()) {
                        properties.setProperty("computer2.moveFunction", "4");
                    }
                }

            }
            play = true;
            return true;


    }

    private boolean load() {
        try {
            properties.load((GUIApplication.class.getResourceAsStream(fileLocation)));
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            while (!play) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Game game = new Game(properties);
            Application application = new Application(game, properties);
            frame.setVisible(false);
            application.run();
        };
    }


}


