package game2048;

import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author TODO: YOUR NAME HERE
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;

        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.
        // 1. 旋转棋盘，使得所有移动都朝向“上”方向进行处理

        // 1. 旋转棋盘，使得所有移动都朝向“上”方向进行处理
        board.setViewingPerspective(side);

        for (int col = 0; col < board.size(); col++) {
            if (tiltColumn(col)) {
                changed = true;
            }
        }

        // 3. 恢复棋盘的原始方向
        board.setViewingPerspective(Side.NORTH);

        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }
    private boolean tiltColumn(int col) {
        boolean moved = false;
        int size = board.size();

        boolean[] merged = new boolean[size];

        for (int row = size - 2; row >= 0; row--) {  // 从倒数第二行开始向下处理
            Tile t = board.tile(col, row);
            if (t == null) {
                continue;  // 当前格子为空，跳过
            }

            int newRow = row;
            while (newRow + 1 < size && board.tile(col, newRow + 1) == null) {
                newRow++;  // 移动到最上方可达的空格
            }

            if (newRow + 1 < size) {
                Tile nextTile = board.tile(col, newRow + 1);
                if (nextTile != null && nextTile.value() == t.value() && !merged[newRow + 1]) {
                    board.move(col, newRow + 1, t);
                    merged[newRow + 1] = true;
                    score += board.tile(col, newRow + 1).value();
                    moved = true;
                } else if (newRow != row) {
                    // 只是单纯向上移动
                    board.move(col, newRow, t);
                    moved = true;
                }
            } else if (newRow != row) {
                // 只是单纯向上移动
                board.move(col, newRow, t);
                moved = true;
            }
        }

        return moved;
    }

    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /**
     * Returns true if at least one space on the Board is empty.
     * Empty spaces are stored as null.
     */
    public static boolean emptySpaceExists(Board b) {
        // TODO: Fill in this function.
        int size = b.size();  // 获取棋盘大小
        for (int i = 0; i <size; i++) {
            for (int j = 0; j < size; j++) {
                if (b.tile(i, j) == null) {
                    return true;
                }

            }


        }
        return false;

    }





    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        int size = b.size();  // 获取棋盘大小
        int maxValue = MAX_PIECE;  // 最高的方块值

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Tile t = b.tile(i, j);  // ✅ 正确获取 Tile
                if (t != null && t.value() == maxValue) {  // ✅ 先检查 t 是否为空
                    return true;
                }
            }
        }

        return false;

    }

    // 遍历结束，如果没有找到 MAX_PIECE，返回 false


    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        // TODO: Fill in this function.
        int size = b.size();  // 获取棋盘大小
        int maxValue = MAX_PIECE;  // 最高的方块值
        for (int i = 0; i <size; i++) {
            for (int j = 0; j < size; j++) {
                if (b.tile(i, j) == null) {
                    return true;
                }

            }


        }

        for (int i = 0; i < size-1; i++) {
            for (int j = 0; j < size; j++) {
                Tile t1 = b.tile(i, j);  // ✅ 正确获取 Tile
                //   Tile t2 = b.tile(i, j+1);
                Tile t3 = b.tile(i+1, j);

                if(t1.value()== t3.value()){
                    return true;
                }
            }
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size-1; j++) {
                Tile t1 = b.tile(i, j);  // ✅ 正确获取 Tile
                Tile t2 = b.tile(i, j+1);
                // Tile t3 = b.tile(i+1, j);

                if(t1.value()== t2.value()){
                    return true;
                }
            }
        }
        return false;
    }


    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
