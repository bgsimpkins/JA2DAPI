/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twodapi.lwjgl;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author ben
 */
public class MouseControlsGL
{
    public static final int LEFT_BUTTON = 0;
    public static final int CENTER_BUTTON = 2;
    public static final int RIGHT_BUTTON = 1;

    private Rectangle2D.Float rect;
    private Point mousePoint;

    public MouseControlsGL()
    {
        if (!Mouse.isCreated()) try
        {
            Mouse.create();
        } catch (LWJGLException ex)
        {
            System.err.println("MouseControlsGL: Could not create mouse!");
            ex.printStackTrace();
        }

        rect = new Rectangle2D.Float();
        mousePoint = new Point();

    }
    
    public boolean mouseOver(MouseableGL mouseable)
    {
        Vector2f loc = mouseable.getLocation();
        Dimension size = mouseable.getSize();

        if (mouseable instanceof TextGL) rect.setRect(loc.x, loc.y, size.getWidth(), size.getHeight());

        else rect.setRect(loc.x-size.width/2, loc.y-size.height/2, size.getWidth(), size.getHeight());

        //Y origin of lwjgl mouse is button. Need to flip
        return rect.contains(Mouse.getX(), Display.getDisplayMode().getHeight()-Mouse.getY());
    }
    public Point getMouseLocation()
    {
        //Y origin of lwjgl mouse is button. Need to flip
        mousePoint.setLocation(Mouse.getX(), Display.getDisplayMode().getHeight()-Mouse.getY());
        return mousePoint;
    }

    public boolean buttonPressed(int button)
    {
        return Mouse.isButtonDown(button);
    }

    public static void main(String[] args)
    {
        GLPainter painter = new GLPainter(800,600)
        {
            MouseControlsGL mc;
            SpriteGL ship;

            public void gameInit()
            {
                //setFullscreen(true);

                mc = new MouseControlsGL();
                ship = new SpriteGL("/images/ship.gif");
                ship.setLocation(100, 100);
                addToPaintList(ship);
            }
            
            public void gameUpdate(float interpolation)
            {
                ship.setDrawBounds(mc.mouseOver(ship));
                //System.out.println("Mouse loc="+mc.getMouseLocation());
            }
        };
        painter.start();
    }

}
