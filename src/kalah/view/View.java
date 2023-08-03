package kalah.view;

import kalah.model.Board;
import kalah.model.Player;
import kalah.model.IllegalMoveException;
import kalah.model.GameBoard;

import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Toolkit;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.util.Stack;
import java.awt.Dimension;

/**
 * Main window of the game.
 */
public class View extends JFrame {

    /**
     * Default level in the game.
     */
    public static final int DEFAULT_LEVEL = 3;

    /**
     * Default {@link Player} to start the game.
     */
    public static final Player DEF_STARTER = Player.HUMAN;
    @Serial
    private static final long serialVersionUID = 278130078114038271L;
    private final static int MAX_LEVEL = 10;
    private final static int MAX_PITS = 12;
    private final static int MAX_SEEDS = 12;
    private final GridPanel gridPanel;
    private Board game;
    private int level = DEFAULT_LEVEL;
    private JButton newGameBtn;
    private JButton switchBtn;
    private JButton undoBtn;
    private JButton quitBtn;
    private JComboBox<Integer> pitsPerPlayer;
    private JComboBox<Integer> seedsPerPit;
    private JComboBox<Integer> levelsCombo;
    private SlotLabel fromLabel;
    private SlotLabel toLabel;
    private int clickedPit;

    /**
     * Creates main game window with its components.
     *
     * @param game The game to be read.
     */
    public View(Board game) {
        this.game = game;
        setTitle("Kalah");
        Container pane = getContentPane();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pane.setLayout(new BorderLayout());
        gridPanel = new GridPanel(game.getPitsPerPlayer(),
                game.getSeedsPerPit());
        pane.add(gridPanel);
        createControlRow();
        setPreferredSize(new Dimension(1000, 400));
        setMinimumSize(new Dimension(650, 300));
        revalidate();
        pack();
        setFocusable(true);

    }

    private Integer[] fillArray(int num) {
        Integer[] arr = new Integer[num];
        for (int i = 0; i < num; i++) {
            arr[i] = i + 1;
        }
        return arr;
    }

    private void createControlRow() {

        JPanel controlRow = new JPanel();
        JLabel level = new JLabel("l:");
        JLabel pits = new JLabel("p:");
        JLabel seeds = new JLabel("s:");

        levelsCombo = new JComboBox<>(fillArray(MAX_LEVEL));
        pitsPerPlayer = new JComboBox<>(fillArray(MAX_PITS));
        seedsPerPit = new JComboBox<>(fillArray(MAX_SEEDS));
        newGameBtn = new JButton("New");
        switchBtn = new JButton("Switch");
        undoBtn = new JButton("Undo");
        quitBtn = new JButton("Quit");
        levelsCombo.setSelectedIndex(this.level - 1);
        pitsPerPlayer.setSelectedIndex(game.getPitsPerPlayer() - 1);
        seedsPerPit.setSelectedIndex(game.getSeedsPerPit() - 1);
        controlRow.add(pits);
        controlRow.add(pitsPerPlayer);
        controlRow.add(seeds);
        controlRow.add(seedsPerPit);
        controlRow.add(level);
        controlRow.add(levelsCombo);
        controlRow.add(newGameBtn);
        controlRow.add(switchBtn);
        controlRow.add(undoBtn);
        controlRow.add(quitBtn);
        getContentPane().add(controlRow, BorderLayout.SOUTH);
        //setFocusable(true);
    }

    private void highlightComponent(Player player) {
        if (player == Player.HUMAN) {
            fromLabel =
                    gridPanel.getSlotsGrid()[0][clickedPit].getLabels()[1][0];
        } else {
            fromLabel = gridPanel.getSlotsGrid()[0][2 * game.getPitsPerPlayer()
                    + 1 - game.sourcePitOfLastMove()].getLabels()[0][0];
        }
        if (game.targetPitOfLastMove() == 2 * game.getPitsPerPlayer() + 1) {
            toLabel = gridPanel.getSlotsGrid()[0][0].getLabels()[1][0];

        } else if (game.targetPitOfLastMove() <= game.getPitsPerPlayer()) {
            toLabel = gridPanel.getSlotsGrid()[0][game.targetPitOfLastMove()
                    + 1].getLabels()[1][0];
        } else {
            toLabel = gridPanel.getSlotsGrid()[0][2 * game.getPitsPerPlayer()
                    + 1 - game.targetPitOfLastMove()].getLabels()[0][0];
        }
      


    }

