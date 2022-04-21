package factories;

import CSCU9N6Library.TileMap;
import gameStates.EGameState;
import gameStates.IGameState;
import helperClasses.TilemapHelper;
import levelEvents.*;
import renderableObjects.IDrawable;
import soundsAndMusic.IGameSound;
import spaceShipGame.GameObjects;
import spaceShipGame.SpaceshipGame;

/**
 * A factory which makes it easier to add events to levels (Game State Objects).
 */
public class LevelEventFactory
{
    private SpaceshipGame spaceshipGame;
    private IGameState level;

    /**
     * The constructor.
     * @param spaceshipGame The SpaceshipGame object to get the required data from.
     */
    public LevelEventFactory(SpaceshipGame spaceshipGame)
    {
        this.spaceshipGame = spaceshipGame;
    }

    /**
     * Sets the game state to add the level events to.
     * @param gameState An IGameState object which the level events are to be added to.
     */
    public void setGameState(IGameState gameState)
    {
        this.level = gameState;
    }

    /**
     * Generates and adds a DisplayDrawable event.
     * @param drawable  The IDrawable object to add to the game state.
     * @param renderLayer   The render layer this object is to be drawn at.
     * @param eventTime A long which is the time to perform this event at.
     */
    public void addDisplayDrawableEvent(IDrawable drawable, GameObjects.ERenderLayer renderLayer, long eventTime)
    {
        this.level.addLevelEvent(new DisplayDrawable(drawable, this.spaceshipGame.getGameObjects(), renderLayer), eventTime);
    }

    /**
     * Generates and adds a DisplayTileMap event
     * @param tileMap   A TileMap object which is to be displayed.
     * @param eventTime A long which is the time to perform the event at.
     */
    public void addDisplayTileMapEvent(TileMap tileMap, long eventTime)
    {
        this.level.addLevelEvent(new DisplayTileMap(tileMap, this.spaceshipGame.getGameObjects(), this.spaceshipGame.getScreenWidth(), this.spaceshipGame.getScreenHeight()), eventTime);
    }

    /**
     * Generates and adds a GravityShift event to the level.
     * @param xGravity  A float which is the new horizontal gravity strength.
     * @param yGravity  A float which is the new vertical gravity strength
     * @param eventTime A long which is the time in millis for the event to occur
     */
    public void addGravityShiftEvent(float xGravity, float yGravity, long eventTime)
    {
        this.level.addLevelEvent(new GravityShift(xGravity, yGravity, this.spaceshipGame.getPhysics()), eventTime);
    }

    /**
     * Generates and adds a new PlaySound event to the level.
     * @param gameSound An IGameSound to add to the game
     * @param eventTime A long which is the time to perform the event
     */
    public void addPlaySoundEvent(IGameSound gameSound, long eventTime)
    {
        this.level.addLevelEvent(new PlaySound(gameSound, this.spaceshipGame.getGameObjects()), eventTime);
    }

    /**
     * Generates a PrepareToEndLevel event and adds it to the current level
     * @param nextState An Enum which is the next state for the game to assume.
     * @param eventTime A long which is the time to perform the event at.
     */
    public void addPrepareToEndLevelEvent(EGameState nextState, long eventTime)
    {
        this.level.addLevelEvent(new PrepareToEndLevel(this.spaceshipGame, nextState), eventTime);
    }

    /**
     * Generates a ShipManoeuvre event and adds it to the current level.
     * @param xSpeed    A float describing the new horizontal speed of the spaceship
     * @param ySpeed    A float describing the new vertical speed of the spaceship
     * @param eventTime A long which is the time for the event to occur
     */
    public void addShipManoeuvreEvent(float xSpeed, float ySpeed, long eventTime)
    {
        this.level.addLevelEvent(new ShipManoeuvre(xSpeed, ySpeed, this.spaceshipGame.getEntityUpdateFactory()), eventTime);
    }

    /**
     * Generates a new SpawnGameObjects event and adds it to the level.
     * @param tileType  An Enum which is the type of tile the objects should be spawned on
     * @param tileMap   A TileMap object which is the map to spawn new objects on
     * @param factory   An IGameObjectFactory which will generate the new objects
     * @param eventTime A long which is the time for the event to occur
     */
    public void addSpawnEvent(TilemapHelper.ETileType tileType, TileMap tileMap, IGameObjectFactory factory, long eventTime)
    {
        this.level.addLevelEvent(new SpawnGameObjects(tileType, tileMap, factory), eventTime);
    }
}
