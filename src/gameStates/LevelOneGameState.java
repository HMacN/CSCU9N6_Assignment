package gameStates;

import CSCU9N6Library.TileMap;
import factories.CargoCrateFactory;
import factories.PlayerFactory;
import helperClasses.EntityUpdate;
import factories.EntityUpdateFactory;
import helperClasses.GameObjects;
import helperClasses.StarFieldGenerator;
import helperClasses.TilemapHelper;
import physics.PhysicsEngine;
import renderableObjects.IDrawable;
import spaceShipGame.SpaceshipGame;

import java.util.LinkedList;

import static helperClasses.GameObjects.ERenderLayer.*;
import static helperClasses.TilemapHelper.ETileType.*;

public class LevelOneGameState implements IGameState
{
    private SpaceshipGame spaceshipGame;
    private EntityUpdateFactory updateFactory;
    private GameObjects gameObjects;
    private StarFieldGenerator starFieldGenerator = new StarFieldGenerator();
    private TileMap tileMap = new TileMap();

    private PlayerFactory playerFactory;
    private CargoCrateFactory crateFactory;
    private PhysicsEngine physicsEngine;
    private LinkedList<IDrawable> thingsToDeleteAtTheEnd = new LinkedList<>();

    private long timeInState = 0;
    private long levelProgressInMillis = 0;
    private boolean paused = false;

    public LevelOneGameState(SpaceshipGame spaceshipGame)
    {
        this.spaceshipGame = spaceshipGame;
        this.updateFactory = this.spaceshipGame.getEntityUpdateFactory();
        this.gameObjects = this.spaceshipGame.getGameObjects();
        this.physicsEngine = this.spaceshipGame.getPhysics();

        this.tileMap.loadMap("maps", "SpaceShipOne.txt");
        this.gameObjects.addTileMap(this.tileMap, this.spaceshipGame.getScreenWidth(), this.spaceshipGame.getScreenHeight());
        this.physicsEngine.setTileMap(this.tileMap);

        this.playerFactory = new PlayerFactory(this.spaceshipGame, this.gameObjects);
        this.crateFactory = new CargoCrateFactory(this.spaceshipGame, this.gameObjects);
        //this.cargoCrateFactory = new CargoCrateFactory();

        TilemapHelper.spawnEntityOnMap(player, this.tileMap, this.playerFactory);
        TilemapHelper.spawnEntityOnMap(cargoCrate, this.tileMap, this.crateFactory);
    }

    @Override
    public EntityUpdate getUpdate(long millisSinceLastUpdate)
    {
        if (!this.paused)
        {
            this.levelProgressInMillis = this.levelProgressInMillis + millisSinceLastUpdate;
        }
        this.timeInState = this.timeInState + millisSinceLastUpdate;

        performUpdates();

        this.updateFactory.setMillisSinceLastUpdate(millisSinceLastUpdate);
        EntityUpdate update = this.updateFactory.getEntityUpdate();

        this.gameObjects.addDrawable(this.starFieldGenerator.spawnBackgroundStars(update, 1.1f), starFieldLayer1);
        this.gameObjects.addDrawable(this.starFieldGenerator.spawnBackgroundStars(update, 1.0f), starFieldLayer2);
        this.gameObjects.addDrawable(this.starFieldGenerator.spawnBackgroundStars(update, 0.9f), starFieldLayer3);

        return update;
    }

    private void performUpdates()
    {

    }
}
