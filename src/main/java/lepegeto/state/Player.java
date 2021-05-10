package lepegeto.state;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlEnum;

/**
 * Represents a player.
 */
public enum Player {
    BLUE,
    RED;

    public Player other() {
        if(this.equals(RED)) {
            return BLUE;
        } else {
            return RED;
        }
    }

    @Override
    public String toString() {
        if(this.equals(BLUE)) {
            return "Blue Player";
        } else {
            return "Red Player";
        }
    }

}
