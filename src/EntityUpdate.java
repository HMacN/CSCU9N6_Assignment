public class EntityUpdate
{
    private long millisSinceLastUpdate;
    private int playerXOffset;
    private int playerYOffset;
    private float spaceshipXSpeed;
    private float spaceshipYSpeed;

    public EntityUpdate(long millisSinceLastUpdate, int playerXOffset, int playerYOffset,
                        float spaceshipXSpeed, float spaceshipYSpeed)
    {
        this.millisSinceLastUpdate = millisSinceLastUpdate;
        this.playerXOffset = playerXOffset;
        this.playerYOffset = playerYOffset;
        this.spaceshipXSpeed = spaceshipXSpeed;
        this.spaceshipYSpeed = spaceshipYSpeed;
    }

    public long getMillisSinceLastUpdate()
    {
        return millisSinceLastUpdate;
    }

    public int getPlayerXOffset()
    {
        return playerXOffset;
    }

    public int getPlayerYOffset()
    {
        return playerYOffset;
    }

    public float getSpaceshipXSpeed()
    {
        return spaceshipXSpeed;
    }

    public float getSpaceshipYSpeed()
    {
        return spaceshipYSpeed;
    }
}

