package levelEvents;

import CSCU9N6Library.TileMap;
import factories.IGameObjectFactory;
import helperClasses.TilemapHelper;

public class SpawnGameObjects implements ILevelEvent
{
    private boolean selfDestruct = false;
    private long targetTimeInMillis = 1_000;
    private TilemapHelper.ETileType tileType;
    private TileMap tileMap;
    private IGameObjectFactory factory;

    public SpawnGameObjects(TilemapHelper.ETileType tileType, TileMap tileMap, IGameObjectFactory factory)
    {
        this.tileType = tileType;
        this.tileMap = tileMap;
        this.factory = factory;
    }

    @Override
    public void setCurrentTime(long millisInState)
    {
        if (millisInState > targetTimeInMillis && !this.selfDestruct)   //If it's the target time and this event hasn't happened yet.
        {
            TilemapHelper.spawnEntityOnMap(tileType, this.tileMap, this.factory);
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
