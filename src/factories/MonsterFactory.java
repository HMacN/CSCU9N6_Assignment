package factories;

import renderableObjects.AlienBugMonster;
import spaceShipGame.GameObjects;
import spaceShipGame.SpaceshipGame;

public class MonsterFactory implements IGameObjectFactory
{
    private SpaceshipGame spaceshipGame;
    private GameObjects gameObjects;

    public MonsterFactory(SpaceshipGame spaceshipGame, GameObjects gameObjects)
    {
        this.gameObjects = gameObjects;
        this.spaceshipGame = spaceshipGame;
    }

    @Override
    public void spawnNewAt(float xCoord, float yCoord)
    {
        AlienBugMonster alienBugMonster = new AlienBugMonster(this.spaceshipGame, xCoord, yCoord);
        this.gameObjects.addPhysicsEntity(alienBugMonster.getCollider());
        this.gameObjects.addDrawable(alienBugMonster, GameObjects.ERenderLayer.spaceShipLayer);
    }
}
