/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twodapi.scroller;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import twodapi.core.Frame2D;
import twodapi.core.KeyControls;
import twodapi.core.Paintable;
import twodapi.core.GamePainter;
import twodapi.core.Painter;

/**A simple background layer for a side scroller that efficiently wraps an image horizontally around the screen
 * and allows it to be scrolled left and right.
 *
 * @author bsimpkins
 */
public class BackgroundLayer implements Paintable
{
    private BufferedImage image;

    private float xScroll = 0;

    private float toScrollX = 0;

    private float yScroll = 0;

    private float toScrollY = 0;

    private boolean wrapX = true;

    private boolean wrapY = true;

    //private JComponent comp;
    private Painter painter;

    /**For diagnostics. The number of images drawn at any given time*/
    protected int imageNum = 0;

    /**Creates a new background layer.
     *
     * @param file The filename of the image to be the background.
     * @param comp The component on which the background will be painted.
     */
    public BackgroundLayer(String file, Painter painter)
    {
        this.painter = painter;

        try {
            image = ImageIO.read(this.getClass().getResource(file));
        } catch (IOException ex) 
        {
            System.err.println("BackgroundLayer: Could not load background image "+file);
        }
    }

    /**Scrolls the background horizontally the specified number of pixels
     *
     * @param toScroll The number of pixels to scroll
     */
    public void scrollX(float toScroll)
    {
        this.toScrollX = toScroll;
    }

    /**Scrolls the background vertically the specified number of pixels
     *
     * @param toScroll The number of pixels to scroll
     */
    public void scrollY(float toScroll)
    {
        this.toScrollY = toScroll;
    }

    /**Sets the vertical scroll of the background image.
     *
     * @param scroll Vertical scroll
     */
    public void setYScroll(float scroll)
    {
        this.yScroll = scroll;
    }

    /**Returns the vertical scroll of the background image..
     *
     * @return Vertical scroll
     */
    public float getYScroll()
    {
        return yScroll;
    }

    public void setWrap(boolean wrapX, boolean wrapY)
    {
        this.wrapX = wrapX;
        this.wrapY = wrapY;
    }

    /**Override of the {@link Paintable} method. Do not call directly*/
    public void update(float interpolation)
    {
        if (toScrollX != 0)
        {
            setXScroll(getXScroll() + toScrollX);
            toScrollX = 0;
        }

        if (toScrollY != 0)
        {
            setYScroll(getYScroll() + toScrollY);
            toScrollY = 0;
        }

        //Wrap scroll location. Works in correspondence with image wrap optimization in <code>paintObject()</code>.

        if (wrapX)
        {
            if (getXScroll() >= image.getWidth()) setXScroll(getXScroll() - image.getWidth());
            else if (getXScroll() <= -image.getWidth()) setXScroll(getXScroll() + image.getWidth());
        }
        if (wrapY)
        {
            if (getYScroll() >= image.getHeight()) setYScroll(getYScroll() - image.getHeight());
            else if (getYScroll() <= -image.getHeight()) setYScroll(getYScroll() + image.getHeight());
        }

        

    }

    /**Override of the {@link Paintable} method. Do not call directly*/
    public void paintObject(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        //g2.drawImage(image, (int)Math.round(-scroll),yLoc, null);

        imageNum = 0;

        //Wrap image when needed.
        //Attempt to optimize: Only wrap to cover screen area
        //TODO: This wrap optimization could use work. This is probably not optimal (See KillerGaveProgrammingforJava ch 12 for another idea).
        int x = (int)Math.round(-getXScroll());
        int y = (int)Math.round(-getYScroll());

        for (; y < painter.getSize().height; y += image.getHeight())
        {

            //start at scroll point, work right and repeat image until off screen
            for (; x < painter.getSize().width; x += image.getWidth())
            {
                g2.drawImage(image, x,y, null);
                imageNum++;
                if (!wrapX) break;
            }


            if (!wrapX && !wrapY) break;
            else if (!wrapX) continue;

            //start at scroll point, work left and repeat image until off screen
            x = (int)Math.round(-getXScroll())- image.getWidth();    //Don't double previously drawn one
            for (; x > -image.getWidth(); x -= image.getWidth())
            {
                g2.drawImage(image, x,y, null);
                imageNum++;
            }

            if (!wrapY) break;
        }

        if (!wrapY) return;

        y = (int)Math.round(-getYScroll()) - image.getHeight();


        for (; y > -image.getHeight(); y -= image.getHeight())
        {
            //start at scroll point, work right and repeat image until off screen
            for (; x < painter.getSize().width; x += image.getWidth())
            {
                g2.drawImage(image, (int)Math.round(x),y, null);
                imageNum++;
                if (!wrapX) break;
            }

            if (!wrapX) continue;
            //start at scroll point, work left and repeat image until off screen
            x = (int)Math.round(-getXScroll())- image.getWidth();//Don't double previously drawn one
            for (; x > -image.getWidth(); x -= image.getWidth())
            {
                g2.drawImage(image, (int)Math.round(x),y, null);
                imageNum++;
            }
        }

    }

    public static void main(String[] args)
    {
        Frame2D frame = new Frame2D(Frame2D.REG_PAINTER,800,600,false);

        GamePainter painter = (GamePainter)frame.getPainter();
        painter.setFrameRate(30);

        final KeyControls control = new KeyControls(painter.getComponent());
        control.set(KeyEvent.VK_RIGHT);
        control.set(KeyEvent.VK_LEFT);
        control.set(KeyEvent.VK_UP);
        control.set(KeyEvent.VK_DOWN);

        BackgroundLayer background = new BackgroundLayer("/images/cavetile2-sm.jpg",painter)
        {
            @Override
            public void update(float interpolation)
            {

                if (control.isPressed(KeyEvent.VK_RIGHT))
                {
                    this.scrollX(6);
                }
                else if (control.isPressed(KeyEvent.VK_LEFT))
                {
                    this.scrollX(-6);
                }
                if (control.isPressed(KeyEvent.VK_UP))
                {
                    this.scrollY(-3f);
                }
                else if (control.isPressed(KeyEvent.VK_DOWN))
                {
                    this.scrollY(3f);
                }

                super.update(0);
            }

            @Override
            public void paintObject(Graphics g)
            {
                super.paintObject(g);

                g.setColor(Color.WHITE);
                g.drawString("ImageNum="+this.imageNum,10,40);
            }
        };

        background.setYScroll(-50);
        background.setWrap(false, false);

        painter.addToPaintList(background);

    }

    /**Gets the scroll value
     * @return the scroll
     */
    public float getXScroll() {
        return xScroll;
    }

    /**Sets the scroll value
     * @param scroll the scroll to set
     */
    public void setXScroll(float scroll) {
        this.xScroll = scroll;
    }

}
