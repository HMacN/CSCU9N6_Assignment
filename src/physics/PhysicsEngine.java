//  Assignment submission for CSCU9N6
//  Student Number: 2823735/1
//  Date of Submission: 22/04/2022

package physics;

import CSCU9N6Library.TileMap;
import helperClasses.EntityUpdate;
import helperClasses.TilemapHelper;

import java.util.ArrayList;

/**
 * The class used to do all physics calculations in the game.  Contains an arraylist of colliders which have their collision detection done each update.
 */
public class PhysicsEngine
{
    private ArrayList<Collider> colliders;
    private TileMap tileMap;
    private float xAxisGravityPerMilli = 0.000_0f;
    private float yAxisGravityPerMilli = 0.000_2f;
    private float frictionChangePerMilli = 0.999f;        //Change in speed for colliders due to general friction.
    private float minimumSpeed = 0.001f;                     //A minimum speed for the colliders.
    private float speedLossDueToTileMapCollision = 0.5f;    //Factor for how much speed should an object lose when it hits a tile.
    private float constantOfElasticity = 0.1f;              //Factor used in collider-on-collider calculations.
    private float millisSinceLastUpdate;

    /**
     * The constructor.
     * @param colliders     An ArrayList of colliders which will be the subjects of the physics calculations each update.
     */
    public PhysicsEngine(ArrayList<Collider> colliders)
    {
        this.colliders = colliders;
    }

    /**
     * Sets the TileMap to be used for detecting tile collisions.
     * @param tileMap   A TileMap object which is the tilemap to use for detecting tile collisions.
     */
    public void setTileMap(TileMap tileMap)
    {
        this.tileMap = tileMap;
    }

