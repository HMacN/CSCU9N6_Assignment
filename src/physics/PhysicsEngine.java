package physics;

import CSCU9N6Library.TileMap;
import helperClasses.EntityUpdate;

import java.util.ArrayList;

public class PhysicsEngine
{
    private ArrayList<Collider> colliders;
    private TileMap tileMap;
    private float xAxisGravityPerMilli = 0.000_0f;
    private float yAxisGravityPerMilli = 0.000_1f;
    private float frictionChangePerMilli = 0.000_4f;
    private float tileWidth;
    private float tileHeight;
    private float minimumSpeed = 0.01f;
    private float speedLossDueToTileMapCollision = 0.5f;

    public PhysicsEngine(ArrayList<Collider> colliders)
    {
        this.colliders = colliders;
    }

    public void setTileMap(TileMap tileMap)
    {
        this.tileMap = tileMap;
        this.tileWidth = this.tileMap.getTileWidth();
        this.tileHeight = this.tileMap.getTileHeight();
    }

    public void update(EntityUpdate update)
    {
        //Work out how much the speed should be reduced by.
        float frictionChangeFactor = this.frictionChangePerMilli * update.getMillisSinceLastUpdate();

        //Work out x and y axis accelerations.
        float xAxisGravitySpeedChange = this.xAxisGravityPerMilli * update.getMillisSinceLastUpdate();
        float yAxisGravitySpeedChange = this.yAxisGravityPerMilli * update.getMillisSinceLastUpdate();

        for (Collider collider : colliders)
        {
            checkForAndHandleTilemapCollisions(collider);
            //get collider mass and location

            //get tilemap collisions
            //handle tilemap collisions

            //get physics collisions
            //handle physics collisions

            //resolve gravity and friction
            handleGravity(collider, xAxisGravitySpeedChange, yAxisGravitySpeedChange);
            handleFriction(collider, frictionChangeFactor);
        }
    }

    private void handleMinimumSpeed(Collider collider)
    {
        if (collider.getXSpeed() < this.minimumSpeed && collider.getXSpeed() > -this.minimumSpeed)
        {
            collider.setXSpeed(0.0f);
        }

        if (collider.getYSpeed() < this.minimumSpeed && collider.getYSpeed() > -this.minimumSpeed)
        {
            collider.setYSpeed(0.0f);
        }
    }

    private void handleFriction(Collider collider, float frictionChangeFactor)
    {
        if (collider.isIgnoreFriction())
        {
            return;
        }

        //Apply an amount of friction so that objects come to a stop.
        collider.setYSpeed(collider.getYSpeed() * (1 - frictionChangeFactor));
        collider.setXSpeed(collider.getXSpeed() * (1 - frictionChangeFactor));
    }

    private void handleGravity(Collider collider, float xAxisGravityAcceleration, float yAxisGravityAcceleration)
    {
        if (collider.isIgnoringGravity())
        {
            return;
        }
        collider.setXSpeed(collider.getXSpeed() + xAxisGravityAcceleration);

        if (collider.isIgnoringDownwardsGravity() && yAxisGravityAcceleration > 0)
        {
            return;
        }
        collider.setYSpeed(collider.getYSpeed() + yAxisGravityAcceleration);
    }

    public void setGravity(float xAxisGravityPerMilli, float yAxisGravityPerMilli)
    {
        this.xAxisGravityPerMilli = xAxisGravityPerMilli;
        this.yAxisGravityPerMilli = yAxisGravityPerMilli;
    }

    public void checkForAndHandleTilemapCollisions(Collider collider)
    {
        // Take a note of a sprite's current position
        float leftEdge = collider.getXCoord();
        float rightEdge = collider.getXCoord() + collider.getWidth();
        float topEdge = collider.getYCoord();
        float bottomEdge = collider.getYCoord() + collider.getHeight();

        //Check if there is a collision with the tile map.
        boolean collidingTopLeft = detectTileMapCollisionAtPoint(leftEdge, topEdge);
        boolean collidingTopRight = detectTileMapCollisionAtPoint(rightEdge, topEdge);
        boolean collidingBottomLeft = detectTileMapCollisionAtPoint(leftEdge, bottomEdge);
        boolean collidingBottomRight = detectTileMapCollisionAtPoint(rightEdge, bottomEdge);

        //If there is a collision, handle it, otherwise return.
        if (collidingTopLeft || collidingTopRight || collidingBottomLeft || collidingBottomRight)
        {
            handleTileMapCollisionForEntity(collider, collidingTopLeft, collidingTopRight, collidingBottomLeft, collidingBottomRight);
        }

        return;
    }

    private int countCollidingCorners(boolean collidingTopLeft, boolean collidingTopRight,
                                      boolean collidingBottomLeft, boolean collidingBottomRight)
    {
        int collidingCorners = 0;

        if (collidingTopLeft)
        {
            collidingCorners++;
        }

        if (collidingTopRight)
        {
            collidingCorners++;
        }

        if (collidingBottomLeft)
        {
            collidingCorners++;
        }

        if (collidingBottomRight)
        {
            collidingCorners++;
        }

        return collidingCorners;
    }

    private void handleTwoCornerTileCollision(Collider collider, boolean collidingTopLeft, boolean collidingTopRight,
                                              boolean collidingBottomLeft, boolean collidingBottomRight)
    {
        if (collidingTopLeft && collidingBottomLeft)    //Left face
        {
            moveColliderToGetAwayFromTile(collider, EDirection.right);
        }

        if (collidingTopRight && collidingBottomRight) //Right face
        {
            moveColliderToGetAwayFromTile(collider, EDirection.left);
        }

        if (collidingTopLeft && collidingTopRight)  //Top face
        {
            moveColliderToGetAwayFromTile(collider, EDirection.down);
        }

        if (collidingBottomLeft && collidingBottomRight)    //Bottom face
        {
            moveColliderToGetAwayFromTile(collider, EDirection.up);
        }
    }

