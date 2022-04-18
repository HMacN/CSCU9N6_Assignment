package renderableObjects;

import CSCU9N6Library.Sprite;
import helperClasses.EntityUpdate;
import helperClasses.SpriteFactory;
import physics.IPhysicsEntity;

import java.awt.*;

public class Player implements IPhysicsEntity, IDrawable
{
    private Sprite sprite = SpriteFactory.getSpriteFromPNGFile("player", 1, 4, 60);

    public Player(int xCoord, int yCoord)
    {
        this.sprite.setX(xCoord);
        this.sprite.setY(yCoord);
    }

    @Override
    public void draw(Graphics2D graphics2D)
    {
        this.sprite.draw(graphics2D);
    }

    @Override
    public void setSelfDestructBoundaries(int maxXCoord, int minXCoord, int maxYCoord, int minYCoord)
    {

    }

    @Override
    public void update(EntityUpdate entityUpdate)
    {
        this.sprite.update(entityUpdate.getMillisSinceLastUpdate());
    }

    @Override
    public double getXSpeed()
    {
        return this.sprite.getVelocityX();
    }

    @Override
    public void setXSpeed(float xSpeed)
    {
        this.sprite.setVelocityX(xSpeed);
    }

    @Override
    public double getYSpeed()
    {
        return this.sprite.getVelocityY();
    }

    @Override
    public void setYSpeed(float ySpeed)
    {
        this.sprite.setVelocityY(ySpeed);
    }

    @Override
    public void setXCoord(float xCoord)
    {
        this.sprite.setX(xCoord);
    }

    @Override
    public float getXCoord()
    {
        return this.sprite.getX();
    }

    @Override
    public void setYCoord(float yCoord)
    {
        this.sprite.setY(yCoord);
    }

    @Override
    public float getYCoord()
    {
        return this.sprite.getY();
    }

    @Override
    public boolean getSelfDestructStatus()
    {
        return false;
    }
}