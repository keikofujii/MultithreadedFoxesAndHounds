import java.awt.Color;
import java.util.concurrent.Phaser;

/**
 * Abstract parent class for objects that can occupy a cell in the Field
 */
// Will need to ask Field for its position
// Will sleep a random amount of time from .75 - 1.25 seconds
// When they wake they'll perform the appropriate actions then sleep again
/**
 * @param i
 *            the x coordinate that the occupant is located at
 * 
 */
public abstract class FieldOccupant extends Thread
{
    public FieldOccupant(int i, int j, Phaser phaser, Field theField)
    {
        p_xCoord = i;
        p_yCoord = j;
        p_phaser = phaser;
        p_theField = theField;
    }

    /**
     * @return the color to use for a cell containing a particular kind of
     *         occupant
     */
    abstract public Color getDisplayColor();

    public Phaser getPhaser()
    {
        return p_phaser;
    }
    
    public int getXCoord()
    {
        return p_xCoord;
    }

    public int getYCoord()
    {
        return p_yCoord;
    }
    
    public Field getField()
    {
        return p_theField;
    }

    private int p_xCoord;
    private int p_yCoord;
    private Phaser p_phaser;
    Field p_theField;
}