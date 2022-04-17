package renderableObjects;

import CSCU9N6Library.Sprite;
import helperClasses.EntityUpdate;

import java.awt.*;

public interface IDrawable
{
	void draw(Graphics2D graphics2D);

	void setSelfDestructBoundaries(int maxXCoord, int minXCoord, int maxYCoord, int minYCoord);

	void update(EntityUpdate entityUpdate);

	void setParallax(float parallax);

	double getXSpeed();

	void setXSpeed(float xSpeed);

	double getYSpeed();

	void setYSpeed(float ySpeed);

	void setXCoord(float xCoord);

	void setYCoord(float yCoord);

	boolean getSelfDestructStatus();
}