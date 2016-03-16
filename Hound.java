import java.awt.Color;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.Phaser;


/**
 * This class describes the Hound object for Multithreaded Foxes and 
 * Hounds
 */
public class Hound extends FieldOccupant
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
                                        instanceof Hound
                                        && neighborsOfNeighbors.get(j).getXCoord() != 
                                        getXCoord() && neighborsOfNeighbors.get(j).getYCoord() != 
                                                getYCoord())
                                {
                                    neighboringHounds.add(neighborsOfNeighbors.get(j));
                                } // if
                                j++;
                            } // while
                            
                            if (neighboringHounds.size() > 0)
                            {
                                // Prioritize the cells to figure out what
                                // to lock first
                                // Will eventually lock
                                // Ourselves
                                cellsToLock.add(getField().getCellAt(getXCoord(), getYCoord()));
                                
                                // The fox we plan to eat
                                cellsToLock.add(neighboringFoxes.get(randomFoxIndex));
                                
                                // The hound to potentially mate with
                                cellsToLock.add(neighboringHounds.get(0));
                                
                                // Lock the first cell
                                synchronized (cellsToLock.poll())
                                {
                                    // Since at this point, we're locking, 
                                    // then this means we've chosen to 
                                    // attempt an action. 
                                    actionAttempted = true;
                                    
                                    // Lock the second cell
                                    synchronized (cellsToLock.poll())
                                    {
                                        
                                        // Lock the third cell
                                        synchronized (cellsToLock.poll())
                                        {
                                            // Make sure that there's still a fox in 
                                            // the cell
                                            if (neighboringFoxes.get(randomFoxIndex)
                                                    .getOccupant() instanceof Fox)
                                            {
                                                // Eat!!!
                                                eats();
                                                
                                                // Kill the fox thread
                                                neighboringFoxes.get(randomFoxIndex)
                                                        .getOccupant().interrupt();
                                                
                                                // Remove fox from the field 
                                                neighboringFoxes.get(randomFoxIndex).setOccupant(null);
        
                                                // If there is a hound to mate with, 
                                                // make a baby
                                                if (neighboringHounds.size() > 0)
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
                                
                            } // if you have hound neighbors
                            else
                            {
                                // Prioritize the cells to figure out what
                                // to lock first
                                // Will eventually lock
                                // Ourselves
                                cellsToLock.add(getField().getCellAt(getXCoord(), getYCoord()));
                                
                                // The fox we plan to eat
                                cellsToLock.add(neighboringFoxes.get(randomFoxIndex));
                                
                                
                                // Lock the first cell
                                synchronized (cellsToLock.poll())
                                {
                                    // Since at this point, we're locking, 
                                    // then this means we've chosen to 
                                    // attempt an action. 
                                    actionAttempted = true;
                                    
                                    // Lock the second cell
                                    synchronized (cellsToLock.poll())
                                    {
                                        // Make sure that there's still a fox in 
                                        // the cell
                                        if (neighboringFoxes.get(randomFoxIndex)
                                                .getOccupant() instanceof Fox)
                                        {
                                            // Eat!!!
                                            eats();

                                            
                                            // Kill the fox thread
                                            neighboringFoxes.get(randomFoxIndex)
                                                    .getOccupant().interrupt();
                                            
                                            // Remove fox from the field 
                                            neighboringFoxes.get(randomFoxIndex).setOccupant(null);
    
                                            // If there is a hound to mate with, 
                                            // make a baby
                                            if (neighboringHounds.size() > 0)
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
                                                
                                            } // synchronied
                                            
                
                                        } // if neighbor is still fox

                                        // Signal the field to redraw itself
                                        synchronized (getField().getDrawField())
                                        {
                                            getField().setDrawField(true);
                                            getField().getDrawField().notify();
                                        } // synchronized 
                                    } // synchronize neighboring fox
                                } // synchronize yourself
                            } // else
                            
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
                        while (j < neighborsOfNeighbors.size())
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
                                // We've found another hound neighbor to
                                // mate with
                            
                                neighboringHounds.add(neighborsOfNeighbors.get(j));
                          
                            } // if
                            j++;
                        } // while

                        // If there are Hound neighbors
                        if (neighboringHounds.size() > 0)
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

                                // Prioritize the cells to figure out what
                                // to lock first
                                // Will eventually lock
                                // The fox we plan to eat
                                cellsToLock.add(neighboringFoxes.get(randomFoxIndex));
                                // Get the null cell
                                cellsToLock.add(neighborCells.get(0));
                                // Lock ourselves
                                cellsToLock.add(getField().getCellAt(getXCoord(), getYCoord()));
                                // Lock the hound we want to breed with
                                cellsToLock.add(neighboringHounds.get(0));
                                
                                // Lock the fox cell so no one else can eat
                                // it
                                synchronized (cellsToLock.poll())
                                {
                                    // At this point, we've locked cells
                                    // so we're commited to attempting 
                                    // the action
                                    actionAttempted = true;
                                    
                                    synchronized(cellsToLock.poll())
                                    {
                                        synchronized(cellsToLock.poll())
                                        {
                                            synchronized(cellsToLock.poll())
                                            {
                                                // Make sure there's still a fox in the
                                                // cell
                                                if (neighboringFoxes.get(randomFoxIndex)
                                                        .getOccupant() instanceof Fox)
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
                                                    
                                                    // EAT
                                                    eats();
                                                    
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
                                                    
                                                } // sychronize empty cell 
                                                
                                                
                                            }
                                        }
          
                                    } // if neighboring cell of empty holds fox
                                } // synchronize fox cell
                            } // if there are 2 foxes available
                        } // if there are hound neighbors

                    } // if the neighbor cell is empty
                    k++;
                } // while no action is attempted

                

                // Get the random sleep time for the hound
                sleepyTime = (long) random
                        .nextInt(MAX_SLEEP_TIME - MIN_SLEEP_TIME)
                        + MIN_SLEEP_TIME;

                // Sleep
                Thread.sleep(sleepyTime);

                // Wake up and check to see if we've died of starvation
                if (getHungrier(sleepyTime))
                {
                    // If we've died, kill the thread
                    Thread.currentThread().interrupt();
                }
                
            }
            getField().getCellAt(getXCoord(), 
                    getYCoord()).setOccupant(null);
            
            
            
        } catch(InterruptedException e)
        {
            synchronized (getField().getCellAt(getXCoord(), 
                    getYCoord()))
            {
                getField().getCellAt(getXCoord(), 
                        getYCoord()).setOccupant(null);
                
            }
            // Make the field draw again since there's been a change
            synchronized (getField().getDrawField())
            {
                getField().setDrawField(true);
                getField().getDrawField().notify();
            }
        }
    }

    // Default starve time for Hounds
    public static final long DEFAULT_STARVE_TIME = 3000;
    // Class variable for all hounds
    private static float p_houndStarveTime = DEFAULT_STARVE_TIME;

    // Instance attributes to keep track of how hungry we are
    private float p_fedStatus;

}