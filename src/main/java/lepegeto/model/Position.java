package lepegeto.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

import java.util.Objects;

/**
 * Represents a 2D position.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Position implements Cloneable {
    /**
     * X coordinate of {@link Position}.
     */
    private int row;
    /**
     * Y coordinate of {@link Position}.
     */
    private int col;

    /**
     * Default constructor.
     */
    public Position() {
        this.row = 0;
        this.col = 0;
    }

    /**
     * Creates a {@link Position} object.
     *
     * @param row the row coordinate of the position
     * @param col the column coordinate of the position
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Returns the row coordinate of the position.
     * @return the X coordinate
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the column coordinate of the position.
     * @return the Y coordinate
     */
    public int getCol() {
        return col;
    }

    /**
     * Return return the position whose vertical and horizontal distances from this
     * position are equal to the coordinate changes of the direction given.
     *
     * @return The result position
     * @param direction a direction that specifies a change in the coordinates
     */
    public Position getTarget(Direction direction) {
        return new Position(row + direction.getColChange(), col + direction.getColChange());
    }

    /**
     * Return the position North from this one.
     * @return the {@link Position} North from this one
     */
    public Position getNorth() {
        return getTarget(Direction.NORTH);
    }

    /**
     * Return the position Northeast from this one.
     * @return the {@link Position} Northeast from this one
     */
    public Position getNortheast() {
        return getTarget(Direction.NORTHEAST);
    }

    /**
     * Return the position East from this one.
     * @return the {@link Position} East from this one
     */
    public Position getEast() {
        return getTarget(Direction.EAST);
    }

    /**
     * Return the position Southeast from this one.
     * @return the {@link Position} Southeast from this one
     */
    public Position getSoutheast() {
        return getTarget(Direction.SOUTHEAST);
    }
    /**
     * Return the position South from this one.
     * @return the {@link Position} South from this one
     */
    public Position getSouth() {
        return getTarget(Direction.SOUTH);
    }

    /**
     * Return the position Southwest from this one.
     * @return the {@link Position} Southwest from this one
     */
    public Position getSouthwest() {
        return getTarget(Direction.SOUTHWEST);
    }

    /**
     * Return the position West from this one.
     * @return the {@link Position} West from this one
     */
    public Position getWest() {
        return getTarget(Direction.WEST);
    }

    /**
     * Return the position Northwest from this one.
     * @return the {@link Position} Northwest from this one
     */
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

    /**
     * Moves position North.
     */
    public void setNorth() {
        setTarget(Direction.NORTH);
    }

    /**
     * Moves position Northeast.
     */
    public void setNortheast() {
        setTarget(Direction.NORTHEAST);
    }

    /**
     * Moves position East.
     */
    public void setEast() {
        setTarget(Direction.EAST);
    }

    /**
     * Moves position Southeast.
     */
    public void setSoutheast() {
        setTarget(Direction.SOUTHEAST);
    }

    /**
     * Moves position South.
     */
    public void setSouth() {
        setTarget(Direction.SOUTH);
    }

    /**
     * Moves position Southwest.
     */
    public void setSouthwest() {
        setTarget(Direction.SOUTHWEST);
    }

    /**
     * Moves position West.
     */
    public void setWest() {
        setTarget(Direction.WEST);
    }

    /**
     * Moves position Northwest.
     */
    public void setNorthwest() {
        setTarget(Direction.NORTHWEST);
    }

    /**
     * {@return whether {@code this} and {@code o} is equal}.
     * @param o the other object
     */
    @Override
    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }

        return (o instanceof Position p) && p.row == row && p.col == col;
    }

    /**
     * {@return hash code of object}.
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    /**
     * {@return a deep copy of object}.
     */
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

    /**
     * {@return String representation of class}.
     */
    @Override
    public String toString() {
        return String.format("(%d,%d)", row, col);
    }
}
