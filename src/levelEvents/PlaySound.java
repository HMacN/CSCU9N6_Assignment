package levelEvents;

import soundsAndMusic.IGameSound;
import spaceShipGame.GameObjects;

public class PlaySound implements ILevelEvent
{
    private boolean selfDestruct = false;
    private long targetTimeInMillis = 1_000;
    private IGameSound gameSound;
    private GameObjects gameObjects;

    public PlaySound(IGameSound gameSound, GameObjects gameObjects)
    {
        this.gameSound = gameSound;
        this.gameObjects = gameObjects;
    }

    @Override
    public void setCurrentTime(long millisInState)
    {
        if (millisInState > targetTimeInMillis && !this.selfDestruct)   //If it's the target time and this event hasn't happened yet.
        {
            this.gameObjects.addSound(this.gameSound);
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
