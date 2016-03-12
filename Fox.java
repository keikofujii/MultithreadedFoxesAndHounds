import java.awt.Color;
import java.util.concurrent.Phaser;

/**
 * Foxes can display themselves
 */

public class Fox extends FieldOccupant 
{ 
   public Fox(int i, int j, Phaser phaser)
    {
        super(i, j, phaser);
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
       // Register the new object
       getPhaser().register();
       
       // Arrive and wait for the rest of the objects
       getPhaser().arriveAndAwaitAdvance(); 
       
       // Run forever, or until we're terminated 
       while (true)
       {
           
           // See if a neighboring cell has another Fox as neighbor
           
           // Make sure that neighbor has at most one Hound as neighbor
           
           // If both are true then give birth to one fox 
           
           // Sleep
       }
   }
}