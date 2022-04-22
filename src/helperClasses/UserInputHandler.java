//  Assignment submission for CSCU9N6
//  Student Number: 2823735/1
//  Date of Submission: 22/04/2022

package helperClasses;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

/**
 * A class which captures all the user inputs, and which can be subscribed to for event notifications.
 */
public class UserInputHandler implements MouseListener, KeyListener
{
    private JFrame window;  //The JFrame to listen to for user input events.
    private LinkedList<MouseListener> mouseListeners = new LinkedList<>();  //Subscribers for mouse events.
    private LinkedList<KeyListener> keyListeners = new LinkedList<>();  //Subscribers for keyboard events.

    /**
     * The constructor.  Immediately registers this object with a given JFrame to listen for updates.
     * @param window    A JFrame to listen to for user input events.
     */
    public UserInputHandler(JFrame window)
    {
        this.window = window;
        this.window.addMouseListener(this);
        this.window.addKeyListener(this);
    }

    /**
     * Allow an object to subscribe for mouse events.
     * @param listener  A MouseListener object to give future event notifications to.
     */
    public void addMouseListener(MouseListener listener)
    {
        this.mouseListeners.add(listener);
    }

    /**
     * Allow an object to subscribe for keyboard events.
     * @param listener  A KeyboardListener object to give future event notifications to.
     */
    public void addKeyListener(KeyListener listener)
    {
        this.keyListeners.add(listener);
    }

    /**
     * Notifies all relevant subscribers of a new user input event.
     * @param e The new user input event to give to all relevant subscribers.
     */
    @Override
    public void mouseClicked(MouseEvent e)
    {
        for (MouseListener listener : this.mouseListeners)
        {
            listener.mouseClicked(e);
        }
    }

    /**
     * Notifies all relevant subscribers of a new user input event.
     * @param e The new user input event to give to all relevant subscribers.
     */
    @Override
    public void mousePressed(MouseEvent e)
    {
        for (MouseListener listener : this.mouseListeners)
        {
            listener.mousePressed(e);
        }
    }

    /**
     * Notifies all relevant subscribers of a new user input event.
     * @param e The new user input event to give to all relevant subscribers.
     */
    @Override
    public void mouseReleased(MouseEvent e)
    {
        for (MouseListener listener : this.mouseListeners)
        {
            listener.mouseReleased(e);
        }
    }

    /**
     * Notifies all relevant subscribers of a new user input event.
     * @param e The new user input event to give to all relevant subscribers.
     */
    @Override
    public void mouseEntered(MouseEvent e)
    {
        for (MouseListener listener : this.mouseListeners)
        {
            listener.mouseEntered(e);
        }
    }

    /**
     * Notifies all relevant subscribers of a new user input event.
     * @param e The new user input event to give to all relevant subscribers.
     */
    @Override
    public void mouseExited(MouseEvent e)
    {
        for (MouseListener listener : this.mouseListeners)
        {
            listener.mouseExited(e);
        }
    }

    /**
     * Notifies all relevant subscribers of a new user input event.
     * @param e The new user input event to give to all relevant subscribers.
     */
    @Override
    public void keyTyped(KeyEvent e)
    {
        for (KeyListener listener : this.keyListeners)
        {
            listener.keyTyped(e);
        }
    }

    /**
     * Notifies all relevant subscribers of a new user input event.
     * @param e The new user input event to give to all relevant subscribers.
     */
    @Override
    public void keyPressed(KeyEvent e)
    {
        for (KeyListener listener : this.keyListeners)
        {
            listener.keyPressed(e);
        }
    }

    /**
     * Notifies all relevant subscribers of a new user input event.
     * @param e The new user input event to give to all relevant subscribers.
     */
    @Override
    public void keyReleased(KeyEvent e)
    {
        for (KeyListener listener : this.keyListeners)
        {
            listener.keyReleased(e);
        }
    }
}
