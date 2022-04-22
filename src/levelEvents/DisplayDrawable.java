package levelEvents;

import renderableObjects.IDrawable;
import spaceShipGame.GameObjects;

/**
 * A level event which stores an IDrawable object and adds it to the game at the allotted time.
 */
public class DisplayDrawable implements ILevelEvent
{
    private boolean selfDestruct = false;
    private long targetTimeInMillis = 1_000;
    private IDrawable drawable;
    private GameObjects gameObjects;
    private GameObjects.ERenderLayer renderLayer;

    /**
     * The constructor.
     * @param drawable  An IDrawable object to spawn in to the game.
     * @param gameObjects   A GameObjects object which is the collection of in-game objects to add the drawable to.
     * @param renderLayer   An Enum which is the render layer that the drawable should be added at.
     */
    public DisplayDrawable(IDrawable drawable, GameObjects gameObjects, GameObjects.ERenderLayer renderLayer)
    {
        this.drawable = drawable;
        this.gameObjects = gameObjects;
        this.renderLayer = renderLayer;
    }

    /**
     * Notifies this event of how much time has elapsed in the current game state.
     * @param millisInState A long which is the elapsed time that the game has been in its current state.
     */
    @Override
    public void updateCurrentTime(long millisInState)
    {
        if (millisInState > targetTimeInMillis && !this.selfDestruct)   //If it's the target time and this event hasn't happened yet.
        {
            this.gameObjects.addDrawable(drawable, renderLayer);
            this.selfDestruct = true;
        }
    }

    /**
     * Resets the target time for this event.  Note that setting it to before the current time will cause the event to trigger.
     * @param targetTimeInMillis    A long which is the new target time for the event.
     */
    @Override
    public void setNewTargetTime(long targetTimeInMillis)
    {
        this.targetTimeInMillis = targetTimeInMillis;
    }

    /**
     * A getter for the self destruct flag.  Used to determine whether or not to remove the event rather than update it.
     * @return  A boolean which represents the self-destruct flag.
     */
    @Override
    public boolean isReadyToSelfDestruct()
    {
        return this.selfDestruct;
    }

    /**
     * A setter for the self-destruct flag.  Also sets the target time to a very high number to help ensure that the event does not accidentally trigger.
     */
    @Override
    public void cancel()
    {
        this.selfDestruct = true;
        this.targetTimeInMillis = 999_999_999;
    }
}
