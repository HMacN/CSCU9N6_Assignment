//  Assignment submission for CSCU9N6
//  Student Number: 2823735/1
//  Date of Submission: 22/04/2022

package renderableObjects;

import CSCU9N6Library.Sprite;
import factories.SpriteFactory;
import helperClasses.EntityUpdate;
import physics.Collider;
import physics.IHasCollider;
import soundsAndMusic.DistanceSound;
import spaceShipGame.SpaceshipGame;

import java.awt.*;
import java.util.Random;

/**
 * A monster for the player to fight.  Contains a physics object, and has an on-screen animation.
 */
public class AlienBugMonster implements IHasCollider, IDrawable
{
    private Collider collider;
    private Sprite sprite;
    private SpaceshipGame spaceshipGame;
    private Random random;

    private boolean selfDestructWhenOffScreen = false;
    private boolean dead = false;
    private boolean goingLeft = true;
    private long millisSinceLastGrowl = 0;
    private long millisSinceLastJump = 0;
    private long millisSinceLastTurn = 0;
    private float controlAuthority = 0.1f;
    private int millisUntilNextTurn = 3_000;

    /**
     * The constructor.
     * @param spaceshipGame A SpaceShipGame object to interrogate for needed data.
     * @param xCoord    A float describing the starting x-axis coordinate for this monster.
     * @param yCoord    A float describing the starting y-axis coordinate for this monster.
     */
    public AlienBugMonster(SpaceshipGame spaceshipGame, float xCoord, float yCoord)
    {
        this.spaceshipGame = spaceshipGame;
        this.random = new Random();

        this.sprite = SpriteFactory.getSpriteFromPNGFile("monsterAnim", 1, 4, 60);
        scaleSpriteCorrectly();
        this.sprite.playAnimation();
        this.sprite.show();

        this.collider = new Collider(xCoord, yCoord, 31.0f, 31.0f, 1.0f, this);
        this.collider.setXControlSpeed(controlAuthority);
    }

    /**
     * The simple AI for this monster.  Causes it to growl, jump, and turn at semi-random intervals.
     * Does nothing if this monster is dead.
     * @param millisSinceLastUpdate A long which is the time since the last update in milliseconds.
     */
    private void handleAI(long millisSinceLastUpdate)
    {
        if (this.dead)
        {
            return;
        }

        handleGrowling(millisSinceLastUpdate);
        handleJumping(millisSinceLastUpdate);
        handleTurning(millisSinceLastUpdate);
    }

    /**
     * Adds a growl to the game sounds list at semi-random intervals.
     * @param millisSinceLastUpdate A long which is the time since the last update in milliseconds.
     */
    private void handleGrowling(long millisSinceLastUpdate)
    {
        this.millisSinceLastGrowl = this.millisSinceLastGrowl + millisSinceLastUpdate;

        if (millisSinceLastGrowl > 10_000)
        {
            millisSinceLastGrowl = 0;

            this.spaceshipGame.getGameObjects().addSound(new DistanceSound("sounds/growl.wav", this.spaceshipGame, this.sprite));
        }
    }

    /**
     * Makes the monster jump at semi-random intervals.
     * @param millisSinceLastUpdate A long which is the time since the last update in milliseconds.
     */
    private void handleJumping(long millisSinceLastUpdate)
    {
        this.millisSinceLastJump = this.millisSinceLastJump + millisSinceLastUpdate;

        if (millisSinceLastJump > 4_000)
        {
            millisSinceLastJump = 0;

            this.collider.setYSpeed(this.collider.getYSpeed() + controlAuthority * 10.0f);
        }
    }

    /**
     * Makes the monster change direction at semi-random intervals.
     * @param millisSinceLastUpdate A long which is the time since the last update in milliseconds.
     */
    private void handleTurning(long millisSinceLastUpdate)
    {
        this.millisSinceLastTurn = this.millisSinceLastTurn + millisSinceLastUpdate;

        if (millisSinceLastTurn > millisUntilNextTurn)
        {
            millisSinceLastTurn = 0;
            millisUntilNextTurn = 3_000 + random.nextInt(3_000);

            if (random.nextInt(4) > 1)
            {
                this.goingLeft = !this.goingLeft;
                this.collider.setXControlSpeed(-1 * this.collider.getXControlSpeed());
                scaleSpriteCorrectly();
            }
        }
    }

