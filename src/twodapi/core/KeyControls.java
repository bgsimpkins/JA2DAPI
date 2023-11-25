/*
 * KeyControls.java
 *
 * Created on July 18, 2009, 8:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package twodapi.core;

import javax.swing.JComponent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

/**This class essentially wraps handling of <code>java.awt.event</code> key events. With the game framework used
 * in this API, only two methods (<code>isPressed()</code> and <code>isTapped()</code>) are currently needed to
 * check for all needed key states. For optimization, this class does track key states, so all keys of interest
 * must be registered with <code>set()</code>.
 *
 * @author bsimpkins
 */
public class KeyControls
{
    private HashMap<Integer,KeyState> keyMap = new HashMap();
    //private Iterator iter;
    
    /** Creates a new instance of KeyControls */
    public KeyControls(JComponent comp) 
    {
        comp.requestFocus();
        comp.setFocusable(true);
        comp.addKeyListener(new ControlListener());
    }
    
    /**Adds a key to the listener. Use <code>KeyEvent</code> key codes.
     *@param key <code>KeyEvent</code> key code of the key to be added
     *@param value <code>true</code> if key is pressed. <code>false</code> if not pressed.
     */
    public void set(int key, boolean value)
    {
        keyMap.put(key,new KeyState(value,value));
    

    }

    /**Adds a key to the listener. Use <code>KeyEvent</code> key codes. Sets value to <code>false</code> by default.
     *@param key <code>KeyEvent</code> key code of the key to be added
     */
    public void set(int key)
    {
        keyMap.put(key,new KeyState(false,false));
    }

    /**Returns true if a particular key is current pressed (held down). NOTE: Key must be registered with the <code>set()</code>
     * method, or it won't be processed.
     * @param key <code>KeyEvent</code> key code of the key being checked
     * @return True if the key is pressed
     */
    public boolean isPressed(int key)
    {
        if (keyMap != null && keyMap.get(key) != null)
            return keyMap.get(key).isPressed();
        else return false;
    }

    /**Returns true if a key has been pressed and NOT checked with this method since the key was pressed. This is essentially an exhaustive version of
     * <code>isPressed()</code> most useful in cases where a single key press should only fire one event. Unless a really low
     * frame rate is being used, <code>isPressed</code> will return true several times over the course of even a quick key press.
     * @param key <code>KeyEvent</code> key code of the key being checked
     * @return True if the key has been tapped
     */
    public boolean isTapped(int key)
    {

        if (keyMap != null && keyMap.get(key) != null)
        {
            if (keyMap.get(key).isTapped())
            {
                keyMap.get(key).setTapped(false);
                return (true);
            }
            else return false;
        }
        else return false;
    }

    /**Resets all key states*/
    public void clearAllKeys()
    {
        Iterator iter = keyMap.entrySet().iterator();
        while(iter.hasNext())
        {
            KeyState state = (KeyState)((Map.Entry)(iter.next())).getValue();
            state.setPressed(false);
            state.setTapped(false);
        }
    }

    private void processKeyEvent(KeyEvent e, boolean pressed)
    {
        int keyCode = e.getKeyCode();
        
        if (keyMap.containsKey(keyCode))
        {        
            if (keyMap.get(keyCode).isPressed() != pressed)
            {
                keyMap.get(keyCode).setPressed(pressed);
            }
        }

        if (keyMap.containsKey(keyCode))
        {
            if (pressed && !keyMap.get(keyCode).isTapped())
            {
                keyMap.get(keyCode).setTapped(true);
            }
        }
    }

    /**Override to receive key pressed events from AWT thread*/
    protected void keyPressedEvent(KeyEvent e){}

    /**Override to receive key released events from AWT thread*/
    protected void keyReleasedEvent(KeyEvent e){}
    
    class ControlListener implements KeyListener
    {              
        public void keyPressed(KeyEvent e)
        {
            processKeyEvent(e, true);
            keyPressedEvent(e);
        }

        public void keyReleased(KeyEvent e)
        {
            processKeyEvent(e, false);
            keyReleasedEvent(e);
        }

        public void keyTyped(KeyEvent e)
        {
            //Do nothing
        }
    }
    
}
