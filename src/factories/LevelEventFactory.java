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

public class LevelEventFactory
{
    private SpaceshipGame spaceshipGame;
    private IGameState level;

    public LevelEventFactory(SpaceshipGame spaceshipGame)
    {
        this.spaceshipGame = spaceshipGame;
    }

    public void setGameState(IGameState gameState)
    {
        this.level = gameState;
    }

    public void addDisplayDrawableEvent(IDrawable drawable, GameObjects.ERenderLayer renderLayer, long eventTime)
    {
        this.level.addLevelEvent(new DisplayDrawable(drawable, this.spaceshipGame.getGameObjects(), renderLayer), eventTime);
    }

    public void addDisplayTileMapEvent(TileMap tileMap, long eventTime)
    {
        this.level.addLevelEvent(new DisplayTileMap(tileMap, this.spaceshipGame.getGameObjects(), this.spaceshipGame.getScreenWidth(), this.spaceshipGame.getScreenHeight()), eventTime);
    }

    public void addGravityShiftEvent(float xGravity, float yGravity, long eventTime)
    {
        this.level.addLevelEvent(new GravityShift(xGravity, yGravity, this.spaceshipGame.getPhysics()), eventTime);
    }

    public void addPlaySoundEvent(IGameSound gameSound, long eventTime)
    {
        this.level.addLevelEvent(new PlaySound(gameSound, this.spaceshipGame.getGameObjects()), eventTime);
    }

    public void addPrepareToEndLevelEvent(EGameState nextState, long eventTime)
    {
        this.level.addLevelEvent(new PrepareToEndLevel(this.spaceshipGame, nextState), eventTime);
    }

    public void addShipManoeuvreEvent(float xSpeed, float ySpeed, long eventTime)
    {
        this.level.addLevelEvent(new ShipManoeuvre(xSpeed, ySpeed, this.spaceshipGame.getEntityUpdateFactory()), eventTime);
    }

    public void addSpawnEvent(TilemapHelper.ETileType tileType, TileMap tileMap, IGameObjectFactory factory, long eventTime)
    {
        this.level.addLevelEvent(new SpawnGameObjects(tileType, tileMap, factory), eventTime);
    }
}
