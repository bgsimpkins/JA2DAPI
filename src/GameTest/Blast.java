/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package GameTest;

import twodapi.core.*;

import javax.swing.JComponent;

import java.awt.geom.Line2D;
import java.awt.Color;

/**
 *
 * @author bsimpkins
 */
public class Blast extends Sprite2D
{
    double theta = 0;

    int length = 0;

    private Painter painter;

    public Blast(int length,Painter painter)
    {
        super(new Line2D.Double(0,0,0,0),Color.WHITE,10);

        this.painter = painter;
        this.length = length;
    }

    public void setTheta(double theta)
    {
        this.theta = theta;

        //Position setting was done before this. Need to force update.
        update(0);
    }

    @Override
    public void updateState(float interpolation)
    {
        if (isVisible() && isOffscreen(painter)) setVisible(false);

        if (isVisible())
        {
            move(theta, length);
        }
    }
}
