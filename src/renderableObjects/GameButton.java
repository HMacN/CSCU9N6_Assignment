package renderableObjects;

import CSCU9N6Library.Sprite;
import helperClasses.Debug;
import helperClasses.EntityUpdate;
import helperClasses.IButtonFunctionObject;
import helperClasses.UserInputHandler;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GameButton implements IDrawable, MouseListener
{
    private Sprite pressedSprite;
    private Sprite unPressedSprite;
    private Sprite sprite;
    private boolean selfDestructStatus = false;
    private UserInputHandler inputHandler;
    private IButtonFunctionObject buttonFunctionObject;

    private int maxXCoord;
    private int minXCoord;
    private int maxYCoord;
    private int minYCoord;

    private int clickBoundaryTop;
    private int clickBoundaryBottom;
    private int clickBoundaryLeft;
    private int clickBoundaryRight;

    public GameButton(int screenWidth, int screenHeight, Sprite unPressedSprite, Sprite pressedSprite, UserInputHandler inputHandler, IButtonFunctionObject functionObject)
    {
        this.maxXCoord = screenWidth;
        this.minXCoord = -screenWidth;
        this.maxYCoord = screenHeight;
        this.minYCoord = -screenHeight;

        this.unPressedSprite = unPressedSprite;
        this.pressedSprite = pressedSprite;
        this.sprite = this.unPressedSprite;
        this.buttonFunctionObject = functionObject;

        this.inputHandler = inputHandler;
        inputHandler.addMouseListener(this);

        setUpClickBoundaries();
    }

    private void setUpClickBoundaries()
    {
        this.clickBoundaryTop = (int) this.sprite.getY();
        this.clickBoundaryLeft = (int) this.sprite.getX();

        this.clickBoundaryBottom = (int) this.sprite.getY() + this.sprite.getHeight();
        this.clickBoundaryRight = (int) this.sprite.getX() + this.sprite.getWidth();
    }

    public void draw(Graphics2D graphics2D)
    {
        this.sprite.draw(graphics2D);
    }

    public void setSelfDestructBoundaries(int maxXCoord, int minXCoord, int maxYCoord, int minYCoord)
    {
        this.maxXCoord = maxXCoord;
        this.minXCoord = minXCoord;
        this.maxYCoord = maxYCoord;
        this.minYCoord = minYCoord;
    }

    /**
     * @param entityUpdate
     */
    public void update(EntityUpdate entityUpdate)
    {
        this.sprite.update(entityUpdate.getMillisSinceLastUpdate());

        setUpClickBoundaries();

        updateSelfDestructStatus();
    }

    /**
     * @param parallax
     */
    public void setParallax(float parallax)
    {

    }

    public double getXSpeed()
    {
        return this.sprite.getVelocityX();
    }

    public void setXSpeed(float xSpeed)
    {
        this.pressedSprite.setVelocityX(xSpeed);
        this.unPressedSprite.setVelocityX(xSpeed);
    }

    public double getYSpeed()
    {
        return this.sprite.getVelocityY();
    }

    public void setYSpeed(float ySpeed)
    {
        this.pressedSprite.setVelocityY(ySpeed);
        this.unPressedSprite.setVelocityY(ySpeed);
    }

    public void setXCoord(float xCoord)
    {
        this.pressedSprite.setX(xCoord);
        this.unPressedSprite.setX(xCoord);
    }

    public void setYCoord(float yCoord)
    {
        this.pressedSprite.setY(yCoord);
        this.unPressedSprite.setY(yCoord);
    }

    public boolean getSelfDestructStatus()
    {
        return this.selfDestructStatus;
    }

    private void updateSelfDestructStatus()
    {
        if (this.sprite.getY() < this.minYCoord
                || this.sprite.getY() > this.maxYCoord
                || this.sprite.getX() < this.minXCoord
                || this.sprite.getX() > this.maxXCoord)
        {
            this.selfDestructStatus = true;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        if (e.getX() < this.clickBoundaryRight
                && e.getX() > this.clickBoundaryLeft
                && e.getY() > this.clickBoundaryTop
                && e.getY() < this.clickBoundaryBottom)
        {
            this.sprite = this.pressedSprite;
            this.buttonFunctionObject.onButtonPress();
        }
    }

    @Override
    public void mousePressed(MouseEvent e)
    {

    }

    @Override
    public void mouseReleased(MouseEvent e)
    {

    }

    @Override
    public void mouseEntered(MouseEvent e)
    {

    }

    @Override
    public void mouseExited(MouseEvent e)
    {

    }
}