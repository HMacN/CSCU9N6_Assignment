package helperClasses;

import CSCU9N6Library.TileMap;
import factories.IGameObjectFactory;

public class TilemapHelper
{
    public static char tileMapCharFromGameCoordinates(float xCoord, float yCoord, TileMap tileMap)
    {
        //Work out tile from coordinates given.
        int tileMapX = (int) xCoord / tileMap.getTileWidth();
        int tileMapY = (int) yCoord / tileMap.getTileHeight();

        return tileMap.getTileChar(tileMapX, tileMapY);
    }

    public static boolean isThisPointOnALadder(float xCoord, float yCoord, TileMap tileMap)
    {
        if (tileMapCharFromGameCoordinates(xCoord, yCoord, tileMap) == ETileType.ladder.tileType)
        {
            return true;
        }

        return false;
    }

    public static boolean isThisPointOnAHullTile(float xCoord, float yCoord, TileMap tileMap)
    {
        if (tileMapCharFromGameCoordinates(xCoord, yCoord, tileMap) == ETileType.hull.tileType)
        {
            return true;
        }

        return false;
    }

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

    public static boolean isThisPointOnAGravityLift(float xCoord, float yCoord, TileMap tileMap)
    {
        if (tileMapCharFromGameCoordinates(xCoord, yCoord, tileMap) == ETileType.gravLift.tileType)
        {
            return true;
        }

        return false;
    }

    public enum ETileType
    {
        player('p'),
        cargoCrate('c'),
        hull('X'),
        ladder('l'),
        gravLift('^')
        ;

        private final char tileType;


        ETileType(char tileType)
        {
            this.tileType = tileType;
        }
    }
}