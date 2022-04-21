package levelEvents;

import physics.PhysicsEngine;

public class GravityShift implements ILevelEvent
{
    private boolean selfDestruct = false;
    private long targetTimeInMillis = 1_000;
    private float xAxisGravityPerMilli;
    private float yAxisGravityPerMilli;
    private PhysicsEngine physicsEngine;

    public GravityShift(float xAxisGravityPerMilli, float yAxisGravityPerMilli, PhysicsEngine physicsEngine)
    {
        this.xAxisGravityPerMilli = xAxisGravityPerMilli;
        this.yAxisGravityPerMilli = yAxisGravityPerMilli;
        this.physicsEngine = physicsEngine;
    }

    @Override
    public void setCurrentTime(long millisInState)
    {
        if (millisInState > targetTimeInMillis && !this.selfDestruct)   //If it's the target time and this event hasn't happened yet.
        {
            this.physicsEngine.setGravity(this.xAxisGravityPerMilli, this.yAxisGravityPerMilli);
            this.selfDestruct = true;
        }
    }

    @Override
    public void setNewTargetTime(long targetTimeInMillis)
    {
        this.targetTimeInMillis = targetTimeInMillis;
    }

    @Override
    public boolean isReadyToSelfDestruct()
    {
        return this.selfDestruct;
    }

    @Override
    public void cancel()
    {
        this.selfDestruct = true;
        this.targetTimeInMillis = 999_999_999;
    }
}
