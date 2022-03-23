public class EntityUpdateFactory
{
    private long millisSinceLastUpdate = 0;
    private int playerXOffset = 0;
    private int playerYOffset = 0;
    private float spaceshipXSpeed = 0.0f;
    private float spaceshipYSpeed = 0.0f;

    public void setMillisSinceLastUpdate(long millisSinceLastUpdate)
    {
        this.millisSinceLastUpdate = millisSinceLastUpdate;
    }

    public void setPlayerXOffset(int playerXOffset)
    {
        this.playerXOffset = playerXOffset;
    }

    public void setPlayerYOffset(int playerYOffset)
    {
        this.playerYOffset = playerYOffset;
    }

    public void setSpaceshipXSpeed(float spaceshipXSpeed)
    {
        this.spaceshipXSpeed = spaceshipXSpeed;
    }

    public void setSpaceshipYSpeed(float spaceshipYSpeed)
    {
        this.spaceshipYSpeed = spaceshipYSpeed;
    }

    public EntityUpdate getEntityUpdate()
    {
        return new EntityUpdate(
                this.millisSinceLastUpdate,
                this.playerXOffset,
                this.playerYOffset,
                this.spaceshipXSpeed,
                this.spaceshipYSpeed);
    }
}