    private void update() {
        for (int i = 0; i < game.getPitsPerPlayer() + 2; i++) {
            if (i == 0) {
                gridPanel.getSlotsGrid()[0][0].getLabels()[1][0]
                        .setText(game.getSeeds(2 * game.getPitsPerPlayer()
                                + 1) + "");
            } else if (i == game.getPitsPerPlayer() + 1) {
                gridPanel.getSlotsGrid()[0][game.getPitsPerPlayer() + 1]
                        .getLabels()[1][0].setText(game.getSeeds(
                        game.getPitsPerPlayer()) + "");
            } else {
                gridPanel.getSlotsGrid()[0][i].getLabels()[0][0].setText(
                        game.getSeeds(2 * game.getPitsPerPlayer() + 1 - i)
                                + "");
                gridPanel.getSlotsGrid()[0][i].getLabels()[1][0].
                        setText(game.getSeeds(i - 1) + "");
            }

        }
    }

    private void updateWinMessage() {
        if (game.isGameOver()) {
            String msg;
            if (game.getWinner() == Player.HUMAN) {
                msg = "Congratulations! You won with "
                        + game.getSeedsOfPlayer(Player.HUMAN) + " seeds versus "
                        + game.getSeedsOfPlayer(Player.MACHINE)
                        + " seeds of the machine.";
                JOptionPane.showMessageDialog(this, msg);
            } else if (game.getWinner() == Player.MACHINE) {
                msg = "Sorry! Machine wins with "
                        + game.getSeedsOfPlayer(Player.MACHINE)
                        + " seeds versus your "
                        + game.getSeedsOfPlayer(Player.HUMAN) + ".";
                JOptionPane.showMessageDialog(this, msg);

            } else {
                msg = "Nobody wins. Tie with "
                        + game.getSeedsOfPlayer(Player.HUMAN)
                        + " seeds for each player.";
                JOptionPane.showMessageDialog(this, msg);
            }
        }
    }

    /**
     * Controller class that links game logic and view.
     */
    public class Controller {
        private final Stack<Board> undoStack;
        private View view;

        /**
         * Controller to links view with game logic.
         *
         * @param view View to be read.
         */
        public Controller(View view) {
            this.view = view;
            undoStack = new Stack<>();
        }

        /**
         * Runs the application to make the user interact with the game.
         */
        public void run() {
            view.setVisible(true);
            setQuitAction();
            setNewGameAction();
            setSwitchAction();
            setLevelsAction();
            undoBtn.setEnabled(false);
            setPitsPerPlayerAction();
            setSeedsPerPit();
            gridPanel.getField().addMouseListener(new MouseAdapterSlot());
            setUndoAction();
            view.addKeyListener(new MyKeyListener());
        }

        private void makeMachineMove() {
            try {

                game.machineMove();
                highlightComponent(Player.MACHINE);
                view.update();
                if (game.isGameOver()) {
                    updateWinMessage();
                    resetHighlighted();
                    return;
                }
                if (game.next() == Player.MACHINE) {
                    JOptionPane.showMessageDialog(view,
                            "You must miss a turn");
                    new java.util.Timer().schedule(new java.util.TimerTask() {
                        @Override
                        public void run() {
                            resetHighlighted();
                        }
                    }, 1000);
                    makeMachineMove();


                }else {
                    new java.util.Timer().schedule(new java.util.TimerTask() {
                        @Override
                        public void run() {
                            resetHighlighted();
                        }
                    }, 1000);
                }

            } catch (IllegalMoveException e) {
                java.awt.Toolkit.getDefaultToolkit().beep();
            } catch (InterruptedException e) {
                java.awt.Toolkit.getDefaultToolkit().beep();
            }
        }

        private void resetHighlighted() {
            fromLabel.setHighlighted(false);
            toLabel.setHighlighted(false);
            fromLabel.repaint();
            toLabel.repaint();

        }


