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

/**
 * A bullet that the player can shoot out.  Kills both the player and Monsters if it hits them.  Also has negative mass,
 * and so will "pull" objects towards the player if it hits them.
 *
 * Should disappear as soon as it hits anything, and ignore both gravity and friction while it exists.
 */
public class Bullet implements IDrawable, IHasCollider
{
    private Sprite sprite;
    private Collider collider;
    private GameObjects gameObjects;
    private SpaceshipGame spaceshipGame;

    private boolean selfDestructWhenOffScreen = false;
    private float screenWidth;
    private float screenHeight;

    /**
     * The constructor.
     * @param screenWidth   A float which is the screen width in pixels.
     * @param screenHeight  A float which is the screen height in pixels.
     * @param xCoord    A float which is the starting x-coordinate for the bullet.
     * @param yCoord    A float which is the starting y-coordinate for the bullet.
     * @param xSpeed    A float which is the starting x-axis speed for the bullet.
     * @param ySpeed    A float which is the starting y-axis speed for the bullet.
     * @param gameObjects   The GameObjects object to add a collision sound to.
     * @param spaceshipGame The SpaceShipGame object needed to generate new Sounds.
     */
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

    /**
     * Draws the bullet on the screen.
     * @param graphics2D	The Graphics2D object to draw this object on.
     * @param xOffset	A float which is the horizontal offset in pixels to draw this object at.
     * @param yOffset	A float which is the vertical offset in pixels to draw this object at.
     */
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

    /**
     * Updates the sprite for the current update cycle.
     * @param entityUpdate	An EntityUpdate object containing the update information for this update cycle.
     */
    @Override
    public void update(EntityUpdate entityUpdate)
    {
        this.sprite.update(entityUpdate.getMillisSinceLastUpdate());
    }

    /**
     * Set the horizontal speed of this object's sprite.
     * @param xSpeed	A float which is the new horizontal speed in pixels per millisecond.
     */
    @Override
    public void setXSpeed(float xSpeed)
    {
        this.sprite.setVelocityX(xSpeed);
    }

    /**
     * Set the vertical speed of this object's sprite.
     * @param ySpeed	A float which is the new vertical speed in pixels per millisecond.
     */
    @Override
    public void setYSpeed(float ySpeed)
    {
        this.sprite.setVelocityY(ySpeed);
    }

    /**
     * A getter for the self-destruct flag.  Allows this object to be removed when the player can no longer see it.
     * @return	A boolean describing whether this object can be removed when out of sight.
     */
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

    /**
     * Getter for this object's Collider.
     * @return  A Collider object which has this object as it's parent.
     */
    public Collider getCollider()
    {
        return collider;
    }

    /**
     * A setter for the self-destruct flag.  Allows this object to be removed when the player can no longer see it.
     */
    @Override
    public void setSelfDestructWhenOffScreen()
    {
        this.selfDestructWhenOffScreen = true;
    }

    /**
     * Deletes the bullet now that it has hit something.
     * @param parentOfOtherCollider An IHasCollider object which is the parent object of the other collider in the collision.
     */
    @Override
    public void hasCollidedWith(IHasCollider parentOfOtherCollider)
    {
        handleHittingSomething();
    }

    /**
     * Deletes the bullet now that it has hit something.
     */
    @Override
    public void collidedWithTile()
    {
        handleHittingSomething();
    }

    /**
     * Hides the bullet's sprite, sets it's collider to self-destruct, and plays a "zap" sound.
     */
    private void handleHittingSomething()
    {
        this.sprite.hide();
        this.collider.setToSelfDestruct();
        this.gameObjects.addSound(new DistanceSound("sounds/zap.wav", this.spaceshipGame, this.collider));
    }
}
