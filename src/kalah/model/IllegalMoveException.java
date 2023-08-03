package kalah.model;

import java.io.Serial;

/**
 * An exception signaling that an illegal move during the game.
 */
public class IllegalMoveException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -8856775944693868259L;

    /**
     * Empty constructor.
     */
    public IllegalMoveException() {
        super();
    }

    /**
     * Instantiates a new {@code IllegalMoveException} object.
     *
     * @param err The error message.
     */
    public IllegalMoveException(String err) {
        super(err);
    }

}
