/*
 * Fisheyeable.java
 *
 * Created on September 11, 2008, 11:54 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package twodapi.effects;
import java.awt.Point;
import java.awt.Dimension;

/**When implemented, the interface allows a display object to have Fisheye display capability.
 *
 * @author Ben
 */
public interface Fisheyeable 
{
    /**Sets the position of the Fisheyable object. IMPORTANT: This position is the center of the
     *object.
     *@param pos The desired center position of the object*/
    public void setPositionFisheye(Point pos);
    
    /**Returns the center position of the Fisheyeable object
     *@return The center position of the object*/
    public Point getPositionFisheye();
    
    /**Sets the size of the Fisheyeable object
     *@param size The desired size of the object.*/
    public void setSizeFisheye(Dimension size);
    
    /**Returns the size of the Fisheyeable object
     *@return The size of the object*/
    public Dimension getSizeFisheye();    
    
}
