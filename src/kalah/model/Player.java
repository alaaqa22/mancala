package kalah.model;

/**
 * Represent a player in mancala game.
 */
public enum Player {
    /**
     * Human player.
     */
    HUMAN,
    /**
     * Machine player.
     */
    MACHINE;

    /**
     * Returns the other player of the current player.
     *
     * @return Other player.
     */
    public Player other() {
        return (this == HUMAN) ? MACHINE : HUMAN;
    }
}
