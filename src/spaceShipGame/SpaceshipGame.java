package spaceShipGame;

import helperClasses.EntityUpdateFactory;
import helperClasses.GameObjects;
import CSCU9N6Library.GameCore;
import gameStates.IGameState;
import gameStates.MainMenuState;
import helperClasses.UserInputHandler;
import physics.PhysicsEngine;

import java.awt.*;

/**
 * Game created for CSCU9N6 Computer Games Development.
 *
 * Student Number: 2823735
 * Date of Submission: 14/04/2022
 */

public class SpaceshipGame extends GameCore
{
    //Set up window size.
    static int SCREEN_WIDTH = 1024;
    static int SCREEN_HEIGHT = 768;

    //Set up storage for all in-game objects.
    private GameObjects gameObjects = new GameObjects();

    //Set up classes to handle the in-game logic.
    private PhysicsEngine physics = new PhysicsEngine(gameObjects.getColliders());
    private EntityUpdateFactory entityUpdateFactory = new EntityUpdateFactory();
    private IGameState gameState = new MainMenuState(this);

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
        this.gameObjects.setPhysicsEngine(this.physics);

        //Load the initial game state.
        loadNewGameState(gameState);
    }

    /**
     * Method to handle the switching from one game state or level to another.
     *
     * @param newState  An IGameState which describes the new state to be loaded.
     */
    public void loadNewGameState(IGameState newState)
    {
        //Set the new game state.
        this.gameState = newState;
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