    private void handleOneCornerTileCollision(Collider collider, boolean collidingTopLeft, boolean collidingTopRight,
                                              boolean collidingBottomLeft, boolean collidingBottomRight)
    {
        if (collidingTopLeft)   //Top left corner only
        {
            if (getXAxisGridOffset(collider) > getYAxisGridOffset(collider))    //If shorter distance to get out on x axis
            {
                moveColliderToGetAwayFromTile(collider, EDirection.right);
            }
            else
            {
                moveColliderToGetAwayFromTile(collider, EDirection.down);
            }
        }
        else if (collidingTopRight) //Top right corner only
        {
            if ((this.tileMap.getTileWidth() - getXAxisGridOffset(collider)) > getYAxisGridOffset(collider))    //If shorter distance to get out on x axis
            {
                moveColliderToGetAwayFromTile(collider, EDirection.left);
            }
            else
            {
                moveColliderToGetAwayFromTile(collider, EDirection.down);
            }
        }
        else if (collidingBottomLeft) //Bottom left corner only
        {
            if (getXAxisGridOffset(collider) > (this.tileMap.getTileHeight() - getYAxisGridOffset(collider)))    //If shorter distance to get out on x axis
            {
                moveColliderToGetAwayFromTile(collider, EDirection.right);
            }
            else
            {
                moveColliderToGetAwayFromTile(collider, EDirection.up);
            }
        }
        else if (collidingBottomRight) //Bottom right corner only
        {
            if ((this.tileMap.getTileWidth() - getXAxisGridOffset(collider)) > (this.tileMap.getTileHeight() - getYAxisGridOffset(collider)))    //If shorter distance to get out on x axis
            {
                moveColliderToGetAwayFromTile(collider, EDirection.left);
            }
            else
            {
                moveColliderToGetAwayFromTile(collider, EDirection.up);
            }
        }
    }

    private void handleTileMapCollisionForEntity(Collider collider,
                                                 boolean collidingTopLeft,
                                                 boolean collidingTopRight,
                                                 boolean collidingBottomLeft,
                                                 boolean collidingBottomRight)
    {
        //get colliding corner count
        int collidingCorners = countCollidingCorners(collidingTopLeft, collidingTopRight, collidingBottomLeft, collidingBottomRight);

        if (collidingCorners == 1)
        {
            handleOneCornerTileCollision(collider, collidingTopLeft, collidingTopRight, collidingBottomLeft, collidingBottomRight);
        }
        else if (collidingCorners > 1)
        {
            handleTwoCornerTileCollision(collider, collidingTopLeft, collidingTopRight, collidingBottomLeft, collidingBottomRight);
        }

    }

    private void moveColliderToGetAwayFromTile(Collider collider, EDirection direction)
    {
        if (direction == EDirection.up)
        {
            collider.setYSpeed(-collider.getYSpeed() * this.speedLossDueToTileMapCollision);  //Reverse the y-axis velocity.
            collider.setYCoord(collider.getYCoord() - getYAxisGridOffset(collider) + heightDiffBetweenEntitySizeAndTileSize(collider));  //Move the collider up out of the collision.
            handleMinimumSpeed(collider);
        }
        else if (direction == EDirection.down)
        {
            collider.setYSpeed(-collider.getYSpeed() * this.speedLossDueToTileMapCollision);  //Reverse the y-axis velocity.
            collider.setYCoord(collider.getYCoord() + (this.tileMap.getTileHeight() - getYAxisGridOffset(collider)));  //Move the collider down out of the collision.
            handleMinimumSpeed(collider);
        }
        else if (direction == EDirection.left)
        {
            collider.setXSpeed(-collider.getXSpeed() * this.speedLossDueToTileMapCollision);  //Reverse the x-axis velocity.
            collider.setXCoord(collider.getXCoord() - getXAxisGridOffset(collider) + widthDiffBetweenEntitySizeAndTileSize(collider));  //Move the collider left out of the collision.
            handleMinimumSpeed(collider);
        }
        else if (direction == EDirection.right)
        {
            collider.setXSpeed(-collider.getXSpeed() * this.speedLossDueToTileMapCollision);  //Reverse the x-axis velocity.
            collider.setXCoord(collider.getXCoord() + (this.tileMap.getTileWidth() - getXAxisGridOffset(collider)));  //Move the collider right out of the collision.
            handleMinimumSpeed(collider);
        }
    }

    private float widthDiffBetweenEntitySizeAndTileSize(Collider collider)
    {
        float difference = this.tileMap.getTileWidth() - collider.getWidth();

        return difference;
    }

    private float heightDiffBetweenEntitySizeAndTileSize(Collider collider)
    {
        float difference = this.tileMap.getTileHeight() - collider.getHeight();

        return difference;
    }

    private float getYAxisGridOffset(Collider collider)
    {
        return collider.getYCoord() % this.tileMap.getTileHeight();
    }

    private float getXAxisGridOffset(Collider collider)
    {
        return collider.getXCoord() % this.tileMap.getTileWidth();
    }

    private boolean detectTileMapCollisionAtPoint(float xCoord, float yCoord)
    {
        int xTile = (int)(xCoord / this.tileWidth);
        int yTile = (int)(yCoord / this.tileHeight);

        char tileChar = this.tileMap.getTileChar(xTile, yTile);

        if (tileChar == 'X') // If it's an 'X', it's a collision.
        {
            return true;
        }

        return false;
    }

    public enum EFaceOfCollider
    {
        topFace,
        bottomFace,
        leftFace,
        rightFace
    }

    private enum EDirection
    {
        up,
        down,
        left,
        right
    }
}