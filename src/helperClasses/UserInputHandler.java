package helperClasses;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

public class UserInputHandler implements MouseListener, KeyListener
{
    private JFrame window;
    private LinkedList<MouseListener> mouseListeners = new LinkedList<>();
    private LinkedList<KeyListener> keyListeners = new LinkedList<>();

    public UserInputHandler(JFrame window)
    {
        this.window = window;
        this.window.addMouseListener(this);
        this.window.addKeyListener(this);
    }

    public void addMouseListener(MouseListener listener)
    {
        this.mouseListeners.add(listener);
    }

    public void addKeyListener(KeyListener listener)
    {
        this.keyListeners.add(listener);
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        for (MouseListener listener : this.mouseListeners)
        {
            listener.mouseClicked(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        for (MouseListener listener : this.mouseListeners)
        {
            listener.mousePressed(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        for (MouseListener listener : this.mouseListeners)
        {
            listener.mouseReleased(e);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        for (MouseListener listener : this.mouseListeners)
        {
            listener.mouseEntered(e);
        }
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        for (MouseListener listener : this.mouseListeners)
        {
            listener.mouseExited(e);
        }
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
        for (KeyListener listener : this.keyListeners)
        {
            listener.keyTyped(e);
        }
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        for (KeyListener listener : this.keyListeners)
        {
            listener.keyPressed(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        for (KeyListener listener : this.keyListeners)
        {
            listener.keyReleased(e);
        }
    }
}
