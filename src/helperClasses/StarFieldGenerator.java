package helperClasses;

import CSCU9N6Library.Animation;
import CSCU9N6Library.Sprite;
import renderableObjects.BackgroundEntity;
import renderableObjects.IDrawable;

import java.util.LinkedList;
import java.util.Random;

public class StarFieldGenerator
{
    private final long PIXELS_PER_STAR = 4_000;
    private EntityUpdate entityUpdate;
    private Random random = new Random();
    private long leftOverAreaToAddToNextUpdate;
    private float parallax;


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
            BackgroundEntity newStar = generateRandomStar(this.entityUpdate.getScreenWidth(), this.entityUpdate.getScreenHeight());
            configureStarSpeed(newStar);
            configureStarLocationIfSpaceshipTravellingInXAxis(newStar);
            listOfStars.add(newStar);
        }

        //Spawn stars on the yAxis.
        for (int i = 0; i < yAxisStars; i++)
        {
            BackgroundEntity newStar = generateRandomStar(this.entityUpdate.getScreenWidth(), this.entityUpdate.getScreenHeight());
            configureStarSpeed(newStar);
            configureStarLocationIfSpaceshipTravellingInYAxis(newStar);
            listOfStars.add(newStar);
        }

        return listOfStars;
    }

    private BackgroundEntity generateRandomStar(int screenWidth, int screenHeight)
    {
        //Instantiate background entity.
        BackgroundEntity starToReturn = new BackgroundEntity(screenWidth, screenHeight);

        //Set up sprite.
        Animation starAnimation = new Animation();
        starAnimation.loadAnimationFromSheet("images/StarWhite.png", 1, 1, 10_000);
        Sprite starSprite = new Sprite(starAnimation);
        starSprite.show();
        starToReturn.setSprite(starSprite);
        starToReturn.setParallax(this.parallax);

        return starToReturn;
    }

    private void configureStarSpeed(BackgroundEntity star)
    {
        star.setXSpeed(-this.entityUpdate.getSpaceshipXSpeed());
        star.setYSpeed(-this.entityUpdate.getSpaceshipYSpeed());
    }

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

    private int starsToSpawnIfSpaceshipIsTravellingInTheXAxis()
    {
        return numberOfStarsToSpawnOnAxis(this.entityUpdate.getSpaceshipXSpeed(), this.entityUpdate.getScreenHeight());
    }

    private int starsToSpawnIfSpaceshipIsTravellingInTheYAxis()
    {
        return numberOfStarsToSpawnOnAxis(this.entityUpdate.getSpaceshipYSpeed(), this.entityUpdate.getScreenWidth());
    }

    private int numberOfStarsToSpawnOnAxis(float axisSpeed, int crossAxisLength)
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

        //Multiply this by the screen width to get the area to fill with stars.
        areaExposed = crossAxisLength * axisDistance;

        //Save area that isn't filled with stars for next time.
        this.leftOverAreaToAddToNextUpdate = (areaExposed + this.leftOverAreaToAddToNextUpdate) % (PIXELS_PER_STAR);

        //Calculate and return the number of stars to spawn on this axis.
        numberOfStars = (int) ((areaExposed + this.leftOverAreaToAddToNextUpdate) / (PIXELS_PER_STAR));

        return numberOfStars;
    }
}
