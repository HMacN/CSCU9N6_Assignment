package helperClasses;

import java.awt.*;

public class EntityUpdate
{
    private final long MILLIS_SINCE_LAST_UPDATE;
    private final int PLAYER_X_OFFSET;
    private final int PLAYER_Y_OFFSET;
    private final float SPACESHIP_X_SPEED;
    private final float SPACESHIP_Y_SPEED;
    private final int SCREEN_WIDTH;
    private final int SCREEN_HEIGHT;

    public EntityUpdate(long millisSinceLastUpdate,
                        int playerXOffset,
                        int playerYOffset,
                        float spaceshipXSpeed,
                        float spaceshipYSpeed,
                        int screenWidth,
                        int screenHeight)
    {
        this.MILLIS_SINCE_LAST_UPDATE = millisSinceLastUpdate;
        this.PLAYER_X_OFFSET = playerXOffset;
        this.PLAYER_Y_OFFSET = playerYOffset;
        this.SPACESHIP_X_SPEED = spaceshipXSpeed;
        this.SPACESHIP_Y_SPEED = spaceshipYSpeed;
        this.SCREEN_WIDTH = screenWidth;
        this.SCREEN_HEIGHT = screenHeight;
    }

    public int getScreenWidth()
    {
        return this.SCREEN_WIDTH;
    }

    public int getScreenHeight()
    {
        return this.SCREEN_HEIGHT;
    }

    public long getMillisSinceLastUpdate()
    {
        return MILLIS_SINCE_LAST_UPDATE;
    }

    public int getPlayerXOffset()
    {
        return PLAYER_X_OFFSET;
    }

    public int getPlayerYOffset()
    {
        return PLAYER_Y_OFFSET;
    }

    public float getSpaceshipXSpeed()
    {
        return SPACESHIP_X_SPEED;
    }

    public float getSpaceshipYSpeed()
    {
        return SPACESHIP_Y_SPEED;
    }
}

