//  Assignment submission for CSCU9N6
//  Student Number: 2823735/1
//  Date of Submission: 22/04/2022

package physics;

/**
 * A physics object which can collide with map tiles and other colliders.  Acts as a data storage object, with the actual physics calculations being done in the PhysicsEngine.
 */
public class Collider
{
    private float xCoord;
    private float yCoord;
    private float xSpeed = 0.0f;
    private float ySpeed = 0.0f;
    private float xControlSpeed = 0.0f;
    private float yControlSpeed = 0.0f;

    private float width;
    private float height;
    private float halfWidth;
    private float halfHeight;
    private float inverseMass;

    private boolean selfDestruct = false;
    private boolean ignoringGravity = false;
    private boolean ignoringFriction = false;
    private boolean collisionsAlreadyHandled = false;

    private IHasCollider parent;    //The "owning" object, which will be notified of collisions.

    /**
     * The constructor.
     * @param xCoord    A float describing the starting x-coordinate of the collider.
     * @param yCoord    A float describing the starting y-coordinate of the collider.
     * @param width    A float describing the starting width of the collider.
     * @param height    A float describing the starting height of the collider.
     * @param mass    A float describing the starting mass of the collider (should not be zero, as it's inverse is calculated).
     * @param parent    An IHasCollider object which will "own" this collider, and be notified about any collisions.
     */
    public Collider(float xCoord, float yCoord, float width, float height, float mass, IHasCollider parent)
    {
        this.parent = parent;

        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.width = width;
        this.height = height;

        this.inverseMass = 1 / mass;    //Inverse mass is used in the physics calculations, so work that out now.

        calculateHalfSizes();   //Half sizes are used in the physics calculations, so work those out now.
    }

    /**
     * A setter for the horizontal position of the collider.
     * @param xCoord    A float describing the new horizontal position.
     */
    public void setXCoord(float xCoord)
    {
        this.xCoord = xCoord;
    }

    /**
     * A setter for the vertical position of the collider.
     * @param yCoord    A float describing the new vertical position.
     */
    public void setYCoord(float yCoord)
    {
        this.yCoord = yCoord;
    }

    /**
     * A setter for the horizontal speed of the collider.
     * @param xSpeed    A float describing the new horizontal speed.
     */
    public void setXSpeed(float xSpeed)
    {
        this.xSpeed = xSpeed;
    }

    /**
     * A setter for the vertical speed of the collider.
     * @param ySpeed    A float describing the new vertical speed.
     */
    public void setYSpeed(float ySpeed)
    {
        this.ySpeed = ySpeed;
    }

    /**
     * A getter for the horizontal position of the collider.
     * @return  A float which is the horizontal position of the collider.
     */
    public float getXCoord()
    {
        return xCoord;
    }

    /**
     * A getter for the vertical position of the collider.
     * @return  A float which is the vertical position of the collider.
     */
    public float getYCoord()
    {
        return yCoord;
    }

    /**
     * A getter for the horizontal speed of the collider.
     * @return  A float which is the horizontal speed of the collider.
     */
    public float getXSpeed()
    {
        return xSpeed;
    }

    /**
     * A getter for the vertical speed of the collider.
     * @return  A float which is the vertical speed of the collider.
     */
    public float getYSpeed()
    {
        return ySpeed;
    }

    /**
     * A getter for the width of the collider.
     * @return  A float which is the width of the collider.
     */
    public float getWidth()
    {
        return width;
    }

    /**
     * A getter for the height of the collider.
     * @return  A float which is the height of the collider.
     */
    public float getHeight()
    {
        return height;
    }

    /**
     * A getter for the self-destruct flag of this collider.
     * @return  A boolean which describes whether of not this collider should be destroyed.
     */
    public boolean getSelfDestructStatus()
    {
        return selfDestruct;
    }

    /**
     * A setter for the self-destruct flag.
     */
    public void setToSelfDestruct()
    {
        this.selfDestruct = true;
    }

    /**
     * A getter for the ignoring gravity flag.
     * @return  A boolean which describes if this collider is ignoring gravity.
     */
    public boolean isIgnoringGravity()
    {
        return ignoringGravity;
    }

    /**
     * A setter for whether or not this collider should ignore gravity.
     * @param ignoringGravity   A boolean which describes whether or not this collider should ignore gravity.
     */
    public void setIgnoringGravity(boolean ignoringGravity)
    {
        this.ignoringGravity = ignoringGravity;
    }

