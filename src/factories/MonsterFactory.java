package factories;

import renderableObjects.AlienBugMonster;
import spaceShipGame.GameObjects;
import spaceShipGame.SpaceshipGame;

/**
 * A factory for the monster enemies in the game
 */
public class MonsterFactory implements IGameObjectFactory
{
    private SpaceshipGame spaceshipGame;
    private GameObjects gameObjects;

    /**
     * The constructor for the factory.
     * @param spaceshipGame A SpaceshipGame object to interrogate for needed data
     * @param gameObjects   A GameObjects class to spawn new monsters into.
     */
    public MonsterFactory(SpaceshipGame spaceshipGame, GameObjects gameObjects)
    {
        this.gameObjects = gameObjects;
        this.spaceshipGame = spaceshipGame;
    }

    /**
     * Spawns a new monster at the given in-game coordinates
     * @param xCoord   A float which is the horizontal coordinate
     * @param yCoord    A float which is the vertical coordinate
     */
    @Override
    public void spawnNewAt(float xCoord, float yCoord)
    {
        AlienBugMonster alienBugMonster = new AlienBugMonster(this.spaceshipGame, xCoord, yCoord);
        this.gameObjects.addPhysicsEntity(alienBugMonster.getCollider());
        this.gameObjects.addDrawable(alienBugMonster, GameObjects.ERenderLayer.spaceShipLayer);
    }
}
