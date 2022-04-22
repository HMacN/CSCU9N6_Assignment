//  Assignment submission for CSCU9N6
//  Student Number: 2823735/1
//  Date of Submission: 22/04/2022

package levelEvents;

import CSCU9N6Library.TileMap;
import factories.IGameObjectFactory;
import helperClasses.TilemapHelper;

/**
 * A level event which spawns a particular type of game objects at each tile in the tilemap which corresponds to a particular tile type.
 */
public class SpawnGameObjects implements ILevelEvent
{
    private boolean selfDestruct = false;
    private long targetTimeInMillis = 1_000;
    private TilemapHelper.ETileType tileType;
    private TileMap tileMap;
    private IGameObjectFactory factory;

    /**
     * The constructor.
     * @param tileType  An enum which describes the type of tile to spawn the new objects on.
     * @param tileMap   A TileMap to check for possible spawning locations on.
     * @param factory   An IGameObjectFactory which produces the desired new game objects.
     */
    public SpawnGameObjects(TilemapHelper.ETileType tileType, TileMap tileMap, IGameObjectFactory factory)
    {
        this.tileType = tileType;
        this.tileMap = tileMap;
        this.factory = factory;
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
            TilemapHelper.spawnEntityOnMap(tileType, this.tileMap, this.factory);
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
