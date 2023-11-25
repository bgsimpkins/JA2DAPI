/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package GameTest.GL;

import java.awt.Point;
import org.lwjgl.input.Keyboard;
import twodapi.lwjgl.CollisionMapScrollerGL;
import twodapi.lwjgl.FlipBookSpriteGL;
import twodapi.lwjgl.GLGame;
import twodapi.lwjgl.GLPainter;

/**
 *
 * @author ben
 */
public class CollisionMapTest extends GLGame
{
    private CollisionMapScrollerGL scroller;
    private FlipBookSpriteGL ship;
    private GLPainter painter;

    String collisionMap, image;

    public CollisionMapTest(String collisionMap, String image)
    {
        painter = createPainter(1000,700);
        this.collisionMap = collisionMap;
        this.image = image;
        start();
    }

    @Override
    public void initGame()
    {
        scroller = new CollisionMapScrollerGL(collisionMap,image,painter);
        scroller.addLayer("/images/stars/stars2b.gif",new Point(0,0),true,true);
        scroller.setParallax(.2f);
        scroller.setSubImageSize(500, 700);
        scroller.setScrollStops(false, false);
        painter.addToPaintList(scroller);
        
        ship = new FlipBookSpriteGL(new String[]{"/images/2dShip1.png","/images/2dShip_down.png","/images/2dShip_up.png"});
        ship.setLocation(500, 300);
        ship.setScale(.15f, .15f);

        painter.addToPaintList(ship);
    }

    public void update(float interpolation)
    {
        scroller.scroll(150*interpolation, 0);

        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
        {
            ship.move(300*interpolation,0);
        }
        else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
        {
            ship.move(-300*interpolation,0);
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_UP))
        {
            ship.setFrameIndex(2);
            ship.move(0, -150*interpolation);
        }
        else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
        {
            ship.setFrameIndex(1);
            ship.move(0, 150*interpolation);
        }
        else  ship.setFrameIndex(0);


        if (scroller.collidesWith(ship.getCollisionBounds()))
        {
            ship.setDrawBounds(true);
        }
        else ship.setDrawBounds(false);
        
    }

    public static void main(String[] args)
    {
        new CollisionMapTest(args[0],args[1]);
    }
}
