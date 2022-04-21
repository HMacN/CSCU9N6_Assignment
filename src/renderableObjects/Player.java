package renderableObjects;

import CSCU9N6Library.Sprite;
import CSCU9N6Library.TileMap;
import factories.BulletFactory;
import factories.EntityUpdateFactory;
import factories.SpriteFactory;
import gameStates.EGameState;
import gameStates.IGameState;
import helperClasses.*;
import levelEvents.PrepareToEndLevel;
import physics.Collider;
import physics.IHasCollider;
import soundsAndMusic.DistanceSound;
import spaceShipGame.SpaceshipGame;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Player implements IDrawable, KeyListener, IHasCollider
{
    private Sprite movingSprite = SpriteFactory.getSpriteFromPNGFile("playerAnim", 1, 4, 60);
    private Sprite stillSprite = SpriteFactory.getSpriteFromPNGFile("player", 1, 1, 10_000);
    private Sprite sprite = stillSprite;
    private Collider collider;

    private boolean selfDestructWhenOffScreen = false;

    private float startingX;
    private float startingY;
    private float screenHeight;
    private float screenWidth;
    private EPlayerState playerState = EPlayerState.standingStill;

    private EntityUpdateFactory updateFactory;
    private BulletFactory bulletFactory;
    private UserInputHandler inputHandler;
    private TileMap tileMap;
    private SpaceshipGame spaceshipGame;
    private IGameState level;

    private boolean dead = false;
    private boolean upKeyPressed = false;
    private boolean downKeyPressed = false;
    private boolean leftKeyPressed = false;
    private boolean rightKeyPressed = false;
    private float controlAuthority = 0.1f;

    public Player(SpaceshipGame spaceshipGame, float xCoord, float yCoord)
    {
        this.spaceshipGame = spaceshipGame;
        this.screenHeight = spaceshipGame.getScreenHeight();
        this.screenWidth = spaceshipGame.getScreenWidth();
        this.startingX = screenWidth / 2.0f;
        this.startingY = screenHeight / 2.0f;
        this.level = this.spaceshipGame.getGameState();

        this.movingSprite.setX(this.startingX);
        this.movingSprite.setY(this.startingY);
        this.movingSprite.setScale(0.70f);

        this.stillSprite.setX(this.startingX);
        this.stillSprite.setY(this.startingY);
        this.stillSprite.setScale(0.70f);

        this.collider = new Collider(xCoord, yCoord, 31.0f, 31.0f, 1.0f, this);

        this.updateFactory = spaceshipGame.getEntityUpdateFactory();
        this.inputHandler = spaceshipGame.getUserInputHandler();
        this.inputHandler.addKeyListener(this);
        this.tileMap = spaceshipGame.getGameObjects().getTileMap();
        this.bulletFactory = new BulletFactory(spaceshipGame, spaceshipGame.getGameObjects());
    }

    public Collider getCollider()
    {
        return collider;
    }

    private void die()
    {
        this.dead = true;
        this.sprite.hide();
        this.collider.setToSelfDestruct();
        this.spaceshipGame.getGameObjects().addSound(new DistanceSound("sounds/splat.wav", this.spaceshipGame, this.collider));
        this.level.addLevelEvent(new PrepareToEndLevel(this.spaceshipGame, EGameState.mainMenu), 0);
    }

    @Override
    public void draw(Graphics2D graphics2D, float xOffset, float yOffset)
    {
        //Draw the sprite.
        this.sprite.drawTransformed(graphics2D);
    }

    @Override
    public void update(EntityUpdate entityUpdate)
    {
        this.sprite.update(entityUpdate.getMillisSinceLastUpdate());

        //Only change the offset when not self destructing, so that this sprite will leave the screen if it is due to destruct.
        if (!this.selfDestructWhenOffScreen)
        {
            this.updateFactory.setPlayerXOffset(this.startingX - this.collider.getXCoord());
            this.updateFactory.setPlayerYOffset(this.startingY - this.collider.getYCoord());
        }

        workOutIfOnLadderAndSetColliderToIgnoreGravityIfSo();

        setSpriteAndControlSpeedForPlayerState();
    }

    private void setSpriteAndControlSpeedForPlayerState()
    {
        switch (this.playerState)
        {
            case standingStill:
            {
                this.sprite = this.stillSprite;
                this.collider.setXControlSpeed(0.0f);
                this.collider.setYControlSpeed(0.0f);
                break;
            }
            case facingLeft:
            {
                this.sprite = this.movingSprite;
                this.sprite.pauseAnimationAtFrame(1);
                this.sprite.setScale(-0.7f, 0.7f);
                this.collider.setXControlSpeed(0.0f);
                this.collider.setYControlSpeed(0.0f);
                break;
            }
            case facingRight:
            {
                this.sprite = this.movingSprite;
                this.sprite.pauseAnimationAtFrame(1);
                this.sprite.setScale(0.7f, 0.7f);
                this.collider.setXControlSpeed(0.0f);
                this.collider.setYControlSpeed(0.0f);
                break;
            }
            case goingLeft:
            {
                this.sprite = this.movingSprite;
                this.sprite.playAnimation();
                this.sprite.setScale(-0.7f, 0.7f);
                this.collider.setXControlSpeed(-1 * this.controlAuthority);
                this.collider.setYControlSpeed(0.0f);
                break;
            }
            case goingRight:
            {
                this.sprite = this.movingSprite;
                this.sprite.playAnimation();
                this.sprite.setScale(0.7f, 0.7f);
                this.collider.setXControlSpeed(this.controlAuthority);
                this.collider.setYControlSpeed(0.0f);
                break;
            }
            case goingUp:
            {
                this.sprite = this.stillSprite;
                this.sprite.setScale(0.7f, 0.7f);
                this.collider.setXControlSpeed(0.0f);
                this.collider.setYControlSpeed(-1 * this.controlAuthority);
                break;
            }
            case goingDown:
            {
                this.sprite = this.stillSprite;
                this.sprite.setScale(0.7f, 0.7f);
                this.collider.setXControlSpeed(0.0f);
                this.collider.setYControlSpeed(this.controlAuthority);
                break;
            }
        }
    }

    private void workOutIfOnLadderAndSetColliderToIgnoreGravityIfSo()
    {
        if (TilemapHelper.isThisPointOnALadder(this.collider.getXAxisCentroid(), this.collider.getYAxisCentroid(), this.tileMap))
        {
            if (!this.collider.isIgnoringGravity())  //If not already on a ladder
            {
                //Set ignoring gravity, and stop movement as the player "grabs on"
                this.collider.setIgnoringGravity(true);
                stopHorizontalSpeedOnlyIfNotUnderPlayerControl();   //Only kill x-axis speed if the player is going in the opposite direction from the control inputs.
                this.collider.setYSpeed(0.0f);
            }
        }
        else
        {
            this.collider.setIgnoringGravity(false);
        }
    }

    @Override
    public void setXSpeed(float xSpeed)
    {
        this.sprite.setVelocityX(xSpeed);
    }

    @Override
    public void setYSpeed(float ySpeed)
    {
        this.sprite.setVelocityY(ySpeed);
    }

    public void setSelfDestructWhenOffScreen()
    {
        this.selfDestructWhenOffScreen = true;
    }

    @Override
    public boolean getSelfDestructWhenOffScreen()
    {
        if (this.dead)
        {
            return true;
        }

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

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_KP_UP)
        {
            this.upKeyPressed = true;

            if (atMostOneKeyFlagSet())    //Only if this is the only key pressed.
            {
                this.playerState = EPlayerState.goingUp;
            }
        }

        if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_KP_DOWN)
        {
            this.downKeyPressed = true;

            if (atMostOneKeyFlagSet())    //Only if this is the only key pressed.
            {
                this.playerState = EPlayerState.goingDown;
            }
        }

        if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_KP_LEFT)
        {
            this.leftKeyPressed = true;

            if (atMostOneKeyFlagSet())    //Only if this is the only key pressed.
            {
                this.playerState = EPlayerState.goingLeft;
            }
        }

        if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_KP_RIGHT)
        {
            this.rightKeyPressed = true;

            if (atMostOneKeyFlagSet())    //Only if this is the only key pressed.
            {
                this.playerState = EPlayerState.goingRight;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_SPACE)
        {
            spawnABulletInTheCorrectDirection();
        }

        if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_KP_UP)
        {
            this.upKeyPressed = false;

            if (atMostOneKeyFlagSet())    //Only if this is the last key to be released.
            {
                this.playerState = EPlayerState.standingStill;
            }
        }

        if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_KP_DOWN)
        {
            this.downKeyPressed = false;

            if (atMostOneKeyFlagSet())    //Only if this is the last key to be released.
            {
                this.playerState = EPlayerState.standingStill;
            }
        }

        if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_KP_LEFT)
        {
            this.leftKeyPressed = false;

            if (atMostOneKeyFlagSet())    //Only if this is the last key to be released.
            {
                this.playerState = EPlayerState.facingLeft;
            }
        }

        if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_KP_RIGHT)
        {
            this.rightKeyPressed = false;

            if (atMostOneKeyFlagSet())    //Only if this is the last key to be released.
            {
                this.playerState = EPlayerState.facingRight;
            }
        }
    }

    private void spawnABulletInTheCorrectDirection()
    {
        float bulletStartingX = this.collider.getXAxisCentroid();
        float bulletStartingY = this.collider.getYAxisCentroid();


        if (this.playerState == EPlayerState.facingLeft || this.playerState == EPlayerState.goingLeft)
        {
            this.bulletFactory.setBulletsGoingLeft();
            bulletStartingX = bulletStartingX - collider.getWidth();    //Give it half a player width to not immediately collide with the player.
            this.bulletFactory.spawnNewAt(bulletStartingX, bulletStartingY);
        }
        else if (this.playerState == EPlayerState.facingRight || this.playerState == EPlayerState.goingRight)
        {
            this.bulletFactory.setBulletsGoingRight();
            bulletStartingX = bulletStartingX + collider.getWidth();    //Give it half a player width to not immediately collide with the player.
            this.bulletFactory.spawnNewAt(bulletStartingX, bulletStartingY);
        }
    }

    private boolean atMostOneKeyFlagSet()
    {
        int keys = 0;

        if (this.leftKeyPressed)
            keys++;
        if (this.rightKeyPressed)
            keys++;
        if (this.upKeyPressed)
            keys++;
        if (this.downKeyPressed)
            keys++;

        if (keys <= 1)
            return true;

        return false;
    }

    private void stopHorizontalSpeedOnlyIfNotUnderPlayerControl()
    {
        if (this.collider.getXSpeed() > 0 && this.rightKeyPressed)  //If moving right under player control.
        {
            return; //Don't stop player.
        }

        if (this.collider.getXSpeed() < 0 && this.leftKeyPressed)   //If moving left under player control.
        {
            return; //Don't stop player.
        }

        this.collider.setXSpeed(0.0f);  //Stop player moving horizontally.
    }

    @Override
    public void hasCollidedWith(Object object)
    {
        if (object.getClass().equals(AlienBugMonster.class) || object.getClass().equals(Bullet.class))
        {
            die();
        }
    }

    @Override
    public void collidedWithTile()
    {

    }

    private enum EPlayerState
    {
        standingStill,

        facingLeft,
        goingLeft,

        facingRight,
        goingRight,

        goingUp,
        goingDown,
    }
}