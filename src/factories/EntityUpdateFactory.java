package factories;

import helperClasses.EntityUpdate;

/**
 * A factory which creates new data classes to pass to the rest of the game.  Also used to track some information.
 */
public class EntityUpdateFactory
{
    private long millisSinceLastUpdate = 0;
    private float playerXOffset = 0.0f;
    private float playerYOffset = 0.0f;
    private float spaceshipXSpeed = 0.0f;
    private float spaceshipYSpeed = 0.0f;
    private int screenWidth = 0;
    private int screenHeight = 0;

    /**
     * Generates a new update object to pass to other game objects and systems.
     * @return  An EntityUpdate object, which contains data about the current game state.
     */
    public EntityUpdate getEntityUpdate()
    {
        return new EntityUpdate(
                this.millisSinceLastUpdate,
                this.playerXOffset,
                this.playerYOffset,
                this.spaceshipXSpeed,
                this.spaceshipYSpeed,
                this.screenWidth,
                this.screenHeight);
    }

    /**
     * Setter for the time elapsed since the last update was generated.
     * @param millisSinceLastUpdate A long which is the time in milliseconds since the last update.
     */
    public void setMillisSinceLastUpdate(long millisSinceLastUpdate)
    {
        this.millisSinceLastUpdate = millisSinceLastUpdate;
    }

    /**
     * Setter for the size of the render-able area on the screen.
     *
     * @param screenWidth   An integer which is the width of the screen in pixels
     * @param screenHeight  An integer which is the height of the screen in pixels
     */
    public void setScreenSize(int screenWidth, int screenHeight)
    {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    /**
     * Setter for the horizontal distance needed to offset the sprites so that the player stays in the middle of the screen.
     * @param playerXOffset A float which is the distance in pixels needed to offset the sprites.
     */
    public void setPlayerXOffset(float playerXOffset)
    {
        this.playerXOffset = playerXOffset;
    }

    /**
     * Setter for the vertical distance needed to offset the sprites so that the player stays in the middle of the screen.
     * @param playerYOffset A float which is the distance in pixels needed to offset the sprites.
     */
    public void setPlayerYOffset(float playerYOffset)
    {
        this.playerYOffset = playerYOffset;
    }

    /**
     * Setter for the horizontal speed the star-field in the background passes by at.
     * @param spaceshipXSpeed A float which is the distance in pixels needed to offset the sprites.
     */
    public void setSpaceshipXSpeed(float spaceshipXSpeed)
    {
        this.spaceshipXSpeed = spaceshipXSpeed;
    }

    /**
     * Setter for the vertical speed the star-field in the background passes by at.
     * @param spaceshipYSpeed A float which is the distance in pixels needed to offset the sprites.
     */
    public void setSpaceshipYSpeed(float spaceshipYSpeed)
    {
        this.spaceshipYSpeed = spaceshipYSpeed;
    }
}
