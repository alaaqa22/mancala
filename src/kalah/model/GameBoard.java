package kalah.model;

import java.util.Arrays;

/**
 * A class for the mancala game, includes the game logic and help methods*.
 */
public class GameBoard implements Board {
    private  Player openingPlayer;
    private int level;
    private  int pitsPerPlayer;
    private  int seedsPerPit;
    private int[] board;
    private int sourcePit;
    private  int humanStore;
    private  int machineStore;
    private int targetPit;
    private Player currPlayer;


    /**
     * Create mancala game.
     *
     * @param pitsPerPlayer Number of Pits for each player.
     * @param seedsPerPit Number of seeds in each pit.
     * @param openingPlayer The player who start the game.
     * @param level Level of the game.
     * @throws IllegalArgumentException If the provided parameter is invalid,
     *         e.g., the given pits or seeds less than 1.
     */
    public GameBoard(int pitsPerPlayer, int seedsPerPit, Player openingPlayer,
                     int level) {
        if (pitsPerPlayer < 1 || seedsPerPit < 1) {
            throw new IllegalArgumentException();
        }
        this.pitsPerPlayer = pitsPerPlayer;
        this.seedsPerPit = seedsPerPit;
        this.openingPlayer = openingPlayer;
        this.board = new int[2 * (pitsPerPlayer + 1)];
        this.level = level;
        machineStore = board.length - 1;
        humanStore = pitsPerPlayer;
        currPlayer = openingPlayer;
        createBoard();
    }

