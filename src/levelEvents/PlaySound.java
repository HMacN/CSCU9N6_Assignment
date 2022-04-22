package levelEvents;

import soundsAndMusic.IGameSound;
import spaceShipGame.GameObjects;

/**
 * A level event which stores an IGameSound object to add to the game at the specified time.
 */
public class PlaySound implements ILevelEvent
{
    private boolean selfDestruct = false;
    private long targetTimeInMillis = 1_000;
    private IGameSound gameSound;
    private GameObjects gameObjects;

    /**
     * The constructor.
     * @param gameSound An IGameSound object to be added to the game.
     * @param gameObjects   A GameObjects object which is the collection of in-game objects to add the drawable to.
     */
    public PlaySound(IGameSound gameSound, GameObjects gameObjects)
    {
        this.gameSound = gameSound;
        this.gameObjects = gameObjects;
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
            this.gameObjects.addSound(this.gameSound);
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
     * @return
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
