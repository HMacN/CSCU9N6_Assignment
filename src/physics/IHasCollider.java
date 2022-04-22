package physics;

/**
 * An interface to allow colliders to exchange the identities of their parents when they collide.  Useful for working out what you've hit.
 */
public interface IHasCollider
{
    /**
     * Notifies this object that it's collider has hit the collider of another object.
     * @param parentOfOtherCollider An IHasCollider object which is the parent object of the other collider in the collision.
     */
    void hasCollidedWith(IHasCollider parentOfOtherCollider);

    /**
     * Notifies this object that it's collider has hit a terrain tile.
     */
    void collidedWithTile();
}
