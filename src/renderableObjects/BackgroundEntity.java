package renderableObjects;

import CSCU9N6Library.*;
import helperClasses.EntityUpdate;

import java.awt.*;

public class BackgroundEntity implements IDrawable
{

	private final float screenWidth;
	private final float screenHeight;
	private Sprite sprite;
	private float parallax = 1.0f;

	public BackgroundEntity(float screenWidth, float screenHeight)
	{
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}

	public void draw(Graphics2D graphics2D, float xOffset, float yOffset)
	{
		//this.sprite.setX(this.sprite.getX() + xOffset);
		//this.sprite.setY(this.sprite.getY() + yOffset);
		this.sprite.draw(graphics2D);
		//this.sprite.setX(this.sprite.getX() - xOffset);
		//this.sprite.setY(this.sprite.getY() - yOffset);
	}

	/**
	 * 
	 * @param sprite
	 */
	public void setSprite(Sprite sprite)
	{
		this.sprite = sprite;
	}

	/**
	 * 
	 * @param entityUpdate
	 */
	public void update(EntityUpdate entityUpdate)
	{
		this.setXSpeed(-entityUpdate.getSpaceshipXSpeed() * this.parallax);
		this.setYSpeed(-entityUpdate.getSpaceshipYSpeed() * this.parallax);

		this.sprite.update(entityUpdate.getMillisSinceLastUpdate());
	}

	/**
	 * 
	 * @param parallax
	 */
	public void setParallax(float parallax)
	{
		this.parallax = parallax;
	}

	public float getXSpeed()
	{
		return this.sprite.getVelocityX();
	}

	public void setXSpeed(float xSpeed)
	{
		this.sprite.setVelocityX(xSpeed);
	}

	public float getYSpeed()
	{
		return this.sprite.getVelocityY();
	}

	public void setYSpeed(float ySpeed)
	{
		this.sprite.setVelocityY(ySpeed);
	}

	public void setXCoord(float xCoord)
	{
		this.sprite.setX(xCoord);
	}

	public void setYCoord(float yCoord)
	{
		this.sprite.setY(yCoord);
	}

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

	public void setSelfDestructWhenOffScreen()
	{
		//Do nothing - background entities should always disappear when off screen.
	}
}