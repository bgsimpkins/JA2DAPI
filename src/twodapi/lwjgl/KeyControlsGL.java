/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twodapi.lwjgl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.lwjgl.input.Keyboard;
import twodapi.core.KeyState;

/**
 *
 * @author ben
 */
public class KeyControlsGL
{
    private HashMap<Integer,KeyState> keyMap = new HashMap();

    public KeyControlsGL(GLPainter painter)

    {
        boolean created = checkKeyboardCreated();
        if (created) painter.setKeyControls(this);
    }

    private boolean checkKeyboardCreated()
    {
        boolean created = Keyboard.isCreated();

        if (!created) System.err.println("KeyControlsGL: No keyboard system created. Make sure GLPainter " +
                "is created before creating KeyControlsGL and that KeyControlsGL is created on the OpenGL thread");

        return created;
    }

     /**Adds a key to the listener. Use <code>KeyEvent</code> key codes.
     *@param key <code>KeyEvent</code> key code of the key to be added
     *@param value <code>true</code> if key is pressed. <code>false</code> if not pressed.
     */
    public void set(int key, boolean value)
    {
        checkKeyboardCreated();
        keyMap.put(key,new KeyState(value,value));

    }

     /**Adds a key to the listener. Use <code>KeyEvent</code> key codes. Sets value to <code>false</code> by default.
     *@param key <code>KeyEvent</code> key code of the key to be added
     */
    public void set(int key)
    {
        checkKeyboardCreated();
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
        /*if (keyMap != null && keyMap.get(key) != null)
        {
            if (keyMap.get(key).isTapped())
            {
                return true;
            }
        }

        return false;*/

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

    private void processKeyEvent(int keyCode, boolean pressed)
    {

        if (keyMap.containsKey(keyCode))
        {
            if (pressed && !keyMap.get(keyCode).isPressed() && !keyMap.get(keyCode).isTapped())
            {
                keyMap.get(keyCode).setTapped(true);
            }

            if (keyMap.get(keyCode).isPressed() != pressed)
            {
                keyMap.get(keyCode).setPressed(pressed);
            }

            
        }
    }
    public void update()
    {
        Iterator iter = keyMap.entrySet().iterator();
        
        while(iter.hasNext())
        {
            int keyCode = (Integer)((Map.Entry)(iter.next())).getKey();
            boolean pressed = Keyboard.isKeyDown(keyCode);
//            System.out.print("|"+keyCode);
//            if (pressed)
//            {
//                System.out.print("|pressed="+keyCode);
//            }
            processKeyEvent(keyCode,pressed);
        }
//        System.out.println("_________");
    }

    public static void main(String[] args)
    {
        GLPainter painter = new GLPainter(400,300)
        {
            @Override
            public void gameInit()
            {
                KeyControlsGL kc = createKeyControls();
                kc.set(Keyboard.KEY_SPACE);
            }

            @Override
            public void gameUpdate(float interpolation)
            {
                if (getKeyControls().isTapped(Keyboard.KEY_SPACE))
                {
                    System.out.println("tapped!!");
                }

                if (getKeyControls().isPressed(Keyboard.KEY_SPACE))
                {
                    System.out.println("pressed");
                }
            }
        };
        painter.start();

    }
}
