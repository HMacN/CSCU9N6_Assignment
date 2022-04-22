package levelEvents;

import factories.EntityUpdateFactory;

/**
 * A level event which changes the spaceship speed (the speed of the background star field).
 */
public class ShipManoeuvre implements ILevelEvent
{
    private boolean selfDestruct = false;
    private long targetTimeInMillis = 1_000;
    private float xSpeed;
    private float ySpeed;
    private EntityUpdateFactory updateFactory;

    /**
     * The constructor.
     * @param xSpeed    A float which is the new horizontal speed of the spaceship.
     * @param ySpeed    A float which is the new vertical speed of the spaceship.
     * @param updateFactory An EntityUpdateFactory which is to be notified of the speed changes.
     */
    public ShipManoeuvre(float xSpeed, float ySpeed, EntityUpdateFactory updateFactory)
    {
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.updateFactory = updateFactory;
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
            this.updateFactory.setSpaceshipXSpeed(this.xSpeed);
            this.updateFactory.setSpaceshipYSpeed(this.ySpeed);
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
     */    @Override
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
