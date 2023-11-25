/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twodapi.lwjgl;

import java.awt.Dimension;
import java.util.ArrayList;
import twodapi.core.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author ben
 */
public class GLPainter
{
    private AnimationThread thread;
    private boolean gameRunning = true;
    private boolean readyToUpdate = true;

    private ArrayList<ArrayList<PaintableGL>> layerList;

    private long prevTime = 0l;

    private SpriteGL background = null;

    private Vector3f backgroundColor = null;

    private Dimension size;

    private int numLayers = 5;
    
    private float frTimer = 0;
    private int frN = 0;
    private int fps = 0;

    private boolean vsync = true;

    private KeyControlsGL keyControls;

    public GLPainter(int width, int height)
    {
        size = new Dimension(width,height);
        layerList = new ArrayList();
        initLayers(5);
        init(width,height);
        //thread = new AnimationThread();
        //thread.addListener(this);

        
    }

    public GLPainter()
    {
        layerList = new ArrayList();
        initLayers(5);
    }

    private void initLayers(int numLayers)
    {
        //layerList = new ArrayList();
        this.numLayers = numLayers;

        for (int i = 0; i < numLayers; i++)
        {
            layerList.add(new ArrayList());
        }
    }

     /**For the layer the specified <code>PaintableGL</code> is in, this gives the index of it's
     * position (layer within the layer).
     * @param paintable A {@link PaintableGL}.
     * @return The position of the <code>PaintableGL</code> within the layer it is currently being rendered. Return -1 if the <code>PaintableGL</code> cannot be found
     */
    public int getPositionInLayer(PaintableGL paintable)
    {
        for (int i = 0; i < layerList.size(); i++)
        {
            ArrayList<PaintableGL> layer = layerList.get(i);

            for (int j = 0; j < layer.size(); j++)
            {
                if (paintable == layer.get(j)) return j;
            }
        }

        //return -1 if paintable can't be found in layer list
        return -1;
    }

    /**Brings a {@link PaintableGL} to the front of the layer it is in (so it is renderer last/on top).
     *
     * @param paintable <code>PaintableGL</code> to bring to front.
     */
    public void bringToFront(PaintableGL paintable)
    {
        for (int i = 0; i < layerList.size(); i++)
        {
            ArrayList<PaintableGL> layer = layerList.get(i);

            for (int j = 0; j < layer.size(); j++)
            {
                if (paintable == layer.get(j))
                {
                    boolean removed = layer.remove(paintable);

                    if (removed)
                    {
                        layer.add(paintable);
                    }


                    break;
                }
            }
        }
    }

    public void addToPaintList(PaintableGL p)
    {
        if (p != null)
            layerList.get(0).add(p);
    }

     public void addToPaintList(PaintableGL paintable, int layer)
    {
        if (paintable != null)
        {
            layerList.get(layer).add(paintable);
        }
    }

      /**Removes the specified {@link PaintableGL}.
     *
     * @param paintable <code>PaintableGL</code> object
     */
    public void removeFromPaintList(PaintableGL paintable)
    {
        if (paintable != null)
        {
            for (int i = 0; i < layerList.size(); i++)
            {
                ArrayList paintList = layerList.get(i);
                if (paintList.contains(paintable))
                {
                    paintList.remove(paintable);
                }
            }
        }
    }

     public void removeAll()
    {
        for (int i = 0; i < layerList.size(); i++)
        {
            ArrayList layer = layerList.get(i);
            layer.clear();
        }
    }
    
    public void setFullscreen(boolean fs)
    {
        try {
            Display.setFullscreen(fs);
        } catch (LWJGLException ex) 
        {
            System.err.println("GLPainter: Cannot set fullscreen mode! "+ex);
        }
    }

    public void setKeyControls(KeyControlsGL kc)
    {
        this.keyControls = kc;
    }

    public KeyControlsGL getKeyControls()
    {
        return keyControls;
    }

    public KeyControlsGL createKeyControls()
    {
        this.keyControls = new KeyControlsGL(this);
        return keyControls;
    }

    public Dimension getSize()
    {
        return size;
    }


    private void init(int width, int height)
    {
        try {
            setDisplayMode(width,height);
            Display.create();
            Display.setVSyncEnabled(true);
            Display.setTitle("JA2DAPI");
            // grab the mouse, dont want that hideous cursor when we're playing!
            //Mouse.setGrabbed(true);
            // enable textures since we're going to use these for our sprites
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            //GL11.glEnable(GL11.GL_LIGHTING);

            GL11.glEnable(GL11.GL_BLEND); // Enabled blending
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // selects blending method
            GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or transperancy
            //GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function
            // disable the OpenGL depth test since we're rendering 2D graphics
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glOrtho(0, width, height, 0, -1, 1);

        } catch (LWJGLException ex) {
            Logger.getLogger(GLPainter.class.getName()).log(Level.SEVERE, null, ex);
        }
        

        //initLayers(5);

        
    }

    public void start()
    {
        gameInit();

        gameLoop();
    }

