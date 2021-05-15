package lepegeto.model;

/**
 * Represents the four main and the four secondary directions.
 */
public enum Direction {
    NORTH(-1, 0),
    NORTHEAST(-1, 1),
    EAST(0,1),
    SOUTHEAST(1, 1),
    SOUTH(1, 0),
    SOUTHWEST(1, -1),
    WEST(0, -1),
    NORTHWEST(-1, -1);

    private final int rowChange;
    private final int colChange;
    Direction(int rowChange, int colChange) {
        this.rowChange = rowChange;
        this.colChange = colChange;
    }

    /**
     * {@return the change in the row coordinate when moving to the direction}
     */
    public int getColChange() {
        return colChange;
    }

    /**
     * {@return the change in the column coordinate when moving to the direction}
     */
    public int getRowChange() {
        return rowChange;
    }

    /**
     * {@return the direction that corresponds to the coordinate changes specified}
     *
     * @param rowChange the change in the row coordinate
     * @param colChange the change in the column coordinate
     */
    public static Direction of(int rowChange, int colChange) throws IllegalArgumentException {
        for(var direction: values()) {
            if(direction.rowChange == rowChange && direction.colChange == colChange) {
                return direction;
            }
        }
        throw new IllegalArgumentException();
    }
}
