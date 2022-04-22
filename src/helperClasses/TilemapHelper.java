//  Assignment submission for CSCU9N6
//  Student Number: 2823735/1
//  Date of Submission: 22/04/2022

package helperClasses;

import CSCU9N6Library.TileMap;
import factories.IGameObjectFactory;

/**
 * A holder for functions that are useful for dealing with the TileMap class in the library.  Used to help avoid "butchering" that class.
 */
public class TilemapHelper
{
    /**
     * A function to find the char value of a map tile, using the in-game coordinates, rather than the TileMap grid coordinates.
     * @param xCoord    A float which is the in-game x-axis coordinate to check at.
     * @param yCoord    A float which is the in-game y-axis coordinate to check at.
     * @param tileMap   A TileMap to check a coordinate on.
     * @return  A char value, which is the char in the TileMap at the given coordinates.
     */
    public static char tileMapCharFromGameCoordinates(float xCoord, float yCoord, TileMap tileMap)
    {
        //Work out tile from coordinates given.
        int tileMapX = (int) xCoord / tileMap.getTileWidth();
        int tileMapY = (int) yCoord / tileMap.getTileHeight();

        return tileMap.getTileChar(tileMapX, tileMapY);
    }

    /**
     * Checks if the given point in the game world is on a ladder on the TileMap.
     * @param xCoord    A float which is the in-game x-axis coordinate to check at.
     * @param yCoord    A float which is the in-game y-axis coordinate to check at.
     * @param tileMap   A TileMap to check a coordinate on.
     * @return  A boolean which describes whether or not the given coordinates correspond to a ladder on the TileMap
     */
    public static boolean isThisPointOnALadder(float xCoord, float yCoord, TileMap tileMap)
    {
        if (tileMapCharFromGameCoordinates(xCoord, yCoord, tileMap) == ETileType.ladder.tileType)
        {
            return true;
        }

        return false;
    }

    /**
     * Checks if the given point in the game world is on a hull tile on the TileMap.
     * @param xCoord    A float which is the in-game x-axis coordinate to check at.
     * @param yCoord    A float which is the in-game y-axis coordinate to check at.
     * @param tileMap   A TileMap to check a coordinate on.
     * @return  A boolean which describes whether or not the given coordinates correspond to a hull tile on the TileMap
     */
    public static boolean isThisPointOnAHullTile(float xCoord, float yCoord, TileMap tileMap)
    {
        if (tileMapCharFromGameCoordinates(xCoord, yCoord, tileMap) == ETileType.hull.tileType)
        {
            return true;
        }

        return false;
    }

    /**
     * A function which iterates over the whole tilemap, and spawns a game object on each tile which corresponds to the "trigger" tile type.
     * @param type  An Enum which describes which tile type to spawn the new entities on.
     * @param tileMap   The TileMap object to iterate over.
     * @param factory   An IGameObjectFactory which produces the kind of entity to spawn.
     */
    public static void spawnEntityOnMap(ETileType type, TileMap tileMap, IGameObjectFactory factory)
    {
        for (int tileMapXCoord = 0; tileMapXCoord < tileMap.getMapWidth(); tileMapXCoord++) //For each element in a row.
        {
            for (int tileMapYCoord = 0; tileMapYCoord < tileMap.getMapHeight(); tileMapYCoord++)    //For each element in a column.
            {
                if (tileMap.getTileChar(tileMapXCoord, tileMapYCoord) == type.tileType) //If the tile matches the target type
                {
                    factory.spawnNewAt(tileMapXCoord * tileMap.getTileWidth(), tileMapYCoord * tileMap.getTileHeight());    //create a new entity at that point.
                }
            }
        }
    }

    /**
     * Checks if the given point in the game world is on a gravity lift on the TileMap.
     * @param xCoord    A float which is the in-game x-axis coordinate to check at.
     * @param yCoord    A float which is the in-game y-axis coordinate to check at.
     * @param tileMap   A TileMap to check a coordinate on.
     * @return  A boolean which describes whether or not the given coordinates correspond to a gravity lift on the TileMap
     */
    public static boolean isThisPointOnAGravityLift(float xCoord, float yCoord, TileMap tileMap)
    {
        if (tileMapCharFromGameCoordinates(xCoord, yCoord, tileMap) == ETileType.gravLift.tileType)
        {
            return true;
        }

        return false;
    }

    /**
     * An enum which records which char values in the tilemap correspond to which types of in-game objects.
     */
    public enum ETileType
    {
        player('p'),
        cargoCrate('c'),
        hull('X'),
        ladder('l'),
        gravLift('^'),
        monster('M'),
        ;

        private final char tileType;

        ETileType(char tileType)
        {
            this.tileType = tileType;
        }
    }
}
