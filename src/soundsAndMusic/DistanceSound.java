package soundsAndMusic;

import CSCU9N6Library.Sound;
import physics.Collider;
import spaceShipGame.GameObjects;
import spaceShipGame.SpaceshipGame;

public class DistanceSound extends Sound implements IGameSound
{
    private GameObjects gameObjects;
    private SpaceshipGame spaceshipGame;
    private float maxDistance = 1_000.0f;
    private Collider collider;

    public DistanceSound(String fileName, GameObjects gameObjects, SpaceshipGame spaceshipGame, Collider collider)
    {
        super(fileName);
        this.gameObjects = gameObjects;
        this.collider = collider;
        this.spaceshipGame = spaceshipGame;
    }

    @Override
    public void play()
    {
        float volume = calculateVolume();

        this.setFilter(new SetVolumeSoundFilter(volume));
        super.play();
    }

    private float calculateVolume()
    {
        float volume = 1.0f;

        volume = volume * (calculateMaxDistanceMinusDistance() / this.maxDistance);

        System.out.println("Distance: " + calculateDistanceToPlayer() + ", Volume: " + volume);

        return volume;
    }

    private float calculateMaxDistanceMinusDistance()
    {
        float maxMinusActual = this.maxDistance - calculateDistanceToPlayer();

        if (maxMinusActual < 0)
        {
            return 0.0f;
        }

        return maxMinusActual;
    }

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
