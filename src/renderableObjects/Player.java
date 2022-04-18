package renderableObjects;

import CSCU9N6Library.Sprite;
import helperClasses.EntityUpdate;
import helperClasses.EntityUpdateFactory;
import helperClasses.SpriteFactory;
import helperClasses.UserInputHandler;
import physics.Collider;
import physics.IPhysicsEntity;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Player implements IPhysicsEntity, IDrawable, KeyListener
{
    private Sprite sprite = SpriteFactory.getSpriteFromPNGFile("player", 1, 4, 60);
    private Collider collider;

    private boolean restingOnTopFace = false;
    private boolean restingOnBottomFace = false;
    private boolean restingOnLeftFace = false;
    private boolean restingOnRightFace = false;
    private float minYCoord;
    private float minXCoord;
    private float maxYCoord;
    private float maxXCoord;
    private boolean selfDestructStatus = false;

    private float startingX;
    private float startingY;

    private EntityUpdateFactory updateFactory;
    private UserInputHandler inputHandler;

    public Player(int screenWidth, int screenHeight, EntityUpdateFactory updateFactory, UserInputHandler inputHandler)
    {
        this.startingX = screenWidth / 2.0f;
        this.startingY = screenHeight / 2.0f;

        this.sprite.setX(this.startingX);
        this.sprite.setY(this.startingY);
        this.sprite.setScale(0.70f);

        this.collider = new Collider(this.startingX, this.startingY, 31.0f, 31.0f);

        this.maxXCoord = screenWidth;
        this.minXCoord = -screenWidth;
        this.maxYCoord = screenHeight;
        this.minYCoord = -screenHeight;

        this.updateFactory = updateFactory;
        this.inputHandler = inputHandler;
        this.inputHandler.addKeyListener(this);
    }

    public Collider getCollider()
    {
        return collider;
    }

    @Override
    public void draw(Graphics2D graphics2D, float xOffset, float yOffset)
    {
        this.sprite.drawTransformed(graphics2D);
    }

    @Override
    public void setSelfDestructBoundaries(int maxXCoord, int minXCoord, int maxYCoord, int minYCoord)
    {

    }

    @Override
    public void update(EntityUpdate entityUpdate)
    {
        this.sprite.update(entityUpdate.getMillisSinceLastUpdate());

        this.updateFactory.setPlayerXOffset(this.startingX - this.collider.getXCoord());
        this.updateFactory.setPlayerYOffset(this.startingY - this.collider.getYCoord());
    }

    @Override
    public float getXSpeed()
    {
        return this.sprite.getVelocityX();
    }

    @Override
    public void setXSpeed(float xSpeed)
    {
        this.sprite.setVelocityX(xSpeed);
    }

    @Override
    public float getYSpeed()
    {
        return this.sprite.getVelocityY();
    }

    @Override
    public void setYSpeed(float ySpeed)
    {
        this.sprite.setVelocityY(ySpeed);
    }

    @Override
    public void setXCoord(float xCoord)
    {
        this.sprite.setX(xCoord);
    }

    @Override
    public void setYCoord(float yCoord)
    {
        this.sprite.setY(yCoord);
    }

    @Override
    public boolean getSelfDestructStatus()
    {
        if (this.sprite.getY() < this.minYCoord
                || this.sprite.getY() > this.maxYCoord
                || this.sprite.getX() < this.minXCoord
                || this.sprite.getX() > this.maxXCoord )
        {
            this.selfDestructStatus = true;
            this.collider.setToSelfDestruct();
        }

        return this.selfDestructStatus;
    }

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    @Override
    public void keyPressed(KeyEvent e)
    {

    }

    @Override
    public void keyReleased(KeyEvent e)
    {

    }
}