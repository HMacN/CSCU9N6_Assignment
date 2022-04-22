//  Assignment submission for CSCU9N6
//  Student Number: 2823735/1
//  Date of Submission: 22/04/2022

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

/**
 * The player's in-game avatar.
 */
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

    /**
     * The constructor.
     * @param spaceshipGame A SpaceShipGame object to interrogate for needed data.
     * @param xCoord    A float which is the horizontal starting coordinate of the collider in pixels.
     * @param yCoord    A float which is the vertical starting coordinate of the collider in pixels.
     */
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

    /**
     * A getter for this object's collider.
     * @return  A Collider object which is owned by this object.
     */
    public Collider getCollider()
    {
        return collider;
    }

    /**
     * Handles everything that needs to happen when the player dies.  Starts the shutdown process for the level.
     */
    private void die()
    {
        this.dead = true;
        this.sprite.hide();
        this.collider.setToSelfDestruct();
        this.spaceshipGame.getGameObjects().addSound(new DistanceSound("sounds/splat.wav", this.spaceshipGame, this.sprite));
        this.level.addLevelEvent(new PrepareToEndLevel(this.spaceshipGame, EGameState.mainMenu), 0);
    }

    /**
     * Draws the current sprite to the display area.
     * @param graphics2D	The Graphics2D object to draw this object on.
     * @param xOffset	A float which is the horizontal offset in pixels to draw this object at.
     * @param yOffset	A float which is the vertical offset in pixels to draw this object at.
     */
    @Override
    public void draw(Graphics2D graphics2D, float xOffset, float yOffset)
    {
        //Draw the sprite, ignoring the offsets (everything else is offset to this).
        this.sprite.drawTransformed(graphics2D);
    }

    /**
     * Performs all needed changes for this update cycle.
     * @param entityUpdate	An EntityUpdate object containing the update information for this update cycle.
     */
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

    /**
     * Changes the sprite and animation for the player based on the state that the player is currently in.
     */
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
                this.collider.setYControlSpeed(-2 * this.controlAuthority);
                break;
            }
            case goingDown:
            {
                this.sprite = this.stillSprite;
                this.sprite.setScale(0.7f, 0.7f);
                this.collider.setXControlSpeed(0.0f);
                this.collider.setYControlSpeed(2 * this.controlAuthority);
                break;
            }
        }
    }

    /**
     * Checks the tilemap to see if the player is on a ladder and sets the collider to ignore gravity if so.
     * This represents the player grabbing on to a ladder if they are falling.
     */
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

    /**
     * A setter for the sprite's horizontal speed.
     * @param xSpeed	A float which is the new horizontal speed in pixels per millisecond.
     */
    @Override
    public void setXSpeed(float xSpeed)
    {
        this.sprite.setVelocityX(xSpeed);
    }

    /**
     * A setter for the sprite's vertical speed.
     * @param ySpeed	A float which is the new vertical speed in pixels per millisecond.
     */
    @Override
    public void setYSpeed(float ySpeed)
    {
        this.sprite.setVelocityY(ySpeed);
    }

    /**
     * Sets the self destruct flag, which allows the object to be removed when it leaves the render area.
     */
    @Override
    public void setSelfDestructWhenOffScreen()
    {
        this.selfDestructWhenOffScreen = true;
    }

    /**
     * Getter for the self-destruct status of the player.  Only true if the player is off-screen and due to be removed.
     * @return  A boolean which is true if the player sprite is safe to remove from the game.
     */
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
        //Needed by the interface
    }

    /**
     * Handles user inputs.  Only used to start the player moving on screen.
     * @param e A KeyEvent to respond to.
     */
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

    /**
     * Handles user input.  Used to fire the gun, and to stop the player moving when a movement key is released.
     * @param e A KeyEvent to respond to.
     */
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

    /**
     * Adds a new bullet to the game world, which should be heading in the direction the player is facing.
     */
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

    /**
     * Checks how many key flags ae set, and returns false if more than one key is currently pressed.
     * @return  A boolean which describes if more than one key is pressed.
     */
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

    /**
     * Stops the player moving horizontally if the player is not holding down a horizontal movement control key.
     * Used to represent the player "grabbing on to" a ladder if they fall past it.
     */
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

    /**
     * Works out if the player has collided with either a monster or a bullet, and calls the "die" function if so.
     * @param parentOfOtherCollider An IHasCollider object which is the parent object of the other collider in the collision.
     */
    @Override
    public void hasCollidedWith(IHasCollider parentOfOtherCollider)
    {
        if (parentOfOtherCollider.getClass().equals(AlienBugMonster.class) || parentOfOtherCollider.getClass().equals(Bullet.class))
        {
            die();
        }
    }

    @Override
    public void collidedWithTile()
    {
        //Needed for interface
    }

    /**
     * An enum which represents the different states a player object can be in.
     */
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