//  Assignment submission for CSCU9N6
//  Student Number: 2823735/1
//  Date of Submission: 22/04/2022

package renderableObjects;

import CSCU9N6Library.*;
import helperClasses.EntityUpdate;

import java.awt.*;

/**
 * A class which is displayed in the background of the game to provide depth and context.
 * Basically just a wrapper for the Sprite class.
 */
public class BackgroundEntity implements IDrawable
{
	private final float screenWidth;
	private final float screenHeight;
	private Sprite sprite;
	private float parallax = 1.0f;

	/**
	 * The constructor.
	 * @param screenWidth	A float which is the width of the display area in pixels.
	 * @param screenHeight	A float which is the height of the display area in pixels.
	 */
	public BackgroundEntity(float screenWidth, float screenHeight)
	{
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}

	/**
	 * Calls the draw function of this object's sprite.
	 * Ignores horizontal or vertical offset, as it parallaxes past the spaceship, not the player.
	 * @param graphics2D	The Graphics2D object to draw this object on.
	 * @param xOffset	A float which is the horizontal offset in pixels to draw this object at.
	 * @param yOffset	A float which is the vertical offset in pixels to draw this object at.
	 */
	@Override
	public void draw(Graphics2D graphics2D, float xOffset, float yOffset)
	{
		this.sprite.draw(graphics2D);
	}

	/**
	 * Sets the sprite of this object.
	 * @param sprite	A Sprite which is the new sprite for this object.
	 */
	public void setSprite(Sprite sprite)
	{
		this.sprite = sprite;
	}

	/**
	 * Works out how much this sprite should move based on the spaceship speed and the parallax setting for this object.
	 * @param entityUpdate	An EntityUpdate object containing the update information for this update cycle.
	 */
	@Override
	public void update(EntityUpdate entityUpdate)
	{
		this.setXSpeed(-entityUpdate.getSpaceshipXSpeed() * this.parallax);
		this.setYSpeed(-entityUpdate.getSpaceshipYSpeed() * this.parallax);

		this.sprite.update(entityUpdate.getMillisSinceLastUpdate());
	}

	/**
	 * Sets the parallax factor for this object.  This controls how quickly it passes by behind the spaceship,
	 * relative to the spaceship speed.
	 * @param parallax	A float which is the parallax factor.
	 */
	public void setParallax(float parallax)
	{
		this.parallax = parallax;
	}

	/**
	 * A getter for the horizontal speed of the sprite.
	 * @return	A float which is the horizontal speed of the sprite.
	 */
	public float getXSpeed()
	{
		return this.sprite.getVelocityX();
	}

	/**
	 * Set the horizontal speed of this object's sprite.
	 * @param xSpeed	A float which is the new horizontal speed in pixels per millisecond.
	 */
	@Override
	public void setXSpeed(float xSpeed)
	{
		this.sprite.setVelocityX(xSpeed);
	}

	/**
	 * A getter for the vertical speed of the sprite.
	 * @return	A float which is the vertical speed of the sprite.
	 */
	public float getYSpeed()
	{
		return this.sprite.getVelocityY();
	}

	/**
	 * Set the vertical speed of this object's sprite.
	 * @param ySpeed	A float which is the new vertical speed in pixels per millisecond.
	 */
	@Override
	public void setYSpeed(float ySpeed)
	{
		this.sprite.setVelocityY(ySpeed);
	}

	/**
	 * Set the horizontal location of this object's sprite.
	 * @param xCoord	A float which is the new horizontal location in pixels.
	 */
	public void setXCoord(float xCoord)
	{
		this.sprite.setX(xCoord);
	}

	/**
	 * Set the vertical location of this object's sprite.
	 * @param yCoord	A float which is the new vertical location in pixels.
	 */
	public void setYCoord(float yCoord)
	{
		this.sprite.setY(yCoord);
	}

	/**
	 * A getter for the self-destruct flag.  Allows this object to be removed when the player can no longer see it.
	 * @return	A boolean describing whether this object can be removed when out of sight.
	 */
	@Override
	public boolean getSelfDestructWhenOffScreen()
	{
		//Note that the negative of the screen width is used here instead of the negative of the sprite width due to sprite.getWidth() not working.

		if (this.sprite.getX() < this.screenWidth && this.sprite.getX() > (-1 * this.screenWidth))   //If the sprite is still likely to get rendered.
		{
			if (this.sprite.getY() < this.screenHeight && this.sprite.getY() > (-1 * this.screenHeight)) //If the sprite is still likely to get rendered.
			{
				return false;
			}
		}

		//If the sprite has gone off the screen:
		return true;
	}

	/**
	 * A setter for the self-destruct flag.  Allows this object to be removed when the player can no longer see it.
	 */
	@Override
	public void setSelfDestructWhenOffScreen()
	{
		//Do nothing - background entities should always disappear when off screen.
	}
}