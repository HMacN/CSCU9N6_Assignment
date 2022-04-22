//  Assignment submission for CSCU9N6
//  Student Number: 2823735/1
//  Date of Submission: 22/04/2022

package factories;

import spaceShipGame.GameObjects;
import renderableObjects.CargoCrate;
import spaceShipGame.SpaceshipGame;

/**
 * Factory for cargo crate game objects.  Spawns the crates directly into the game objects collection.
 */
public class CargoCrateFactory implements IGameObjectFactory
{
    private SpaceshipGame spaceshipGame;
    private GameObjects gameObjects;

    /**
     * The constructor.
     * @param spaceshipGame The game to get information from.
     * @param gameObjects   The collection to spawn bullets into.
     */
    public CargoCrateFactory(SpaceshipGame spaceshipGame, GameObjects gameObjects)
    {
        this.spaceshipGame = spaceshipGame;
        this.gameObjects = gameObjects;
    }

    /**
     * Add a new crate to the game at the given coordinates.
     * @param xCoord    The horizontal coordinate of the new crate
     * @param yCoord    The vertical coordinate of the new crate
     */
    @Override
    public void spawnNewAt(float xCoord, float yCoord)
    {
        CargoCrate crate = new CargoCrate(xCoord, yCoord, this.spaceshipGame.getScreenWidth(), this.spaceshipGame.getScreenHeight());
        this.gameObjects.addPhysicsEntity(crate.getCollider());
        this.gameObjects.addDrawable(crate, GameObjects.ERenderLayer.spaceShipLayer);
    }
}
