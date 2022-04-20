package physics;

import CSCU9N6Library.TileMap;
import helperClasses.Debug;
import helperClasses.EntityUpdate;
import helperClasses.TilemapHelper;

import java.util.ArrayList;

public class PhysicsEngine
{
    private ArrayList<Collider> allColliders;
    private ArrayList<Collider> possiblyCollidingColliders = new ArrayList<>();
    private TileMap tileMap;
    private float xAxisGravityPerMilli = 0.000_0f;
    private float yAxisGravityPerMilli = 0.000_1f;
    private float frictionChangePerMilli = 0.000_4f;
    private float minimumSpeed = 0.01f;
    private float speedLossDueToTileMapCollision = 0.5f;
    private float constantOfElasticity = 0.9f;

    public PhysicsEngine(ArrayList<Collider> colliders)
    {
        this.allColliders = colliders;
    }

    public void setTileMap(TileMap tileMap)
    {
        this.tileMap = tileMap;
    }

    public void update(EntityUpdate update)
    {
        //Clear list of possibly colliding colliders.
        this.possiblyCollidingColliders = new ArrayList<>();

        //Work out how much the speed should be reduced by.
        float frictionChangeFactor = this.frictionChangePerMilli * update.getMillisSinceLastUpdate();

        //Work out x and y axis accelerations.
        float xAxisGravitySpeedChange = this.xAxisGravityPerMilli * update.getMillisSinceLastUpdate();
        float yAxisGravitySpeedChange = this.yAxisGravityPerMilli * update.getMillisSinceLastUpdate();

        for (Collider collider : allColliders)
        {
            checkForAndHandleTilemapCollisions(collider);
            checkForAndHandleColliderCollisions(collider);

            handleGravity(collider, xAxisGravitySpeedChange, yAxisGravitySpeedChange);
            handleFriction(collider, frictionChangeFactor);
        }
    }

    private void checkForAndHandleColliderCollisions(Collider collider)
    {
        for (Collider otherCollider : this.allColliders)
        {
            //Check that the other collider has not already been handled.
            if (otherCollider.collisionsAlreadyHandled())
            {
                continue;   //Skip to the next collider.
            }

            //Check that the other collider is not this one.
            if (collider.equals(otherCollider))
            {
                continue;   //Skip to the next collider.
            }

            //Check that the other collider is near enough horizontally.
            if (horizontalDistanceBetweenCentroidsIsGreaterThanCombinedHalfWidths(collider, otherCollider))
            {
                continue;   //Skip to the next collider.
            }

            //Check that the other collider is near enough vertically.
            if (verticalDistanceBetweenCentroidsIsGreaterThanCombinedHalfHeights(collider, otherCollider))
            {
                continue;   //Skip to the next collider.
            }

            //The two colliders must be overlapping to have got this far.

            processImpulseTransferBetweenColliders(collider, otherCollider);
            ensureCollidersAreNotOverlapping(collider, otherCollider);
        }

        //No other colliders should be able to collide with this one during this update.
        collider.setCollisionsHandled();
    }

    private void ensureCollidersAreNotOverlapping(Collider collider, Collider otherCollider)
    {
        float horizontalDistance = findDistanceToMoveHorizontallyToEscapeCollision(collider, otherCollider);
        float verticalDistance = findDistanceToMoveVerticallyToEscapeCollision(collider, otherCollider);

        if (Math.abs(horizontalDistance) < Math.abs(verticalDistance))  //If the horizontal distance is shorter
        {
            collider.setXCoord(collider.getXCoord() + horizontalDistance);
        }
        else
        {
            collider.setYCoord(collider.getYCoord() + verticalDistance);
        }
    }

    private float findDistanceToMoveVerticallyToEscapeCollision(Collider collider, Collider otherCollider)
    {
        //Work out which is further up.
        float positionDifference = collider.getYCoord() - otherCollider.getYCoord();
        float distanceToMove;

        if (positionDifference < 0) //If collider is further up than otherCollider
        {
            distanceToMove = -1 * (positionDifference + collider.getHeight());
        }
        else   //If the collider is further down than otherCollider
        {
            distanceToMove = otherCollider.getHeight() - positionDifference;
        }

        return distanceToMove;
    }

