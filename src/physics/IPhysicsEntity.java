package physics;

import CSCU9N6Library.*;
import helperClasses.EntityUpdate;

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

	void update(EntityUpdate entityUpdate);

    boolean getSelfDestructStatus();
}