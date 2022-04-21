package levelEvents;

import CSCU9N6Library.TileMap;
import renderableObjects.IDrawable;
import spaceShipGame.GameObjects;

public class DisplayDrawable implements ILevelEvent
{
    private boolean selfDestruct = false;
    private long targetTimeInMillis = 1_000;
    private IDrawable drawable;
    private GameObjects gameObjects;
    private GameObjects.ERenderLayer renderLayer;

    public DisplayDrawable(IDrawable drawable, GameObjects gameObjects, GameObjects.ERenderLayer renderLayer)
    {
        this.drawable = drawable;
        this.gameObjects = gameObjects;
        this.renderLayer = renderLayer;
    }

    @Override
    public void setCurrentTime(long millisInState)
    {
        if (millisInState > targetTimeInMillis && !this.selfDestruct)   //If it's the target time and this event hasn't happened yet.
        {
            this.gameObjects.addDrawable(drawable, renderLayer);
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
