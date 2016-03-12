import java.awt.Color;
import java.util.concurrent.Phaser;

/**
 * Foxes can display themselves
 */
// Passive about being eaten
// If find neighboring empty cell that has another fox as neighbor 
// and cll has at most one Hound as a neighbor (sleeping or not)
// then new Fox is born (only one Fox)
// If eaten while checking, then update Field
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
   }
}