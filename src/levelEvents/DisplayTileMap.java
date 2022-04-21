package levelEvents;

import CSCU9N6Library.TileMap;
import spaceShipGame.GameObjects;

public class DisplayTileMap implements ILevelEvent
{
    private boolean selfDestruct = false;
    private long targetTimeInMillis = 1_000;
    private TileMap tileMap;
    private GameObjects gameObjects;
    private int screenWidth;
    private int screenHeight;

    public DisplayTileMap(TileMap tileMap, GameObjects gameObjects, int screenWidth, int screenHeight)
    {
        this.tileMap = tileMap;
        this.gameObjects = gameObjects;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public void setCurrentTime(long millisInState)
    {
        if (millisInState > targetTimeInMillis && !this.selfDestruct)   //If it's the target time and this event hasn't happened yet.
        {
            this.gameObjects.addTileMap(this.tileMap);
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
