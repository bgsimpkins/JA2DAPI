/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package GameTest.GL;

import org.lwjgl.input.Keyboard;
import twodapi.lwjgl.*;

public class GLStarter extends GLPainter
{
    private SpriteGL ship;
    private KeyControlsGL keyControls;

    public GLStarter()
    {
        super(1024,768);
    }

    public void gameInit()
    {
        keyControls = createKeyControls();
        keyControls.set(Keyboard.KEY_LEFT);
        keyControls.set(Keyboard.KEY_RIGHT);
        keyControls.set(Keyboard.KEY_UP);

        setBackground("/images/purple-galaxy-stars.jpg");

        ship = new SpriteGL("/images/ship.gif");
        ship.setLocation(500, 330);
        ship.setScreenWrap(true, this);
        addToPaintList(ship);
    }

    public void gameUpdate(float interpolation)
    {
        if (keyControls.isPressed(Keyboard.KEY_LEFT)) ship.rotate(-500*interpolation);
        else if (keyControls.isPressed(Keyboard.KEY_RIGHT)) ship.rotate(500*interpolation);
        if (keyControls.isPressed(Keyboard.KEY_UP)) ship.moveRadial(ship.getRotation()-90, 300*interpolation);
    }

    public static void main(String[] args)
    {
        new GLStarter().start();
    }
    
}