    private float findDistanceToMoveHorizontallyToEscapeCollision(Collider collider, Collider otherCollider)
    {
        //Work out which is further to the left.
        float positionDifference = collider.getXCoord() - otherCollider.getXCoord();
        float distanceToMove;

        if (positionDifference < 0) //If collider is further left than otherCollider
        {
            distanceToMove = -1 * (positionDifference + collider.getWidth());
        }
        else   //If the collider is further right than otherCollider
        {
            distanceToMove = otherCollider.getWidth() - positionDifference;
        }

        return distanceToMove;
    }

    private void processImpulseTransferBetweenColliders(Collider collider, Collider otherCollider)
    {
        //Calculate differences in speeds.
        float horizontalRelativeSpeed = collider.getXSpeed() - otherCollider.getXSpeed();   //Signage: collider is hitting otherCollider
        float verticalRelativeSpeed = collider.getYSpeed() - otherCollider.getYSpeed();     //Signage: collider is hitting otherCollider

        //Calculate impulses.
        float horizontalImpulse = (1 + constantOfElasticity) * (horizontalRelativeSpeed) / (collider.getInverseMass() + otherCollider.getInverseMass());
        float verticalImpulse = (1 + constantOfElasticity) * (verticalRelativeSpeed) / (collider.getInverseMass() + otherCollider.getInverseMass());

        //Calculate horizontal speed changes.
        float colliderHorizontalSpeedChange = -horizontalImpulse * collider.getInverseMass();   //Negative due to signage: collider is losing impulse from hitting something.
        float otherColliderHorizontalSpeedChange = horizontalImpulse * otherCollider.getInverseMass();  //Positive due to signage: otherCollider is gaining impulse form being hit.

        //Calculate vertical speed changes.
        float colliderVerticalSpeedChange = -verticalImpulse * collider.getInverseMass();   //Negative due to signage: collider is losing impulse from hitting something.
        float otherColliderVerticalSpeedChange = verticalImpulse * otherCollider.getInverseMass();  //Positive due to signage: otherCollider is gaining impulse form being hit.

        //Apply horizontal speed changes.
        collider.setXSpeed(collider.getXSpeed() + colliderHorizontalSpeedChange);
        otherCollider.setXSpeed(otherCollider.getXSpeed() + otherColliderHorizontalSpeedChange);

        //Apply vertical speed changes.
        collider.setYSpeed(collider.getYSpeed() + colliderVerticalSpeedChange);
        otherCollider.setYSpeed(otherCollider.getYSpeed() + otherColliderVerticalSpeedChange);
    }

    private boolean verticalDistanceBetweenCentroidsIsGreaterThanCombinedHalfHeights(Collider collider, Collider otherCollider)
    {
        float verticalDistance = Math.abs(collider.getYAxisCentroid() - otherCollider.getYAxisCentroid());  //Distance between centres of colliders.
        float combinedHalfHeights = collider.getHalfHeight() + otherCollider.getHalfHeight();   //Distance within which there will be a collision.

        if (verticalDistance > combinedHalfHeights)
        {
            return true;
        }

        return false;
    }

    private boolean horizontalDistanceBetweenCentroidsIsGreaterThanCombinedHalfWidths(Collider collider, Collider otherCollider)
    {
        float horizontalDistance = Math.abs(collider.getXAxisCentroid() - otherCollider.getXAxisCentroid());  //Distance between centres of colliders.
        float combinedHalfWidths = collider.getHalfWidth() + otherCollider.getHalfWidth();   //Distance within which there will be a collision.

        if (horizontalDistance > combinedHalfWidths)
        {
            return true;
        }

        return false;
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
        boolean collidingTopLeft = TilemapHelper.detectTileMapCollisionAtPoint(leftEdge, topEdge, tileMap);
        boolean collidingTopRight = TilemapHelper.detectTileMapCollisionAtPoint(rightEdge, topEdge, tileMap);
        boolean collidingBottomLeft = TilemapHelper.detectTileMapCollisionAtPoint(leftEdge, bottomEdge, tileMap);
        boolean collidingBottomRight = TilemapHelper.detectTileMapCollisionAtPoint(rightEdge, bottomEdge, tileMap);

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