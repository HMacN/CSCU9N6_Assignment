//  Assignment submission for CSCU9N6
//  Student Number: 2823735/1
//  Date of Submission: 22/04/2022

package renderableObjects;

import CSCU9N6Library.Sprite;
import helperClasses.EntityUpdate;
import factories.SpriteFactory;
import physics.Collider;
import physics.IHasCollider;

import java.awt.*;

/**
 * A cargo crate to put in the SpaceShip.  These are basically just physics entities for the player to interact with.
 */
public class CargoCrate implements IDrawable, IHasCollider
{
    private Sprite sprite;
    private Collider collider;

    private boolean selfDestructWhenOffScreen = false;
    private float screenWidth;
    private float screenHeight;

    public CargoCrate(float xCoord, float yCoord, float screenWidth, float screenHeight)
    {
        this.sprite = SpriteFactory.getSpriteFromPNGFile("crate");
        this.sprite.show();
        this.collider = new Collider(xCoord, yCoord, 31.0f, 31.0f, 5.0f, this);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    /**
     * Calls the draw function of this object's sprite, and applies any horizontal or vertical offset.
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
        this.sprite.drawTransformed(graphics2D);
    }

    /**
     * Updates this object for this update cycle, and performs any state changes that this object may need to undergo.
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
     * A getter for this object's collider.
     * @return  A Collider which has this object as it's parent.
     */
    public Collider getCollider()
    {
        return collider;
    }

    /**
     * A setter for the self-destruct flag.  Allows this object to be removed when the player can no longer see it.
     */
    public void setSelfDestructWhenOffScreen()
    {
        this.selfDestructWhenOffScreen = true;
    }

    @Override
    public void hasCollidedWith(IHasCollider parentOfOtherCollider)
    {
        //Needed for the interface
    }

    @Override
    public void collidedWithTile()
    {
        //Needed for the interface
    }
}
