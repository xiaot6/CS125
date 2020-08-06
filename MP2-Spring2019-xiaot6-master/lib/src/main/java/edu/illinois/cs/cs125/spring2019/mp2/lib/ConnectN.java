package edu.illinois.cs.cs125.spring2019.mp2.lib;
/**
 * public class.
 */

public class ConnectN {
    /**
     * Maximum valid latitude.
     */
    public static final int MIN_WIDTH = 6;
    /**
     * Maximum valid latitude.
     */
    public static final int MAX_WIDTH = 16;
    /**
     * Maximum valid latitude.
     */
    public static final int MIN_HEIGHT = 6;
    /**
     * Maximum valid latitude.
     */
    public static final int MAX_HEIGHT = 16;
    /**
     * Maximum valid latitude.
     */
    public static final int MIN_N = 4;
    /**
     * Maximum valid latitude.
     */
    private int width = 0;
    /**
     * Maximum valid latitude.
     */
    private int height = 0;
    /**
     * Maximum valid latitude.
     */
    private int n = 0;
    /**
     * Maximum valid latitude.
     */
    private Player[][] board = new Player[MAX_WIDTH][MAX_HEIGHT];
    /**
     * Maximum valid latitude.
     */
    private boolean start = false;
    /**
     * Maximum valid latitude.
     */
    private boolean end = false;

    /**
     * @param setWidth  the width for the new ConnectN board
     * @param setHeight the height for the new ConnectN board
     * @param setN      the N value for the new ConnectN board
     */
    public ConnectN(final int setWidth, final int setHeight, final int setN) {
        if (setWidth <= MAX_WIDTH && setWidth >= MIN_WIDTH) {
            this.width = setWidth;
        } else {
            this.width = 0;
        }
        if (setHeight <= MAX_HEIGHT && setHeight >= MIN_HEIGHT) {
            this.height = setHeight;
        } else {
            this.height = 0;
        }
        if (setN >= MIN_N && this.width >= this.height && setN <= this.width - 1 && width != 0 && height != 0) {
            this.n = setN;
        } else if (setN >= MIN_N && this.height > this.width && setN <= this.height - 1 && width != 0 && height != 0) {
            this.n = setN;
        } else {
            this.n = 0;
        }
    }

    /**
     * Create a new ConnectN board with uninitialized width, height, and N value.
     */
    public ConnectN() {
        this(0, 0, 0);
    }

    /**
     * Create a new ConnectN board with given width and height, but uninitialized N value.
     *
     * @param setWidth  the width for the new ConnectN board
     * @param setHeight the height for the new ConnectN board
     */
    public ConnectN(final int setWidth, final int setHeight) {
        this(setWidth, setHeight, 0);

    }

    /**
     * Create a new ConnectN board with dimensions and N value copied from another board.
     *
     * @param otherBoard the ConnectN board to copy width, height, and N from
     */
    public ConnectN(final ConnectN otherBoard) {
        this.width = otherBoard.width;
        this.height = otherBoard.height;
        this.n = otherBoard.n;
    }

    /**
     * Get the current board width.
     *
     * @return the current board width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Get the current board width.
     *
     * @param setWidth - the new width to set
     * @return true if the width was set successfully, false on error
     */
    public boolean setWidth(final int setWidth) {
        if (start == true) {
            return false;
        }
        if (setWidth > MAX_WIDTH || setWidth < MIN_WIDTH) {
            return false;
        }
        if (width >= height) {
            if (n > Math.max(setWidth, height) - 1) {
                n = 0;
            }
        }
        this.width = setWidth;
        return true;
    }

    /**
     * Get the current board height.
     *
     * @return the current board height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Get Attempt to set the board height.
     *
     * @param setHeight - the new height to set
     * @return true if the height was set successfully, false on error
     */
    public boolean setHeight(final int setHeight) {
        if (start == true) {
            return false;
        }
        if (setHeight > MAX_HEIGHT || setHeight < MIN_HEIGHT) {
            return false;
        }
        if (height >= width) {
            if (n > Math.max(setHeight, width) - 1) {
                n = 0;
            }
        }
        this.height = setHeight;
        return true;
    }

