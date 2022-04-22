package renderableObjects;

import helperClasses.EntityUpdate;

import java.awt.*;

/**
 * An interface which allows all the different render-able game objects to be put in a list of things to draw and iterated over.
 */
public interface IDrawable
{
	/**
	 * Calls the draw function of this object's sprite, and applies any horizontal or vertical offset.
	 * @param graphics2D	The Graphics2D object to draw this object on.
	 * @param xOffset	A float which is the horizontal offset in pixels to draw this object at.
	 * @param yOffset	A float which is the vertical offset in pixels to draw this object at.
	 */
	void draw(Graphics2D graphics2D, float xOffset, float yOffset);

	/**
	 * Updates this object for this update cycle, and performs any state changes that this object may need to undergo.
	 * @param entityUpdate	An EntityUpdate object containing the update information for this update cycle.
	 */
	void update(EntityUpdate entityUpdate);

	/**
	 * Set the horizontal speed of this object's sprite.
	 * @param xSpeed	A float which is the new horizontal speed in pixels per millisecond.
	 */
	void setXSpeed(float xSpeed);

	/**
	 * Set the vertical speed of this object's sprite.
	 * @param ySpeed	A float which is the new vertical speed in pixels per millisecond.
	 */
	void setYSpeed(float ySpeed);

	/**
	 * A getter for the self-destruct flag.  Allows this object to be removed when the player can no longer see it.
	 * @return	A boolean describing whether this object can be removed when out of sight.
	 */
	boolean getSelfDestructWhenOffScreen();

	/**
	 * A setter for the self-destruct flag.  Allows this object to be removed when the player can no longer see it.
	 */
	void setSelfDestructWhenOffScreen();
}