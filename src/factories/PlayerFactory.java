//  Assignment submission for CSCU9N6
//  Student Number: 2823735/1
//  Date of Submission: 22/04/2022

package factories;

import spaceShipGame.GameObjects;
import renderableObjects.Player;
import spaceShipGame.SpaceshipGame;

import static spaceShipGame.GameObjects.ERenderLayer.spaceShipLayer;

/**
 * A factory for player objects.  As there can be only one player, this factory keeps track of the spawned player object.
 */
public class PlayerFactory implements IGameObjectFactory
{
    private Player player;
    private SpaceshipGame spaceshipGame;
    private GameObjects gameObjects;

    /**
     * The constructor.
     * @param spaceshipGame A SpaceshipGame object to harvest for needed data
     * @param gameObjects   A GameObjects class to add the new player object to.
     */
    public PlayerFactory(SpaceshipGame spaceshipGame, GameObjects gameObjects)
    {
        this.spaceshipGame = spaceshipGame;
        this.gameObjects = gameObjects;
    }

    /**
     * Spawn a new player object at the given coordinates.
     * @param xCoord   A float which is the horizontal coordinate
     * @param yCoord    A float which is the vertical coordinate
     */
    @Override
    public void spawnNewAt(float xCoord, float yCoord)
    {
        if (this.player == null)    //If there isn't already a player.
        {
            this.player = new Player(
                    this.spaceshipGame,
                    xCoord,
                    yCoord);

            this.gameObjects.addDrawable(this.player, spaceShipLayer);
            this.gameObjects.addPhysicsEntity(this.player.getCollider());
        }
    }
}
