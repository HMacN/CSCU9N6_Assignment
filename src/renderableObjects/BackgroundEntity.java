package renderableObjects;

import CSCU9N6Library.*;
import helperClasses.EntityUpdate;

import java.awt.*;

public class BackgroundEntity implements IDrawable
{

	private Sprite sprite;
	private float parallax = 1.0f;
	private boolean selfDestructWhenOffScreen = false;

	private float screenWidth;
	private int screenHeight;

	public BackgroundEntity(int screenWidth, int screenHeight)
	{
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}

	public void draw(Graphics2D graphics2D, float xOffset, float yOffset)
	{
		this.sprite.setX(this.sprite.getX() + xOffset);
		this.sprite.setY(this.sprite.getY() + yOffset);
		this.sprite.draw(graphics2D);
		this.sprite.setX(this.sprite.getX() - xOffset);
		this.sprite.setY(this.sprite.getY() - yOffset);
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
		if (!this.selfDestructWhenOffScreen)
		{
			return false;   //Don't self destruct without being told to do so.
		}

		if (this.sprite.getX() > this.screenWidth || this.sprite.getX() < -this.sprite.getWidth())   //If the sprite has gone off the sides of the screen.
		{
			return true;
		}

		if (this.sprite.getY() > this.screenHeight || this.sprite.getY() < -this.sprite.getHeight()) //If the sprite has gone off the top or bottom of the screen.
		{
			return true;
		}

		//If the sprite is due for destruction, but is still in view:
		return false;
	}

	public void setSelfDestructWhenOffScreen()
	{
		this.selfDestructWhenOffScreen = true;
	}
}