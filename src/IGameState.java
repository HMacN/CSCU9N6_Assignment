public interface IGameState
{
    void update(EntityUpdate entityUpdate);

    void initialSetup();
}