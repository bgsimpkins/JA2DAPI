/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twodapi.core;

/**An internal class that tracks registered key states.
 *
 * @author bsimpkins
 */
public class KeyState
{

    private boolean pressed;
    private boolean tapped;

    public KeyState(boolean pressed, boolean tapped)
    {
        this.pressed = pressed;
        this.tapped = tapped;
    }

    /**
     * @return the pressed
     */
    public boolean isPressed() {
        return pressed;
    }

    /**
     * @param pressed the pressed to set
     */
    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    /**
     * @return the tapped
     */
    public boolean isTapped() {
        return tapped;
    }

    /**
     * @param tapped the tapped to set
     */
    public void setTapped(boolean tapped) {
        this.tapped = tapped;
    }


}
