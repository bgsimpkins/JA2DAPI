/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twodapi.core;

/**This interface allows a class to receive updates from an {@link AnimationThread} object. To register
 * with the {@link AnimationThread} object, you must add your class with the <code>addListener()</code> method.
 *
 * @author ben
 */
public interface Animatable 
{
    /**This is called when the game state is to be updated, and should update the implementing class's game state
     * appropriately. Unless the CPU is getting extrememly bogged down, this is called every frame.
     */
    public void updateState(float interpolation);

    /**This is called when it is time for rendering, and should render the implementing class's objects as such.
     * If the CPU is getting bogged down, this call may be skipped for a frame, however, {@link AnimationThread}
     * limits the number of <code>render()</code> calls that can be skipped.
     */
    public void render();
}
