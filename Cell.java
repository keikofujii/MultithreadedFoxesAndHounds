/**
 * This class describes a cell in the Foxes and Hounds field
 */
public class Cell implements Comparable<Cell>
{
    /**
     * Constructor for Cell class
     * 
     * @param xCoord the x coordinate of the cell
     * @param yCoord the y coordinate of the cell
     * @param occupant this is an optional parameter that will default
     * to null. It holds the occupant of the cell.
     */
    public Cell(int xCoord, int yCoord, FieldOccupant occupant)
    {
        p_xCoord = xCoord;
        p_yCoord = yCoord;
        p_occupant = occupant;
    }
    
    /*
     * Constructor for Cell class without a occupant parameter
     * The occupant will default to null in this case
     * 
     * @param xCoord the x coordinate of the cell
     * @param yCoord the y coordinate of the cell
     */
    public Cell(int xCoord, int yCoord)
    {
        p_xCoord = xCoord;
        p_yCoord = yCoord;
        p_occupant = null;
    }
    
    /**
     * A method to get the x coordinate of the cell
     * @return the x coordinate of the cell
     */
    public int getXCoord()
    {
        return p_xCoord;
    }

    /**
     * A method to get the y coordinate of the cell
     * @return the y coordinate of the cell
     */
    public int getYCoord()
    {
        return p_yCoord;
    }

    /**
     * A method to get the occupant of the cell
     * @return the occupant of the cell
     */
    public FieldOccupant getOccupant()
    {
        return p_occupant;
    }

    /**
     * A method to set the occupant of the cell
     * @param occupant the value to set the occupant to
     */
    public void setOccupant(FieldOccupant occupant)
    {
        p_occupant = occupant;
    }

    /**
     * Compares this object with the specified object for order. Returns 
     * a negative integer, zero, or a positive integer as this object is 
     * less than, equal to, or greater than the specified object.
     * 
     * Comparator is based on the X coordinate and Y coordinate of the 
     * cell
     * 
     * Cells that have a higher Y coordinate have higher priority, with
     * ties being broken by the X coordinate (higher coordinate has 
     * higher priority)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Cell o)
    {
        // Default to having the elements equal
        int compare = 0;
        
        // If this Y coordinate is greater, then this object has
        // higher priority
        if (getYCoord() > o.getYCoord())
        {
            compare = 1;
        }
        // Else if the specified object's Y coordinate is greater, then
        // it has higher priority
        else if (o.getYCoord() > getYCoord())
        {
            compare = -1;
        }
        // Else Y Coords are equal, now look at the X coords
        else
        {
            // If this object's X coordinate is greater, then it has
            // greater priority
            if (getXCoord() > o.getXCoord())
            {
                compare = 1;
            }
            // Else if the specified object's X coordinate is greater
            // then it has greater priority
            else if (o.getXCoord() > getXCoord())
            {
                compare = -1;
            }
        }
        
        return compare;
    }

    private int p_xCoord;
    private int p_yCoord;
    private FieldOccupant p_occupant;
}
