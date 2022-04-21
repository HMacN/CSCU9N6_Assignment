package renderableObjects;

import CSCU9N6Library.Sprite;
import helperClasses.Debug;
import helperClasses.EntityUpdate;
import factories.SpriteFactory;
import physics.Collider;

import java.awt.*;

public class CargoCrate implements IDrawable
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
        this.collider = new Collider(xCoord, yCoord, 31.0f, 31.0f, 5.0f);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
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
        this.sprite.drawTransformed(graphics2D);
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
        Debug.print("Crate horizontal speed: " + this.sprite.getVelocityX());
    }

    @Override
    public void setYSpeed(float ySpeed)
    {
        this.sprite.setVelocityY(ySpeed);
        Debug.print("Crate vertical speed: " + this.sprite.getVelocityY());
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
}
