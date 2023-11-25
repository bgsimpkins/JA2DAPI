/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twodapi.lwjgl;

import java.awt.Dimension;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author ben
 */
public interface MouseableGL
{
    public static final int LEFT_BUTTON = 0;
    public static final int CENTER_BUTTON = 2;
    public static final int RIGHT_BUTTON = 1;

    public Vector2f getLocation();
    public Dimension getSize();
}