    /**
     * A getter for this object's collider.
     * @return  A Collider object which belongs to this object.
     */
    public Collider getCollider()
    {
        return this.collider;
    }

    /**
     * Handles setting the scale of the displayed sprite.  Used to show which way the monster is facing.
     */
    private void scaleSpriteCorrectly()
    {
        if (goingLeft)
        {
            this.sprite.setScale(0.7f, 0.7f);
        }
        else
        {
            this.sprite.setScale(-0.7f, 0.7f);
        }
    }

    /**
     * Checks if the monster has collided with a bullet, and sets it to "dead" if so.
     * @param parentOfOtherCollider An IHasCollider object which is the parent object of the other collider in the collision.
     */
    @Override
    public void hasCollidedWith(IHasCollider parentOfOtherCollider)
    {
        if (parentOfOtherCollider.getClass().equals(Bullet.class))
        {
            die();
        }
    }

    /**
     * Performs all updates needed for monster death.
     */
    private void die()
    {
        this.dead = true;
        this.sprite.hide();
        this.collider.setToSelfDestruct();
        this.spaceshipGame.getGameObjects().addSound(new DistanceSound("sounds/splat.wav", this.spaceshipGame, this.sprite));
    }

    @Override
    public void collidedWithTile()
    {
        //Needed for interface.
    }

    /**
     * Updates the sprite position and draws it to the screen.
     * @param graphics2D	The Graphics2D object to draw this object on.
     * @param xOffset	A float which is the horizontal offset in pixels to draw this object at.
     * @param yOffset	A float which is the vertical offset in pixels to draw this object at.
     */
    @Override
    public void draw(Graphics2D graphics2D, float xOffset, float yOffset)
    {
        if (!this.selfDestructWhenOffScreen)    //If not set to self destruct
        {
            //Update to the collider position.
            this.sprite.setX(this.collider.getXCoord());
            this.sprite.setY(this.collider.getYCoord());

            //Apply the offset.
            this.sprite.setX(this.sprite.getX() + xOffset);
            this.sprite.setY(this.sprite.getY() + yOffset);
        }

        //Draw the sprite.
        this.sprite.drawTransformed(graphics2D);
    }

    /**
     * Updates the sprite and AI for this update cycle.
     * @param entityUpdate	An EntityUpdate object containing the update information for this update cycle.
     */
    @Override
    public void update(EntityUpdate entityUpdate)
    {
        this.sprite.update(entityUpdate.getMillisSinceLastUpdate());

        handleAI(entityUpdate.getMillisSinceLastUpdate());
    }

    /**
     * Setter for the sprite horizontal speed.
     * @param xSpeed	A float which is the new horizontal speed in pixels per millisecond.
     */
    @Override
    public void setXSpeed(float xSpeed)
    {
        this.sprite.setVelocityX(xSpeed);
    }

    /**
     * Setter for the sprite vertical speed.
     * @param ySpeed	A float which is the new vertical speed in pixels per millisecond.
     */
    @Override
    public void setYSpeed(float ySpeed)
    {
        this.sprite.setVelocityY(ySpeed);
    }

    /**
     * Getter for the self-destruct flag.  Allows the object to be removed when it is out of sight of the player.
     * @return  A boolean describing if the object may be safely removed.
     */
    @Override
    public boolean getSelfDestructWhenOffScreen()
    {

        if (this.dead)   //If the monster is dead
        {
            return true;    //It's not visible, so get rid of it.
        }

        if (!this.selfDestructWhenOffScreen)
        {
            return false;   //Don't self destruct without being told to do so.
        }

        if (this.sprite.getX() > this.spaceshipGame.getScreenWidth() || this.sprite.getX() < -this.sprite.getWidth())   //If the sprite has gone off the sides of the screen.
        {
            return true;
        }

        if (this.sprite.getY() > this.spaceshipGame.getScreenHeight() || this.sprite.getY() < -this.sprite.getHeight()) //If the sprite has gone off the top or bottom of the screen.
        {
            return true;
        }

        //If the sprite is due for destruction, but is still in view:
        return false;
    }

    /**
     * Sets the self destruct flag of this object to "true".
     */
    @Override
    public void setSelfDestructWhenOffScreen()
    {
        this.selfDestructWhenOffScreen = true;
    }
}
