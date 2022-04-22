package soundsAndMusic;

import CSCU9N6Library.Sound;
import physics.Collider;
import spaceShipGame.SpaceshipGame;

/**
 * A daughter class of Sound which uses a volume control filter to reduce the sound of an event based on it's distance from the player.
 */
public class DistanceSound extends Sound implements IGameSound
{
    private SpaceshipGame spaceshipGame;
    private float maxDistance = 1_000.0f;
    private Collider collider;

    /**
     * The constructor.
     * @param fileName  A String which is the name of the file to play.
     * @param spaceshipGame A SpaceshipGame object to interrogate for needed data.
     * @param collider  A Collider object to get the sound location from.
     */
    public DistanceSound(String fileName, SpaceshipGame spaceshipGame, Collider collider)
    {
        super(fileName);
        this.collider = collider;
        this.spaceshipGame = spaceshipGame;
    }

    /**
     * Starts the sound playing.
     */
    @Override
    public void play()
    {
        float volume = calculateVolume();

        this.setFilter(new SetVolumeSoundFilter(volume));
        super.play();
    }

    /**
     * Works out the volume based on the difference between the player location and the collider location.  Returns zero if the player is beyond the maximum range of the sound.
     * @return  A float which is the factor to multiply the sound amplitude by.
     */
    private float calculateVolume()
    {
        float volume = 1.0f;

        volume = volume * (calculateMaxDistanceMinusActualDistance() / this.maxDistance);

        return volume;
    }

    /**
     * Computes the distance between the player and the source of the sound.  Returns zero if the actual distance is larger than the maximum.
     * @return  A float which is the difference in pixels between the distance to the player and the maximum distance the sound can be heard at.
     */
    private float calculateMaxDistanceMinusActualDistance()
    {
        if (calculateDistanceToPlayer() > this.maxDistance)
        {
            return 0.0f;
        }

        return this.maxDistance - calculateDistanceToPlayer();
    }

    /**
     * Computes the distance between the player and the source of the sound.
     * @return  A float which is the distance in pixels between the player and the source of the sound.
     */
    private float calculateDistanceToPlayer()
    {
        //float xDistance = this.collider.getXAxisCentroid() - this.gameObjects.getPlayerXOffset();
        //float yDistance = this.collider.getYAxisCentroid() - this.gameObjects.getPlayerYOffset();

        float xDistance = this.collider.getXAxisCentroid() - (this.spaceshipGame.getScreenWidth() / 2.0f);
        float yDistance = this.collider.getYAxisCentroid() - (this.spaceshipGame.getScreenHeight() / 2.0f);

        float totalDistance = (float) Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));

        return totalDistance;
    }
}