    /**
     * Get the current board N value.
     *
     * @return the current board N value
     */
    public int getN() {
        return this.n;
    }

    /**
     * Attempt to set the current board N value.
     * Note that:
     * <p>
     * N cannot be set after the game has started
     * N cannot be set before the width or the height
     * N cannot be less than 4
     * N can be at most 1 less than the maximum of the width and height
     *
     * @param setN - the new N
     * @return true if N was set successfully, false otherwise
     */
    public boolean setN(final int setN) {
        if (start == true) {
            return false;
        }
        if (this.width == 0 || this.height == 0) {
            return false;
        }
        if (setN < MIN_N || setN > Math.max(this.width, this.height) - 1) {
            return false;
        }
        this.n = setN;
        return true;
    }

    /**
     * @param player - the player attempting the move
     * @param setX   -the X coordinate that they are trying to place a tile at
     * @param setY   - the Y coordinate that they are trying to place a tile at
     * @return true if the move succeeds, false on error
     */
    public boolean setBoardAt(final Player player, final int setX, final int setY) {
        if (this.width == 0 || this.height == 0 || this.n == 0 || player == null) {
            return false;
        }
        if (setX >= this.width || setY >= this.height || setX < 0 || setY < 0) {
            return false;
        }
        if (end == true) {
            return false;
        }
        if (board[setX][setY] != null) {
            return false;
        }
        for (int y = 0; y < setY; y++) {
            if (board[setX][y] == null) {
                return false;
            }
        }
        board[setX][setY] = player;
        start = true;
        return true;
    }

    /**
     * @param player - the player attempting the move
     * @param setX   - the X coordinate for the stack that they are trying to drop a tile in
     * @return true if the move succeeds, false on error
     */
    public boolean setBoardAt(final Player player, final int setX) {
        if (this.width == 0 || this.height == 0 || this.n == 0 || player == null) {
            return false;
        }
        if (setX >= this.width || setX < 0) {
            return false;
        }
        if (end == true) {
            return false;
        }
        if (board[setX][height - 1] != null) {
            return false;
        }
        int y;
        for (y = 0; y < this.height; y++) {
            if (board[setX][y] == null) {
                break;
            }
        }
        board[setX][y] = player;
        start = true;
        return true;
    }

    /**
     * @param getX -the X coordinate that they are trying to place a tile at
     * @param getY - the Y coordinate that they are trying to place a tile at
     * @return true if the move succeeds, false on error
     */
    public Player getBoardAt(final int getX, final int getY) {
        if (getX >= MAX_WIDTH || getX < 0) {
            return null;
        }
        if (getY >= MAX_HEIGHT || getY < 0) {
            return null;
        }
        if (start == false) {
            return null;
        }
        if (board[getX][getY] == null) {
            return null;
        }
        return board[getX][getY];
    }