    private void createBoard() {
        for (int i = 0; i < board.length - 1; i++) {
            if (i == humanStore || i == machineStore) {
                board[i] = 0;
            } else {
                board[i] = seedsPerPit;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player getOpeningPlayer() {
        return openingPlayer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player next() {
        if (currPlayer == Player.HUMAN && targetPit == humanStore) {
            return currPlayer;
        } else if (currPlayer == Player.MACHINE && targetPit == machineStore) {
            return currPlayer;
        }
        currPlayer = currPlayer.other();
        return currPlayer;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board move(int pit) {
        if (isGameOver() || currPlayer != Player.HUMAN) {
            throw new IllegalMoveException();
        } else if (pit < 0 || pit > pitsPerPlayer - 1) {
            throw new IllegalArgumentException();
        }
        return humanMove(pit, this);
    }

    /**
     * Method to make a human move.
     *
     * @param pit The pit we want to move seeds from.
     * @param gb The given game board.
     * @return The board after the move.
     */
    private Board humanMove(int pit, GameBoard gb) {
        if (gb.board[pit] == 0) {
            return null;
        }
        gb.sourcePit = pit;
        int numOfSeeds = gb.board[pit];
        gb.board[pit] = 0;
        while (numOfSeeds > 0) {
            pit++;
            if (pit == machineStore) {
                pit = 0;
            }
            gb.board[pit]++;
            if (numOfSeeds == 1) {
                gb.targetPit = pit;
                if (gb.board[targetPit] == 1 && gb.targetPit < humanStore
                        && gb.board[oppositePit(gb.targetPit)] > 0
                        && pit != oppositePit(pit)) {
                    gb.board[humanStore]
                            += gb.board[oppositePit(gb.targetPit)] + 1;
                    gb.board[targetPit] = 0;
                    gb.board[oppositePit(targetPit)] = 0;

                }
            }
            numOfSeeds--;
        }
        return gb;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board machineMove() throws InterruptedException {
        if (isGameOver() || currPlayer != Player.MACHINE) {
            throw new IllegalMoveException();
        }
        int bestPit = -1;
        Node root = createTree(this, level);
        double bestValue = max(root, root.getGameBoard().level);
        root.setBoardVal(evaluateGameBoard(root.getGameBoard(), -1));
        bestValue -= root.getBoardVal();
        for (int i = 0; i < pitsPerPlayer; i++) {
            if (root.getChildren().get(i) == null) {
                continue;
            }
            if (root.getChildren().get(i).getBoardVal() == bestValue) {
                bestPit = i + humanStore + 1;
                sourcePit = bestPit;
                targetPit = root.getChildren().get(i).getGameBoard().targetPit;
                break;
            }
        }
        return machineMoveOnBoard(bestPit, this);
    }

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException If level less than 1.
     */
    @Override
    public void setLevel(int level) {
        if (level < 1) {
            throw new IllegalArgumentException();
        }
        this.level = level;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGameOver() {
        int seedsOfPitsHuman
                = getSeedsOfPlayer(Player.HUMAN) - board[humanStore];
        int seedsOfPitsMachine
                = getSeedsOfPlayer(Player.MACHINE) - board[machineStore];
        return seedsOfPitsHuman == 0 || seedsOfPitsMachine == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player getWinner() {
        if (isGameOver()) {
            if (getSeedsOfPlayer(Player.HUMAN)
                    > getSeedsOfPlayer(Player.MACHINE)) {
                return Player.HUMAN;
            } else if (getSeedsOfPlayer(Player.MACHINE)
                    > getSeedsOfPlayer(Player.HUMAN)) {
                return Player.MACHINE;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException If the provided parameter is invalid,
     *         e.g., the defined pit is not on the board.
     */
    @Override
    public int getSeeds(int pit) {
        if (pit < 0 || pit > machineStore) {
            throw new IllegalArgumentException();
        }
        return board[pit];
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int sourcePitOfLastMove() {
        return sourcePit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int targetPitOfLastMove() {
        return targetPit;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int getPitsPerPlayer() {
        return pitsPerPlayer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSeedsPerPit() {
        return seedsPerPit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSeedsOfPlayer(Player player) {
        int sum = 0;
        if (player.equals(Player.HUMAN)) {
            for (int i = 0; i <= humanStore; i++) {
                sum += board[i];
            }
        } else if (player.equals(Player.MACHINE)) {
            for (int i = humanStore + 1; i <= machineStore; i++) {
                sum += board[i];
            }
        }
        return sum;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board clone() {
        GameBoard copy;
        try {
            copy = (GameBoard) super.clone();
            copy.sourcePit = sourcePit;
            copy.targetPit = targetPit;
            copy.currPlayer = currPlayer;
            copy.level = level;
            copy.board = board.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return copy;
    }

    /**
     * Returns the digits number of the maximum value in board.
     *
     * @param board The board to read.
     * @return The maximum number of digits.
     */
    private int getMaxDigits(int[] board) {
        if (board.length == 0) {
            return 0;
        }
        int max = Arrays.stream(board).max().orElse(0);
        return (int) Math.log10(max) + 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        final int numOfDigits = getMaxDigits(board);
        int lNumOfDigits = numOfDigits;
        for (int i = machineStore; i > humanStore; i--) {
            while (String.valueOf(board[i]).length() < lNumOfDigits) {
                sb.append(" ");
                lNumOfDigits--;
            }
            lNumOfDigits = numOfDigits;
            if (i > humanStore + 1) {
                sb.append(board[i]).append(" ");
            } else {
                sb.append(board[i]);
            }
        }
        sb.append(System.lineSeparator());
        sb.append("  ");
        while (lNumOfDigits > 1) {
            sb.append(" ");
            lNumOfDigits--;
        }
        lNumOfDigits = numOfDigits;
        for (int i = 0; i <= humanStore; i++) {
            while (String.valueOf(board[i]).length() < lNumOfDigits) {
                sb.append(" ");
                lNumOfDigits--;
            }
            lNumOfDigits = numOfDigits;
            if (i < humanStore) {
                sb.append(board[i]).append(" ");
            } else {
                sb.append(board[i]);
            }
        }
        return sb.toString();
    }
    /**
     * Method to maximize player.
     *
     * @param node The given node.
     * @param depth The given depth.
     * @return The best value.
     */
    private double max(Node node, int depth) {
        if (depth == 0 || node.getGameBoard().isGameOver()) {
            return evaluateGameBoard(node.getGameBoard(), depth);
        }
        double bestValue = Integer.MIN_VALUE;
        for (Node childNode : node.getChildren()) {
            if (childNode == null) {
                continue;
            }
            double tempValue;
            if (childNode.getGameBoard().currPlayer == Player.MACHINE) {
                tempValue =
                        Math.max(max(childNode, depth - 1), bestValue);

            } else {
                tempValue =
                        Math.max(min(childNode, depth - 1), bestValue);
            }
            childNode.setBoardVal(tempValue);
            if (tempValue > bestValue) {
                bestValue = tempValue;
            }
        }
        return bestValue + evaluateGameBoard(node.getGameBoard(), depth);

    }

    /**
     * Method to minimize player.
     *
     * @param node The given node.
     * @param depth The given depth.
     * @return The best value.
     */
    private double min(Node node, int depth) {
        if (depth == 0 || node.getGameBoard().isGameOver()) {
            return evaluateGameBoard(node.getGameBoard(), depth);
        }
        double bestValue = Integer.MAX_VALUE;
        for (Node childNode : node.getChildren()) {
            if (childNode == null) {
                continue;
            }
            double tempValue;
            if (childNode.getGameBoard().currPlayer == Player.HUMAN) {
                tempValue = Math.min(min(childNode, depth - 1), bestValue);

            } else {
                tempValue = Math.min(max(childNode, depth - 1), bestValue);
            }
            childNode.setBoardVal(tempValue);
            if (tempValue < bestValue) {
                bestValue = tempValue;
            }
        }
        return bestValue + evaluateGameBoard(node.getGameBoard(), depth);
    }

    /**
     * Method to create tree according to the given level.
     *
     * @param gb The given game board.
     * @param level The given level.
     * @return The root node.
     */
    private Node createTree(GameBoard gb, int level) {
        GameBoard cBoard = (GameBoard) gb.clone();
        Node root = new Node(cBoard, level);
        createSubTree(root, level);
        return root;
    }

    /**
     * Create subtree for each Node.
     *
     * @param node  Node to read.
     * @param level Level to read.
     */
    private void createSubTree(Node node, int level) {
        if (level > 0 && !node.getGameBoard().isGameOver()) {
            GameBoard tmp = node.getGameBoard();
            for (int i = 0; i < pitsPerPlayer; i++) {
                GameBoard board = (GameBoard) tmp.clone();
                if ((board.board[i] != 0 && board.currPlayer == Player.HUMAN)
                        || (board.board[i + humanStore + 1] != 0
                        && board.currPlayer == Player.MACHINE)) {
                    makeMove(i, board.currPlayer, board);
                    Node chNode = new Node(board, i);
                    node.addChild(i, chNode);
                    board.currPlayer = board.next();
                    createSubTree(chNode, level - 1);
                } else {
                    node.addChild(i, null);
                }
            }
        }
    }

    private void makeMove(int pit, Player player, GameBoard gameBoard) {
        if (player == Player.HUMAN) {
            gameBoard.humanMove(pit, gameBoard);
        } else {
            gameBoard.machineMoveOnBoard(pit + humanStore + 1, gameBoard);
        }
    }

    /**
     * Method to make a machine move.
     *
     * @param pit The pit to move seeds from.
     * @param gb The given game board.
     * @return The game board after the move.
     */
    private Board machineMoveOnBoard(int pit, GameBoard gb) {
        gb.sourcePit = pit;
        int numOfSeeds = gb.board[pit];
        gb.board[pit] = 0;
        while (numOfSeeds > 0) {
            pit++;
            if (pit == board.length) {
                pit = 0;
            } else if (pit == humanStore) {
                pit++;
            }
            gb.board[pit]++;
            if (numOfSeeds == 1) {
                gb.targetPit = pit;
                if (gb.targetPit < machineStore && gb.targetPit > humanStore
                        && gb.board[gb.targetPit] == 1
                        && gb.board[oppositePit(gb.targetPit)] > 0) {
                    gb.board[machineStore]
                            += gb.board[oppositePit(targetPit)] + 1;
                    gb.board[gb.targetPit] = 0;
                    gb.board[oppositePit(targetPit)] = 0;
                }
            }
            numOfSeeds--;
        }
        return gb;
    }

    /**
     * Returns score c according to the player.
     *
     * @param seeds The given board.
     * @param player The given player.
     * @return Score c of the given player.
     */
    private double calculateScoreC(int[] seeds, Player player) {
        int[] capturedSeeds = new int[seeds.length];
        int sum = 0;
        int sourcePit = 0;
        int store = machineStore;
        int lastPit = humanStore;

        if (player == Player.MACHINE) {
            sourcePit = pitsPerPlayer + 1;
            lastPit = machineStore;
            store = humanStore;
        }
        for (int i = sourcePit; i < lastPit; i++) {
            int targetPit = i + seeds[i];
            if (targetPit == store) {
                targetPit++;
            }
            if (targetPit >= board.length) {
                targetPit = targetPit - board.length;
            }
            if (seeds[i] > 0
                    && seeds[i] <= 2 * pitsPerPlayer + 1
                    && (seeds[targetPit] == 0 || targetPit == i)
                    && targetPit < lastPit && targetPit >= sourcePit) {
                int oppositeSeeds = seeds[oppositePit(targetPit)];
                if (targetPit <= i) {
                    oppositeSeeds += 1;
                }
                if (oppositeSeeds > capturedSeeds[targetPit]) {
                    sum -= capturedSeeds[targetPit];
                    sum += oppositeSeeds;
                    capturedSeeds[targetPit] = oppositeSeeds;
                }
            }
        }
        return sum;
    }

    /**
     * Returns the opposite pit of the given pit.
     *
     * @param pit The given pit.
     * @return The opposite pit.
     */
    private int oppositePit(int pit) {
        return machineStore - 1 - pit;
    }

    /**
     * Returns score c.
     *
     * @param board The given board.
     * @return score c.
     */
    private double getScoreC(int[] board) {
        double scoreHuman = calculateScoreC(board, Player.HUMAN);
        double scoreMachine = calculateScoreC(board, Player.MACHINE);

        return scoreMachine - 1.5 * scoreHuman;
    }

    /**
     * Returns score s in the given board.
     *
     * @param board The given board.
     * @return Score s.
     */
    private double computeScoreS(int[] board) {
        return board[machineStore] - 1.5 *  board[humanStore];
    }

    /**
     * Returns score p in the given board.
     *
     * @param board The given board.
     * @return Score p.
     */
    private double computeScoreP(int[] board) {
        int sumHuman = 0;
        int sumMachine = 0;
        for (int i = 0; i < humanStore; i++) {
            if (board[i] == 0 && board[oppositePit(i)] >= 2 * seedsPerPit) {
                sumHuman++;
            }
        }
        for (int i = humanStore + 1; i < machineStore; i++) {
            if (board[i] == 0 && board[oppositePit(i)] >= 2 * seedsPerPit) {
                sumMachine++;
            }
        }
        return sumMachine - 1.5 * sumHuman;
    }

    /**
     * Returns total score v.
     *
     * @param gameBoard The given game board.
     * @param level The given level.
     * @return Total score v.
     */
    private double computeScoreV(GameBoard gameBoard, int level) {
        return calScoreV(gameBoard, Player.MACHINE, level)
                - 1.5 * calScoreV(gameBoard, Player.HUMAN, level);
    }

    /**
     * Returns score v.
     *
     * @param gameBoard The given game board.
     * @param player The given player.
     * @param level The given level.
     * @return Score v for the given player.
     */
    private double calScoreV(GameBoard gameBoard, Player player, int level) {
        if (gameBoard.isGameOver() && gameBoard.getWinner() == player) {
            return 500.0 / (this.level - level);
        } else {
            return 0;
        }
    }

    /**
     * Method to evaluate the game board.
     *
     * @param gameBoard The given gameBoard.
     * @param level The given level.
     * @return The board value.
     */
    private double evaluateGameBoard(GameBoard gameBoard, int level) {
        return 3 * computeScoreS(gameBoard.board) + getScoreC(gameBoard.board)
                + computeScoreP(gameBoard.board)
                + computeScoreV(gameBoard, level);
    }
}
