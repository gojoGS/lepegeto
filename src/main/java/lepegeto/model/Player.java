package lepegeto.model;

import jakarta.xml.bind.annotation.*;

/**
 * Represents a player.
 */
@XmlType
@XmlEnum
public enum Player {
    @XmlEnumValue("Blue") BLUE,
    @XmlEnumValue("Red") RED;

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
