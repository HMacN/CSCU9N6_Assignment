package levelEvents;

import spaceShipGame.SpaceshipGame;

public class TerminateState implements ILevelEvent
{
    private boolean selfDestruct = false;
    private long targetTimeInMillis = 1_000;
    private SpaceshipGame spaceshipGame;

    public TerminateState(SpaceshipGame spaceshipGame)
    {
        this.spaceshipGame = spaceshipGame;
    }

    @Override
    public void setCurrentTime(long millisInState)
    {
        if (millisInState > targetTimeInMillis && !this.selfDestruct)   //If it's the target time and this event hasn't happened yet.
        {
            this.spaceshipGame.loadNextGameState();
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
