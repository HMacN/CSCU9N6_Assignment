package physics;

import CSCU9N6Library.*;
import helperClasses.EntityUpdate;

public interface IPhysicsEntity
{
	void update(EntityUpdate entityUpdate);

    double getXSpeed();

    void setXSpeed(float xSpeed);

    double getYSpeed();

    void setYSpeed(float ySpeed);

    void setXCoord(float xCoord);

    float getXCoord();

    void setYCoord(float yCoord);

    float getYCoord();

    boolean getSelfDestructStatus();
}