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

/**
 * A game state to contain a level of the game
 */
public class LevelGameState implements IGameState, KeyListener
{
    private SpaceshipGame spaceshipGame;
    private EntityUpdateFactory updateFactory;
    private GameObjects gameObjects;
    private StarFieldGenerator starFieldGenerator = new StarFieldGenerator();
    private TileMap tileMap;

    private PlayerFactory playerFactory;
    private CargoCrateFactory crateFactory;
    private MonsterFactory monsterFactory;
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

        this.playerFactory = new PlayerFactory(this.spaceshipGame, this.gameObjects);
        this.crateFactory = new CargoCrateFactory(this.spaceshipGame, this.gameObjects);
        this.monsterFactory = new MonsterFactory(this.spaceshipGame, this.gameObjects);

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
     * "Enter" key: clears all events and return the game to the main menu.
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
    }
}
