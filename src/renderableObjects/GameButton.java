package renderableObjects;

import CSCU9N6Library.Sprite;
import helperClasses.EntityUpdate;
import helperClasses.IButtonFunctionObject;
import helperClasses.UserInputHandler;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * A button to be displayed on screen.  Displays a different sprite when clicked on.
 * Notifies another object that it has been pressed by calling a method on an attribute object which acts as a function pointer.
 */
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

    /**
     * The constructor.
     * @param screenWidth   A float which is the width of the screen in pixels
     * @param screenHeight  A float which is the height of the screen in pixels
     * @param unPressedSprite   A Sprite, which will be displayed before the button is pressed
     * @param pressedSprite A Sprite, which will be displayed after the button is pressed
     * @param inputHandler  A UserInputHandler object, to register with for mouse events
     * @param functionObject    An object which will call whatever function the button is supposed to perform.
     */
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
    }

    /**
     * Computes and stores the region of the screen that the button covers.
     */
    private void setUpClickBoundaries()
    {
        this.clickBoundaryTop = (int) this.sprite.getY();
        this.clickBoundaryLeft = (int) this.sprite.getX();

        this.clickBoundaryBottom = (int) this.sprite.getY() + this.sprite.getHeight();
        this.clickBoundaryRight = (int) this.sprite.getX() + this.sprite.getWidth();
    }

    /**
     * Draws the button to the display area.
     * @param graphics2D	The Graphics2D object to draw this object on.
     * @param xOffset	A float which is the horizontal offset in pixels to draw this object at.
     * @param yOffset	A float which is the vertical offset in pixels to draw this object at.
     */
    @Override
    public void draw(Graphics2D graphics2D, float xOffset, float yOffset)
    {
        this.sprite.draw(graphics2D);
        this.visible = this.sprite.isVisible();
    }

    /**
     * Updates the displayed sprite.
     * @param entityUpdate	An EntityUpdate object containing the update information for this update cycle.
     */
    @Override
    public void update(EntityUpdate entityUpdate)
    {
        this.sprite.update(entityUpdate.getMillisSinceLastUpdate());
    }

    /**
     * Getter for the horizontal speed of the sprite.
     * @return  A float which is the horizontal speed of the sprite in pixels per millisecond
     */
    public float getXSpeed()
    {
        return this.sprite.getVelocityX();
    }

    /**
     * Set the horizontal speed of this object's sprite.
     * @param xSpeed	A float which is the new horizontal speed in pixels per millisecond.
     */
    @Override
    public void setXSpeed(float xSpeed)
    {
        this.pressedSprite.setVelocityX(xSpeed);
        this.unPressedSprite.setVelocityX(xSpeed);
    }

    /**
     * A getter for the vertical speed of this object's sprite.
     * @return  A float which is the vertical speed of this object's sprite in pixels per millisecond
     */
    public float getYSpeed()
    {
        return this.sprite.getVelocityY();
    }

    /**
     * Set the vertical speed of this object's sprite.
     * @param ySpeed	A float which is the new vertical speed in pixels per millisecond.
     */
    @Override
    public void setYSpeed(float ySpeed)
    {
        this.pressedSprite.setVelocityY(ySpeed);
        this.unPressedSprite.setVelocityY(ySpeed);
    }

    /**
     * A setter for the x-coordinate of the sprite.
     * @param xCoord    A float which is the new x-coord of the sprite.
     */
    public void setXCoord(float xCoord)
    {
        this.pressedSprite.setX(xCoord);
        this.unPressedSprite.setX(xCoord);
    }

    /**
     * A setter for the y-coordinate of the sprite.
     * @param yCoord    A float which is the new y-coord of the sprite.
     */
    public void setYCoord(float yCoord)
    {
        this.pressedSprite.setY(yCoord);
        this.unPressedSprite.setY(yCoord);
    }


    /**
     * A getter for the self-destruct flag.  Allows this object to be removed when the player can no longer see it.
     * @return	A boolean describing whether this object can be removed when out of sight.
     */
    @Override
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

    /**
     * A setter for the self-destruct flag.  Allows this object to be removed when the player can no longer see it.
     */
    public void setSelfDestructWhenOffScreen()
    {
        this.selfDestructWhenOffScreen = true;
    }

    /**
     * Checks if the button is being displayed, and then checks if the user has clicked inside it's boundary.
     * @param e A MouseEvent to handle.
     */
    @Override
    public void mouseClicked(MouseEvent e)
    {
        //Do nothing if the button is not displayed.
        if (!this.visible)
        {
            return;
        }

        setUpClickBoundaries(); //Confirm where the button covers.

        if (e.getX() < this.clickBoundaryRight
                && e.getX() > this.clickBoundaryLeft
                && e.getY() > this.clickBoundaryTop
                && e.getY() < this.clickBoundaryBottom)
        {
            this.sprite = this.pressedSprite;
            this.buttonFunctionObject.onButtonPress();  //Call the function of the button.
        }
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        //Needed for interface
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        //Needed for interface
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        //Needed for interface
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        //Needed for interface
    }
}