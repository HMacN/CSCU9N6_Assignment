package renderableObjects;

import CSCU9N6Library.Sprite;
import helperClasses.EntityUpdate;

import java.awt.*;

public interface IDrawable
{
	void draw(Graphics2D graphics2D, float xOffset, float yOffset);

	void setSelfDestructBoundaries(int maxXCoord, int minXCoord, int maxYCoord, int minYCoord);

	void update(EntityUpdate entityUpdate);

	float getXSpeed();

	void setXSpeed(float xSpeed);

	float getYSpeed();

	void setYSpeed(float ySpeed);

	void setXCoord(float xCoord);

	void setYCoord(float yCoord);

	boolean getSelfDestructStatus();
}