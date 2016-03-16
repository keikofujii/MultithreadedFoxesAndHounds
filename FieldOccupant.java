import java.awt.Color;
import java.util.concurrent.Phaser;

/**
 * Abstract parent class for objects that can occupy a cell in the Field
 */

public abstract class FieldOccupant extends Thread
{
    /**
     * The constructor for the FieldOccupant
     * 
     * @param xCoord the X coordinate for the field occupant
     * @param yCoord the Y coordinate for the field occupant
     * @param phaser the phaser that the field occupant will wait for to 
     * start
     * @param theField the field that the field occupant is located in
     */
    public FieldOccupant(int xCoord, int yCoord, Phaser phaser, 
            Field theField)
    {
        p_xCoord = xCoord;
        p_yCoord = yCoord;
        p_phaser = phaser;
        p_theField = theField;
    }

    /**
     * @return the color to use for a cell containing a particular kind of
     *         occupant
     */
    abstract public Color getDisplayColor();

    /**
     * A method to get the phaser for this field occupant
     * @return the phaser that the field occupant will wait for to start
     */
    public Phaser getPhaser()
    {
        return p_phaser;
    }
    
    /**
     * A method to get the x coordinate of the occupant
     * @return the x coordinate of the occupant
     */
    public int getXCoord()
    {
        return p_xCoord;
    }

    /**
     * A method to get the y coordinate of the occupant
     * @return the y coordinate of the occupant
     */
    public int getYCoord()
    {
        return p_yCoord;
    }
    
    /**
     * A method to get the field for the occupant
     * @return the field that the occupant is located in
     */
    public Field getField()
    {
        return p_theField;
    }

    private int p_xCoord;
    private int p_yCoord;
    private Phaser p_phaser;
    Field p_theField;
}