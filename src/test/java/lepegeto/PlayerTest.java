package lepegeto;

import lepegeto.model.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void other() {
        assertEquals(Player.RED, Player.RED.other().other());
        assertNotEquals(Player.RED, Player.RED.other());
    }
}