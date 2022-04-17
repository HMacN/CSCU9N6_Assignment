package renderableObjects;

import CSCU9N6Library.Sprite;
import helperClasses.EntityUpdate;
import physics.IPhysicsEntity;
import physics.PhysicsCollider;

public class GameConsole implements IPhysicsEntity
{

    @Override
    public Sprite getSprite()
    {
        return null;
    }

    @Override
    public void setSprite(Sprite sprite)
    {

    }

    @Override
    public PhysicsCollider getPhysicsCollider()
    {
        return null;
    }

    @Override
    public void setPhysicsCollider(PhysicsCollider collider)
    {

    }

    @Override
    public void update(EntityUpdate entityUpdate)
    {

    }

    @Override
    public boolean getSelfDestructStatus()
    {
        return false;
    }
}