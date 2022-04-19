package renderableObjects;

import CSCU9N6Library.Sprite;
import helperClasses.EntityUpdate;
import physics.Collider;

import java.awt.*;

public class CargoCrate implements IDrawable
{
    private Sprite sprite;
    private Collider collider;

    @Override
    public void draw(Graphics2D graphics2D, float xOffset, float yOffset)
    {

    }

    @Override
    public void setSelfDestructBoundaries(int maxXCoord, int minXCoord, int maxYCoord, int minYCoord)
    {

    }

    @Override
    public void update(EntityUpdate entityUpdate)
    {

    }

    @Override
    public float getXSpeed()
    {
        return 0;
    }

    @Override
    public void setXSpeed(float xSpeed)
    {

    }

    @Override
    public float getYSpeed()
    {
        return 0;
    }

    @Override
    public void setYSpeed(float ySpeed)
    {

    }

    @Override
    public void setXCoord(float xCoord)
    {

    }

    @Override
    public void setYCoord(float yCoord)
    {

    }

    @Override
    public boolean getSelfDestructStatus()
    {
        return false;
    }

    public Collider getCollider()
    {
        return collider;
    }
}
