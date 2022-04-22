package levelEvents;

import physics.PhysicsEngine;

/**
 * A level event which changes the gravity on the spaceship.
 */
public class GravityShift implements ILevelEvent
{
    private boolean selfDestruct = false;
    private long targetTimeInMillis = 1_000;
    private float xAxisGravityPerMilli;
    private float yAxisGravityPerMilli;
    private PhysicsEngine physicsEngine;

    /**
     * The constructor.
     * @param xAxisGravityPerMilli  The new horizontal gravity in pixels per millisecond.
     * @param yAxisGravityPerMilli  The new vertical gravity in pixels per millisecond.
     * @param physicsEngine     A PhysicsEngine object whose gravity settings will be adjusted.
     */
    public GravityShift(float xAxisGravityPerMilli, float yAxisGravityPerMilli, PhysicsEngine physicsEngine)
    {
        this.xAxisGravityPerMilli = xAxisGravityPerMilli;
        this.yAxisGravityPerMilli = yAxisGravityPerMilli;
        this.physicsEngine = physicsEngine;
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
            this.physicsEngine.setGravity(this.xAxisGravityPerMilli, this.yAxisGravityPerMilli);
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
