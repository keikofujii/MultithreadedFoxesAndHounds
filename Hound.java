import java.awt.Color;
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
// the Foxes is consumed and a well-fed Hound is born into the empty cell. 
// As above, the Hound that killed the Fox and birthed the new Hound has 
// its hunger gone.
// If no Fox, or if task is not completed, then hunger increases proportionally
// to length of time it was asleep
// If it starves, it dies (thread terminates itself
// Fox is passive about being eaten
// Should only birth/eat one thing
// Fox cannot be eaten before it is birthed
// A Hound can eat a Fox that's in the middle of checking neighboring cells
// Interrupt threads to terminate
public class Hound extends FieldOccupant 
{ 
   /**
    * Create a hound 
    */
   public Hound(int i, int j, Phaser phaser)
   {   
       super(i, j, phaser);
      // Start out well-fed
      eats();
   }


   /**
    * @return true if this Hound has starved to death
    */
   public boolean hasStarved()
   {
      return p_fedStatus == 0;
   }

   /**
    * Make this Hound hungrier
    *
    * @return true if the Hound has starved to death
    */
   public boolean getHungrier()
   {
      // Decrease the fed status of this Hound
      p_fedStatus--;
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
       // Register the new object
       getPhaser().register();
       
       // Arrive and wait for the rest of the objects
       getPhaser().arriveAndAwaitAdvance(); 
   }

   // Default starve time for Hounds
   public static final float DEFAULT_STARVE_TIME = 3;
   private static float p_houndStarveTime = DEFAULT_STARVE_TIME; // Class variable for all hounds

   // Instance attributes to keep track of how hungry we are
   private float p_fedStatus;

}