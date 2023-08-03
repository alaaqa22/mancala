package kalah;

import kalah.model.Board;
import kalah.model.GameBoard;
import kalah.view.View;

/**
 * Class to run the game.
 */
public final class MainGame {
    private MainGame() {
        throw new UnsupportedOperationException("Illegal call of utility "
                + "class constructor.");
    }

    /**
     * Run the program.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        Board game  = new GameBoard(Board.DEFAULT_PITS_PER_PLAYER,
                Board.DEFAULT_SEEDS_PER_PIT, View.DEF_STARTER,
                View.DEFAULT_LEVEL);
        View view = new View(game);
        View.Controller c = view.new Controller(view);
        c.run();
    }
}
