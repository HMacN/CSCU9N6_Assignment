import game2D.Animation;
import game2D.Sprite;

import java.util.LinkedList;
import java.util.Random;

public class StarfieldGenerator
{
    private final long PIXELS_PER_STAR = 1_000;
    private EntityUpdate entityUpdate;
    private Random random = new Random();
    private long leftOverAreaToAddToNextUpdate;


    public LinkedList<BackgroundEntity> spawnBackgroundStars(EntityUpdate entityUpdate)
    {
        this.entityUpdate = entityUpdate;

        LinkedList<BackgroundEntity> listOfStars = new LinkedList<>();

        int xAxisStars = xAxisStarsToSpawn();
        int yAxisStars = yAxisStarsToSpawn();

        //Spawn stars on the xAxis.
        for (int i = 0; i < xAxisStars; i++)
        {
            BackgroundEntity newStar = generateRandomStar();
            configureStarSpeed(newStar);
            configureStarYAxisLocation(newStar);
            listOfStars.add(newStar);
        }

        //Spawn stars on the yAxis.
        for (int i = 0; i < yAxisStars; i++)
        {
            BackgroundEntity newStar = generateRandomStar();
            configureStarSpeed(newStar);
            configureStarXAxisLocation(newStar);
            listOfStars.add(newStar);
        }

        return listOfStars;
    }

    private BackgroundEntity generateRandomStar()
    {
        //Instantiate background entity.
        BackgroundEntity starToReturn = new BackgroundEntity();

        //Set up sprite.
        Animation starAnimation = new Animation();
        starAnimation.loadAnimationFromSheet("images/StarWhite.png", 1, 1, 10_000);
        Sprite starSprite = new Sprite(starAnimation);
        starSprite.show();
        starToReturn.setSprite(starSprite);

        return starToReturn;
    }

    private void configureStarSpeed(BackgroundEntity star)
    {
        star.setXSpeed(-this.entityUpdate.getSpaceshipXSpeed());
        star.setYSpeed(-this.entityUpdate.getSpaceshipYSpeed());
    }

    private void configureStarXAxisLocation(BackgroundEntity star)
    {
        star.setYCoord(random.nextInt(SpaceshipGame.SCREEN_HEIGHT));

        if (star.getXSpeed() < 0)
        {
            star.setXCoord(SpaceshipGame.SCREEN_WIDTH);
        }
        else
        {
            star.setXCoord(0);
        }
    }

    private void configureStarYAxisLocation(BackgroundEntity star)
    {
        star.setXCoord(random.nextInt(SpaceshipGame.SCREEN_WIDTH));

        if (star.getYSpeed() < 0)
        {
            star.setYCoord(SpaceshipGame.SCREEN_HEIGHT);
        }
        else
        {
            star.setYCoord(0);
        }
    }

    private int xAxisStarsToSpawn()
    {
        return numberOfStarsToSpawnOnAxis(this.entityUpdate.getSpaceshipXSpeed(), SpaceshipGame.SCREEN_HEIGHT);
    }

    private int yAxisStarsToSpawn()
    {
        return numberOfStarsToSpawnOnAxis(this.entityUpdate.getSpaceshipYSpeed(), SpaceshipGame.SCREEN_WIDTH);
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

        Debug.print("Area left over: " + this.leftOverAreaToAddToNextUpdate);

        return numberOfStars;
    }
}
