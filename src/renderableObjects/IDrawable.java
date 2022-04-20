package renderableObjects;

import CSCU9N6Library.Sprite;
import helperClasses.EntityUpdate;

import java.awt.*;

public interface IDrawable
{
	void draw(Graphics2D graphics2D, float xOffset, float yOffset);

	void update(EntityUpdate entityUpdate);

	void setXSpeed(float xSpeed);

	void setYSpeed(float ySpeed);

	boolean getSelfDestructWhenOffScreen();

	void setSelfDestructWhenOffScreen();
}