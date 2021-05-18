package lepegeto.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {
    private GameState state;

    @BeforeEach
    private void setUp() {
        state = new GameState();
    }

    @Test
    void nextPlayer() {
        var player1 = state.getCurrentPlayer();
        state.nextPlayer();
        var player2 = state.getCurrentPlayer();

        assertNotEquals(player1, player2);
        assertNotEquals(player1, player1.other());
        assertEquals(player1, player2.other());
        assertNotEquals(player2, player2.other());
        assertEquals(player2, player1.other());
    }

    @Test
    void getCurrentPlayerPositions() {
        var blue = new Position[] {
                new Position(0,0),
                new Position(0,1),
                new Position(0,2),
                new Position(0,3),
                new Position(0,4)};

        var red = new Position[] {
                new Position(4,0),
                new Position(4,1),
                new Position(4,2),
                new Position(4,3),
                new Position(4,4)};

        assertArrayEquals(blue, state.getCurrentPlayerPositions());

        state.nextPlayer();

        assertArrayEquals(red, state.getCurrentPlayerPositions());
    }

    @Test
    void isCurrentPlayerWinner() {
        assertFalse(state.isCurrentPlayerWinner());
    }

    @Test
    void isOccupiedByCurrentPlayer() {
        var positions = state.getCurrentPlayerPositions();

        for(var position : positions) {
            assertTrue(state.isOccupiedByCurrentPlayer(position));
        }

        state.nextPlayer();

        for(var position : positions) {
            assertFalse(state.isOccupiedByCurrentPlayer(position));
        }

    }

    @Test
    void getPositionAt() {
        assertThrows(IllegalArgumentException.class, () -> state.getPositionAt(new Position(-1, -1)));
        assertDoesNotThrow(() -> state.getPositionAt(new Position(0, 0)));

        var position = state.getPositionAt(new Position(0, 0));
        assertSame(position, state.getCurrentPlayerPositions()[0]);
        assertEquals(position, new Position(0, 0));
    }

    @Test
    void isForbidden() {
        var positions = new Position[] {
                new Position(1, 1),
                new Position(3, 1),
                new Position(1, 3),
                new Position(3, 3)
        };

        for(var position: positions) {
            assertTrue(state.isForbidden(position));
        }

        for(var position: state.getCurrentPlayerPositions()) {
            assertFalse(state.isForbidden(position));
        }
    }

    @Test
    void isRed() {
        var positions = state.getCurrentPlayerPositions();

        for(var position : positions) {
            assertFalse(state.isRed(position));
        }

        state.nextPlayer();
        positions = state.getCurrentPlayerPositions();

        for(var position : positions) {
            assertTrue(state.isRed(position));
        }
    }

    @Test
    void isFree() {
        var positions = state.getCurrentPlayerPositions();

        for(var position : positions) {
            assertFalse(state.isFree(position));
        }

        state.nextPlayer();

        for(var position : positions) {
            assertFalse(state.isFree(position));
        }

        positions = new Position[] {
                new Position(2,0),
                new Position(2,1),
                new Position(2,2),
                new Position(2,3),
                new Position(2,4)};

        for(var position : positions) {
            assertTrue(state.isFree(position));
        }
    }

    @Test
    void getCurrentPlayer() {
        assertEquals(Player.BLUE, state.getCurrentPlayer());
        assertNotEquals(Player.RED, state.getCurrentPlayer());

        state.nextPlayer();

        assertEquals(Player.RED, state.getCurrentPlayer());
        assertNotEquals(Player.BLUE, state.getCurrentPlayer());
    }

    @Test
    void isBlue() {
        var positions = state.getCurrentPlayerPositions();

        for(var position : positions) {
            assertTrue(state.isBlue(position));
        }

        state.nextPlayer();
        positions = state.getCurrentPlayerPositions();

        for(var position : positions) {
            assertFalse(state.isBlue(position));
        }
    }

    @Test
    void owner() {
        var positions = state.getCurrentPlayerPositions();

        for(var position : positions) {
            assertEquals(Owner.BLUE, state.owner(position));
        }

        state.nextPlayer();
        positions = state.getCurrentPlayerPositions();

        for(var position : positions) {
            assertEquals(Owner.RED, state.owner(position));
        }

        positions = new Position[] {
                new Position(2,0),
                new Position(2,1),
                new Position(2,2),
                new Position(2,3),
                new Position(2,4)};

        for(var position : positions) {
            assertEquals(Owner.NONE, state.owner(position));
        }

        positions = new Position[] {
                new Position(1, 1),
                new Position(3, 1),
                new Position(1, 3),
                new Position(3, 3)
        };

        for(var position : positions) {
            assertEquals(Owner.FORBIDDEN, state.owner(position));
        }
    }

    @Test
    void move() {
        var position = new Position(0, 0);
        state.move(Direction.NORTH, position);
        assertEquals(position, new Position(-1, 0));

        position = new Position(0, 0);
        state.move(Direction.SOUTH, position);
        assertEquals(position, new Position(1, 0));

        position = new Position(0, 0);
        state.move(Direction.WEST, position);
        assertEquals(position, new Position(0, -1));

        position = new Position(0, 0);
        state.move(Direction.EAST, position);
        assertEquals(position, new Position(0, 1));

        position = new Position(0, 0);
        state.move(Direction.NORTHWEST, position);
        assertEquals(position, new Position(-1, -1));

        position = new Position(0, 0);
        state.move(Direction.NORTHEAST, position);
        assertEquals(position, new Position(-1, 1));

        position = new Position(0, 0);
        state.move(Direction.SOUTHWEST, position);
        assertEquals(position, new Position(1, -1));

        position = new Position(0, 0);
        state.move(Direction.SOUTHEAST, position);
        assertEquals(position, new Position(1, 1));

    }

    @Test
    void testClone() {
        var clone = state.clone();
        assertTrue(clone.equals(state));
        assertNotSame(state, clone);
    }

    @Test
    void testToString() {
        assertEquals("{blue: [(0,0), (0,1), (0,2), (0,3), (0,4)], red: [(4,0), (4,1), (4,2), (4,3), (4,4)], forbidden: [(1,1), (1,3), (3,1), (3,3)], BOARD_SIZE: 5, currentPlayer: Blue}", state.toString());
        state.nextPlayer();
        assertEquals("{blue: [(0,0), (0,1), (0,2), (0,3), (0,4)], red: [(4,0), (4,1), (4,2), (4,3), (4,4)], forbidden: [(1,1), (1,3), (3,1), (3,3)], BOARD_SIZE: 5, currentPlayer: Red}", state.toString());
    }
}