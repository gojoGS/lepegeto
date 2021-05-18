package lepegeto.model;

/**
 * Represents the four main and the four secondary directions.
 */
public enum Direction {
    /**
     * Direction of up.
     */
    NORTH(-1, 0),
    /**
     * Direction of up and right.
     */
    NORTHEAST(-1, 1),
    /**
     * Direction of right.
     */
    EAST(0, 1),
    /**
     * Direction of down and right.
     */
    SOUTHEAST(1, 1),
    /**
     * Direction of down.
     */
    SOUTH(1, 0),
    /**
     * Direction of down and left.
     */
    SOUTHWEST(1, -1),
    /**
     * Direction of left.
     */
    WEST(0, -1),
    /**
     * Direction of up and left.
     */
    NORTHWEST(-1, -1);

    /**
     * Difference in Y coordinates.
     */
    private final int rowChange;
    /**
     * Difference in X coordinates.
     */
    private final int colChange;

    /**
     * Initializes a {@link Direction} with these two differences.
     *
     * @param rowChange change in Y coordinates
     * @param colChange change in X coordinates
     */
    Direction(int rowChange, int colChange) {
        this.rowChange = rowChange;
        this.colChange = colChange;
    }

    /**
     * Creates a {@link Direction} from two ints.
     *
     * @param rowChange the change in the row coordinate
     * @param colChange the change in the column coordinate
     * @return the direction that corresponds to the coordinate changes specified.
     */
    public static Direction of(int rowChange, int colChange) throws IllegalArgumentException {
        for (var direction : values()) {
            if (direction.rowChange == rowChange && direction.colChange == colChange) {
                return direction;
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Returns the column change of the {@link Direction}.
     *
     * @return the change in the row coordinate when moving to the direction
     */
    public int getColChange() {
        return colChange;
    }

    /**
     * Returns the row change of the {@link Direction}.
     *
     * @return the change in the column coordinate when moving to the direction
     */
    public int getRowChange() {
        return rowChange;
    }
}
