package physics;

import CSCU9N6Library.TileMap;
import helperClasses.EntityUpdate;
import helperClasses.TilemapHelper;

import java.util.ArrayList;

public class PhysicsEngine
{
    private ArrayList<Collider> colliders;
    private TileMap tileMap;
    private float xAxisGravityPerMilli = 0.000_0f;
    private float yAxisGravityPerMilli = 0.000_3f;
    private float frictionChangePerMilli = 0.000_4f;
    private float minimumSpeed = 0.01f;
    private float speedLossDueToTileMapCollision = 0.3f;
    private float constantOfElasticity = 0.9f;

    public PhysicsEngine(ArrayList<Collider> colliders)
    {
        this.colliders = colliders;
    }

    public void setTileMap(TileMap tileMap)
    {
        this.tileMap = tileMap;
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
            //Move the colliders according to their speed.
            collider.setXCoord(collider.getXCoord() + collider.getXSpeed() * update.getMillisSinceLastUpdate());
            collider.setYCoord(collider.getYCoord() + collider.getYSpeed() * update.getMillisSinceLastUpdate());

            //Handle all collisions.
            checkForAndHandleTilemapCollisions(collider);
            checkForAndHandleColliderCollisions(collider);

            //Handle environmental effects.
            handleGravity(collider, xAxisGravitySpeedChange, yAxisGravitySpeedChange);
            handleFriction(collider, frictionChangeFactor);

            //Now move colliders based on their control input.
            collider.setXCoord(collider.getXCoord() + collider.getXControlSpeed() * update.getMillisSinceLastUpdate());
            collider.setYCoord(collider.getYCoord() + collider.getYControlSpeed() * update.getMillisSinceLastUpdate());
        }
    }

    private void checkForAndHandleColliderCollisions(Collider collider)
    {
        //No colliders have had their collisions handled.
        for (Collider unhandledCollider : this.colliders)
        {
            unhandledCollider.setCollisionsHandled(false);
        }

        for (Collider otherCollider : this.colliders)
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
            stopColliderOverlapsAndHandleImpulseTransfer(collider, otherCollider);

            //No other colliders should be able to collide with this one during this update.
            collider.setCollisionsHandled(true);
        }
    }

    private void stopColliderOverlapsAndHandleImpulseTransfer(Collider collider, Collider otherCollider)
    {
        float horizontalDistance = findDistanceToMoveHorizontallyToEscapeCollision(collider, otherCollider);
        float verticalDistance = findDistanceToMoveVerticallyToEscapeCollision(collider, otherCollider);

        if (Math.abs(horizontalDistance) < Math.abs(verticalDistance))  //If the horizontal distance is shorter
        {
            //Move collider horizontally.
            collider.setXCoord(collider.getXCoord() + horizontalDistance);
            //Only handle horizontal impulse.
            processHorizontalImpulseTransfer(collider, otherCollider);
        }
        else
        {
            //Move collider vertically.
            collider.setYCoord(collider.getYCoord() + verticalDistance);
            //Only handle vertical impulse.
            processVerticalImpulseTransfer(collider, otherCollider);
        }

        //Tell the colliders parents what they've hit.
        collider.hasCollidedWith(otherCollider.getParent());
        otherCollider.hasCollidedWith(collider.getParent());
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

    private void processVerticalImpulseTransfer(Collider collider, Collider otherCollider)
    {
        //Calculate difference in speeds.
        float verticalRelativeSpeed = collider.getYSpeed() - otherCollider.getYSpeed();     //Signage: collider is hitting otherCollider

        //Calculate impulse.
        float verticalImpulse = (1 + constantOfElasticity) * (verticalRelativeSpeed) / (collider.getInverseMass() + otherCollider.getInverseMass());


        //Calculate vertical speed changes.
        float colliderVerticalSpeedChange = -verticalImpulse * collider.getInverseMass();   //Negative due to signage: collider is losing impulse from hitting something.
        float otherColliderVerticalSpeedChange = verticalImpulse * otherCollider.getInverseMass();  //Positive due to signage: otherCollider is gaining impulse form being hit.

        //If speed changes are too small, discard them.
        colliderVerticalSpeedChange = discardSmallSpeedChange(0.001f, colliderVerticalSpeedChange);
        otherColliderVerticalSpeedChange = discardSmallSpeedChange(0.001f, otherColliderVerticalSpeedChange);


        //Apply vertical speed changes.
        collider.setYSpeed(collider.getYSpeed() + colliderVerticalSpeedChange);
        otherCollider.setYSpeed(otherCollider.getYSpeed() + otherColliderVerticalSpeedChange);
    }

    private void processHorizontalImpulseTransfer(Collider collider, Collider otherCollider)
    {
        //Calculate difference in speeds.
        float horizontalRelativeSpeed = collider.getXSpeed() - otherCollider.getXSpeed();   //Signage: collider is hitting otherCollider

        //Calculate impulse.
        float horizontalImpulse = (1 + constantOfElasticity) * (horizontalRelativeSpeed) / (collider.getInverseMass() + otherCollider.getInverseMass());

        //Calculate horizontal speed changes.
        float colliderHorizontalSpeedChange = -horizontalImpulse * collider.getInverseMass();   //Negative due to signage: collider is losing impulse from hitting something.
        float otherColliderHorizontalSpeedChange = horizontalImpulse * otherCollider.getInverseMass();  //Positive due to signage: otherCollider is gaining impulse form being hit.

        //If speed changes are too small, discard them.
        colliderHorizontalSpeedChange = discardSmallSpeedChange(0.001f, colliderHorizontalSpeedChange);
        otherColliderHorizontalSpeedChange = discardSmallSpeedChange(0.001f, otherColliderHorizontalSpeedChange);

        //Apply horizontal speed changes.
        collider.setXSpeed(collider.getXSpeed() + colliderHorizontalSpeedChange);
        otherCollider.setXSpeed(otherCollider.getXSpeed() + otherColliderHorizontalSpeedChange);
    }

    private float discardSmallSpeedChange(float minimumSpeed, float speed)
    {
        if (speed < minimumSpeed && speed > -minimumSpeed)
        {
            speed = 0.0f;
        }

        return speed;
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
        if (collider.isIgnoringFriction())  //Ignore this collider if it ignores friction.
        {
            return;
        }

        //Apply an amount of friction so that objects come to a stop.
        collider.setYSpeed(collider.getYSpeed() * (1 - frictionChangeFactor));
        collider.setXSpeed(collider.getXSpeed() * (1 - frictionChangeFactor));
    }

    private void handleGravity(Collider collider, float xAxisGravityAcceleration, float yAxisGravityAcceleration)
    {
        if (collider.isIgnoringGravity() || collider.getInverseMass() <= 0) //If the collider is ignoring gravity, or shouldn't be affected by it:
        {
            return;
        }
        collider.setXSpeed(collider.getXSpeed() + xAxisGravityAcceleration);
        collider.setYSpeed(collider.getYSpeed() + yAxisGravityAcceleration);

        //Handle gravity lifts.
        if (TilemapHelper.isThisPointOnAGravityLift(collider.getXAxisCentroid(), collider.getYAxisCentroid(), this.tileMap))
        {
            //If it's on a lift, cancel out the normal vertical gravity, and make it fall upwards.
            collider.setYSpeed(collider.getYSpeed() - yAxisGravityAcceleration - 0.001f);
        }
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
        boolean collidingTopLeft = TilemapHelper.isThisPointOnAHullTile(leftEdge, topEdge, tileMap);
        boolean collidingTopRight = TilemapHelper.isThisPointOnAHullTile(rightEdge, topEdge, tileMap);
        boolean collidingBottomLeft = TilemapHelper.isThisPointOnAHullTile(leftEdge, bottomEdge, tileMap);
        boolean collidingBottomRight = TilemapHelper.isThisPointOnAHullTile(rightEdge, bottomEdge, tileMap);

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

            collider.bouncedOffTile();
        }
        else if (direction == EDirection.down)
        {
            collider.setYSpeed(-collider.getYSpeed() * this.speedLossDueToTileMapCollision);  //Reverse the y-axis velocity.
            collider.setYCoord(collider.getYCoord() + (this.tileMap.getTileHeight() - getYAxisGridOffset(collider)));  //Move the collider down out of the collision.
            handleMinimumSpeed(collider);

            collider.bouncedOffTile();
        }
        else if (direction == EDirection.left)
        {
            collider.setXSpeed(-collider.getXSpeed() * this.speedLossDueToTileMapCollision);  //Reverse the x-axis velocity.
            collider.setXCoord(collider.getXCoord() - getXAxisGridOffset(collider) + widthDiffBetweenEntitySizeAndTileSize(collider));  //Move the collider left out of the collision.
            handleMinimumSpeed(collider);

            collider.bouncedOffTile();
        }
        else if (direction == EDirection.right)
        {
            collider.setXSpeed(-collider.getXSpeed() * this.speedLossDueToTileMapCollision);  //Reverse the x-axis velocity.
            collider.setXCoord(collider.getXCoord() + (this.tileMap.getTileWidth() - getXAxisGridOffset(collider)));  //Move the collider right out of the collision.
            handleMinimumSpeed(collider);

            collider.bouncedOffTile();
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

    public enum EDirection
    {
        up,
        down,
        left,
        right
    }
}