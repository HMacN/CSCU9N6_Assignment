//  Assignment submission for CSCU9N6
//  Student Number: 2823735/1
//  Date of Submission: 22/04/2022

package soundsAndMusic;

import CSCU9N6Library.Sound;
import CSCU9N6Library.Sprite;
import spaceShipGame.SpaceshipGame;

/**
 * A daughter class of Sound which uses a volume control filter to reduce the sound of an event based on it's distance from the player.
 */
public class DistanceSound extends Sound implements IGameSound
{
    private SpaceshipGame spaceshipGame;
    private float maxDistance = 1_000.0f;
    private Sprite soundSource;

    /**
     * The constructor.
     * @param fileName  A String which is the name of the file to play.
     * @param spaceshipGame A SpaceshipGame object to interrogate for needed data.
     * @param soundSource  A Sprite object to get the sound location from.
     */
    public DistanceSound(String fileName, SpaceshipGame spaceshipGame, Sprite soundSource)
    {
        super(fileName);
        this.soundSource = soundSource;
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
     * Works out the volume based on the difference between the player location and the sprite location.  Returns zero if the player is beyond the maximum range of the sound.
     * @return  A float which is the factor to multiply the sound amplitude by.
     */
    private float calculateVolume()
    {

        return (calculateMaxDistanceMinusActualDistance() / this.maxDistance);
    }

    /**
     * Computes the difference between the maximum distance to hear sounds at and the actual distance.  Returns zero if the actual distance is larger than the maximum.
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
        float xDistance = this.soundSource.getX() - (this.spaceshipGame.getScreenWidth() / 2.0f);
        float yDistance = this.soundSource.getY() - (this.spaceshipGame.getScreenHeight() / 2.0f);

        float totalDistance = (float) Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));

        return totalDistance;
    }
}
