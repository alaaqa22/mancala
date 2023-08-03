package kalah.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Class to represent a node in the tree.
 */
public class Node {
    private final GameBoard gameBoard;
    private final int pit;
    private double boardVal;
    private List<Node> children = new LinkedList<>();

    /**
     * Create node with the given game board and pit.
     *
     * @param gameBoard The given game board.
     * @param pit The given pit.
     */
    public Node(GameBoard gameBoard, int pit) {
        this.gameBoard = gameBoard;
        this.pit = pit;
    }

    /**
     * Method to add child in list.
     *
     * @param i Index of list.
     * @param node Node that will be added in index i.
     */
    public void addChild(int i, Node node) {
        children.add(i, node);
    }

    /**
     * Returns game board.
     *
     * @return The game board.
     */
    public GameBoard getGameBoard() {
        return gameBoard;
    }

    /**
     * Returns board value.
     *
     * @return board value.
     */
    public double getBoardVal() {
        return boardVal;
    }

    /**
     * Set the value of the board.
     *
     * @param boardVal The given board value.
     */
    public void setBoardVal(double boardVal) {
        this.boardVal = boardVal;
    }

    /**
     * Returns list of children.
     *
     * @return list of children
     */
    public List<Node> getChildren() {

        return Collections.unmodifiableList(children);
    }
}