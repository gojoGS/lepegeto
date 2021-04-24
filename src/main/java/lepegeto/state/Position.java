package lepegeto.state;

import java.util.Objects;

public class Position implements Cloneable {
    private int row;
    private int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Position getTarget(Direction direction) {
        return new Position(row + direction.getColChange(), col + direction.getColChange());
    }

    public Position getNorth() {
        return getTarget(Direction.NORTH);
    }

    public Position getNortheast() {
        return getTarget(Direction.NORTHEAST);
    }

    public Position getEast() {
        return getTarget(Direction.EAST);
    }

    public Position getSoutheast() {
        return getTarget(Direction.SOUTHEAST);
    }

    public Position getSouth() {
        return getTarget(Direction.SOUTH);
    }

    public Position getSouthwest() {
        return getTarget(Direction.SOUTHWEST);
    }

    public Position getWest() {
        return getTarget(Direction.WEST);
    }

    public Position getNorthwest() {
        return getTarget(Direction.NORTHWEST);
    }

    public void setTarget(Direction direction) {
        row += direction.getRowChange();
        col += direction.getColChange();
    }

    public void setNorth() {
        setTarget(Direction.NORTH);
    }

    public void setNortheast() {
        setTarget(Direction.NORTHEAST);
    }

    public void setEast() {
        setTarget(Direction.EAST);
    }

    public void setSoutheast() {
        setTarget(Direction.SOUTHEAST);
    }

    public void setSouth() {
        setTarget(Direction.SOUTH);
    }

    public void setSouthwest() {
        setTarget(Direction.SOUTHWEST);
    }

    public void setWest() {
        setTarget(Direction.WEST);
    }

    public void setNorthwest() {
        setTarget(Direction.NORTHWEST);
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }

        return (o instanceof Position p) && p.row == row && p.col == col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public Position clone() {
        Position copy;
        try {
            copy = (Position) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
        return copy;
    }
    @Override
    public String toString() {
        return String.format("(%d,%d)", row, col);
    }
}