    /**
     * Gets the midpoint of the collider in the vertical axis.
     * @return  A float which is the vertical mid-point of this collider.
     */
    public float getYAxisCentroid()
    {
        return this.yCoord + this.halfHeight;
    }

    /**
     * Gets the midpoint of the collider in the horizontal axis.
     * @return  A float which is the horizontal mid-point of this collider.
     */
    public float getXAxisCentroid()
    {
        return this.xCoord + this.halfWidth;
    }

    /**
     * Getter for the half-width of the collider.  Used in physics calculations.
     * @return  A float which is the half-width of the collider.
     */
    public float getHalfWidth()
    {
        return this.halfWidth;
    }

    /**
     * Getter for the half-height of the collider.  Used in physics calculations.
     * @return  A float which is the half-height of the collider.
     */
    public float getHalfHeight()
    {
        return this.halfHeight;
    }

    /**
     * Computes the half-dimensions of the collider in advance, so that it doesn't need to happen during the physics calculations.
     */
    private void calculateHalfSizes()
    {
        this.halfWidth = this.width / 2.0f;
        this.halfHeight = this.height / 2.0f;
    }

    /**
     * A getter for whether or not this collider has already had all possible collision events handled this update cycle.
     * @return  A boolean which describes whether or not all collisions for this collider have already been handled.
     */
    public boolean collisionsAlreadyHandled()
    {
        return collisionsAlreadyHandled;
    }

    /**
     * A setter for whether or not this collider has already had all possible collision events handled this update cycle.
     * @param collisionsHandled     A boolean which describes whether or not all collisions for this collider have already been handled.
     */
    public void setCollisionsHandled(boolean collisionsHandled)
    {
        this.collisionsAlreadyHandled = collisionsHandled;
    }

    /**
     * A getter for the inverse mass of this collider.  Used in physics calculations.
     * @return  A float which is the inverse mass of this collider.
     */
    public float getInverseMass()
    {
        return inverseMass;
    }

    /**
     * A getter for the control speed of this collider.  Used to work out the response to player or AI input.
     * @return  A float which is the horizontal control speed of the collider.
     */
    public float getXControlSpeed()
    {
        return xControlSpeed;
    }

    /**
     * A setter for the control speed of this collider.  Used to work out the response to player or AI input.
     * @param xControlSpeed   A float which is the horizontal control speed of the collider.
     */
    public void setXControlSpeed(float xControlSpeed)
    {
        this.xControlSpeed = xControlSpeed;
    }

    /**
     * A getter for the control speed of this collider.  Used to work out the response to player or AI input.
     * @return  A float which is the vertical control speed of the collider.
     */
    public float getYControlSpeed()
    {
        return yControlSpeed;
    }

    /**
     * A setter for the control speed of this collider.  Used to work out the response to player or AI input.
     * @param yControlSpeed   A float which is the vertical control speed of the collider.
     */
    public void setYControlSpeed(float yControlSpeed)
    {
        this.yControlSpeed = yControlSpeed;
    }

    /**
     * A getter for the Ignoring Friction flag for this collider.
     * @return A boolean which describes whether or not the collider is ignoring friction.
     */
    public boolean isIgnoringFriction()
    {
        return this.ignoringFriction;
    }

    /**
     * A setter for the Ignoring Friction flag for this collider.
     * @param ignoringFriction  A boolean which describes whether or not the collider is ignoring friction.
     */
    public void setIgnoringFriction(boolean ignoringFriction)
    {
        this.ignoringFriction = ignoringFriction;
    }

    /**
     * Notifies the parent object of this collider that the collider has hit a map tile.
     */
    public void bouncedOffTile()
    {
        this.parent.collidedWithTile();
    }

    /**
     * Getter for the parent object of this collider.
     * @return  An IHasCollider object which is the parent of this collider.
     */
    public IHasCollider getParent()
    {
        return this.parent;
    }

    /**
     * Notifies the parent of this collider that the collider has hit another collider, and passes it the parent object of the other collider.
     * @param thingThatThisColliderHasHit   An IHasCollider object which is the parent object of the other collider in the collision.
     */
    public void hasCollidedWith(IHasCollider thingThatThisColliderHasHit)
    {
        this.parent.hasCollidedWith(thingThatThisColliderHasHit);
    }
}
