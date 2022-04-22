//  Assignment submission for CSCU9N6
//  Student Number: 2823735/1
//  Date of Submission: 22/04/2022

package factories;

import CSCU9N6Library.Sound;
import renderableObjects.Bullet;
import spaceShipGame.GameObjects;
import spaceShipGame.SpaceshipGame;

/**
 * A factory for bullet objects.
 */
public class BulletFactory implements IGameObjectFactory
{
    private SpaceshipGame spaceshipGame;
    private GameObjects gameObjects;
    private float bulletXSpeed = 0.0f;
    private float bulletYSpeed = 0.0f;

    /**
     * The constructor.
     *
     * @param spaceshipGame The game to pull needed data from.
     * @param gameObjects   The collection to add the bullets to.
     */
    public BulletFactory(SpaceshipGame spaceshipGame, GameObjects gameObjects)
    {
        this.spaceshipGame = spaceshipGame;
        this.gameObjects = gameObjects;
    }

    /**
     * Sets the bullets produced by the factory to go left.
     */
    public void setBulletsGoingLeft()
    {
        this.bulletYSpeed = 0.0f;
        this.bulletXSpeed = -0.4f;
    }

    /**
     * Sets the bullets produced by the factory to go right.
     */
    public void setBulletsGoingRight()
    {
        this.bulletYSpeed = 0.0f;
        this.bulletXSpeed = 0.4f;
    }

    /**
     * Creates a new bullet and adds it to the game objects collection.
     *
     * @param xCoord    The horizontal coordinate to spawn the bullet at.
     * @param yCoord    The vertical coordinate to spawn the bullet at.
     */
    @Override
    public void spawnNewAt(float xCoord, float yCoord)
    {
        Bullet bullet = new Bullet(this.spaceshipGame.getScreenWidth(), this.spaceshipGame.getScreenHeight(), xCoord, yCoord, this.bulletXSpeed, this.bulletYSpeed, this.gameObjects, this.spaceshipGame);
        this.gameObjects.addDrawable(bullet, GameObjects.ERenderLayer.spaceShipLayer);
        this.gameObjects.addPhysicsEntity(bullet.getCollider());
        this.gameObjects.addSound(new Sound("sounds/fireGun.wav"));
    }
}
