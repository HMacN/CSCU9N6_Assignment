package physics;

public class Collider
{
    private float xCoord;
    private float yCoord;
    private float xSpeed = 0.0f;
    private float ySpeed = 0.0f;
    private float xControlSpeed = 0.0f;
    private float yControlSpeed = 0.0f;

    private float width;
    private float height;
    private float halfWidth;
    private float halfHeight;
    private float inverseMass;

    private boolean selfDestruct = false;
    private boolean ignoringGravity = false;
    private boolean collisionsAlreadyHandled = false;

    public Collider(float xCoord, float yCoord, float width, float height, float mass)
    {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.width = width;
        this.height = height;

        this.inverseMass = 1 / mass;

        calculateHalfSizes();
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

    public float getYAxisCentroid()
    {
        return this.yCoord + this.halfHeight;
    }

    public float getXAxisCentroid()
    {
        return this.xCoord + this.halfWidth;
    }

    public float getHalfWidth()
    {
        return this.halfWidth;
    }

    public float getHalfHeight()
    {
        return this.halfHeight;
    }

    private void calculateHalfSizes()
    {
        this.halfWidth = this.width / 2.0f;
        this.halfHeight = this.height / 2.0f;
    }

    public boolean collisionsAlreadyHandled()
    {
        return collisionsAlreadyHandled;
    }

    public void setCollisionsHandled(boolean collisionsHandled)
    {
        this.collisionsAlreadyHandled = collisionsHandled;
    }

    public float getInverseMass()
    {
        return inverseMass;
    }

    public float getXControlSpeed()
    {
        return xControlSpeed;
    }

    public void setXControlSpeed(float xControlSpeed)
    {
        this.xControlSpeed = xControlSpeed;
    }

    public float getYControlSpeed()
    {
        return yControlSpeed;
    }

    public void setYControlSpeed(float yControlSpeed)
    {
        this.yControlSpeed = yControlSpeed;
    }
}
