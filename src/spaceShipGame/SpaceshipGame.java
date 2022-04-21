package spaceShipGame;

import CSCU9N6Library.Sound;
import factories.*;
import CSCU9N6Library.GameCore;
import gameStates.*;
import helperClasses.UserInputHandler;
import levelEvents.*;
import physics.PhysicsEngine;
import soundsAndMusic.MIDIPlayer;

import java.awt.*;

import static gameStates.EGameState.*;
import static helperClasses.TilemapHelper.ETileType.*;
import static spaceShipGame.GameObjects.ERenderLayer.UILayer;
import static spaceShipGame.GameObjects.ERenderLayer.spaceStationLayer;

/**
 * Game created for CSCU9N6 Computer Games Development.
 *
 * Student Number: 2823735
 */

public class SpaceshipGame extends GameCore
{
    //Set up window size.
    static int SCREEN_WIDTH = 1024;
    static int SCREEN_HEIGHT = 768;

    //Set up storage for all in-game objects.
    private GameObjects gameObjects = new GameObjects();

    //Set up classes to handle the in-game logic.
    private PhysicsEngine physics;
    private EntityUpdateFactory entityUpdateFactory = new EntityUpdateFactory();
    private IGameState gameState;
    private EGameState nextState;

    private LevelEventFactory levelEventFactory = new LevelEventFactory(this);
    private PlayerFactory playerFactory = new PlayerFactory(this, this.getGameObjects());
    private CargoCrateFactory cargoCrateFactory = new CargoCrateFactory(this, this.getGameObjects());
    private MonsterFactory monsterFactory = new MonsterFactory(this, this.getGameObjects());

