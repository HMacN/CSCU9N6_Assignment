//  Assignment submission for CSCU9N6
//  Student Number: 2823735/1
//  Date of Submission: 22/04/2022

package gameStates;

import CSCU9N6Library.TileMap;
import factories.*;
import helperClasses.EntityUpdate;
import helperClasses.TilemapHelper;
import levelEvents.*;
import spaceShipGame.GameObjects;
import helperClasses.StarFieldGenerator;
import physics.PhysicsEngine;
import spaceShipGame.SpaceshipGame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

import static spaceShipGame.GameObjects.ERenderLayer.*;

/**
 * A game state to contain a level of the game
 */
public class LevelGameState implements IGameState, KeyListener
{
    private SpaceshipGame spaceshipGame;
    private EntityUpdateFactory updateFactory;
    private GameObjects gameObjects;
    private StarFieldGenerator starFieldGenerator;
    private TileMap tileMap;

    private MonsterFactory monsterFactory;
    private LevelEventFactory levelEventFactory;
    private PhysicsEngine physicsEngine;

    private long millisInState = 0;
    private long levelProgressInMillis = 0;
    private boolean paused = false;

    private LinkedList<ILevelEvent> levelEvents = new LinkedList<>();
    private LinkedList<ILevelEvent> levelEventsToAdd = new LinkedList<>();

    /**
     * The constructor.
     * @param spaceshipGame The SpaceshipGame object that this level is a part of.
     */
    public LevelGameState(SpaceshipGame spaceshipGame, String mapFile)
    {
        this.spaceshipGame = spaceshipGame;
        this.updateFactory = this.spaceshipGame.getEntityUpdateFactory();
        this.gameObjects = this.spaceshipGame.getGameObjects();
        this.physicsEngine = this.spaceshipGame.getPhysics();

        this.tileMap = new TileMap();
        this.tileMap.loadMap("maps", mapFile);

        this.gameObjects.addTileMap(this.tileMap);
        this.physicsEngine.setTileMap(this.tileMap);
        this.starFieldGenerator = new StarFieldGenerator(this.spaceshipGame.getScreenWidth(), this.spaceshipGame.getScreenHeight());

        this.monsterFactory = new MonsterFactory(this.spaceshipGame, this.gameObjects);
        this.levelEventFactory = new LevelEventFactory(this.spaceshipGame);
        this.levelEventFactory.setGameState(this);

        this.spaceshipGame.getUserInputHandler().addKeyListener(this);
    }

    /**
     * Generates a new update data object, which will be used to update the rest of the program
     * @param millisSinceLastUpdate A long which is the time since the last update
     * @return  An EntityUpdate object with the needed update information
     */
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

    /**
     * Add a new LevelEvent to this level.
     * @param event An ILevelEvent to add to the level
     * @param millisUntilEventTriggers  A long which is the time for the event to occur
     */
    @Override
    public void addLevelEvent(ILevelEvent event, long millisUntilEventTriggers)
    {
        event.setNewTargetTime(this.millisInState + millisUntilEventTriggers);  //Add the current time to the countdown timer.
        this.levelEventsToAdd.add(event);
    }

    /**
     * Updates all the level events with the current time, also safely adds new events and deletes old ones.
     */
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
                event.updateCurrentTime(this.millisInState);
            }
        }

        this.levelEvents.removeAll(eventsToDelete);
    }

    /**
     * Required function for the interface.
     * @param e
     */
    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    /**
     * Required function for the interface.
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e)
    {

    }

    /**
     * registers keys being released and performs appropriate actions.
     * @param e A KeyEvent from the keyboard
     */
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

        if (e.getKeyCode() == KeyEvent.VK_NUMPAD5)
        {
            //Spawn a new wave of monsters.
            this.levelEventFactory.addSpawnEvent(TilemapHelper.ETileType.monster, this.tileMap, this.monsterFactory, 0);
        }

        if (e.getKeyCode() == KeyEvent.VK_NUMPAD8)
        {
            //Set spaceship to going up.
            this.levelEventFactory.addGravityShiftEvent(0.0f, 0.000_3f, 0);
            this.levelEventFactory.addShipManoeuvreEvent(0.0f, -0.3f, 0);
        }

        if (e.getKeyCode() == KeyEvent.VK_NUMPAD9)
        {
            //Set spaceship to going up and right.
            this.levelEventFactory.addGravityShiftEvent(-0.000_3f, 0.000_3f, 0);
            this.levelEventFactory.addShipManoeuvreEvent(0.3f, -0.3f, 0);
        }

        if (e.getKeyCode() == KeyEvent.VK_NUMPAD6)
        {
            //Set spaceship to going right.
            this.levelEventFactory.addGravityShiftEvent(-0.000_3f, 0.0f, 0);
            this.levelEventFactory.addShipManoeuvreEvent(0.3f, 0.0f, 0);
        }

        if (e.getKeyCode() == KeyEvent.VK_NUMPAD3)
        {
            //Set spaceship to going down and right.
            this.levelEventFactory.addGravityShiftEvent(-0.000_3f, -0.000_3f, 0);
            this.levelEventFactory.addShipManoeuvreEvent(0.3f, 0.3f, 0);
        }

        if (e.getKeyCode() == KeyEvent.VK_NUMPAD2)
        {
            //Set spaceship to going down.
            this.levelEventFactory.addGravityShiftEvent(0.0f, -0.000_3f, 0);
            this.levelEventFactory.addShipManoeuvreEvent(0.0f, 0.3f, 0);
        }

        if (e.getKeyCode() == KeyEvent.VK_NUMPAD1)
        {
            //Set spaceship to going down and left.
            this.levelEventFactory.addGravityShiftEvent(0.000_3f, -0.000_3f, 0);
            this.levelEventFactory.addShipManoeuvreEvent(-0.3f, 0.3f, 0);
        }

        if (e.getKeyCode() == KeyEvent.VK_NUMPAD4)
        {
            //Set spaceship to going left.
            this.levelEventFactory.addGravityShiftEvent(0.000_3f, 0.000_0f, 0);
            this.levelEventFactory.addShipManoeuvreEvent(-0.3f, 0.0f, 0);
        }

        if (e.getKeyCode() == KeyEvent.VK_NUMPAD7)
        {
            //Set spaceship to going up and left.
            this.levelEventFactory.addGravityShiftEvent(0.000_3f, 0.000_3f, 0);
            this.levelEventFactory.addShipManoeuvreEvent(-0.3f, -0.3f, 0);
        }
    }
}
