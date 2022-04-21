package renderableObjects;

import CSCU9N6Library.Sprite;
import helperClasses.EntityUpdate;
import helperClasses.IButtonFunctionObject;
import helperClasses.UserInputHandler;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GameButton implements IDrawable, MouseListener
{
    private final Sprite pressedSprite;
    private final float screenWidth;
    private final float screenHeight;
    private final Sprite unPressedSprite;
    private Sprite sprite;
    private boolean selfDestructWhenOffScreen = false;
    private final UserInputHandler inputHandler;
    private final IButtonFunctionObject buttonFunctionObject;

    private int clickBoundaryTop;
    private int clickBoundaryBottom;
    private int clickBoundaryLeft;
    private int clickBoundaryRight;
    private boolean visible = false;

    public GameButton(float screenWidth, float screenHeight, Sprite unPressedSprite, Sprite pressedSprite, UserInputHandler inputHandler, IButtonFunctionObject functionObject)
    {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.unPressedSprite = unPressedSprite;
        this.pressedSprite = pressedSprite;
        this.sprite = this.unPressedSprite;
        this.buttonFunctionObject = functionObject;

        this.inputHandler = inputHandler;
        inputHandler.addMouseListener(this);

        //setUpClickBoundaries();
    }

    private void setUpClickBoundaries()
    {
        this.clickBoundaryTop = (int) this.sprite.getY();
        this.clickBoundaryLeft = (int) this.sprite.getX();

        this.clickBoundaryBottom = (int) this.sprite.getY() + this.sprite.getHeight();
        this.clickBoundaryRight = (int) this.sprite.getX() + this.sprite.getWidth();
    }

    @Override
    public void draw(Graphics2D graphics2D, float xOffset, float yOffset)
    {
        this.sprite.draw(graphics2D);
        this.visible = this.sprite.isVisible();
    }

    /**
     * @param entityUpdate
     */
    public void update(EntityUpdate entityUpdate)
    {
        this.sprite.update(entityUpdate.getMillisSinceLastUpdate());

        //setUpClickBoundaries();
    }

    /**
     * @param parallax
     */
    public void setParallax(float parallax)
    {

    }

    public float getXSpeed()
    {
        return this.sprite.getVelocityX();
    }

    public void setXSpeed(float xSpeed)
    {
        this.pressedSprite.setVelocityX(xSpeed);
        this.unPressedSprite.setVelocityX(xSpeed);
    }

    public float getYSpeed()
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

    public boolean getSelfDestructWhenOffScreen()
    {
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

    public void setSelfDestructWhenOffScreen()
    {
        this.selfDestructWhenOffScreen = true;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        //Do nothing if the button is not displayed.
        if (!this.visible)
        {
            return;
        }

        setUpClickBoundaries();

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