    /**
     * Performs all the physics calculations for an update cycle.
     * @param update    An EntityUpdate object which contains all the data for this update cycle.
     */
    public void update(EntityUpdate update)
    {
        this.millisSinceLastUpdate = update.getMillisSinceLastUpdate();

        //No colliders have had their collisions handled this update cycle.
        for (Collider unhandledCollider : this.colliders)
        {
            unhandledCollider.setCollisionsHandled(false);
        }

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
            handleFriction(collider, update.getMillisSinceLastUpdate());

            //Now move colliders based on their control input.
            collider.setXCoord(collider.getXCoord() + collider.getXControlSpeed() * update.getMillisSinceLastUpdate());
            collider.setYCoord(collider.getYCoord() + collider.getYControlSpeed() * update.getMillisSinceLastUpdate());
        }
    }

    /**
     * Iterates over the list of colliders and handles any collisions between them.
     * Colliders which have been handled are skipped, as they should already have had all of their collisions resolved.
     *
     * Performs "bounding rects" collision detection.  Other layers of collision detection were considered, but the
     * current calculation is so simple that implementing other systems would have slowed the process down.  The colliders
     * are only ever upright rectangles, so this was felt to be sufficient for the detail of the collision detection
     * system, while also being as simple as possible.
     *
     * @param collider  The Collider object whose collisions for this update cycle are to be resolved.
     */
    private void checkForAndHandleColliderCollisions(Collider collider)
    {
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

            //Tell the colliders parents what they've hit.
            collider.hasCollidedWith(otherCollider.getParent());
            otherCollider.hasCollidedWith(collider.getParent());

            //Actually process the physics of the collision.
            stopColliderOverlapsAndHandleImpulseTransfer(collider, otherCollider);

            //No other colliders should be able to collide with this one during this update.
            collider.setCollisionsHandled(true);
        }
    }

    /**
     * Move two overlapping colliders so that they no longer overlap, and call the appropriate impulse transfer function.
     * Moves the collider by the shortest horizontal or vertical distance to escape the collision.
     * Impulse is only transferred along one axis at a time, in order to stop the colliders "sticking" to each other.
     * @param collider  The collider whose collisions are being processed.
     * @param otherCollider A collider that the current collider has collided with.
     */
    private void stopColliderOverlapsAndHandleImpulseTransfer(Collider collider, Collider otherCollider)
    {
        float horizontalDistance = findDistanceToMoveHorizontallyToEscapeCollision(collider, otherCollider);
        float verticalDistance = findDistanceToMoveVerticallyToEscapeCollision(collider, otherCollider);

        if (Math.abs(horizontalDistance) < Math.abs(verticalDistance))  //If the horizontal distance is shorter
        {
            if (attemptToMoveCollider(collider, 0.0f, horizontalDistance))  //If the collider can be moved
            {
                //TODO Uncomment when fixed
                //processHorizontalImpulseTransfer(collider, otherCollider, false);    //Process the impulse exchange normally
            }
            else
            {
                //TODO Uncomment when fixed
                //processHorizontalImpulseTransfer(collider, otherCollider, true);    //Reflect the impulse back into the other collider.
            }

            //Move collider horizontally.
            //attemptToMoveCollider(collider, 0.0f, horizontalDistance);
            //Only handle horizontal impulse.
            //processHorizontalImpulseTransfer(collider, otherCollider);
        }
        else
        {
            if (attemptToMoveCollider(collider, verticalDistance, 0.0f))  //If the collider can be moved
            {
                //TODO Uncomment when fixed
                //processVerticalImpulseTransfer(collider, otherCollider, false);    //Process the impulse exchange normally
            }
            else
            {
                //TODO Uncomment when fixed
                //processVerticalImpulseTransfer(collider, otherCollider, true);    //Reflect the impulse back into the other collider.
            }

            //Move collider vertically.
            //attemptToMoveCollider(collider, verticalDistance, 0.0f);
            //Only handle vertical impulse.
            //processVerticalImpulseTransfer(collider, otherCollider);
        }
    }

    /**
     * Moves the collider, then checks if the new position is in collision with the tile map.  If so, then move the
     * collider back.  This should stop colliders from getting pushed through the tilemap.
     * @param collider  A Collider object which is to be moved.
     * @param verticalDistance  A float which is the vertical distance in pixels to move the collider.
     * @param horizontalDistance    A float which is the horizontal distance in pixels to move the collider.
     * @return a boolean describing whether or not the move was successful.
     */
    private boolean attemptToMoveCollider(Collider collider, float verticalDistance, float horizontalDistance)
    {
        //Note the original position of the collider.
        float originalX = collider.getXCoord();
        float originalY = collider.getYCoord();

        //Calculate and apply the new position.
        float newX = originalX + horizontalDistance;
        float newY = originalY + verticalDistance;
        collider.setXCoord(newX);
        collider.setYCoord(newY);

        //Check if there is a collision with the tile map.
        boolean collidingTopLeft = isColliderTopLeftCornerCollidingWithTileMap(collider);
        boolean collidingTopRight = isColliderTopRightCornerCollidingWithTileMap(collider);
        boolean collidingBottomLeft = isColliderBottomLeftCornerCollidingWithTileMap(collider);
        boolean collidingBottomRight = isColliderBottomRightCornerCollidingWithTileMap(collider);

        //If now colliding with the tilemap, put the collider back where it was and return false.
        if (collidingTopLeft || collidingTopRight || collidingBottomLeft || collidingBottomRight)
        {
            collider.setXCoord(originalX);
            collider.setYCoord(originalY);
            return false;
        }

        return true;
    }

    /**
     * Works out the shortest distance to move vertically in order to escape the collision.
     * @param collider  The collider whose collisions are being processed.
     * @param otherCollider A collider that the current collider has collided with.
     * @return  A float which describes the shortest vertical distance to move the collider to escape the collision.
     */
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

    /**
     * Works out the shortest distance to move horizontally in order to escape the collision.
     * @param collider  The collider whose collisions are being processed.
     * @param otherCollider A collider that the current collider has collided with.
     * @return  A float which describes the shortest horizontal distance to move the collider to escape the collision.
     */
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

    /**
     * Performs the impulse transfer calculations between the two colliders in the vertical axis.
     * @param collider  The collider whose collisions are being processed.
     * @param otherCollider A collider that the current collider has collided with.
     * @param colliderCantMove  A boolean which describes if the collider can't move.
     */
    private void processVerticalImpulseTransfer(Collider collider, Collider otherCollider, boolean colliderCantMove)
    {
        //Calculate difference in speeds.
        float verticalRelativeSpeed = collider.getYSpeed() + collider.getYControlSpeed() - otherCollider.getYSpeed() - otherCollider.getYControlSpeed();     //Signage: collider is hitting otherCollider

        //Calculate impulse.
        float verticalImpulse = (1 + constantOfElasticity) * (verticalRelativeSpeed) / (collider.getInverseMass() + otherCollider.getInverseMass());

        //Calculate vertical speed changes.
        float colliderVerticalSpeedChange = verticalImpulse * collider.getInverseMass();   //Negative due to signage: collider is losing impulse from hitting something.
        float otherColliderVerticalSpeedChange = -verticalImpulse * otherCollider.getInverseMass();  //Positive due to signage: otherCollider is gaining impulse form being hit.

        //If speed changes are too small, discard them.
        colliderVerticalSpeedChange = discardSmallSpeedChange(colliderVerticalSpeedChange);
        otherColliderVerticalSpeedChange = discardSmallSpeedChange(otherColliderVerticalSpeedChange);


        //Apply vertical speed changes if the collider can't move
        if (colliderCantMove)
        {
            otherCollider.setYSpeed(otherCollider.getYSpeed() + ( otherColliderVerticalSpeedChange));
        }
        else //If the collider can move
        {
            collider.setYSpeed(collider.getYSpeed() - colliderVerticalSpeedChange);
            otherCollider.setYSpeed(otherCollider.getYSpeed() + otherColliderVerticalSpeedChange);
        }
    }

    /**
     * Performs the impulse transfer calculations between the two colliders in the horizontal axis.
     * @param collider  The collider whose collisions are being processed.
     * @param otherCollider A collider that the current collider has collided with.
     * @param colliderCantMove  A boolean which describes whether or not the collider can move.
     */
    private void processHorizontalImpulseTransfer(Collider collider, Collider otherCollider, boolean colliderCantMove)
    {
        //Calculate difference in speeds.
        float horizontalRelativeSpeed = collider.getXSpeed() - collider.getXControlSpeed() - otherCollider.getXSpeed() - otherCollider.getXControlSpeed();   //Signage: collider is hitting otherCollider

        //Calculate impulse.
        float horizontalImpulse = (1 + constantOfElasticity) * (horizontalRelativeSpeed) / (collider.getInverseMass() + otherCollider.getInverseMass());

        //Calculate horizontal speed changes.
        float colliderHorizontalSpeedChange = horizontalImpulse * collider.getInverseMass();   //Negative due to signage: collider is losing impulse from hitting something.
        float otherColliderHorizontalSpeedChange = -horizontalImpulse * otherCollider.getInverseMass();  //Positive due to signage: otherCollider is gaining impulse form being hit.

        //If speed changes are too small, discard them.
        colliderHorizontalSpeedChange = discardSmallSpeedChange(colliderHorizontalSpeedChange);
        otherColliderHorizontalSpeedChange = discardSmallSpeedChange(otherColliderHorizontalSpeedChange);

        //Apply horizontal speed changes if the collider can't move
        if (colliderCantMove)
        {
            otherCollider.setXSpeed(otherCollider.getXSpeed() + ( otherColliderHorizontalSpeedChange));
        }
        else //If the collider can move
        {
            collider.setXSpeed(collider.getXSpeed() - colliderHorizontalSpeedChange);
            otherCollider.setXSpeed(otherCollider.getXSpeed() + otherColliderHorizontalSpeedChange);
        }
    }

    /**
     * Works out if the given speed is below the set minimum speed, and sets it to zero if so.
     * @param speed A float which represents a speed to be checked that it is above the minimum.
     * @return  A float which is the given speed, which will have been set to zero if it is below the minimum.
     */
    private float discardSmallSpeedChange(float speed)
    {
        if (speed < this.minimumSpeed && speed > -this.minimumSpeed)
        {
            speed = 0.0f;
        }

        return speed;
    }

    /**
     * Works out if two colliders overlap on the vertical axis.
     * @param collider  The collider whose collisions are being processed.
     * @param otherCollider A collider that the current collider has collided with.
     * @return  A boolean which is false if the two colliders overlap on the vertical axis.
     */
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

    /**
     * Works out if two colliders overlap on the horizontal axis.
     * @param collider  The collider whose collisions are being processed.
     * @param otherCollider A collider that the current collider has collided with.
     * @return  A boolean which is false if the two colliders overlap on the horizontal axis.
     */
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

    /**
     * Makes sure that the colliders horizontal and vertical speeds are above the minimum speed.
     * @param collider  A Collider object to be checked for compliance with the minimum speed limit.
     */
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

    /**
     * Computes and applies how much to reduce the speed of a collider based on the environmental friction.
     * @param collider  The Collider to perform friction calculations for.
     * @param millisSinceLastUpdate A long which is the milliseconds that have passed since the last update cycle.
     */
    private void handleFriction(Collider collider, long millisSinceLastUpdate)
    {
        if (collider.isIgnoringFriction())  //Ignore this collider if it ignores friction.
        {
            return;
        }

        //Apply an amount of friction so that objects come to a stop.
        collider.setYSpeed((float) (collider.getYSpeed() * (Math.pow(this.frictionChangePerMilli, millisSinceLastUpdate))));
        collider.setXSpeed((float) (collider.getXSpeed() * (Math.pow(this.frictionChangePerMilli, millisSinceLastUpdate))));
    }

    /**
     * Computes and applies how much to change the speeds of a collider based on the force of gravity.
     * @param collider  The collider to perform gravity calculations for.
     * @param xAxisGravityAcceleration  The horizontal speed increase in pixels per millisecond to apply to the collider.
     * @param yAxisGravityAcceleration  The vertical speed increase in pixels per millisecond to apply to the collider.
     */
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

    /**
     * Setter for the gravity on the spaceship.
     * @param xAxisGravityPerMilli  The new horizontal acceleration due to gravity.
     * @param yAxisGravityPerMilli  The new vertical acceleration due to gravity.
     */
    public void setGravity(float xAxisGravityPerMilli, float yAxisGravityPerMilli)
    {
        this.xAxisGravityPerMilli = xAxisGravityPerMilli;
        this.yAxisGravityPerMilli = yAxisGravityPerMilli;
    }

    /**
     * Performs all calculations related to tile map collisions for the given collider.
     * @param collider  The Collider object to process tilemap collisions for.
     */
    public void checkForAndHandleTilemapCollisions(Collider collider)
    {
        //Check if there is a collision with the tile map.
        boolean collidingTopLeft = isColliderTopLeftCornerCollidingWithTileMap(collider);
        boolean collidingTopRight = isColliderTopRightCornerCollidingWithTileMap(collider);
        boolean collidingBottomLeft = isColliderBottomLeftCornerCollidingWithTileMap(collider);
        boolean collidingBottomRight = isColliderBottomRightCornerCollidingWithTileMap(collider);

        //If there is a collision, handle it, otherwise return.
        if (collidingTopLeft || collidingTopRight || collidingBottomLeft || collidingBottomRight)
        {
            handleTileMapCollisionForCollider(collider, collidingTopLeft, collidingTopRight, collidingBottomLeft, collidingBottomRight);
        }

        return;
    }

    /**
     * Works out if this corner is in collision with the tilemap.
     * @param collider  A Collider object which may be in collision with the tilemap.
     * @return  A boolean which describes whether or not the collider is in collision with the tilemap at this corner.
     */
    private boolean isColliderTopLeftCornerCollidingWithTileMap(Collider collider)
    {
        return TilemapHelper.isThisPointOnAHullTile(collider.getXCoord(), collider.getYCoord(), this.tileMap);
    }

    /**
     * Works out if this corner is in collision with the tilemap.
     * @param collider  A Collider object which may be in collision with the tilemap.
     * @return  A boolean which describes whether or not the collider is in collision with the tilemap at this corner.
     */
    private boolean isColliderTopRightCornerCollidingWithTileMap(Collider collider)
    {
        return TilemapHelper.isThisPointOnAHullTile(collider.getXCoord() + collider.getWidth(), collider.getYCoord(), this.tileMap);
    }

    /**
     * Works out if this corner is in collision with the tilemap.
     * @param collider  A Collider object which may be in collision with the tilemap.
     * @return  A boolean which describes whether or not the collider is in collision with the tilemap at this corner.
     */
    private boolean isColliderBottomLeftCornerCollidingWithTileMap(Collider collider)
    {
        return TilemapHelper.isThisPointOnAHullTile(collider.getXCoord(), collider.getYCoord() + collider.getHeight(), this.tileMap);
    }

    /**
     * Works out if this corner is in collision with the tilemap.
     * @param collider  A Collider object which may be in collision with the tilemap.
     * @return  A boolean which describes whether or not the collider is in collision with the tilemap at this corner.
     */
    private boolean isColliderBottomRightCornerCollidingWithTileMap(Collider collider)
    {
        return TilemapHelper.isThisPointOnAHullTile(collider.getXCoord() + collider.getWidth(), collider.getYCoord() + collider.getHeight(), this.tileMap);
    }

    /**
     * Counts the number of corners which have a tilemap collision.
     * @param collidingTopLeft  A boolean describing whether or not this corner of a collider is overlapping a collide-able map tile.
     * @param collidingTopRight A boolean describing whether or not this corner of a collider is overlapping a collide-able map tile.
     * @param collidingBottomLeft   A boolean describing whether or not this corner of a collider is overlapping a collide-able map tile.
     * @param collidingBottomRight  A boolean describing whether or not this corner of a collider is overlapping a collide-able map tile.
     * @return  An int which is the number of corners of a collider which are colliding with the tilemap.
     */
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

    /**
     * Handles tilemap collisions for a collider when two corners are colliding with the tilemap.
     * @param collider  A Collider object which is in collision with the tilemap.
     * @param collidingTopLeft  A boolean describing whether or not this corner of a collider is overlapping a collide-able map tile.
     * @param collidingTopRight A boolean describing whether or not this corner of a collider is overlapping a collide-able map tile.
     * @param collidingBottomLeft   A boolean describing whether or not this corner of a collider is overlapping a collide-able map tile.
     * @param collidingBottomRight  A boolean describing whether or not this corner of a collider is overlapping a collide-able map tile.
     */
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

    /**
     * Handles tilemap collisions for a collider when one corner is colliding with the tilemap.
     * @param collider  A Collider object which is in collision with the tilemap.
     * @param collidingTopLeft  A boolean describing whether or not this corner of a collider is overlapping a collide-able map tile.
     * @param collidingTopRight A boolean describing whether or not this corner of a collider is overlapping a collide-able map tile.
     * @param collidingBottomLeft   A boolean describing whether or not this corner of a collider is overlapping a collide-able map tile.
     * @param collidingBottomRight  A boolean describing whether or not this corner of a collider is overlapping a collide-able map tile.
     */
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

    /**
     * Computes and applies the tile map collision for this update cycle for a collider which has collided with the tilemap.
     * @param collider  A Collider object which is in collision with the tilemap.
     * @param collidingTopLeft  A boolean describing whether or not this corner of a collider is overlapping a collide-able map tile.
     * @param collidingTopRight A boolean describing whether or not this corner of a collider is overlapping a collide-able map tile.
     * @param collidingBottomLeft   A boolean describing whether or not this corner of a collider is overlapping a collide-able map tile.
     * @param collidingBottomRight  A boolean describing whether or not this corner of a collider is overlapping a collide-able map tile.
     */
    private void handleTileMapCollisionForCollider(Collider collider,
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

    /**
     * Moves the collider to escape a tilemap collision, and also sets it's velocity to head away from the tile.
     * @param collider  A Collider which is in collision with the tilemap.
     * @param direction An enum describing the direction to move the collider to get out of the collision.
     */
    private void moveColliderToGetAwayFromTile(Collider collider, EDirection direction)
    {
        float nudgeFactorX = this.millisSinceLastUpdate * this.yAxisGravityPerMilli * 1.5f;    //use this to make sure that colliders dropping into the floor actually get moved back out.
        float nudgeFactorY = this.millisSinceLastUpdate * this.yAxisGravityPerMilli * 1.5f;    //use this to make sure that colliders dropping into the floor actually get moved back out.

        bounceColliderOffTile(collider, direction); //Reset the collider velocity to make it go away from the tile collision.

        if (direction == EDirection.up)
        {
            collider.setYCoord(collider.getYCoord() - getYAxisGridOffset(collider) + heightDiffBetweenColliderSizeAndTileSize(collider) - nudgeFactorY);  //Move the collider up out of the collision.
            //handleMinimumSpeed(collider);
        }
        else if (direction == EDirection.down)
        {
            collider.setYCoord(collider.getYCoord() + (this.tileMap.getTileHeight() - getYAxisGridOffset(collider)) + nudgeFactorY);  //Move the collider down out of the collision.
            //handleMinimumSpeed(collider);
        }
        else if (direction == EDirection.left)
        {
            collider.setXCoord(collider.getXCoord() - getXAxisGridOffset(collider) + widthDiffBetweenColliderSizeAndTileSize(collider) - nudgeFactorX);  //Move the collider left out of the collision.
            //handleMinimumSpeed(collider);
        }
        else if (direction == EDirection.right)
        {
            collider.setXCoord(collider.getXCoord() + (this.tileMap.getTileWidth() - getXAxisGridOffset(collider)) + nudgeFactorX);  //Move the collider right out of the collision.
            //handleMinimumSpeed(collider);
        }

        collider.bouncedOffTile();
    }

    /**
     * Computes and assigns a new speed to the collider based on the direction is is currently travelling in, and the direction it is supposed to be going in.
     * @param collider  A Collider which is in collision with the tilemap.
     * @param directionToBounceIn An enum describing the direction to move the collider to get out of the collision.
     */
    private void bounceColliderOffTile(Collider collider, EDirection directionToBounceIn)
    {
        switch (directionToBounceIn)
        {
            case up:    //If the collider is to go up
            {
                if (collider.getYSpeed() > 0)    //And it's speed is downwards
                {
                    collider.setYSpeed(collider.getYSpeed() * this.speedLossDueToTileMapCollision * (-1));  //reverse and reduce speed.
                }
            }

            case down:    //If the collider is to go down
            {
                if (collider.getYSpeed() < 0)   //And if it's speed is upwards
                {
                    collider.setYSpeed(collider.getYSpeed() * this.speedLossDueToTileMapCollision * (-1));  //reverse and reduce speed.
                }
            }

            case left:    //If the collider is to go left
            {
                if (collider.getXSpeed() > 0) //And if it's speed is rightwards
                {
                    collider.setXSpeed(collider.getXSpeed() * this.speedLossDueToTileMapCollision * (-1));  //reverse and reduce speed.
                }
            }

            case right:    //If the collider is to go right
            {
                if (collider.getXSpeed() < 0)   //And if it's speed is leftwards
                {
                    collider.setXSpeed(collider.getXSpeed() * this.speedLossDueToTileMapCollision * (-1));  //reverse and reduce speed.
                }
            }
        }
    }

    /**
     * Computes the difference between the width of a collider and the tile width of the tile map.
     * @param collider  A Collider object.
     * @return  A float which is the difference between the width of the collider and the width of the map tiles in pixels.
     */
    private float widthDiffBetweenColliderSizeAndTileSize(Collider collider)
    {
        float difference = this.tileMap.getTileWidth() - collider.getWidth();

        return difference;
    }

    /**
     * Computes the difference between the height of a collider and the tile height of the tile map.
     * @param collider  A Collider object.
     * @return  A float which is the difference between the height of the collider and the height of the map tiles in pixels.
     */
    private float heightDiffBetweenColliderSizeAndTileSize(Collider collider)
    {
        float difference = this.tileMap.getTileHeight() - collider.getHeight();

        return difference;
    }

    /**
     * Computes how far from being aligned (with the top edge of the tile it is on) this collider is.
     * @param collider  A Collider object, for which the vertical alignment is required.
     * @return  A float which is the vertical distance to the top of the current map tile.
     */
    private float getYAxisGridOffset(Collider collider)
    {
        return collider.getYCoord() % this.tileMap.getTileHeight();
    }

    /**
     * Computes how far from being aligned (with the left edge of the tile it is on) this collider is.
     * @param collider  A Collider object, for which the horizontal alignment is required.
     * @return  A float which is the horizontal distance to the top of the current map tile.
     */
    private float getXAxisGridOffset(Collider collider)
    {
        return collider.getXCoord() % this.tileMap.getTileWidth();
    }

    /**
     * An enum which describes the four directions a collider can move in.
     */
    public enum EDirection
    {
        up,
        down,
        left,
        right
    }
}