/**
 * This class describes a cell in the Foxes and Hounds field
 */
public class Cell
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
    
    public int getXCoord()
    {
        return p_xCoord;
    }

    public int getYCoord()
    {
        return p_yCoord;
    }

    public FieldOccupant getOccupant()
    {
        return p_occupant;
    }

    public void setOccupant(FieldOccupant p_occupant)
    {
        this.p_occupant = p_occupant;
    }


    private int p_xCoord;
    private int p_yCoord;
    private FieldOccupant p_occupant;
}
