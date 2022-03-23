import game2D.*;

public interface IPhysicsEntity {

	Sprite getSprite();

	/**
	 * 
	 * @param sprite
	 */
	void setSprite(Sprite sprite);

	PhysicsCollider getPhysicsCollider();

	/**
	 * 
	 * @param collider
	 */
	void setPhysicsCollider(PhysicsCollider collider);

	/**
	 * 
	 * @param millisSinceLastUpdate
	 */
	void update(EntityUpdate entityUpdate);

}