    /**
	 * The obligatory main method that creates
	 * an instance of our class and starts it running
	 *
	 * @param args	The list of parameters this program might use (ignored)
     */
    public static void main(String[] args)
    {
        SpaceshipGame game = new SpaceshipGame();
        game.init();
        game.run(false, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

	/**
	 * Perform first-time setup of the game and the window.
	 */
	public void init()
    {
        //Set up the game window.
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        this.entityUpdateFactory.setScreenSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setVisible(true);

        //Register objects with each other.
        this.physics = new PhysicsEngine(this.gameObjects.getColliders());
        this.gameObjects.setPhysicsEngine(this.physics);

        //Load the first game state.
        this.nextState = mainMenu;
        this.loadNextGameState();
    }

    public IGameState getGameState()
    {
        return this.gameState;
    }

    /**
     * Method to handle the switching from one game state or level to another.
     *
     * @param newState  An IGameState which describes the new state to be loaded.
     */
    public void prepareToLoadNewGameState(EGameState newState)
    {
        float horizontalClearanceSpeed = 0.0f;
        float verticalClearanceSpeed = -0.6f;

        this.nextState = newState;

        if (this.gameState != null) //If there is already a game state loaded
        {
            this.gameObjects.clearForeground(horizontalClearanceSpeed, verticalClearanceSpeed);

            if (!this.gameState.getClass().equals(MainMenuState.class))
            {
                //Announce the end of the level, if this wasn't a menu.
                this.gameState.addLevelEvent(new PlaySound(new Sound("sounds/gameOver.wav"), this.getGameObjects()), 0);
            }

            this.gameState.addLevelEvent(new ShipManoeuvre(horizontalClearanceSpeed, verticalClearanceSpeed, this.entityUpdateFactory), 0);
            this.gameState.addLevelEvent(new TerminateState(this), 2_000);
        }
    }

    public void loadNextGameState()
    {
        //Clear away the last of the old state.  All render-able objects should have left the screen by now.
        this.gameObjects.removeTileMap();
        this.gameObjects.stopAllSounds();
        this.entityUpdateFactory.reset();
        this.playerFactory = new PlayerFactory(this, this.getGameObjects());

        //Load up the new game state.
        switch (this.nextState)
        {
            case mainMenu:
            {
                this.gameState = new MainMenuState(this);
                //Main menu adds it's events internally, as it has a few "funny" elements, like buttons, to add.
                break;
            }
            case levelOne:
            {
                this.gameState = new LevelGameState(this, "SpaceShipOne.txt");
                addLevelOneEvents();
                break;
            }
            case levelTwo:
            {
                this.gameState = new LevelGameState(this, "SpaceShipTwo.txt");
                addLevelTwoEvents();
                break;
            }
            case levelThree:
            {
                this.gameState = new LevelGameState(this, "SpaceShipThree.txt");
                addLevelThreeEvents();
                break;
            }
        }

        //Make sure that all states start with no movement or gravity.
        this.entityUpdateFactory.setSpaceshipYSpeed(0.0f);
        this.entityUpdateFactory.setSpaceshipXSpeed(0.0f);
        this.physics.setGravity(0.0f, 0.0f);
    }

    private void addLevelThreeEvents()
    {
        this.levelEventFactory.setGameState(this.gameState);

        //Get set up.
        this.levelEventFactory.addShipManoeuvreEvent(0.0f, -0.1f,  0);
        this.levelEventFactory.addGravityShiftEvent(0.0f, 0.000_5f, 0);
        this.levelEventFactory.addDisplayTileMapEvent(this.gameObjects.getTileMap(), 0);
        this.levelEventFactory.addSpawnEvent(player, this.gameObjects.getTileMap(), playerFactory, 0);
        this.levelEventFactory.addSpawnEvent(cargoCrate, this.gameObjects.getTileMap(), this.cargoCrateFactory, 0);

        //Accelerate
        this.levelEventFactory.addShipManoeuvreEvent(-0.5f, -0.5f,  6_000);
        this.levelEventFactory.addGravityShiftEvent(0.000_5f, 0.000_5f, 6_000);

        //Start cruising
        this.levelEventFactory.addShipManoeuvreEvent(-0.5f, -0.0f,  12_000);
        this.levelEventFactory.addGravityShiftEvent(0.000_0f, 0.000_5f, 12_000);

        //Monster Attack!
        this.levelEventFactory.addSpawnEvent(monster, this.gameObjects.getTileMap(), this.monsterFactory, 15_000);

        //End the level after a couple of minutes.
        this.levelEventFactory.addPrepareToEndLevelEvent(mainMenu, 60_000);
    }

    private void addLevelTwoEvents()
    {
        this.levelEventFactory.setGameState(this.gameState);

        //Get set up.
        this.levelEventFactory.addShipManoeuvreEvent(0.0f, -0.1f,  0);
        this.levelEventFactory.addGravityShiftEvent(0.0f, 0.000_5f, 0);
        this.levelEventFactory.addDisplayTileMapEvent(this.gameObjects.getTileMap(), 0);
        this.levelEventFactory.addSpawnEvent(player, this.gameObjects.getTileMap(), playerFactory, 0);
        this.levelEventFactory.addSpawnEvent(cargoCrate, this.gameObjects.getTileMap(), this.cargoCrateFactory, 0);

        //Accelerate
        this.levelEventFactory.addShipManoeuvreEvent(-0.5f, -0.5f,  6_000);
        this.levelEventFactory.addGravityShiftEvent(0.000_5f, 0.000_5f, 6_000);

        //Start cruising
        this.levelEventFactory.addShipManoeuvreEvent(-0.5f, -0.0f,  12_000);
        this.levelEventFactory.addGravityShiftEvent(0.000_0f, 0.000_5f, 12_000);

        //Monster Attack!
        this.levelEventFactory.addSpawnEvent(monster, this.gameObjects.getTileMap(), this.monsterFactory, 15_000);

        //End the level after a couple of minutes.
        this.levelEventFactory.addPrepareToEndLevelEvent(mainMenu, 60_000);
    }

    private void addLevelOneEvents()
    {
        this.levelEventFactory.setGameState(this.gameState);

        //Get set up.
        this.levelEventFactory.addShipManoeuvreEvent(0.0f, -0.1f,  0);
        this.levelEventFactory.addGravityShiftEvent(0.0f, 0.000_5f, 0);
        this.levelEventFactory.addDisplayTileMapEvent(this.gameObjects.getTileMap(), 0);
        this.levelEventFactory.addSpawnEvent(player, this.gameObjects.getTileMap(), playerFactory, 0);
        this.levelEventFactory.addSpawnEvent(cargoCrate, this.gameObjects.getTileMap(), this.cargoCrateFactory, 0);

        //Accelerate
        this.levelEventFactory.addShipManoeuvreEvent(-0.5f, -0.5f,  6_000);
        this.levelEventFactory.addGravityShiftEvent(0.000_5f, 0.000_5f, 6_000);

        //Start cruising
        this.levelEventFactory.addShipManoeuvreEvent(-0.5f, -0.0f,  12_000);
        this.levelEventFactory.addGravityShiftEvent(0.000_0f, 0.000_5f, 12_000);

        //Monster Attack!
        this.levelEventFactory.addSpawnEvent(monster, this.gameObjects.getTileMap(), this.monsterFactory, 15_000);

        //End the level after a couple of minutes.
        this.levelEventFactory.addPrepareToEndLevelEvent(mainMenu, 60_000);
    }

    /**
     * @param millisSinceLastUpdate
     */
    @Override
    public void update(long millisSinceLastUpdate)
    {
        //Get a new entity update, and apply it to the objects in the game.
        this.gameObjects.update(this.gameState.getUpdate(millisSinceLastUpdate));
    }

    @Override
    public void draw(Graphics2D graphics2D)
    {
        //Fill in background.
        graphics2D.setColor(Color.BLACK);
        graphics2D.fillRect(0, 0, getWidth(), getHeight());

        //Draw in-game objects.
        this.gameObjects.draw(graphics2D);
    }

    public GameObjects getGameObjects()
    {
        return this.gameObjects;
    }

    public int getScreenWidth()
    {
        return SCREEN_WIDTH;
    }

    public int getScreenHeight()
    {
        return SCREEN_HEIGHT;
    }

    public UserInputHandler getUserInputHandler()
    {
        return new UserInputHandler(this);
    }

    public EntityUpdateFactory getEntityUpdateFactory()
    {
        return this.entityUpdateFactory;
    }

    public PhysicsEngine getPhysics()
    {
        return this.physics;
    }
}