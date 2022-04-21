package gameStates;

import CSCU9N6Library.TileMap;
import factories.CargoCrateFactory;
import factories.MonsterFactory;
import factories.PlayerFactory;
import helperClasses.EntityUpdate;
import factories.EntityUpdateFactory;
import levelEvents.*;
import spaceShipGame.GameObjects;
import helperClasses.StarFieldGenerator;
import physics.PhysicsEngine;
import spaceShipGame.SpaceshipGame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

import static spaceShipGame.GameObjects.ERenderLayer.*;
import static helperClasses.TilemapHelper.ETileType.*;

public class LevelThreeGameState implements IGameState, KeyListener
{
    private SpaceshipGame spaceshipGame;
    private EntityUpdateFactory updateFactory;
    private GameObjects gameObjects;
    private StarFieldGenerator starFieldGenerator = new StarFieldGenerator();
    private TileMap tileMap = new TileMap();

    private PlayerFactory playerFactory;
    private CargoCrateFactory crateFactory;
    private MonsterFactory monsterFactory;
    private PhysicsEngine physicsEngine;

    private long millisInState = 0;
    private long levelProgressInMillis = 0;
    private boolean paused = false;

    private LinkedList<ILevelEvent> levelEvents = new LinkedList<>();
    private LinkedList<ILevelEvent> levelEventsToAdd = new LinkedList<>();

    public LevelThreeGameState(SpaceshipGame spaceshipGame)
    {
        this.spaceshipGame = spaceshipGame;
        this.updateFactory = this.spaceshipGame.getEntityUpdateFactory();
        this.gameObjects = this.spaceshipGame.getGameObjects();
        this.physicsEngine = this.spaceshipGame.getPhysics();

        this.tileMap.loadMap("maps", "SpaceShipThree.txt");
        this.physicsEngine.setTileMap(this.tileMap);

        this.playerFactory = new PlayerFactory(this.spaceshipGame, this.gameObjects);
        this.crateFactory = new CargoCrateFactory(this.spaceshipGame, this.gameObjects);
        this.monsterFactory = new MonsterFactory(this.spaceshipGame, this.gameObjects);

        addLevelEvents();
        this.spaceshipGame.getUserInputHandler().addKeyListener(this);
    }

    @Override
    public EntityUpdate getUpdate(long millisSinceLastUpdate)
    {
        if (!this.paused)
        {
            this.levelProgressInMillis = this.levelProgressInMillis + millisSinceLastUpdate;
        }
        this.millisInState = this.millisInState + millisSinceLastUpdate;

        handleLevelEvents();

        this.updateFactory.setMillisSinceLastUpdate(millisSinceLastUpdate);
        EntityUpdate update = this.updateFactory.getEntityUpdate();

        this.gameObjects.addDrawable(this.starFieldGenerator.spawnBackgroundStars(update, 1.1f), starFieldLayer1);
        this.gameObjects.addDrawable(this.starFieldGenerator.spawnBackgroundStars(update, 1.0f), starFieldLayer2);
        this.gameObjects.addDrawable(this.starFieldGenerator.spawnBackgroundStars(update, 0.9f), starFieldLayer3);

        return update;
    }

    @Override
    public void addLevelEvent(ILevelEvent event, long millisUntilEventTriggers)
    {
        event.setNewTargetTime(this.millisInState + millisUntilEventTriggers);  //Add the current time to the countdown timer.
        this.levelEventsToAdd.add(event);
    }

    private void addLevelEvents()
    {
        //Get set up.
        addLevelEvent(new ShipManoeuvre(0.0f, -0.1f, this.updateFactory), 0);
        addLevelEvent(new DisplayTileMap(this.tileMap, this.gameObjects, this.spaceshipGame.getScreenWidth(), this.spaceshipGame.getScreenHeight()), 0);
        addLevelEvent(new SpawnGameObjects(player, this.tileMap, this.playerFactory), 0);
        addLevelEvent(new SpawnGameObjects(cargoCrate, this.tileMap, this.crateFactory), 0);

        //Accelerate
        addLevelEvent(new ShipManoeuvre(-0.5f, 0.0f, this.updateFactory), 6_000);
        addLevelEvent(new GravityShift(0.000_5f, -0.000_1f, this.physicsEngine), 6_000);

        //Start cruising
        addLevelEvent(new ShipManoeuvre(-0.15f, 0.0f, this.updateFactory), 10_000);
        addLevelEvent(new GravityShift(0.0f, 0.000_5f, this.physicsEngine), 10_000);
        addLevelEvent(new SpawnGameObjects(monster, this.tileMap, this.monsterFactory), 10_000);

        //End the level after a couple of minutes.
        addLevelEvent(new PrepareToEndLevel(this.spaceshipGame, EGameState.mainMenu), 120_000);
    }

    private void handleLevelEvents()
    {
        LinkedList<ILevelEvent> eventsToDelete = new LinkedList<>();

        //Do it this bloody stupid way because otherwise Java throws a concurrentModificationException.
        this.levelEvents.addAll(this.levelEventsToAdd);
        this.levelEventsToAdd.clear();

        for (ILevelEvent event : this.levelEvents)
        {
            if (event.isReadyToSelfDestruct())
            {
                eventsToDelete.add(event);
            }
            else
            {
                event.setCurrentTime(this.millisInState);
            }
        }

        this.levelEvents.removeAll(eventsToDelete);
    }

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    @Override
    public void keyPressed(KeyEvent e)
    {

    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
        {
            //If the enter key is clicked, clear all events and return to the main menu.
            for (ILevelEvent event : this.levelEvents)
            {
                event.cancel();
            }

            addLevelEvent(new PrepareToEndLevel(this.spaceshipGame, EGameState.mainMenu), 0);
        }
    }
}
