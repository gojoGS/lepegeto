package lepegeto.model;

import jakarta.xml.bind.annotation.*;

/**
 * Represents a player.
 */
@XmlType
@XmlEnum
public enum Player {
    /**
     * Blue player.
     */
    @XmlEnumValue("Blue") BLUE,
    /**
     * Red player.
     */
    @XmlEnumValue("Red") RED;

    /**
     * Returns the player that is not this one.
     * @return the other player
     */
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
            return "Blue";
        } else {
            return "Red";
        }
    }

}
