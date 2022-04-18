package renderableObjects;

import CSCU9N6Library.*;
import helperClasses.EntityUpdate;

import java.awt.*;

public class BackgroundEntity implements IDrawable
{

	private Sprite sprite;
	private float parallax = 1.0f;
	private boolean selfDestructStatus = false;

	private int maxXCoord;
	private int minXCoord = 0;
	private int maxYCoord;
	private int minYCoord = 0;

	public BackgroundEntity(int screenWidth, int screenHeight)
	{
		this.maxXCoord = screenWidth;
		this.maxYCoord = screenHeight;
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

	public void setSelfDestructBoundaries(int maxXCoord, int minXCoord, int maxYCoord, int minYCoord)
	{
		this.maxXCoord = maxXCoord;
		this.minXCoord = minXCoord;
		this.maxYCoord = maxYCoord;
		this.minYCoord = minYCoord;
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

		updateSelfDestructStatus();
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

	public boolean getSelfDestructStatus()
	{
		return this.selfDestructStatus;
	}

	private void updateSelfDestructStatus()
	{
		if (this.sprite.getY() < this.minYCoord
				|| this.sprite.getY() > this.maxYCoord
				|| this.sprite.getX() < this.minXCoord
				|| this.sprite.getX() > this.maxXCoord )
		{
			this.selfDestructStatus = true;
		}
	}
}