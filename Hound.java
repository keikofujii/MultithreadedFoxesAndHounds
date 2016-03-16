import java.awt.Color;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.Phaser;

/**
 * Hounds can display themsevles. They also get hungry
 */

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
        ArrayList<Cell> neighboringFoxes;
        ArrayList<Cell> neighboringHounds;
        PriorityQueue<Cell> cellsToLock;
        boolean hasHoundNeighbors = false;
        int randomFoxIndex;
        Random random = new Random();
        long sleepyTime;
        int j;
        int k;
        boolean actionAttempted;

        try
        {
            // Arrive and wait for the rest of the objects
            getPhaser().awaitAdvance(0);

            // Run forever, or until we're terminated
            while (true && !Thread.interrupted())
            {
                k = 0;
                j = 0;
                neighboringFoxes = new ArrayList<Cell>();
                neighboringHounds = new ArrayList<Cell>();
                cellsToLock = new PriorityQueue<Cell>();
                
                hasHoundNeighbors = false;
                actionAttempted = false;

                // Get the neighbor cells
                neighborCells = p_theField.getNeighborsOf(getXCoord(),
                        getYCoord());
                
                while (!actionAttempted && k < neighborCells.size())
                {
                 // If a Hound finds any of its neighbors is a Fox, then
                    // it should choose a random neighboring Fox and
                    // if the chosen Fox has another Hound as neighbor, then
                    // a new well-fed Hound is birthed in the cell occupied
                    // by the Fox; the Hound that found the Fox and birthed
                    // the new Hound has its hunger gone
                    // Otherwise the Hound eats the Fox and its hunger is
                    // gone
                    if (neighborCells.get(k).getOccupant() instanceof Fox)
                    {
                        // Iterate through all of the neighbor cells
                        // finding fox neighbors
                        for (int i = 0; i < neighborCells.size(); i++)
                        {
                            if (neighborCells.get(i).getOccupant() instanceof Fox)
                            {
                                neighboringFoxes.add(neighborCells.get(i));
                            } // if
                        } // for

                        // If we found any foxes that were neighboring
                        if (neighboringFoxes.size() > 0)
                        {

                            // Get a random Fox from the list
                            randomFoxIndex = random
                                    .nextInt(neighboringFoxes.size());

                            // Look at the random fox's neighbors to see if 
                            // there's  any hounds to mate with
                            neighborsOfNeighbors = getField().getNeighborsOf(
                                    neighboringFoxes.get(randomFoxIndex)
                                            .getXCoord(),
                                    neighboringFoxes.get(randomFoxIndex)
                                            .getYCoord());

                            // While we haven't found any neighboring
                            // hounds
                            while (j < neighborsOfNeighbors.size())
                            {
                                // If we find a neighbor that's a hound,
                                // keep track of it
                                if (neighborsOfNeighbors.get(j).getOccupant() 
                                        instanceof Hound)
                                {
                                    neighboringHounds.add(neighborsOfNeighbors.get(j));
                                } // if
                                j++;
                            } // while

                            // Prioritize the cells to figure out what
                            // to lock first
                            
                            
                            // Lock ourselves
                            synchronized (getField().getOccupantAt(getXCoord(), getYCoord()))
                            {
                                
                                // Since at this point, we're locking, 
                                // then this means we've chosen to 
                                // attempt an action. 
                                actionAttempted = true;
                                
                                // We want to lock the fox cell so no
                                // one else can match it
                                synchronized (neighboringFoxes.get(randomFoxIndex))
                                {
                                    
                                    // Make sure that there's still a fox in 
                                    // the cell
                                    if (neighboringFoxes.get(randomFoxIndex)
                                            .getOccupant() instanceof Fox)
                                    {
                                        // Eat!!!
                                        eats();
                                        System.out.println("Eat fox");
                                        
                                        // Kill the fox thread
                                        neighboringFoxes.get(randomFoxIndex)
                                                .getOccupant().interrupt();
                                        
                                        // Remove fox from the field 
                                        neighboringFoxes.get(randomFoxIndex).setOccupant(null);

                                        // If there is a hound to mate with, 
                                        // make a baby
                                        if (neighboringHounds.size() > 0)
                                        {
                                            // Pick a hound and hope it's 
                                            // still there
                                            synchronized (neighboringHounds.get(0))
                                            {
                                                if (neighboringHounds.get(0).getOccupant() instanceof Hound)
                                                {
                                                    // Create a new baby hound
                                                    neighboringFoxes.get(randomFoxIndex)
                                                            .setOccupant(
                                                             new Hound(
                                                             neighboringFoxes
                                                             .get(randomFoxIndex)
                                                             .getXCoord(),
                                                             neighboringFoxes
                                                             .get(randomFoxIndex)
                                                             .getYCoord(),
                                                             getPhaser(),
                                                             getField()));

                                                    // Start the new thread
                                                    neighboringFoxes.get(randomFoxIndex)
                                                            .getOccupant().start();
                                                } // if hound is still there
                                                
                                                System.out.println("Baby hound");
                                            } // synchronied
                                            
                
                                        } // if 

                                        // Signal the field to redraw itself
                                        synchronized (getField().getDrawField())
                                        {
                                            getField().setDrawField(true);
                                            getField().getDrawField().notify();
                                        } // synchronized 
                                    } // if neighbor is still fox
                                } // synchronize neighboring fox
                            } // synchronize yourself
                            
                        } // if we have fox neighbors
                    } // if we have a fox neighbor

                    /*
                     * If a Hound finds a neighboring empty cell that has 
                     * at least one other Hound as a neighbor and at least 
                     * two Foxes as neighbors then one of the Foxes is 
                     * consumed and a well-fed Hound is born into the empty
                     * cell. As above, the Hound that killed the Fox and 
                     * birthed the new Hound has its hunger gone.
                     */
                    else if (neighborCells.get(k).getOccupant() == null)
                    {
                        // Get all the neighbors of the empty cell
                        neighborsOfNeighbors = getField().getNeighborsOf(
                                neighborCells.get(0).getXCoord(),
                                neighborCells.get(0).getYCoord());

                        // See if there are any hound neighbors of the
                        // neighboring cell that aren't us
                        while (!hasHoundNeighbors
                                && j < neighborsOfNeighbors.size())
                        {
                            // If there's a neighbor of the empty cell that
                            // is a hound that isn't us
                            if (neighborsOfNeighbors.get(j).getOccupant() 
                                    instanceof Hound
                                    && neighborsOfNeighbors.get(j).getXCoord() != 
                                    getXCoord() && neighborsOfNeighbors.get(j).getYCoord() != 
                                            getYCoord())
                            /*if (neighborsOfNeighbors.get(j).getOccupant() 
                                    instanceof Hound
                                    && neighborsOfNeighbors.get(j) != 
                                    getField().getOccupantAt(getXCoord(), getYCoord()))*/
                            {
                                // Not the same object?? Mulstiple threads/cells? 
                                System.out.println(neighborsOfNeighbors.get(j));
                                System.out.println(getField().getOccupantAt(getXCoord(), getYCoord()));
                                // We've found another hound neighbor to
                                // mate with
                                hasHoundNeighbors = true;
                                System.out.println("True");
                            } // if
                            j++;
                        } // while

                        // If there are Hound neighbors
                        if (hasHoundNeighbors)
                        {
                            // Iterate through all of the neighbor cells
                            // to find foxes
                            for (int i = 0; i < neighborCells.size(); i++)
                            {
                                // If we find a fox neighbor
                                if (neighborCells.get(i).getOccupant() 
                                        instanceof Fox)
                                {
                                    // Keep track of the cell with the fox
                                    neighboringFoxes.add(
                                            neighborCells.get(i));
                                } // if
                            } // for

                            // If we have more than two fox neighbors
                            // we can eat one and have a baby
                            if (neighboringFoxes.size() > 2)
                            {
                                // Get a random Fox from the list
                                randomFoxIndex = random.nextInt(
                                        neighboringFoxes.size());

                                // Lock the fox cell so no one else can eat
                                // it
                                synchronized (neighboringFoxes.get(randomFoxIndex))
                                {
                                    // At this point, we've locked cells
                                    // so we're commited to attempting 
                                    // the action
                                    actionAttempted = true;
                                    
                                    // Make sure there's still a fox in the
                                    // cell
                                    if (neighboringFoxes.get(randomFoxIndex)
                                            .getOccupant() instanceof Fox)
                                    {
                                        synchronized(neighborCells.get(0))
                                        {
                                            if (neighborCells.get(0).getOccupant() == null)
                                            {
                                            
                                                // Create a new baby hound 
                                                // in empty cell
                                                neighborCells.get(0)
                                                        .setOccupant(
                                                        new Hound(
                                                        neighborCells.get(0)
                                                        .getXCoord(),
                                                        neighborCells.get(0)
                                                        .getYCoord(),
                                                        getPhaser(),
                                                        getField()));

                                                // Start the new thread
                                                neighborCells.get(0)
                                                        .getOccupant().start();

                                                
                                            } // if still empty cell
                                        } // sychronize empty cell 
                                        
                                        // EAT
                                        eats();
                                        System.out.println("Eat fox");
                                        
                                        // Kill the fox thread
                                        neighboringFoxes.get(randomFoxIndex)
                                                .getOccupant().interrupt();
                                        
                                        // Set fox to be null on field
                                        neighboringFoxes.get(randomFoxIndex).setOccupant(null);
                                        
                                        // Signal the field to redraw itself
                                        synchronized (getField().
                                                getDrawField())
                                        {
                                            getField().setDrawField(true);
                                            getField().getDrawField().notify();
                                        } // synchronize draw field
          
                                    } // if neighboring cell of empty holds fox
                                } // synchronize fox cell
                            } // if there are 2 foxes available
                        } // if there are hound neighbors

                    } // if the neighbor cell is empty
                    k++;
                } // while no action is attempted

                

                // Get the random sleep time for the hound
                sleepyTime = (long) random
                        .nextInt((MAX_SLEEP_TIME - MIN_SLEEP_TIME) + 1)
                        + MIN_SLEEP_TIME;

                // Sleep
                Thread.sleep(sleepyTime);

                // Wake up and check to see if we've died of starvation
                if (getHungrier(sleepyTime / (long) 1000.0))
                {
                    // If we've died, kill the thread
                    Thread.currentThread().interrupt();
                }

            }
        } catch (InterruptedException e)
        {
            synchronized (getField().getOccupantAt(getXCoord(), 
                    getYCoord()))
            {
                System.out.println("Hound death");
                getField().getOccupantAt(getXCoord(), 
                        getYCoord()).setOccupant(null);
                // Make the field draw again since there's been a change
                synchronized (getField().getDrawField())
                {
                    getField().setDrawField(true);
                    getField().getDrawField().notify();
                }
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