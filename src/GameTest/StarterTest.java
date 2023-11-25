package GameTest;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import twodapi.core.*;

public class StarterTest implements Paintable
{
    private Sprite2D ship = null;

    private KeyControls keyControls = null;

    public StarterTest()
    {
        Frame2D gameFrame = new Frame2D(Frame2D.REG_PAINTER,1024,768,true);
        GamePainter painter = (GamePainter)gameFrame.getPainter();
        painter.setFrameRate(30);
     
        keyControls = new KeyControls(painter.getComponent());
        keyControls.set(KeyEvent.VK_LEFT);
        keyControls.set(KeyEvent.VK_RIGHT);
        ship = new Sprite2D("/images/ship.gif");
        ship.setLocation(500,400);
        ship.setScreenWrap(true, painter);
        painter.addToPaintList(ship);

        painter.addToPaintList(this);

    }

    public void update(float interpolation)
    {
        if (keyControls.isPressed(KeyEvent.VK_LEFT)) ship.rotate(-Math.PI/16);
        else if (keyControls.isPressed(KeyEvent.VK_RIGHT)) ship.rotate(Math.PI/16);
    }
    public void paintObject(Graphics g){}

    public static void main(String[] args)
    {
        new StarterTest();
    }


}

