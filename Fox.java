import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Phaser;

/**
 * Foxes can display themselves
 */

public class Fox extends FieldOccupant
{
    public Fox(int i, int j, Phaser phaser, Field theField)
    {
        super(i, j, phaser, theField);
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
        int foxesFound = 0;
        int i;
        int j;
        int houndCount = 0;
        
        Random random = new Random();
        
        try
        {
            // Register the new object
            getPhaser().register();

            // Arrive and wait for the rest of the objects
            getPhaser().arriveAndAwaitAdvance();
            
            // should get rid of this
            Thread.sleep(1000);
      
            
            // Run forever, or until we're terminated
            while (true)
            {
                i = 0;
                j = 0;
                // Reset hound count for each neighboring cell that we look at
                houndCount = 0;
                foxesFound = 0;
                
                neighborCells = p_theField.getNeighborsOf(getXCoord(), getYCoord());

                // Iterate through all neighboring cells and all neighbors of
                // neighboring cells
                // Keep looking while you haven't found a soulmate and the soulmate
                // isn't surrounded by more than one hound
                while (foxesFound < 2 && houndCount <= 1
                        && i < neighborCells.size())
                {
                    // Reset hound count for each neighboring cell that we look at
                    houndCount = 0;
                    foxesFound = 0;

                    // Look at all neighbors of neighbor if the neighbor is empty
                    if (neighborCells.get(i).getOccupant() == null)
                    {
                        j = 0;
                        // Get the neighbors of the neighboring cell
                        neighborsOfNeighbors = p_theField.getNeighborsOf(neighborCells
                                .get(i).getXCoord(), neighborCells.get(i).getYCoord());
                        
                        while (j < neighborsOfNeighbors.size())
                        {
                            // Need to synchronize?
                            // If it's a fox (hopefully a cute one), then they're a good
                            // dating prospect
                            if (neighborsOfNeighbors.get(j).getOccupant() instanceof Fox)
                            {
                                foxesFound++;
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
                if (foxesFound >= 2 && houndCount <= 1)
                {
                    i--;
                    synchronized (neighborCells.get(i))
                    {
                        // **** while or if?
                        // Since we broke out of the while loop once we found a
                        // successful fox match, then we can use i to get the
                        // square that we want to breed in
                        // Check to make sure first, though that the square
                        // we were looking at is still empty
                        if (neighborCells.get(i).getOccupant() == null)
                        {
                            // Birth a fox
                            neighborCells.get(i).setOccupant(
                                    new Fox(neighborCells.get(i).getXCoord(),
                                            neighborCells.get(i).getYCoord(),
                                            getPhaser(), getField()));
                            
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

                   
                Thread.sleep(random
                        .nextInt((MAX_SLEEP_TIME - MIN_SLEEP_TIME) + 1)
                        + MIN_SLEEP_TIME);

            }
        }
        catch (InterruptedException e)
        {
            // We were eaten. Which is unfortunate.
            System.out.println("Fox died");
            getField().getOccupantAt(getXCoord(), getYCoord()).setOccupant(null);
            // Make the field draw again since there's been a change
            synchronized (getField().getDrawField())
            {
                getField().setDrawField(true);
                getField().getDrawField().notify();
            }
        }
    }
}