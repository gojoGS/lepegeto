package lepegeto.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PlayerTest {

    @Test
    void other() {
        assertEquals(Player.RED, Player.RED.other().other());
        assertNotEquals(Player.RED, Player.RED.other());
    }
}