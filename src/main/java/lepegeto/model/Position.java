package lepegeto.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

import java.util.Objects;

/**
 * Represents a 2D position.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Position implements Cloneable {
    private int row;
    private int col;

    public Position() {
        this.row = 0;
        this.col = 0;
    }

    /**
     * Creates a {@code Position} object.
     *
     * @param row the row coordinate of the position
     * @param col the column coordinate of the position
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * {@return the row coordinate of the position}
     */
    public int getRow() {
        return row;
    }

    /**
     * {@return the column coordinate of the position}
     */
    public int getCol() {
        return col;
    }

    /**
     * {@return @return the position whose vertical and horizontal distances from this
     * position are equal to the coordinate changes of the direction given}
     *
     * @param direction a direction that specifies a change in the coordinates
     */
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

    /**
     * Changes the position by the coordinate changes of the direction given.
     *
     * @param direction a direction that specifies a change in the coordinates
     */
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