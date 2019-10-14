package application.gui;


import application.game.Board;
import application.game.COLOUR;
import com.google.common.base.Optional;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

@Component
public class GUI {

    private BoardGridPanel boardGridPanel;
    private JFrame frame;
    private JPanel mainPanel;
    private JLabel currentPlayer;
    private JLabel currentScore;

    public GUI(Board board) {
        frame = new JFrame("Othello");
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;

        c.weightx = 1.0;
        c.gridwidth = 1;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 0;
        currentPlayer = new JLabel("White's Turn", JLabel.CENTER);
        currentPlayer.setBorder(new EmptyBorder(10, 0, 10, 0));//top,left,bottom,right
        currentPlayer.setFont(new Font(null, Font.PLAIN, 20));
        mainPanel.add(currentPlayer, c);

        currentScore = new JLabel("Score: 0-0", JLabel.CENTER);
        currentScore.setBorder(new EmptyBorder(10, 0, 10, 0));//top,left,bottom,right
        currentScore.setFont(new Font(null, Font.PLAIN, 20));
        c.gridx = 3;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridwidth = 1;
        mainPanel.add(currentScore, c);


        boardGridPanel = new BoardGridPanel(board);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 4;
        c.gridheight = 4;
        mainPanel.add(boardGridPanel, c);

        setup();
    }

    private void setup() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(false);
    }

    public void show() {
        frame.setVisible(true);
    }

    public void hide() {
        frame.setVisible(false);
    }

    public void updateBoard(Board board, COLOUR colour) {
        if (colour.equals(COLOUR.WHITE)) {
            currentPlayer.setText("White's Turn");
        } else if (colour.equals(COLOUR.BLACK)) {
            currentPlayer.setText("Black's Turn");
        }
        currentScore.setText("Score: " + board.getNumberOfWhiteCounters() + "-" + (board.getCountersPlayed() - board.getNumberOfWhiteCounters()));
        boardGridPanel.updateBoard(board);
    }

    public void setWinnerText(Optional<COLOUR> colour) {
        if (colour.isPresent()) {
            if (colour.get().equals(COLOUR.WHITE)) {
                currentPlayer.setText("White Wins");
            } else if (colour.get().equals(COLOUR.BLACK)) {
                currentPlayer.setText("Black Wins");
            }
        } else {
            currentPlayer.setText("Draw");
        }
    }

}
