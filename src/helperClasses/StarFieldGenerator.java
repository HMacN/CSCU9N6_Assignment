//  Assignment submission for CSCU9N6
//  Student Number: 2823735/1
//  Date of Submission: 22/04/2022

package helperClasses;

import CSCU9N6Library.Animation;
import CSCU9N6Library.Sprite;
import renderableObjects.BackgroundEntity;
import renderableObjects.IDrawable;

import java.util.LinkedList;
import java.util.Random;

/**
 * Creates a large number of BackgroundEntities (stars) and sets them around the edge of the screen in such a way that they appear to move past the ship as it travels.
 * Creates three layers of stars, in order to provide a sense of depth.  Maintains a set density of starfield regardless of the spacecraft speed.
 */
public class StarFieldGenerator
{
    private final long PIXELS_PER_STAR = 4_000; //The density of the starfield.  Used to keep the number of stars on screen constant.
    private EntityUpdate entityUpdate;
    private Random random = new Random();
    private long leftOverAreaToAddToNextUpdate;
    private float parallax;

    private int screenHeight;
    private int screenWidth;

    public StarFieldGenerator(int screenWidth, int screenHeight)
    {
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
    }

    /**
     * Creates a list of the stars to add to GameObjects
     * @param entityUpdate  An EntityUpdate object to interrogate for data
     * @param parallax  A float which is the amount the speed should be changed in order to give the illusion of depth.
     * @return  A LinkedList<IDrawable> which contains the stars to add to the background.
     */
    public LinkedList<IDrawable> spawnBackgroundStars(EntityUpdate entityUpdate, float parallax)
    {
        this.entityUpdate = entityUpdate;
        this.parallax = parallax;

        LinkedList<IDrawable> listOfStars = new LinkedList<>();

        int xAxisStars = starsToSpawnIfSpaceshipIsTravellingInTheXAxis();
        int yAxisStars = starsToSpawnIfSpaceshipIsTravellingInTheYAxis();

        //Spawn stars on the xAxis.
        for (int i = 0; i < xAxisStars; i++)
        {
            BackgroundEntity newStar = generateRandomStar();
            configureStarSpeed(newStar);
            configureStarLocationIfSpaceshipTravellingInXAxis(newStar);
            listOfStars.add(newStar);
        }

        //Spawn stars on the yAxis.
        for (int i = 0; i < yAxisStars; i++)
        {
            BackgroundEntity newStar = generateRandomStar();
            configureStarSpeed(newStar);
            configureStarLocationIfSpaceshipTravellingInYAxis(newStar);
            listOfStars.add(newStar);
        }

        return listOfStars;
    }

    /**
     * Creates a star (BackgroundEntity) to be added to the game.
     * @return
     */
    private BackgroundEntity generateRandomStar()
    {
        //Instantiate background entity.
        BackgroundEntity starToReturn = new BackgroundEntity(this.screenWidth, this.screenHeight);

        //Set up sprite.
        Animation starAnimation = new Animation();
        starAnimation.loadAnimationFromSheet("images/StarWhite.png", 1, 1, 10_000);
        Sprite starSprite = new Sprite(starAnimation);
        starSprite.show();
        starToReturn.setSprite(starSprite);
        starToReturn.setParallax(this.parallax);

        return starToReturn;
    }

    /**
     * Works out what speeds the new star should be going at, and sets them
     * @param star  A BackgroundEntity object which is to have its horizontal and vertical speeds set.
     */
    private void configureStarSpeed(BackgroundEntity star)
    {
        star.setXSpeed(-this.entityUpdate.getSpaceshipXSpeed());
        star.setYSpeed(-this.entityUpdate.getSpaceshipYSpeed());
    }

    /**
     * Works out which side of the screen to spawn the star on if the spaceship is travelling horizontally.  The y-axis coordinate is assigned a random number.
     * @param star  A BackgroundEntity object which is to have it's initial location set.
     */
    private void configureStarLocationIfSpaceshipTravellingInXAxis(BackgroundEntity star)
    {
        star.setYCoord(random.nextInt(this.entityUpdate.getScreenHeight()));

        if (star.getXSpeed() < 0)
        {
            star.setXCoord(this.entityUpdate.getScreenWidth());
        }
        else
        {
            star.setXCoord(0);
        }
    }

    /**
     * Works out which side of the screen to spawn the star on if the spaceship is travelling vertically.  The x-axis coordinate is assigned a random number.
     * @param star  A BackgroundEntity object which is to have it's initial location set.
     */
    private void configureStarLocationIfSpaceshipTravellingInYAxis(BackgroundEntity star)
    {
        star.setXCoord(random.nextInt(this.entityUpdate.getScreenWidth()));

        if (star.getYSpeed() < 0)
        {
            star.setYCoord(this.entityUpdate.getScreenHeight());
        }
        else
        {
            star.setYCoord(0);
        }

    }

    /**
     * Works out how many stars to spawn on the left and right edges of the display area if the ship is moving horizontally.
     * @return  An integer which is the number of stars to spawn on the left and right edges of the screen this update.
     */
    private int starsToSpawnIfSpaceshipIsTravellingInTheXAxis()
    {
        return numberOfStarsToSpawnOnAxis(this.entityUpdate.getSpaceshipXSpeed(), this.entityUpdate.getScreenHeight());
    }

    /**
     * Works out how many stars to spawn on the top and bottom edges of the display area if the ship is moving horizontally.
     * @return  An integer which is the number of stars to spawn on the top and bottom edges of the screen this update.
     */
    private int starsToSpawnIfSpaceshipIsTravellingInTheYAxis()
    {
        return numberOfStarsToSpawnOnAxis(this.entityUpdate.getSpaceshipYSpeed(), this.entityUpdate.getScreenWidth());
    }

    /**
     * Given a speed, and knowing the desired starfield density, work out how many stars to spawn to cover the newly rendered area with stars.
     * Called once for each of the two axes of movement of the spaceship.
     * @param axisSpeed A float which is the speed (in pixels per millisecond) of the spaceship.
     * @param widthOfNewArea   An int which is the width of the display area edge the ship is moving towards.
     * @return  An int which is the number of new stars required to maintain starfield density.
     */
    private int numberOfStarsToSpawnOnAxis(float axisSpeed, int widthOfNewArea)
    {
        int axisDistance;
        long areaExposed;
        int numberOfStars;

        //Multiply the speed in milliseconds by the elapsed time in milliseconds.
        axisDistance = (int) (axisSpeed * this.entityUpdate.getMillisSinceLastUpdate());

        if (axisDistance < 0)
        {
            axisDistance = axisDistance * -1;
        }

        //Multiply this by the other axis length to get the area to fill with stars.
        areaExposed = widthOfNewArea * axisDistance;

        //Save area that isn't filled with stars for next time.
        this.leftOverAreaToAddToNextUpdate = (areaExposed + this.leftOverAreaToAddToNextUpdate) % (PIXELS_PER_STAR);

        //Calculate and return the number of stars to spawn on this axis.
        numberOfStars = (int) ((areaExposed + this.leftOverAreaToAddToNextUpdate) / (PIXELS_PER_STAR));

        return numberOfStars;
    }
}
