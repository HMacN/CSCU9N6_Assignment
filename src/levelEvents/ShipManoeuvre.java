package levelEvents;

import factories.EntityUpdateFactory;

public class ShipManoeuvre implements ILevelEvent
{
    private boolean selfDestruct = false;
    private long targetTimeInMillis = 1_000;
    private float xSpeed;
    private float ySpeed;
    private EntityUpdateFactory updateFactory;

    public ShipManoeuvre(float xSpeed, float ySpeed, EntityUpdateFactory updateFactory)
    {
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.updateFactory = updateFactory;
    }

    @Override
    public void setCurrentTime(long millisInState)
    {
        if (millisInState > targetTimeInMillis && !this.selfDestruct)   //If it's the target time and this event hasn't happened yet.
        {
            this.updateFactory.setSpaceshipXSpeed(this.xSpeed);
            this.updateFactory.setSpaceshipYSpeed(this.ySpeed);
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
