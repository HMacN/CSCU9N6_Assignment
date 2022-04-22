//  Assignment submission for CSCU9N6
//  Student Number: 2823735/1
//  Date of Submission: 22/04/2022

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
