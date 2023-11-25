/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twodapi.lwjgl;

import org.lwjgl.input.Keyboard;

/**
 *
 * @author ben
 */
public class BackgroundLayerGL implements PaintableGL
{
    private float xScroll = 0;

    private float toScrollX = 0;

    private float yScroll = 0;

    private float toScrollY = 0;

    private boolean wrapX = true;

    private boolean wrapY = true;

    private SpriteGL tileSprite;

    protected int imageNum = 0;
    
    private GLPainter painter;

    /**Creates a new instance of BackgroundLayerGL. MUST BE CALLED FROM OPENGL THREAD (e.g., GLPainter.initGame())>
     *
     * @param file The file path to the background image
     * @param screenSize
     */
    public BackgroundLayerGL(String file,GLPainter painter)
    {

        this.painter = painter;

        tileSprite = new SpriteGL(file);
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

    public void updateScroll()
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
            if (getXScroll() >= tileSprite.getSize().width) setXScroll(getXScroll() - tileSprite.getSize().width);
            else if (getXScroll() <= -tileSprite.getSize().width) setXScroll(getXScroll() + tileSprite.getSize().width);
        }
        if (wrapY)
        {
            if (getYScroll() >= tileSprite.getSize().height) setYScroll(getYScroll() - tileSprite.getSize().height);
            else if (getYScroll() <= -tileSprite.getSize().height) setYScroll(getYScroll() + tileSprite.getSize().height);
        }



    }

    public void draw(float interpolation) 
    {
        imageNum = 0;

        updateScroll();

        //Wrap image when needed.
        //Attempt to optimize: Only wrap to cover screen area
        //TODO: This wrap optimization could use work. This is probably not optimal (See KillerGaveProgrammingforJava ch 12 for another idea).
        int x = (int)Math.round(-getXScroll());
        int y = (int)Math.round(-getYScroll());

        for (; y < painter.getSize().height; y += tileSprite.getSize().getHeight())
        {

            //start at scroll point, work right and repeat image until off screen
            for (; x < painter.getSize().width; x += tileSprite.getSize().getWidth())
            {
                tileSprite.setLocation(x+tileSprite.getSize().width/2, y+tileSprite.getSize().height/2);
                tileSprite.draw(interpolation);
                imageNum++;
                if (!wrapX) break;
            }


            if (!wrapX && !wrapY) break;
            else if (!wrapX) continue;

            //start at scroll point, work left and repeat image until off screen
            x = (int)Math.round(-getXScroll())- tileSprite.getSize().width;    //Don't double previously drawn one
            for (; x > -tileSprite.getSize().getWidth(); x -= tileSprite.getSize().getWidth())
            {
                tileSprite.setLocation(x+tileSprite.getSize().width/2, y+tileSprite.getSize().height/2);
                tileSprite.draw(interpolation);
                imageNum++;
            }

            if (!wrapY) break;
        }

        if (!wrapY) return;

        y = (int)Math.round(-getYScroll()) - tileSprite.getSize().height;


        for (; y > -tileSprite.getSize().getHeight(); y -= tileSprite.getSize().getHeight())
        {
            //start at scroll point, work right and repeat image until off screen
            for (; x < painter.getSize().width; x += tileSprite.getSize().getWidth())
            {
                tileSprite.setLocation(x+tileSprite.getSize().width/2, y+tileSprite.getSize().height/2);
                tileSprite.draw(interpolation);
                imageNum++;
                if (!wrapX) break;
            }

            if (!wrapX) continue;
            //start at scroll point, work left and repeat image until off screen
            x = (int)Math.round(-getXScroll())- tileSprite.getSize().width;//Don't double previously drawn one
            for (; x > -tileSprite.getSize().getWidth(); x -= tileSprite.getSize().getWidth())
            {
                tileSprite.setLocation(x+tileSprite.getSize().width/2, y+tileSprite.getSize().height/2);
                tileSprite.draw(interpolation);
                imageNum++;
            }
        }

    }

    

    public static void main(String[] args)
    {
        new TestLayer();
    }

}

class TestLayer extends GLPainter
    {
        BackgroundLayerGL layer;

        int scrollAmt = 20;

        public TestLayer()
        {
            super(1024,768);
            start();

        }

        @Override
        public void gameInit()
        {
            layer = new BackgroundLayerGL("/images/stone03.jpg",this);
            layer.setWrap(true, true);
            this.addToPaintList(layer);
        }

        @Override
        public void gameUpdate(float interpolation)
        {
            if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
            {
                layer.scrollX(interpolation*700);
            }
            else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
            {
                layer.scrollX(-interpolation*700);
            }

            if (Keyboard.isKeyDown(Keyboard.KEY_UP))
            {
                layer.scrollY(interpolation*700);
            }
            else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
            {
                layer.scrollY(-interpolation*700);
            }
        }
    }