    private void gameLoop()
    {
        
        while(gameRunning)
        {
            if (readyToUpdate)
            {
                if (backgroundColor != null) GL11.glClearColor(backgroundColor.x, backgroundColor.y, backgroundColor.z, 0);

                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glLoadIdentity();

                //System.out.println("Size of paintList="+paintList.size());

                if (prevTime == 0l)
                {
                    prevTime = System.nanoTime();
                }

                float interpolation = (System.nanoTime()-prevTime)/1000000000f;
                prevTime = System.nanoTime();

                gameUpdate(interpolation);
                updateFPS(interpolation);

                if (background != null) background.draw(interpolation);

                if (keyControls != null) keyControls.update();

                
                //System.out.println("FPS="+fps);

                for (int i = 0; i < layerList.size(); i++)
                {
                    ArrayList<PaintableGL> layer = layerList.get(i);

                    for (int j = 0; j < layer.size(); j++)
                    {
                        layer.get(j).draw(interpolation);
                    }
                }
                
                
                // update window contents
                Display.update();


                //System.out.println("Render!!");

                if(Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                    Display.destroy();
                    System.exit(0);
                }
                //readyToUpdate = false;
                
            }
           
        }
    }

    private void updateFPS(float interpolation)
    {
        frTimer += interpolation;
        frN++;

        if (frN == 10)
        {
            fps = (int)(frN/frTimer);
            frTimer = 0f;
            frN = 0;
        }

    }

    public int getFPS()
    {
        return fps;
    }

    public void setVideoSyncEnabled(boolean enabled)
    {
        Display.setVSyncEnabled(enabled);
        vsync = enabled;
    }

    public boolean isVideoSyncEnabled()
    {
        return vsync;
    }

    public void setBackground(String imagePath)
    {

        SpriteGL sprite = new SpriteGL(imagePath);
        sprite.setLocation(size.width/2, size.height/2);

        float xScale = (float)size.width/(float)sprite.getSize().width;
        float yScale = (float)size.height/(float)sprite.getSize().height;
        //System.out.println("xScale="+xScale+" yScale="+yScale);
        sprite.setScale(xScale, yScale);

        this.background = sprite;
  
    }

    public void setBackgroundColor(float red, float blue, float green)
    {
        backgroundColor = new Vector3f(red,blue,green);
    }

    public Vector3f getBackgroundColor()
    {
        return backgroundColor;
    }

    /**Override to init game (IMPORTANT: Game initilization needs to be done here.
     * This is called on the OpenGL thread. Initialization done on another thread
     * will not work).
     */
    protected void gameInit(){};

    /**Override to update game state
     *
     * @param interpolation Seconds since last update
     */
    protected void gameUpdate(float interpolation){};

    /**
	 * Sets the display mode for fullscreen mode
	 */
	private void setDisplayMode(int width, int height) {

        DisplayMode[] dm = null;
        //Try to force display to preferred size
        try {
            dm = org.lwjgl.util.Display.getAvailableDisplayModes(width, height, -1, -1, -1, -1, 60, 60);

            org.lwjgl.util.Display.setDisplayMode(dm, new String[] {
					"width=" + width,
					"height=" + height,
					"freq=" + 60,
					"bpp="+ org.lwjgl.opengl.Display.getDisplayMode()
									.getBitsPerPixel() });
        } catch (Exception ex) {
        try {
             System.out.println("Specified display size doesn't match available sizes. Previous size="+width+
                            "X"+height+" New size=1024X768");

             dm = Display.getAvailableDisplayModes();

            //If preffered isn't supported, default to 1024X768
            for (int i = 0; i < dm.length; i++)
            {
                if (dm[i].getWidth()==1024 && dm[i].getHeight()==768) Display.setDisplayMode(dm[i]);

            }
            
                /*
                //If preferred size isn't supported, choose next smallest
                
                DisplayMode[] dms = Display.getAvailableDisplayModes();
                boolean matchHeight = false;
                boolean matchWidth = false;
                for (int i = 0; i < dms.length; i++)
                {
                    int w = dms[i].getWidth();
                    int h = dms[i].getHeight();
                    if (width==w) matchWidth = true;
                }
                if (!matchWidth || !matchHeight)
                {

                    DisplayMode bestDM = dms[0];
                    int diff = Math.abs(bestDM.getWidth()-width);

                    for (int j = 0; j < dms.length; j++)
                    {
                        if (Math.abs(dms[j].getWidth()-width) < diff && dms[j].getWidth()-width < 0)
                        {
                            bestDM = dms[j];
                            diff = Math.abs(dms[j].getWidth()-width);
                        }
                    }

                    Display.setDisplayMode(bestDM);
                    System.out.println("Specified display size doesn't match available sizes. Previous size="+width+
                            "X"+height+" New size="+bestDM.getWidth()+"X"+bestDM.getHeight());
                    size= new Dimension(width,height);
                }
                */

            } catch (Exception ex2) {
                System.err.println("GLPainter.setDisplayMode(): Exception while setting display mode!");
                ex2.printStackTrace();
            }
        }



        

	}

    public static void main(String[] args)
    {

        GLPainter painter = new GLPainter(800,600)
        {
            @Override
            public void gameInit()
            {
                setBackground("/images/purple-galaxy-stars.jpg");

                SpriteGL sprite = new SpriteGL("/images/Asteroid.gif")
                {
                    public void update(float interpolation)
                    {
                        float toScale = 1+(interpolation/6);
                        scale(toScale,toScale);
                        moveRadial(45,interpolation*100);
                        rotate((float)(interpolation*180));
                    }
                };
                sprite.setDrawBounds(true);
                sprite.setTransparency(.9f);
                sprite.setLocation(110, 110);
                //sprite.setScale(2);
                addToPaintList(sprite,0);

                SpriteGL ship = new SpriteGL("/images/ship.gif");
                ship.setLocation(400,300);
                addToPaintList(ship,0);
                bringToFront(sprite);
            }

        };
        painter.start();
        //painter.init(800, 600);
        
        

        //painter.start();



    }
}
