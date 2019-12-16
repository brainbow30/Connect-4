package application.gui;

import application.ImmutablePosition;
import application.game.Board;
import application.game.COLOUR;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class BoardGridPanel extends JPanel {

    private static final int GAP = 3;
    private static final Color BG = Color.BLACK;
    private static final int width = 60;
    private static final int height = 60;
    private static final Dimension BTN_PREF_SIZE = new Dimension(width + 5, height + 5);
    private static final BufferedImage whiteCircle = createCircle(Color.WHITE);
    private static final BufferedImage blackCircle = createCircle(Color.BLACK);
    private JButton[][] buttons;
    private Optional<ImmutablePosition> clickedPos;

    public BoardGridPanel(Board board) {
        setup(board);
    }

    private static BufferedImage createCircle(Color color) {
        BufferedImage circleImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = circleImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        int imgWidth = width - 2;
        int imgHeight = height - 2;
        g2.fillOval(0, 0, imgWidth, imgHeight);
        g2.dispose();
        return circleImg;
    }

    private void setup(Board board) {
        setBackground(BG);
        setLayout(new GridLayout(board.getBoardSize(), board.getBoardSize(), GAP, GAP));
        setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
        buttons = createButtons(board);
    }

    private JButton[][] createButtons(Board board) {
        JButton[][] buttons = new JButton[board.getBoardSize()][board.getBoardSize()];
        Color boardBG = new Color(0, 0, 102);
        for (int i = 0; i < board.getBoardSize(); i++) {
            for (int j = 0; j < board.getBoardSize(); j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setPreferredSize(BTN_PREF_SIZE);
                buttons[i][j].setBackground(boardBG);
                final int x = j;
                final int y = i;
                buttons[i][j].addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        clickedPos = Optional.of(ImmutablePosition.builder().x(x).y(y).build());
                    }
                });
                ImmutablePosition pos = ImmutablePosition.builder().x(j).y(i).build();
                if (board.getCounter(pos).isPresent()) {
                    if (board.getCounter(pos).get().getColour().equals(COLOUR.WHITE)) {
                        buttons[i][j].setIcon(new ImageIcon(whiteCircle));
                    } else if (board.getCounter(pos).get().getColour().equals(COLOUR.BLACK)) {
                        buttons[i][j].setIcon(new ImageIcon(blackCircle));
                    }
                }
                add(buttons[i][j]);
            }
        }
        return buttons;
    }

    public void updateBoard(Board board) {
        clickedPos = Optional.absent();
        removeAll();
        buttons = createButtons(board);
        repaint();
        revalidate();
    }

    Optional<ImmutablePosition> getClickedPos() {
        Optional<ImmutablePosition> returnValue = clickedPos;
        clickedPos = Optional.absent();
        return returnValue;
    }

    void highlight(ImmutableList<ImmutablePosition> positions) {
        for (ImmutablePosition pos : positions) {
            buttons[pos.y()][pos.x()].setBorder(new LineBorder(Color.yellow));
        }
    }
}
