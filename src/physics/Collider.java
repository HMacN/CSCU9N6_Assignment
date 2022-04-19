package physics;

public class Collider
{
    private float xCoord;
    private float yCoord;
    private float xSpeed = 0.0f;
    private float ySpeed = 0.0f;
    private float width;
    private float height;
    private boolean selfDestruct = false;
    private boolean ignoringGravity = false;
    private boolean ignoringDownwardsGravity = false;

    public Collider(float xCoord, float yCoord, float width, float height)
    {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.width = width;
        this.height = height;
    }

    public void update(long elapsedTimeInMillis)
    {
        this.xCoord += this.xSpeed * elapsedTimeInMillis;
        this.yCoord += this.ySpeed * elapsedTimeInMillis;
    }

    public void setXCoord(float xCoord)
    {
        this.xCoord = xCoord;
    }

    public void setYCoord(float yCoord)
    {
        this.yCoord = yCoord;
    }

    public void setXSpeed(float xSpeed)
    {
        this.xSpeed = xSpeed;
    }

    public void setYSpeed(float ySpeed)
    {
        this.ySpeed = ySpeed;
    }

    public void setColliderSize(float width, float height)
    {
        this.width = width;
        this.height = height;
    }

    public float getXCoord()
    {
        return xCoord;
    }

    public float getYCoord()
    {
        return yCoord;
    }

    public float getXSpeed()
    {
        return xSpeed;
    }

    public float getYSpeed()
    {
        return ySpeed;
    }

    public float getWidth()
    {
        return width;
    }

    public float getHeight()
    {
        return height;
    }

    public boolean getSelfDestructStatus()
    {
        return selfDestruct;
    }

    public void setToSelfDestruct()
    {
        this.selfDestruct = true;
    }

    public boolean isIgnoringGravity()
    {
        return ignoringGravity;
    }

    public void setIgnoringGravity(boolean ignoringGravity)
    {
        this.ignoringGravity = ignoringGravity;
    }

    public boolean isIgnoringDownwardsGravity()
    {
        return ignoringDownwardsGravity;
    }

    public void setIgnoringDownwardsGravity(boolean ignoringDownwardsGravity)
    {
        this.ignoringDownwardsGravity = ignoringDownwardsGravity;
    }
}
