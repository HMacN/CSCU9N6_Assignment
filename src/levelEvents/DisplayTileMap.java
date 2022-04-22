package levelEvents;

import CSCU9N6Library.TileMap;
import spaceShipGame.GameObjects;

/**
 * A level event which stores a TileMap object and adds it to the game at the allotted time.
 */
public class DisplayTileMap implements ILevelEvent
{
    private boolean selfDestruct = false;
    private long targetTimeInMillis = 1_000;
    private TileMap tileMap;
    private GameObjects gameObjects;
    private int screenWidth;
    private int screenHeight;

    /**
     * The constructor.
     * @param tileMap   A TileMap object which is to be added to the game.
     * @param gameObjects   A GameObjects object, which is the collection of in-game objects to add the TileMap to.
     * @param screenWidth   An int which is the width of the display area in pixels.
     * @param screenHeight  An int which is the height of the display area in pixels.
     */
    public DisplayTileMap(TileMap tileMap, GameObjects gameObjects, int screenWidth, int screenHeight)
    {
        this.tileMap = tileMap;
        this.gameObjects = gameObjects;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
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
            this.gameObjects.addTileMap(this.tileMap);
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
