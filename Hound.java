import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Phaser;

/**
 * Hounds can display themsevles. They also get hungry
 */
// Will still starve after 3 seconds, but will now starve incrementally
// Will check and adjust 'hunger' each time they awake
// If any neighbors are a Fox, then choose a random neighboring Fox
// If the chosen fox has another Hound as neighbor
// New well-fed Hound is birthed in cell of Fox
// Hound that found Fox has hunger gone
// Hound eats Fox and hunger is gone
// Hounds cannot share Foxes; one should succeed and the other go hungry
// No two new Hounds in same cell
// If a Hound finds a neighboring empty cell that has at least one other
// Hound as a neighbor and at least two Foxes as neighbors then one of
// the Foxes is consumed and a well-fed Hound is born into the empty 
// cell.
// As above, the Hound that killed the Fox and birthed the new Hound has
// its hunger gone.
// If no Fox, or if task is not completed, then hunger increases 
// proportionally
// to length of time it was asleep
// If it starves, it dies (thread terminates itself
// Fox is passive about being eaten
// Should only birth/eat one thing
// Fox cannot be eaten before it is birthed
// A Hound can eat a Fox that's in the middle of checking neighboring 
// cells
// Interrupt threads to terminate
public class Hound extends FieldOccupant
{
    /**
     * Create a hound
     */
    public Hound(int i, int j, Phaser phaser, Field theField)
    {
        super(i, j, phaser, theField);
        // Start out well-fed
        eats();
    }

    /**
     * @return true if this Hound has starved to death
     */
    public boolean hasStarved()
    {
        return p_fedStatus <= 0;
    }

    /**
     * Make this Hound hungrier
     *
     * @return true if the Hound has starved to death
     */
    public boolean getHungrier(long sleepTime)
    {
        // Decrease the fed status of this Hound
        p_fedStatus -= sleepTime;
        return hasStarved();
    }

    public void eats()
    {
        // Reset the fed status of this Hound
        p_fedStatus = p_houndStarveTime;
    }

    /**
     * @return the color to use for a cell occupied by a Hound
     */
    @Override
    public Color getDisplayColor()
    {
        return Color.red;
    } // getDisplayColor

    /**
     * @return the text representing a Hound
     */
    @Override
    public String toString()
    {
        return "H";
    }

    /**
     * Sets the starve time for this class
     *
     * @param starveTime
     */
    public static void setStarveTime(float starveTime)
    {
        p_houndStarveTime = starveTime;
    }

    /**
     * @return the starve time for Hounds
     */
    public static float getStarveTime()
    {
        return p_houndStarveTime;
    }

    /**
     * A method that overrides Runnables run method
     */
    @Override
    public void run()
    {
        int MIN_SLEEP_TIME = 750;
        int MAX_SLEEP_TIME = 1250;
        ArrayList<Cell> neighborCells;
        ArrayList<Cell> neighborsOfNeighbors;
        Random random = new Random();
        long sleepyTime; 
        
        try
        {
            // Register the new object
            getPhaser().register();

            // Arrive and wait for the rest of the objects
            getPhaser().arriveAndAwaitAdvance();

            // Run forever, or until we're terminated
            while (true)
            {   
                neighborCells = p_theField.getNeighborsOf(getXCoord(), getYCoord());
                
                // Get the random sleep time for the hound
                sleepyTime = (long)random.nextInt((MAX_SLEEP_TIME - MIN_SLEEP_TIME) + 1)
                        + MIN_SLEEP_TIME;
                
                // Sleep
                Thread.sleep(sleepyTime);
                
                // Wake up and check to see if we've died of starvation
                if (getHungrier(sleepyTime / (long)1000.0))
                {
                    // If we've died, kill the thread
                    Thread.currentThread().interrupt();
                }
                
            }
        }
        catch (InterruptedException e)
        {
            getField().getOccupantAt(getXCoord(), getYCoord()).setOccupant(null);
            // Make the field draw again since there's been a change
            synchronized (getField().getDrawField())
            {
                getField().setDrawField(true);
                getField().getDrawField().notify();
            }
        }
        
    }

    // Default starve time for Hounds
    public static final float DEFAULT_STARVE_TIME = 3;
    // Class variable for all hounds
    private static float p_houndStarveTime = DEFAULT_STARVE_TIME; 

    // Instance attributes to keep track of how hungry we are
    private float p_fedStatus;

}