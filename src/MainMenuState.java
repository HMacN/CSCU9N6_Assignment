import java.util.ArrayList;

public class MainMenuState implements IGameState
{
    private ArrayList<BackgroundEntity> backgroundEntities;
    private ArrayList<IPhysicsEntity> physicsEntities;
    private StarfieldGenerator starfieldGenerator = new StarfieldGenerator();

    public MainMenuState(ArrayList<BackgroundEntity> backgroundEntities, ArrayList<IPhysicsEntity> physicsEntities)
    {
        this.backgroundEntities = backgroundEntities;
        this.physicsEntities = physicsEntities;
    }

    @Override
    public void update(EntityUpdate entityUpdate)
    {
        this.backgroundEntities.addAll(starfieldGenerator.spawnBackgroundStars(entityUpdate));
    }

    @Override
    public void initialSetup()
    {

    }

}
