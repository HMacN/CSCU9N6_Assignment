import java.util.ArrayList;

public class MainMenuState implements IGameState
{
    private ArrayList<BackgroundEntity> backgroundEntities;
    private ArrayList<IPhysicsEntity> physicsEntities;

    public MainMenuState(ArrayList<BackgroundEntity> backgroundEntities, ArrayList<IPhysicsEntity> physicsEntities)
    {
        this.backgroundEntities = backgroundEntities;
        this.physicsEntities = physicsEntities;
    }

    @Override
    public void update(long elapsedTimeSinceLastUpdate)
    {
        spawnNewStars(elapsedTimeSinceLastUpdate);
    }

    @Override
    public void initialSetup()
    {

    }

    private void spawnNewStars(long elapsedTimeSinceLastUpdate)
    {
        int starsToSpawn = (int) elapsedTimeSinceLastUpdate % 1000;

        for (int i = 0; i < starsToSpawn; i++)
        {
            BackgroundEntity newStar = generateRandomStar();
            this.backgroundEntities.add(newStar);
        }
    }

    private BackgroundEntity generateRandomStar()
    {
        return new BackgroundEntity();
    }
}
