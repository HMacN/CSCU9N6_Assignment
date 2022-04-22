//  Assignment submission for CSCU9N6
//  Student Number: 2823735/1
//  Date of Submission: 22/04/2022

package spaceShipGame;

import CSCU9N6Library.Sound;
import factories.*;
import CSCU9N6Library.GameCore;
import gameStates.*;
import helperClasses.UserInputHandler;
import levelEvents.*;
import physics.PhysicsEngine;

import java.awt.*;

import static gameStates.EGameState.*;
import static helperClasses.TilemapHelper.ETileType.*;

/**
 * The Core of the spaceship game.  Contains the "main" function.
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
    private EntityUpdateFactory entityUpdateFactory = new EntityUpdateFactory(SCREEN_WIDTH, SCREEN_HEIGHT);
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

    /**
     * A getter for the currently loaded state of the game.
     * @return  An IGameState object which is the current state of this game.
     */
    public IGameState getGameState()
    {
        return this.gameState;
    }

    /**
     * Method to handle the initial steps for smoothly switching from one game state or level to another.
     *
     * @param newState  An EGameState which describes the new state to be loaded.
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

    /**
     * A function to actually switch the game state to the next one, once all the needed preparation has happened.
     */
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
                //Main menu adds it's events internally, as it has a few "funny" elements, like spawning buttons, to add.
                break;
            }
            case levelOne:
            {
                setUpLevelOne();
                break;
            }
            case levelTwo:
            {
                setUpLevelTwo();
                break;
            }
            case levelThree:
            {
                setUpLevelThree();
                break;
            }
        }

        //Make sure that all states start with no movement or gravity.
        this.entityUpdateFactory.setSpaceshipYSpeed(0.0f);
        this.entityUpdateFactory.setSpaceshipXSpeed(0.0f);
        this.physics.setGravity(0.0f, 0.0f);
    }

    /**
     * A function which performs all needed setup for level three.
     */
    private void setUpLevelThree()
    {
        this.gameState = new LevelGameState(this, "SpaceShipThree.txt");

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
        this.levelEventFactory.addPrepareToEndLevelEvent(mainMenu, 120_000);
    }

    /**
     * A function which performs all needed setup for level two.
     */
    private void setUpLevelTwo()
    {
        this.gameState = new LevelGameState(this, "SpaceShipTwo.txt");

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
        this.levelEventFactory.addPrepareToEndLevelEvent(mainMenu, 120_000);
    }

    /**
     * A function which performs all needed setup for level one.
     */
    private void setUpLevelOne()
    {
        this.gameState = new LevelGameState(this, "SpaceShipOne.txt");

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
        this.levelEventFactory.addPrepareToEndLevelEvent(mainMenu, 120_000);
    }


    /**
     * Starts a new update cycle by getting an update from the current game state and passing it to the GameObjects collection to disseminate to all in-game objects.
     * @param millisSinceLastUpdate A long which is the elapsed time in milliseconds since the last update.
     */
    @Override
    public void update(long millisSinceLastUpdate)
    {
        //Get a new entity update, and apply it to the objects in the game.
        this.gameObjects.update(this.gameState.getUpdate(millisSinceLastUpdate));
    }

    /**
     * Causes the game to render onto the screen.  Sets the background to black and then calls on the GameObjects collection to draw itself.
     * @param graphics2D    A Graphics2D object which is the display area.
     */
    @Override
    public void draw(Graphics2D graphics2D)
    {
        //Fill in background.
        graphics2D.setColor(Color.BLACK);
        graphics2D.fillRect(0, 0, getWidth(), getHeight());

        //Draw in-game objects.
        this.gameObjects.draw(graphics2D);
    }

    /**
     * A getter for the GameObjects object that contains all in-game objects.
     * @return  The GameObjects object for this game.
     */
    public GameObjects getGameObjects()
    {
        return this.gameObjects;
    }

    /**
     * A getter for the display area width.
     * @return  An int which is the width of the display area in pixels.
     */
    public int getScreenWidth()
    {
        return SCREEN_WIDTH;
    }

    /**
     * A getter for the display area height.
     * @return  An int which is the height of the display area in pixels.
     */    public int getScreenHeight()
    {
        return SCREEN_HEIGHT;
    }

    /**
     * A getter for the object which captures all user input for the game.
     * @return  The UserInputHandler object for this game.
     */
    public UserInputHandler getUserInputHandler()
    {
        return new UserInputHandler(this);
    }

    /**
     * A getter for the updateFactory object for this game.
     * @return  The EntityUpdateFactory object for this game.
     */
    public EntityUpdateFactory getEntityUpdateFactory()
    {
        return this.entityUpdateFactory;
    }

    /**
     * A getter for the physics engine for the game.
     * @return  The PhysicsEngine object for the game.
     */
    public PhysicsEngine getPhysics()
    {
        return this.physics;
    }
}