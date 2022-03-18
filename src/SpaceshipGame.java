import game2D.GameCore;

import java.awt.*;
import java.util.ArrayList;

/**
 * Game created for CSCU9N6 Computer Games Development.
 *
 * Student Number: 2823735
 * Date of Submission: 14/04/2022
 */

public class SpaceshipGame extends GameCore
{

    private static int SCREEN_WIDTH = 1024;
    private static int SCREEN_HEIGHT = 768;
    private IGameState gameState = new MainMenuState();
    private GamePhysics physics = new GamePhysics();
    private ArrayList<BackgroundEntity> backgroundEntities = new ArrayList<BackgroundEntity>();
    private ArrayList<IPhysicsEntity> physicsEntities = new ArrayList<IPhysicsEntity>();

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
	 * Spawn new game entities from the current game state.
	 */
	public void init()
    {
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setVisible(true);

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

        //Clear the in-game entities.
        this.backgroundEntities = new ArrayList<BackgroundEntity>();
        this.physicsEntities = new ArrayList<IPhysicsEntity>();

        //Spawn new in-game entities.
    }

    /**
     * @param elapsed
     */
    @Override
    public void update(long elapsed)
    {
        //Update objects the player will interact with.
        for (IPhysicsEntity physicsEntity : this.physicsEntities)
        {
            physicsEntity.update(elapsed);
        }

        //Update background objects.
        for (BackgroundEntity backgroundEntity : this.backgroundEntities)
        {
            backgroundEntity.update(elapsed);
        }
    }

    @Override
    public void draw(Graphics2D g)
    {
        //Draw background objects.
        for (BackgroundEntity backgroundEntity : this.backgroundEntities)
        {
            backgroundEntity.getSprite().draw(g);
        }
        
        //Draw foreground objects.
        for (IPhysicsEntity physicsEntity : this.physicsEntities)
        {
            //physicsEntity.getSprite.draw(g);
        }
    }

    public void interpretUserInput()
    {
        // TODO - implement SpaceshipGame.interpretUserInput
        throw new UnsupportedOperationException();
    }

}