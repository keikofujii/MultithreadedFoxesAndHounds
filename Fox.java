import java.awt.Color;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.Phaser;

/**
 * This class describes the Fox object for multithreaded Foxes and Hounds
 */

public class Fox extends FieldOccupant
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
    public Fox(int xCoord, int yCoord, Phaser phaser, Field theField)
    {
        super(xCoord, yCoord, phaser, theField);
    }

    /**
     * @return the color to use for a cell occupied by a Fox
     */
    @Override
    public Color getDisplayColor()
    {
        return Color.green;
    } // getDisplayColor

    /**
     * @return the text representing a Fox
     */
    @Override
    public String toString()
    {
        return "F";
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
        int i;
        int j;
        int houndCount = 0;
        int randomFoxIndex;
        PriorityQueue<Cell> cellsToLock;
        
        Random random = new Random();
        
        try
        {
            // Arrive and wait for the rest of the objects
            getPhaser().awaitAdvance(0);
            
            // should get rid of this
            Thread.sleep(1000);
      
            
            // Run forever, or until we're terminated
            while (true && !Thread.interrupted())
            {
                i = 0;
                j = 0;
                // Reset hound count for each neighboring cell that we look at
                houndCount = 0;
                neighboringFoxes = new ArrayList<Cell>();
                cellsToLock = new PriorityQueue<Cell>();
                neighborCells = p_theField.getNeighborsOf(getXCoord(), getYCoord());
                
                // Iterate through all neighboring cells and all neighbors of
                // neighboring cells
                // Keep looking while the cell in question
                // isn't surrounded by more than one hound
                // and while it doesn't have more than 2 neighboring
                // foxes
                while (neighboringFoxes.size() < 2 && houndCount <= 1 && i < neighborCells.size())
                {
                    // Reset hound count and neighboring foxes for each 
                    // neighboring cell that we look at
                    houndCount = 0;
                    neighboringFoxes = new ArrayList<Cell>();

                    // Look at all neighbors of neighbor if the neighbor is empty
                    if (neighborCells.get(i).getOccupant() == null)
                    {
                        j = 0;
                        // Get the neighbors of the neighboring cell
                        neighborsOfNeighbors = p_theField.getNeighborsOf(neighborCells
                                .get(i).getXCoord(), neighborCells.get(i).getYCoord());
                        
                        // Iterate through all the neighbors and 
                        // keep track of what type of animal they are
                        while (j < neighborsOfNeighbors.size())
                        {
                            // If it's a fox (hopefully a cute one), 
                            // then they're a good
                            // dating prospect if they're not us
                            if (neighborsOfNeighbors.get(j).getOccupant() instanceof Fox)
                            {
                                neighboringFoxes.add(neighborsOfNeighbors.get(j));
                            }
                            // Keep track of how many hounds we have
                            else if (neighborsOfNeighbors.get(j).getOccupant() instanceof Hound)
                            {
                                houndCount++;
                            }
                            j++;

                        }
                        
                    }
                    i++;
                }
                
                // See if a neighboring cell has another Fox as neighbor
                // Make sure that neighbor has at most one Hound as neighbor
                // If both are true then give birth to one fox
                if (neighboringFoxes.size() >= 2 && houndCount <= 1)
                {
                    // decrement because before we exited the while loop
                    // we incremented
                    i--;
                    
                    // Get a random Fox from the list
                    randomFoxIndex = random
                            .nextInt(neighboringFoxes.size());
                
                    // Get the null cell
                    cellsToLock.add(neighborCells.get(i));
                    // Lock ourselves
                    cellsToLock.add(getField().getCellAt(getXCoord(), getYCoord()));
                    // Lock the hound we want to breed with
                    cellsToLock.add(neighboringFoxes.get(randomFoxIndex));
                    
                    // get all the locks
                    synchronized (cellsToLock.poll())
                    {
                        synchronized (cellsToLock.poll())
                        {
                            synchronized (cellsToLock.poll())
                            {
                                // Since we broke out of the while loop once we found a
                                // successful fox match, then we can use i to get the
                                // square that we want to breed in
                                // Check to make sure first, though that the square
                                // we were looking at is still empty
                                if (neighborCells.get(i).getOccupant() == null)
                                {
                                    // If the occupant that we want to 
                                    // mate with is still a Fox
                                    // then have a baby
                                    if (neighboringFoxes.get(randomFoxIndex).getOccupant() instanceof Fox)
                                    {
                                        // Birth a fox
                                        neighborCells.get(i).setOccupant(
                                                new Fox(neighborCells.get(i).getXCoord(),
                                                        neighborCells.get(i).getYCoord(),
                                                        getPhaser(), getField()));
                                        
                                        // Start the new thread
                                        neighborCells.get(i).getOccupant().start();
                                        
                                        // Make the field draw again since there's been a change
                                        synchronized (getField().getDrawField())
                                        {
                                            getField().setDrawField(true);
                                            getField().getDrawField().notify();
                                        }
                                    }
                                    
                                }
                                
                            }
                        }
                    }
                    
                    

                }

                Thread.sleep(random
                        .nextInt((MAX_SLEEP_TIME - MIN_SLEEP_TIME) + 1)
                        + MIN_SLEEP_TIME);
                
            }
        }
        catch (InterruptedException e)
        {
            // We were eaten. Which is unfortunate.
            synchronized(getField().getCellAt(getXCoord(), getYCoord()))
            {
                // Make the field draw again since there's been a change
                synchronized (getField().getDrawField())
                {
                    getField().setDrawField(true);
                    getField().getDrawField().notify();
                }
            }
        }
    }
}