    /**
     * @return a copy of the current board
     */
    public Player[][] getBoard() {
        if (this.width == 0 || this.height == 0) {
            return null;
        }
        //if (board == null) {
        //return null;
        //}
        Player[][] copyBoard = new Player[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                //copyBoard[x][y] = board[x][y];
                if (board[x][y] == null) {
                    copyBoard[x][y] = null;
                } else {
                    copyBoard[x][y] = new Player(board[x][y]);
                }
            }
        }
        return copyBoard;
    }

    /**
     * @return the winner of the game, or null if the game has not ended
     */

    public Player getWinner() {
        int count = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width - 1; x++) {
                if (getBoardAt(x, y) != null && getBoardAt(x + 1, y) != null) {
                    if (getBoardAt(x, y).equals(getBoardAt(x + 1, y))) {
                        count++;
                        if (count < n - 1 && x == width - 2) {
                            count = 0;
                        }
                        if (count == n - 1) {
                            end = true;
                            getBoardAt(x, y).addScore();
                            return getBoardAt(x, y);
                        }
                    }
                } else {
                    count = 0;
                }
            }
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height - 1; y++) {
                if (getBoardAt(x, y) != null && getBoardAt(x, y + 1) != null) {
                    if (getBoardAt(x, y).equals(getBoardAt(x, y + 1))) {
                        count++;
                        if (count < n - 1 && y == height - 2) {
                            count = 0;
                        }
                        if (count == n - 1) {
                            end = true;
                            getBoardAt(x, y).addScore();
                            return getBoardAt(x, y);
                        }
                    }
                } else {
                    count = 0;
                }
            }
        }
        return null;
    }
    /**
     * @param width  -the X coordinate that they are trying to place a tile at
     * @param height - the height of the new ConnectN instance to create
     * @param n      - the n value of the new ConnectN instance to create
     * @return the new ConnectN instance, or null if the parameters are invalid
     */
    public static ConnectN create(final int width, final int height, final int n) {
        if (width <= MAX_WIDTH && width >= MIN_WIDTH) {
            if (height <= MAX_HEIGHT && height >= MIN_HEIGHT) {
                if (n >= MIN_N && n < Math.max(width, height) && width != 0 && height != 0) {
                    ConnectN newBoard = new ConnectN(width, height, n);
                    return newBoard;
                }
            }
        }
        return null;
    }

    /**
     * @param number - the number of new ConnectN instances to create
     * @param width  -the X coordinate that they are trying to place a tile at
     * @param height - the height of the new ConnectN instance to create
     * @param n      - the n value of the new ConnectN instance to create
     * @return the new ConnectN instance, or null if the parameters are invalid
     */
    public static ConnectN[] createMany(final int number, final int width, final int height, final int n) {
        if (number == 0) {
            return null;
        }
        if (width <= MAX_WIDTH && width >= MIN_WIDTH) {
            if (height <= MAX_HEIGHT && height >= MIN_HEIGHT) {
                if (n >= MIN_N && n < Math.max(width, height) && width != 0 && height != 0) {
                    ConnectN[] newBoardN = new ConnectN[number];
                    //System.out.print(newBoardN);
                    for (int i = 0; i < newBoardN.length; i++) {
                        newBoardN[i] = new ConnectN(width, height, n);
                        //return newBoardN;
                    }
                    return newBoardN;
                }
            }
        }
        return null;
    }

    /**
     *
     * @param firstBoard - the first ConnectN board to compare
     * @param secondBoard - the second ConnectN board to compare
     * @return true if the boards are the same, false otherwise
     */
    public static boolean compareBoards(final ConnectN firstBoard, final ConnectN secondBoard) {
        if (firstBoard == null || secondBoard == null) {
            return false;
        }
        if (firstBoard.width != secondBoard.width) {
            return false;
        }
        if (firstBoard.height != secondBoard.height) {
            return false;
        }
        if (firstBoard.n != secondBoard.n) {
            return false;
        }
        if (firstBoard.width == secondBoard.width && firstBoard.height == secondBoard.height) {
            if (firstBoard.n == secondBoard.n) {
                for (int x = 0; x < firstBoard.width; x++) {
                    for (int y = 0; y < firstBoard.height; y++) {
                        if (firstBoard.getBoardAt(x, y) != null && secondBoard.getBoardAt(x, y) == null) {
                            return false;
                        } else if (firstBoard.getBoardAt(x, y) == null && secondBoard.getBoardAt(x, y) != null) {
                            return false;
                        } else if (firstBoard.getBoardAt(x, y) != null && secondBoard.getBoardAt(x, y) != null) {
                            if (firstBoard.getBoardAt(x, y).equals(secondBoard.getBoardAt(x, y)) == false) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     *
     * @param boards - the array of ConnectN boards to compare
     * @return true if all passed boards are the same, false otherwise
     */
    public static boolean compareBoards(final ConnectN... boards) {
        for (int i = 0; i < boards.length; i++) {
            for (int j = 0; j < boards.length; j++) {
                if (compareBoards(boards[i], boards[j]) == false) {
                    return false;
                }
            }
        }
        return true;
    }
}
