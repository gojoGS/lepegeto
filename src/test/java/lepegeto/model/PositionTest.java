package lepegeto.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {
    Position position;

    void assertPosition(int expectedRow, int expectedCol, Position position) {
        assertAll("position",
                () -> assertEquals(expectedRow, position.getRow()),
                () -> assertEquals(expectedCol, position.getCol())
        );
    }

    @BeforeEach
    void init() {
        position = new Position(0, 0);
    }

    @Test
    void getTarget() {
        assertPosition(-1, 0, position.getTarget(Direction.NORTH));
        assertPosition(1, 0, position.getTarget(Direction.SOUTH));
        assertPosition(0, 1, position.getTarget(Direction.EAST));
        assertPosition(0, -1, position.getTarget(Direction.WEST));
        assertPosition(-1, 1, position.getTarget(Direction.NORTHEAST));
        assertPosition(-1, -1, position.getTarget(Direction.NORTHWEST));
        assertPosition(1, 1, position.getTarget(Direction.SOUTHEAST));
        assertPosition(1, -1, position.getTarget(Direction.SOUTHWEST));
    }

    @Test
    void defaultConstructor() {
        assertPosition(0, 0, new Position());
    }

    @Test
    void getNorth() {
        assertPosition(-1, 0, position.getNorth());
    }

    @Test
    void getNortheast() {
        assertPosition(-1, 1, position.getNortheast());
    }

    @Test
    void getEast() {
        assertPosition(0, 1, position.getEast());
    }

    @Test
    void getSoutheast() {
        assertPosition(1, 1, position.getSoutheast());
    }

    @Test
    void getSouth() {
        position = position.getSouth();
        assertPosition(1, 0, position);
    }

    @Test
    void getSouthwest() {
        assertPosition(1, -1, position.getSouthwest());
    }

    @Test
    void getWest() {
        assertPosition(0, -1, position.getWest());
    }

    @Test
    void getNorthwest() {
        assertPosition(-1, -1, position.getNorthwest());
    }


    @Test
    void setNorth() {
        position.setNorth();
        assertPosition(-1, 0, position);
    }

    @Test
    void setNortheast() {
        position.setNortheast();
        assertPosition(-1, 1, position);
    }

    @Test
    void setEast() {
        position.setEast();
        assertPosition(0, 1, position);
    }

    @Test
    void setSoutheast() {
        position.setSoutheast();
        assertPosition(1, 1, position);
    }

    @Test
    void setSouth() {
        position.setSouth();
        assertPosition(1, 0, position);
    }

    @Test
    void setSouthwest() {
        position.setSouthwest();
        assertPosition(1, -1, position);
    }

    @Test
    void setWest() {
        position.setWest();
        assertPosition(0, -1, position);
    }

    @Test
    void setNorthwest() {
        position.setNorthwest();
        assertPosition(-1, -1, position);
    }

    @Test
    void testEquals() {
        assertTrue(position.equals(position));
        assertTrue(position.equals(new Position(position.getRow(), position.getCol())));
        assertFalse(position.equals(new Position(Integer.MIN_VALUE, position.getCol())));
        assertFalse(position.equals(new Position(position.getRow(), Integer.MAX_VALUE)));
        assertFalse(position.equals(new Position(Integer.MIN_VALUE, Integer.MAX_VALUE)));
        assertFalse(position.equals(null));
        assertFalse(position.equals("string literal"));
    }

    @Test
    void testHashCode() {
        assertTrue(position.hashCode() == position.hashCode());
        assertTrue(position.hashCode() == new Position(position.getRow(), position.getCol()).hashCode());
    }

    @Test
    void testClone() {
        var clone = position.clone();
        assertTrue(clone.equals(position));
        assertNotSame(position, clone);
    }

    @Test
    void testToString() {
        assertEquals("(0,0)", position.toString());
    }
}