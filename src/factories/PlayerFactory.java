package factories;

import spaceShipGame.GameObjects;
import renderableObjects.Player;
import spaceShipGame.SpaceshipGame;

import static spaceShipGame.GameObjects.ERenderLayer.spaceShipLayer;

public class PlayerFactory implements IGameObjectFactory
{
    private Player player;
    private SpaceshipGame spaceshipGame;
    private GameObjects gameObjects;

    public PlayerFactory(SpaceshipGame spaceshipGame, GameObjects gameObjects)
    {
        this.spaceshipGame = spaceshipGame;
        this.gameObjects = gameObjects;
    }

    @Override
    public void spawnNewAt(float xCoord, float yCoord)
    {
        if (this.player == null)    //If there isn't already a player.
        {
            this.player = new Player(
                    this.spaceshipGame.getScreenWidth(),
                    this.spaceshipGame.getScreenHeight(),
                    this.spaceshipGame.getEntityUpdateFactory(),
                    this.spaceshipGame.getUserInputHandler(),
                    this.gameObjects.getTileMap(),
                    xCoord,
                    yCoord);

            this.gameObjects.addDrawable(this.player, spaceShipLayer);
            this.gameObjects.addPhysicsEntity(this.player.getCollider());
        }
    }

    public Player getPlayer()
    {
        return this.player;
    }

    public void deletePlayer()
    {
        if (this.player != null)    //Avoid null pointer exceptions!
        {
            this.player.selfDestructWhenOffScreen();    //Set the object to disappear when off screen.
            this.player = null; //Delete the player reference.
        }
    }
}