        private void makeHumanMove() {
            undoStack.push(game.clone());
            try {
                Board currBoard = game.move(clickedPit - 1);
                if (currBoard == null) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
                undoBtn.setEnabled(true);
                update();
                highlightComponent(Player.HUMAN);
                if (game.isGameOver()) {
                    updateWinMessage();
                    return;
                } else if (game.next() == Player.MACHINE) {
                    new java.util.Timer().schedule(new java.util.TimerTask() {
                        @Override
                        public void run() {
                            resetHighlighted();
                        }
                    }, 1000);
                    makeMachineMove();

                } else {
                    JOptionPane.showMessageDialog(view,
                            "Machine must miss a " + "turn.");
                    new java.util.Timer().schedule(new java.util.TimerTask() {
                        @Override
                        public void run() {
                            resetHighlighted();
                        }
                    }, 1000);

                }


            } catch (IllegalArgumentException | IllegalMoveException ex) {
                Toolkit.getDefaultToolkit().beep();
            }
        }

        /**
         * Undo the last move of human.
         */
        private void setUndoAction() {
            undoBtn.addActionListener(e -> {
                if (!undoStack.empty()) {
                    game = undoStack.pop();
                    update();
                    if (undoStack.isEmpty()) {
                        undoBtn.setEnabled(false);
                    }
                }
            });
        }

        /**
         * Quit the game.
         */
        private void setQuitAction() {
            quitBtn.addActionListener(e -> {
                view.dispose();
                System.exit(0);
            });
        }

        private void createNewGame() {
            game = new GameBoard(game.getPitsPerPlayer(), game.getSeedsPerPit(),
                    game.getOpeningPlayer(), level);
            update();
            if (game.getOpeningPlayer() == Player.MACHINE) {
                makeMachineMove();
            }
            undoStack.clear();
            undoBtn.setEnabled(false);
        }

        /**
         * Change the opening {@link Player}.
         */
        private void switchPlayer() {
            game = new GameBoard(game.getPitsPerPlayer(), game.getSeedsPerPit(),
                    game.getOpeningPlayer().other(), level);
            update();
            if (game.getOpeningPlayer() == Player.MACHINE) {
                makeMachineMove();
            }
        }

        private void setSwitchAction() {
            switchBtn.addActionListener(e -> switchPlayer());
        }

        private void setLevelsAction() {
            levelsCombo.addActionListener(e -> {
                level = (Integer) levelsCombo.getSelectedItem();
                game.setLevel(level);
            });
        }

        private void setNewGameAction() {
            newGameBtn.addActionListener(e -> createNewGame());
        }

        private void setPitsPerPlayerAction() {
            pitsPerPlayer.addActionListener(e -> {
                int numOfPits = (Integer) pitsPerPlayer.getSelectedItem();
                view.dispose();
                game = new GameBoard(numOfPits, game.getSeedsPerPit(),
                        game.getOpeningPlayer(), level);
                view = new View(game);
                view.revalidate();
                view.new Controller(view).run();
            });
        }

        private void setSeedsPerPit() {
            seedsPerPit.addActionListener(e -> {
                int numOfSeeds = (Integer) seedsPerPit.getSelectedItem();
                view.dispose();
                game = new GameBoard(game.getPitsPerPlayer(), numOfSeeds,
                        game.getOpeningPlayer(), level);
                view = new View(game);
                view.new Controller(view).run();
            });
        }

        private class MyKeyListener implements KeyListener {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_N && e.isAltDown()) {
                    createNewGame();
                } else if (e.getKeyCode() == KeyEvent.VK_S && e.isAltDown()) {
                    switchPlayer();
                } else if (e.getKeyCode() == KeyEvent.VK_Q && e.isAltDown()) {
                    //   view.dispose();
                    System.exit(0);
                } else if (e.getKeyCode() == KeyEvent.VK_U && e.isAltDown()) {
                    if (!undoStack.empty()) {
                        game = undoStack.pop();
                        update();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        }

        private class MouseAdapterSlot extends MouseAdapter {
            @Override
            public void mousePressed(MouseEvent e) {
                SlotPanel clickedSlot1 = (SlotPanel) gridPanel.getField()
                        .getComponentAt(e.getPoint());
                clickedPit = clickedSlot1.getGridY();
                makeHumanMove();
            }
        }
    }
}
