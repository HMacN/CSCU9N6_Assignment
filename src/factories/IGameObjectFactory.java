package factories;

/**
 * Interface so that the factories can be swapped in and out of functions which spawn multiple objects into the game.
 */
public interface IGameObjectFactory
{
    /**
     * Spawn a new object at these in-game coordinates.
     * @param xCoord   A float which is the horizontal coordinate
     * @param yCoord    A float which is the vertical coordinate
     */
    void spawnNewAt(float xCoord, float yCoord);
}
