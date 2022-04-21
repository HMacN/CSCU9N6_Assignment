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

    private void handleGrowling(long millisSinceLastUpdate)
    {
        this.millisSinceLastGrowl = this.millisSinceLastGrowl + millisSinceLastUpdate;

        if (millisSinceLastGrowl > 10_000)
        {
            millisSinceLastGrowl = 0;

            this.spaceshipGame.getGameObjects().addSound(new DistanceSound("sounds/growl.wav", this.spaceshipGame, this.collider));
        }
    }

    private void handleJumping(long millisSinceLastUpdate)
    {
        this.millisSinceLastJump = this.millisSinceLastJump + millisSinceLastUpdate;

        if (millisSinceLastJump > 4_000)
        {
            millisSinceLastJump = 0;

            this.collider.setYSpeed(this.collider.getYSpeed() + controlAuthority * 10.0f);
        }
    }

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

    public Collider getCollider()
    {
        return this.collider;
    }

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

    @Override
    public void hasCollidedWith(Object object)
    {
        if (object.getClass().equals(Bullet.class))
        {
            die();
        }
    }

    private void die()
    {
        this.dead = true;
        this.sprite.hide();
        this.collider.setToSelfDestruct();
        this.spaceshipGame.getGameObjects().addSound(new DistanceSound("sounds/splat.wav", this.spaceshipGame, this.collider));
    }

    @Override
    public void collidedWithTile()
    {

    }

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

    @Override
    public void update(EntityUpdate entityUpdate)
    {
        this.sprite.update(entityUpdate.getMillisSinceLastUpdate());

        handleAI(entityUpdate.getMillisSinceLastUpdate());
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

    @Override
    public void setSelfDestructWhenOffScreen()
    {
        this.selfDestructWhenOffScreen = true;
    }
}
