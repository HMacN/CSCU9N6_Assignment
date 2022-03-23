import game2D.GameCore;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

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

    //Set up arrays of in-game objects.
    private ArrayList<BackgroundEntity> backgroundEntities = new ArrayList<>();
    private ArrayList<IPhysicsEntity> physicsEntities = new ArrayList<>();

    //Set up classes to handle the in-game logic.
    private IGameState gameState = new MainMenuState(backgroundEntities, physicsEntities);
    private GamePhysics physics = new GamePhysics();
    private EntityUpdateFactory entityUpdateFactory = new EntityUpdateFactory();

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
        setVisible(true);

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

        //Perform first-time set up of the new game state.
        this.gameState.initialSetup();
    }

    /**
     * @param millisSinceLastUpdate
     */
    @Override
    public void update(long millisSinceLastUpdate)
    {
        //Get a new entity update.
        this.entityUpdateFactory.setMillisSinceLastUpdate(millisSinceLastUpdate);
        this.entityUpdateFactory.setSpaceshipXSpeed(0.0f);
        this.entityUpdateFactory.setSpaceshipYSpeed(-0.1f);
        EntityUpdate updateData = this.entityUpdateFactory.getEntityUpdate();

        //Update Game State.
        this.gameState.update(updateData);

        //Update objects the player will interact with.
        for (IPhysicsEntity physicsEntity : this.physicsEntities)
        {
            physicsEntity.update(updateData);
        }

        //Update background objects.
        LinkedList<BackgroundEntity> entitiesToDelete = new LinkedList<>();
        for (BackgroundEntity backgroundEntity : this.backgroundEntities)
        {
            //Delete background entities that have travelled off screen.
            if (backgroundEntity.tooFarOffScreen())
            {
                entitiesToDelete.add(backgroundEntity);
            }
            else
            {
                backgroundEntity.update(updateData);
            }
        }
        this.backgroundEntities.removeAll(entitiesToDelete);
    }

    @Override
    public void draw(Graphics2D graphics2D)
    {
        //Fill in background.
        graphics2D.setColor(Color.BLACK);
        graphics2D.fillRect(0, 0, getWidth(), getHeight());

        //Draw background objects.
        for (BackgroundEntity backgroundEntity : this.backgroundEntities)
        {
            backgroundEntity.getSprite().draw(graphics2D);
        }
        
        //Draw foreground objects.
        for (IPhysicsEntity physicsEntity : this.physicsEntities)
        {
            physicsEntity.getSprite().draw(graphics2D);
        }
    }

    public void interpretUserInput()
    {
        // TODO - implement SpaceshipGame.interpretUserInput
        throw new UnsupportedOperationException();
    }
}