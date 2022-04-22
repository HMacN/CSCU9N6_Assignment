package renderableObjects;

import CSCU9N6Library.Sprite;
import factories.SpriteFactory;
import helperClasses.EntityUpdate;
import physics.Collider;
import physics.IHasCollider;
import soundsAndMusic.DistanceSound;
import spaceShipGame.GameObjects;
import spaceShipGame.SpaceshipGame;

import java.awt.*;

public class Bullet implements IDrawable, IHasCollider
{
    private Sprite sprite;
    private Collider collider;
    private GameObjects gameObjects;
    private SpaceshipGame spaceshipGame;

    private boolean selfDestructWhenOffScreen = false;
    private float screenWidth;
    private float screenHeight;

    public Bullet(float screenWidth, float screenHeight, float xCoord, float yCoord, float xSpeed, float ySpeed, GameObjects gameObjects, SpaceshipGame spaceshipGame)
    {
        this.sprite = SpriteFactory.getSpriteFromPNGFile("bullet", 1, 4, 60);
        this.sprite.playAnimation();
        this.sprite.show();

        this.collider = new Collider(xCoord, yCoord, 8.0f, 8.0f, -1.0f, this);  //Negative mass!  Ray Gun bullets can "pull" things.
        this.collider.setXSpeed(xSpeed);
        this.collider.setYSpeed(ySpeed);
        this.collider.setIgnoringFriction(true);
        this.collider.setIgnoringGravity(true);

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.gameObjects = gameObjects;
        this.spaceshipGame = spaceshipGame;
    }

    @Override
    public void draw(Graphics2D graphics2D, float xOffset, float yOffset)
    {
        if (!this.selfDestructWhenOffScreen)    //If not set to self destruct
        {
            //Update to the collider position.
            this.sprite.setX(this.collider.getXCoord());
            this.sprite.setY(this.collider.getYCoord());

            //Apply the offset.
            this.sprite.setX(this.sprite.getX() + xOffset);
            this.sprite.setY(this.sprite.getY() + yOffset);
        }

        //Draw the sprite.
        this.sprite.draw(graphics2D);
    }

    @Override
    public void update(EntityUpdate entityUpdate)
    {
        this.sprite.update(entityUpdate.getMillisSinceLastUpdate());
    }

    @Override
    public void setXSpeed(float xSpeed)
    {
        this.sprite.setVelocityX(xSpeed);
    }

    @Override
    public void setYSpeed(float ySpeed)
    {
        this.sprite.setVelocityY(ySpeed);
    }

    @Override
    public boolean getSelfDestructWhenOffScreen()
    {
        if (!this.selfDestructWhenOffScreen)
        {
            return false;   //Don't self destruct without being told to do so.
        }

        if (this.sprite.getX() > this.screenWidth || this.sprite.getX() < -this.sprite.getWidth())   //If the sprite has gone off the sides of the screen.
        {
            return true;
        }

        if (this.sprite.getY() > this.screenHeight || this.sprite.getY() < -this.sprite.getHeight()) //If the sprite has gone off the top or bottom of the screen.
        {
            return true;
        }

        //If the sprite is due for destruction, but is still in view:
        return false;
    }

    public Collider getCollider()
    {
        return collider;
    }

    public void setSelfDestructWhenOffScreen()
    {
        this.selfDestructWhenOffScreen = true;
    }

    @Override
    public void hasCollidedWith(IHasCollider parentOfOtherCollider)
    {
        handleHittingSomething();
    }

    @Override
    public void collidedWithTile()
    {
        handleHittingSomething();
    }

    private void handleHittingSomething()
    {
        this.sprite.hide();
        this.collider.setToSelfDestruct();
        this.gameObjects.addSound(new DistanceSound("sounds/zap.wav", this.spaceshipGame, this.collider));
    }
}
