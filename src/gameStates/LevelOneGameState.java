package gameStates;

import CSCU9N6Library.TileMap;
import helperClasses.EntityUpdate;
import helperClasses.EntityUpdateFactory;
import helperClasses.GameObjects;
import helperClasses.StarFieldGenerator;
import physics.PhysicsEngine;
import renderableObjects.Player;
import spaceShipGame.SpaceshipGame;

import static helperClasses.GameObjects.ERenderLayer.*;

public class LevelOneGameState implements IGameState
{
    private SpaceshipGame spaceshipGame;
    private EntityUpdateFactory updateFactory;
    private GameObjects gameObjects;
    private StarFieldGenerator starFieldGenerator = new StarFieldGenerator();
    private TileMap tileMap = new TileMap();
    private Player player;
    private PhysicsEngine physicsEngine;

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

        //Set up player.
        this.player = new Player(this.spaceshipGame.getScreenWidth(), this.spaceshipGame.getScreenHeight(), this.updateFactory, this.spaceshipGame.getUserInputHandler(), this.tileMap);
        this.gameObjects.addDrawable(this.player, spaceShipLayer);
        this.gameObjects.addPhysicsEntity(this.player.getCollider());

        this.physicsEngine.setTileMap(this.tileMap);
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
