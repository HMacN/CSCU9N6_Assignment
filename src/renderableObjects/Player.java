package renderableObjects;

import CSCU9N6Library.Sprite;
import CSCU9N6Library.TileMap;
import helperClasses.EntityUpdate;
import helperClasses.EntityUpdateFactory;
import helperClasses.SpriteFactory;
import helperClasses.UserInputHandler;
import physics.Collider;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Player implements IDrawable, KeyListener
{
    private Sprite movingSprite = SpriteFactory.getSpriteFromPNGFile("playerAnim", 1, 4, 60);
    private Sprite stillSprite = SpriteFactory.getSpriteFromPNGFile("player", 1, 1, 10_000);
    private Sprite sprite = stillSprite;
    private Collider collider;

    private float minYCoord;
    private float minXCoord;
    private float maxYCoord;
    private float maxXCoord;
    private boolean selfDestructStatus = false;

    private float startingX;
    private float startingY;

    private EntityUpdateFactory updateFactory;
    private UserInputHandler inputHandler;
    private TileMap tileMap;

    private boolean upKeyPressed = false;
    private boolean downKeyPressed = false;
    private boolean leftKeyPressed = false;
    private boolean rightKeyPressed = false;
    private float controlAuthority = 0.1f;

    public Player(int screenWidth, int screenHeight, EntityUpdateFactory updateFactory, UserInputHandler inputHandler, TileMap tileMap)
    {
        this.startingX = screenWidth / 2.0f;
        this.startingY = screenHeight / 2.0f;

        this.movingSprite.setX(this.startingX);
        this.movingSprite.setY(this.startingY);
        this.movingSprite.setScale(0.70f);

        this.stillSprite.setX(this.startingX);
        this.stillSprite.setY(this.startingY);
        this.stillSprite.setScale(0.70f);

        this.collider = new Collider(this.startingX, this.startingY, 31.0f, 31.0f);

        this.maxXCoord = screenWidth;
        this.minXCoord = -screenWidth;
        this.maxYCoord = screenHeight;
        this.minYCoord = -screenHeight;

        this.updateFactory = updateFactory;
        this.inputHandler = inputHandler;
        this.inputHandler.addKeyListener(this);
        this.tileMap = tileMap;
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

        workOutIfOnLadderAndSetColliderToIgnoreGravityIfSo();
    }

    private void workOutIfOnLadderAndSetColliderToIgnoreGravityIfSo()
    {
        if (isThisPointOnALadder(this.collider.getXCoord(), this.collider.getYCoord()) ||
                isThisPointOnALadder(this.collider.getXCoord() + this.collider.getWidth(), this.collider.getYCoord()))
        {
            if (!this.collider.isIgnoringGravity())  //If not already on a ladder
            {
                //Set ignoring gravity, and stop movement as the player "grabs on"
                this.collider.setIgnoringGravity(true);
                this.collider.setXSpeed(0.0f);
                this.collider.setYSpeed(0.0f);
            }
        }
        else
        {
            this.collider.setIgnoringGravity(false);
        }
    }

    private char tileMapChar(float xCoord, float yCoord)
    {
        int tileMapX = (int) xCoord / this.tileMap.getTileWidth();
        int tileMapY = (int) yCoord / this.tileMap.getTileHeight();

        return this.tileMap.getTileChar(tileMapX, tileMapY);
    }

    private boolean isThisPointOnALadder(float xCoord, float yCoord)
    {
        if (tileMapChar(xCoord, yCoord) == 'l')
        {
            return true;
        }

        return false;
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
                || this.sprite.getX() > this.maxXCoord)
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
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_KP_UP)
        {
            if (!this.upKeyPressed)    //Only if the key isn't already down.
            {
                this.upKeyPressed = true;
                this.collider.setYSpeed(this.collider.getYSpeed() - this.controlAuthority);    //Add control speed to the player.
                this.collider.setIgnoreFriction(true);  //Ignore friction while button down.
            }
        }

        if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_KP_DOWN)
        {
            if (!this.downKeyPressed) //Only if the key isn't already down.
            {
                this.downKeyPressed = true;
                this.collider.setYSpeed(this.collider.getYSpeed() + controlAuthority);    //Add control speed to the player.
                this.collider.setIgnoreFriction(true);  //Ignore friction while button down.
            }
        }

        if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_KP_LEFT)
        {
            if (!this.leftKeyPressed) //Only if the key isn't already down.
            {
                sprite = movingSprite;

                this.leftKeyPressed = true;
                this.collider.setXSpeed(this.collider.getXSpeed() - controlAuthority);    //Add control speed to the player.
                this.collider.setIgnoreFriction(true);  //Ignore friction while button down.
                this.sprite.setScale(-0.7f, 0.7f);
            }
        }

        if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_KP_RIGHT)
        {
            sprite = movingSprite;

            if (!this.rightKeyPressed) //Only if the key isn't already down.
            {
                this.rightKeyPressed = true;
                this.collider.setXSpeed(this.collider.getXSpeed() + controlAuthority);    //Add control speed to the player.
                this.collider.setIgnoreFriction(true);  //Ignore friction while button down.
                this.sprite.setScale(0.7f, 0.7f);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_KP_UP)
        {
            if (this.upKeyPressed)    //Only if the key is already down.
            {
                this.upKeyPressed = false;
                this.collider.setYSpeed(0.0f);    //Stop the player.
                stopIgnoringFrictionIfNoOtherButtonsPressed();
            }
        }

        if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_KP_DOWN)
        {
            if (this.downKeyPressed)    //Only if the key is already down.
            {
                this.downKeyPressed = false;
                this.collider.setYSpeed(0.0f);    //Stop the player.
                stopIgnoringFrictionIfNoOtherButtonsPressed();
            }
        }

        if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_KP_LEFT)
        {
            sprite = stillSprite;

            if (this.leftKeyPressed)    //Only if the key is already down.
            {
                this.leftKeyPressed = false;
                this.collider.setXSpeed(0.0f);    //Stop the player.
                stopIgnoringFrictionIfNoOtherButtonsPressed();
            }
        }

        if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_KP_RIGHT)
        {
            sprite = stillSprite;

            if (this.rightKeyPressed)    //Only if the key is already down.
            {
                this.rightKeyPressed = false;
                this.collider.setXSpeed(0.0f);    //Stop the player.
                stopIgnoringFrictionIfNoOtherButtonsPressed();
            }
        }
    }

    private void stopIgnoringFrictionIfNoOtherButtonsPressed()
    {
        if (this.rightKeyPressed || this.leftKeyPressed || this.upKeyPressed || this.downKeyPressed)
        {
            return;
        }

        this.collider.setIgnoreFriction(false);
    }
}