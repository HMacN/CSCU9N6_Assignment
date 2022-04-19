package factories;

import helperClasses.GameObjects;
import renderableObjects.CargoCrate;
import spaceShipGame.SpaceshipGame;

public class CargoCrateFactory implements IGameObjectFactory
{
    private SpaceshipGame spaceshipGame;
    private GameObjects gameObjects;

    public CargoCrateFactory(SpaceshipGame spaceshipGame, GameObjects gameObjects)
    {
        this.spaceshipGame = spaceshipGame;
        this.gameObjects = gameObjects;
    }

    @Override
    public void spawnNewAt(float xCoord, float yCoord)
    {
        CargoCrate crate = new CargoCrate(xCoord, yCoord, this.spaceshipGame.getScreenWidth(), this.spaceshipGame.getScreenHeight());
        this.gameObjects.addPhysicsEntity(crate.getCollider());
        this.gameObjects.addDrawable(crate, GameObjects.ERenderLayer.spaceShipLayer);
    }
}
