package helperClasses;

import java.awt.*;

/**
 * A container class for the data generated on each update.  Passed to most classes and systems to be interrogated for the needed data.
 * Read-only by way of final parameters which only have getters.
 */
public class EntityUpdate
{
    private final long MILLIS_SINCE_LAST_UPDATE;
    private final float PLAYER_X_OFFSET;
    private final float PLAYER_Y_OFFSET;
    private final float SPACESHIP_X_SPEED;
    private final float SPACESHIP_Y_SPEED;
    private final int SCREEN_WIDTH;
    private final int SCREEN_HEIGHT;

    /**
     * Constructor which serves as the only way of adding/altering data in this object.  Just in case.
     *
     * @param millisSinceLastUpdate A long which is the time in milliseconds since the last update.
     * @param playerXOffset A float which is the distance the sprites have to be shifted horizontally to keep the player at the centre of the screen.
     * @param playerYOffset A float which is the distance the sprites have to be shifted vertically to keep the player at the centre of the screen.
     * @param spaceshipXSpeed   A float which is the horizontal speed at which the stars in the background go by.
     * @param spaceshipYSpeed   A float which is the vertical speed at which the stars in the background go by.
     * @param screenWidth   An int which is the width of the screen in pixels
     * @param screenHeight  An int which is the height of the screen in pixels
     */
    public EntityUpdate(long millisSinceLastUpdate,
                        float playerXOffset,
                        float playerYOffset,
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

    /**
     * A getter for the screen width.
     * @return  An int which is the width of the screen in pixels
     */
    public int getScreenWidth()
    {
        return this.SCREEN_WIDTH;
    }

    /**
     * A getter for the screen width.
     * @return  An int which is the height of the screen in pixels
     */
    public int getScreenHeight()
    {
        return this.SCREEN_HEIGHT;
    }

    /**
     * A getter for the elapsed time.
     * @return  A long which is the time since the last update
     */
    public long getMillisSinceLastUpdate()
    {
        return MILLIS_SINCE_LAST_UPDATE;
    }

    /**
     * A getter for the horizontal offset
     * @return  A float which is the distance the sprites have to be shifted horizontally to keep the player at the centre of the screen.
     */
    public float getPlayerXOffset()
    {
        return PLAYER_X_OFFSET;
    }

    /**
     * A getter for the vertical offset
     * @return  A float which is the distance the sprites have to be shifted vertically to keep the player at the centre of the screen.
     */
    public float getPlayerYOffset()
    {
        return PLAYER_Y_OFFSET;
    }

    /**
     * A getter for the spaceship horizontal speed
     * @return  A float which is the horizontal speed at which the stars in the background go by.
     */
    public float getSpaceshipXSpeed()
    {
        return SPACESHIP_X_SPEED;
    }

    /**
     * A getter for the spaceship vertical speed
     * @return  A float which is the horizontal speed at which the stars in the background go by.
     */
    public float getSpaceshipYSpeed()
    {
        return SPACESHIP_Y_SPEED;
    }
}

