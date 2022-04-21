package factories;

import CSCU9N6Library.Sound;
import renderableObjects.Bullet;
import renderableObjects.CargoCrate;
import spaceShipGame.GameObjects;
import spaceShipGame.SpaceshipGame;

public class BulletFactory implements IGameObjectFactory
{
    private SpaceshipGame spaceshipGame;
    private GameObjects gameObjects;
    private float bulletXSpeed = 0.0f;
    private float bulletYSpeed = 0.0f;

    public BulletFactory(SpaceshipGame spaceshipGame, GameObjects gameObjects)
    {
        this.spaceshipGame = spaceshipGame;
        this.gameObjects = gameObjects;
    }

    public void setBulletsGoingUp()
    {
        this.bulletYSpeed = -0.4f;
        this.bulletXSpeed = 0.0f;
    }

    public void setBulletsGoingDown()
    {
        this.bulletYSpeed = 0.4f;
        this.bulletXSpeed = 0.0f;
    }

    public void setBulletsGoingLeft()
    {
        this.bulletYSpeed = 0.0f;
        this.bulletXSpeed = -0.4f;
    }

    public void setBulletsGoingRight()
    {
        this.bulletYSpeed = 0.0f;
        this.bulletXSpeed = 0.4f;
    }

    @Override
    public void spawnNewAt(float xCoord, float yCoord)
    {
        Bullet bullet = new Bullet(this.spaceshipGame.getScreenWidth(), this.spaceshipGame.getScreenHeight(), xCoord, yCoord, this.bulletXSpeed, this.bulletYSpeed, this.gameObjects, this.spaceshipGame);
        this.gameObjects.addDrawable(bullet, GameObjects.ERenderLayer.spaceShipLayer);
        this.gameObjects.addPhysicsEntity(bullet.getCollider());
        this.gameObjects.addSound(new Sound("sounds/fireGun.wav"));
    }
}
