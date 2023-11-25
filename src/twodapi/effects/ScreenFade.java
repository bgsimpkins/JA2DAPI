/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twodapi.effects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import twodapi.core.Paintable;
import twodapi.core.GamePainter;
import twodapi.core.Painter;

/**A simple screen fade in/out effect. IMPORTANT: This should be added to the {@link Painter} a layer
 * above everything else that is being painted or the effect will not work appropriately.
 *
 * @author bsimpkins
 */
public class ScreenFade implements Paintable
{
    private Painter painter;
    //private Paintable cRenderer;
    private Color background;

    private boolean fade = false;
    private boolean faded = false;
    private boolean fadeIn = true;
    private float fadeSpeed = 1f;
    //private AlphaComposite fullAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f);
    private AlphaComposite fadeComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f);
    private float fadeLevel = 0f;
    //private int fadeTimer = 0;

    /**Creates a new instance of ScreenFade.
     *
     * @param painter The painter object
     * @param fadeColor The color of the fade layer
     */
    public ScreenFade(Painter painter, Color fadeColor)
    {
        this.painter = painter;
        this.background = fadeColor;

        /*cRenderer = new Paintable()
        {
            public void update()
            {
                updateCRenderer();
            }
            public void paintObject(Graphics g)
            {
                paintCRenderer(g);
            }
        };
        painter.addCustomRenderer(cRenderer);
        painter.setBackground(background);
        */
    }

    /**Override of the {@link Paintable} method. Do not call directly*/
    public void update(float interpolation){updateCRenderer();}

    /**Override of the {@link Paintable} method. Do not call directly*/
    public void paintObject(Graphics g){paintCRenderer(g);}

    private void updateCRenderer()
    {
        if (!fade && !faded) return;

        //if (fadeTimer <= 0)

        //{
         //   fadeTimer = 10;

            if (fadeIn)
            {
   
                fadeLevel -= fadeSpeed/100f;
                if (fadeLevel < 0f) fade = false;

            }
            else //fadeOut
            {

                fadeLevel += fadeSpeed/100f;
                if (fadeLevel > 1f) {
                    fadeLevel = 1f;
                    fade = false;
                    faded = true;
                }

            }
        //}
        //else{
        //    fadeTimer--;
        //}
    }

    private void paintCRenderer(Graphics g)
    {
        if (!fade && !faded) return;

        Graphics2D g2 = (Graphics2D)g;

        //g2.setComposite(fullAlpha);

        g2.setComposite(fadeComposite.derive(fadeLevel));
        g2.setColor(background);
        g2.fillRect(0,0,painter.getSize().width, painter.getSize().height);

    }

    /**Starts a fade in. If using a {@link GamePainter} <code>Painter</code>, the fade is performed asynchonously. So,
     * with call to <code>fadeIn()</code>, fading does not complete before the function returns.
     */
    public void fadeIn()
    {
        fadeIn = true;
        faded = false;

        fadeLevel = 1f;

        fade = true;
    }


    /**Starts a fade out. If using a {@link GamePainter} <code>Painter</code>, the fade is performed asynchonously. So,
     * with call to <code>fadeIn()</code>, fading does not complete before the function returns.
     */
    public void fadeOut()
    {
        fadeIn = false;

        fadeLevel = 0f;

        fade = true;
    }

    /**Returns true if there is currently a fade in progress.
     *
     * @return true if fading in or out.
     */
    public boolean isFading()
    {
        return fade;
    }

    /**Returns the fade color.
     * @return The current fade color (color to fade in from or to fade out to)
     */
    public Color getFadeColor()
    {
        return background;
    }

    /**Sets the fade color.
     * @param fadeColor The fade color to set (color to fade in from or to fade out to)
     */
    public void setFadeColor(Color fadeColor) {
        this.background = fadeColor;
    }

    /**Returns the fade speed.
     * @return the fadeSpeed
     */
    public float getFadeSpeed() {
        return fadeSpeed;
    }

    /**Sets the fade speed.
     * @param fadeSpeed the fadeSpeed to set
     */
    public void setFadeSpeed(float fadeSpeed) {
        this.fadeSpeed = fadeSpeed;
    }

}
