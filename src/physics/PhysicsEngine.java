package physics;

import CSCU9N6Library.TileMap;
import helperClasses.EntityUpdate;

import java.util.ArrayList;

public class PhysicsEngine
{
    private ArrayList<IPhysicsEntity> physicsEntities;
    private TileMap tileMap;
    private float xAxisGravity = 0.0f;
    private float yAxisGravity = 1.0f;
    private float tileWidth;
    private float tileHeight;

    public PhysicsEngine(ArrayList<IPhysicsEntity> physicsEntities, TileMap tileMap)
    {
        this.physicsEntities = physicsEntities;
        this.tileMap = tileMap;
        this.tileWidth = tileMap.getTileWidth();
        this.tileHeight = tileMap.getTileHeight();
    }

    public void update(EntityUpdate update)
    {
        for (IPhysicsEntity entity : physicsEntities)
        {
            //get entity mass and location

            //get tilemap collisions
            //handle tilemap collisions

            //get physics collisions
            //handle physics collisions

            //resolve gravity and friction
        }
    }

    public void setGravity(float xAxisGravity, float yAxisGravity)
    {
        this.xAxisGravity = xAxisGravity;
        this.yAxisGravity = yAxisGravity;
    }

    public void tileCollision(IPhysicsEntity entity)
    {
        // Take a note of a sprite's current position
        float sx = entity.getXCoord();
        float sy = entity.getYCoord();

        // Divide the sprite’s x coordinate by the width of a tile, to get
        // the number of tiles across the x axis that the sprite is positioned at
        int xTile = (int)(sx / this.tileWidth);
        // The same applies to the y coordinate
        int yTile = (int)(sy / this.tileHeight);

        // What character is at this position?
        char tileChar = this.tileMap.getTileChar(xTile, yTile);


        if (tileChar != '.' && tileChar != 'o') // If it's not a dot (empty space) or an 'o' (spaceship interior), handle it.
        {
            // Here we just stop the sprite.
            entity.setXSpeed(0.0f);
            entity.setYSpeed(0.0f);
            // You should move the sprite to a position that is not colliding
        }
    }
}