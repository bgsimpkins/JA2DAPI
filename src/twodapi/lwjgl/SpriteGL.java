/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twodapi.lwjgl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.color.ColorSpace;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author ben
 */
public class SpriteGL implements PaintableGL, MouseableGL
{
    private Texture texture;

    private Vector2f originalSize;

    private Dimension size;

    private Vector2f location = new Vector2f();

    private Vector2f scale = new Vector2f(1f,1f);

    private float angle = 0f;

    /** The colour model including alpha for the GL image */
    private ColorModel glAlphaColorModel;
    
    /** The colour model for the GL image */
    private ColorModel glColorModel;

    private float transparency = 1f;

    private boolean drawBounds = false;

    private Vector2f[] boundsVerts = new Vector2f[4];
    private Vector2f[] boundsTransVerts = new Vector2f[4];

    private GLPainter painter;

    private boolean screenWrap = false;

    private Polygon collidePoly = new Polygon();

    private boolean visible = true;

    public static final int BILINEAR = GL11.GL_LINEAR;
    public static final int NEAREST_NEIGHBOR = GL11.GL_NEAREST;
    
    public SpriteGL(String imagePath)
    {
        glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                new int[] {8,8,8,8},
                true,
                false,
                ComponentColorModel.TRANSLUCENT,
                DataBuffer.TYPE_BYTE);

        glColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                new int[] {8,8,8,0},
                false,
                false,
                ComponentColorModel.OPAQUE,
                DataBuffer.TYPE_BYTE);

        URL url = this.getClass().getResource(imagePath);

        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(url);
        } catch (IOException ex)
        {
            System.err.println("SpriteGL: Couldn't load sprite image file!");
            ex.printStackTrace();
        }

        loadTexture(bufferedImage,GL11.GL_LINEAR,GL11.GL_LINEAR);

    }

    public SpriteGL(String imagePath, int minFilter, int magFilter)
    {
        glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                new int[] {8,8,8,8},
                true,
                false,
                ComponentColorModel.TRANSLUCENT,
                DataBuffer.TYPE_BYTE);

        glColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                new int[] {8,8,8,0},
                false,
                false,
                ComponentColorModel.OPAQUE,
                DataBuffer.TYPE_BYTE);

        URL url = this.getClass().getResource(imagePath);

        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(url);
        } catch (IOException ex)
        {
            System.err.println("SpriteGL: Couldn't load sprite image file!");
            ex.printStackTrace();
        }

        loadTexture(bufferedImage,minFilter,magFilter);

    }

    public SpriteGL(BufferedImage bufferedImage)
    {
        glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                new int[] {8,8,8,8},
                true,
                false,
                ComponentColorModel.TRANSLUCENT,
                DataBuffer.TYPE_BYTE);

        glColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                new int[] {8,8,8,0},
                false,
                false,
                ComponentColorModel.OPAQUE,
                DataBuffer.TYPE_BYTE);

        loadTexture(bufferedImage,GL11.GL_LINEAR,GL11.GL_LINEAR);
    }

    public SpriteGL(BufferedImage bufferedImage, int minFilter, int magFilter)
    {
        glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                new int[] {8,8,8,8},
                true,
                false,
                ComponentColorModel.TRANSLUCENT,
                DataBuffer.TYPE_BYTE);

        glColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                new int[] {8,8,8,0},
                false,
                false,
                ComponentColorModel.OPAQUE,
                DataBuffer.TYPE_BYTE);

        loadTexture(bufferedImage,minFilter,magFilter);
    }

    private void loadTexture(BufferedImage bufferedImage, int minFilter, int magFilter)
    {
        int target = GL11.GL_TEXTURE_2D; // target
        int dstPixelFormat = GL11.GL_RGBA;     // dst pixel format

        int srcPixelFormat = 0;

        // create the texture ID for this texture

        ByteBuffer temp = ByteBuffer.allocateDirect(4);
        temp.order(ByteOrder.nativeOrder());

        IntBuffer tmp = temp.asIntBuffer();

        GL11.glGenTextures(tmp);
        int textureID = tmp.get(0);
        texture = new Texture(target,textureID);
        

        // bind this texture
        GL11.glBindTexture(target, textureID);
              
        texture.setWidth(bufferedImage.getWidth());
        texture.setHeight(bufferedImage.getHeight());

        size = new Dimension(texture.getImageWidth(),texture.getImageHeight());
        originalSize = new Vector2f((float)size.getWidth(),(float)size.getHeight());
        initBounds(size.width,size.height);

        //System.out.println("Image has alpha="+bufferedImage.getColorModel().hasAlpha());

        if (bufferedImage.getColorModel().hasAlpha()) {
            srcPixelFormat = GL11.GL_RGBA;
        } else {
            srcPixelFormat = GL11.GL_RGB;
        }

        // convert that image into a byte buffer of texture data
        ByteBuffer textureBuffer = convertImageData(bufferedImage,texture);

        if (target == GL11.GL_TEXTURE_2D)
        {
            GL11.glTexParameteri(target, GL11.GL_TEXTURE_MIN_FILTER, minFilter);
            GL11.glTexParameteri(target, GL11.GL_TEXTURE_MAG_FILTER, magFilter);
        }

        // produce a texture from the byte buffer
        GL11.glTexImage2D(target,
                      0,
                      dstPixelFormat,
                      get2Fold(bufferedImage.getWidth()),
                      get2Fold(bufferedImage.getHeight()),
                      0,
                      srcPixelFormat,
                      GL11.GL_UNSIGNED_BYTE,
                      textureBuffer );
    }

    /**
     * Convert the buffered image to a texture
     *
     * @param bufferedImage The image to convert to a texture
     * @param texture The texture to store the data into
     * @return A buffer containing the data
     */
    private ByteBuffer convertImageData(BufferedImage bufferedImage,Texture texture) {
        ByteBuffer imageBuffer = null;
        WritableRaster raster;
        BufferedImage texImage;

        int texWidth = 2;
        int texHeight = 2;

        // find the closest power of 2 for the width and height
        // of the produced texture
        while (texWidth < bufferedImage.getWidth()) {
            texWidth *= 2;
        }
        while (texHeight < bufferedImage.getHeight()) {
            texHeight *= 2;
        }

        texture.setTextureHeight(texHeight);
        texture.setTextureWidth(texWidth);

        // create a raster that can be used by OpenGL as a source
        // for a texture
        if (bufferedImage.getColorModel().hasAlpha()) {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,texWidth,texHeight,4,null);
            texImage = new BufferedImage(glAlphaColorModel,raster,false,new Hashtable());
        } else {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,texWidth,texHeight,3,null);
            texImage = new BufferedImage(glColorModel,raster,false,new Hashtable());
        }

        // copy the source image into the produced image
        Graphics g = texImage.getGraphics();
        g.setColor(new Color(0f,0f,0f,0f));
        g.fillRect(0,0,texWidth,texHeight);
        g.drawImage(bufferedImage,0,0,null);

        // build a byte buffer from the temporary image
        // that be used by OpenGL to produce a texture.
        byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();

        imageBuffer = ByteBuffer.allocateDirect(data.length);
        imageBuffer.order(ByteOrder.nativeOrder());
        imageBuffer.put(data, 0, data.length);
        imageBuffer.flip();

        return imageBuffer;
    }

    /**
     * Get the closest greater power of 2 to the fold number
     *
     * @param fold The target number
     * @return The power of 2
     */
    private int get2Fold(int fold) {
        int ret = 2;
        while (ret < fold) {
            ret *= 2;
        }
        return ret;
    }


    private void initBounds(int width, int height)
    {

        boundsVerts[0] = new Vector2f(0,0);
        boundsVerts[1] = new Vector2f(width,0);
        boundsVerts[2] = new Vector2f(width,height);
        boundsVerts[3] = new Vector2f(0,height);

        boundsTransVerts[0] = new Vector2f(0,0);
        boundsTransVerts[1] = new Vector2f(width,0);
        boundsTransVerts[2] = new Vector2f(width,height);
        boundsTransVerts[3] = new Vector2f(0,height);

    }

    public void setLocation(int x, int y)
    {
        this.location.set(x,y);
    }

    public void setLocation(float x, float y)
    {
        this.location.set(x,y);
    }

    public void setLocation(Vector2f loc)
    {
        setLocation(loc.getX(),loc.getY());
    }

    public Vector2f getLocation()
    {
        return location;
    }

    public Dimension getSize()
    {
        return size;
    }

    public Vector2f getTextureSize()
    {
        return new Vector2f(texture.getWidth(),texture.getHeight());
    }

    public void scale(float xScale, float yScale)
    {
        this.scale.x = this.scale.x*xScale;
        this.scale.y = this.scale.y*yScale;
        size.width = (int)(originalSize.x*this.scale.x);
        size.height = (int)(originalSize.y*this.scale.y);
    }

    public void setScale(float xScale,float yScale)
    {
        this.scale.x = xScale;
        this.scale.y = yScale;
        size.width = (int)(originalSize.x*this.scale.x);
        size.height = (int)(originalSize.y*this.scale.y);
    }

    public void rotate(float r)
    {
        angle += r;

        //check for wraparound
        /*if (this.angle > 2*(float)Math.PI)
            this.angle = this.angle - 2*(float)Math.PI;

        else if (this.angle < 0)
            this.angle = 2*(float)Math.PI + this.angle;*/
    }

    public void setRotation(float angle)
    {
        this.angle = angle;
    }

    /**Sets the screen wrap (wraps horizontally and vertically if <code>wrap</code> is true
     *
     * @param wrap Set to true to turn on screen wrap
     * @param painter {@link GLPainter}
     */
    public void setScreenWrap(boolean wrap,GLPainter painter)
    {
        this.screenWrap = wrap;

        /*if (this.comp == null)*/ this.painter = painter;
        //System.out.println("Comp width: "+comp.getSize().width);
    }

    private void screenWrap()
    {
        Dimension pSize = painter.getSize();

        //goes off left side of screen
        if (getLocation().getX() < -getSize().width/2)
        {
            setLocation(pSize.width + getSize().width/2,getLocation().getY());
        }

        //goes off right side
        else if (getLocation().getX() - getSize().width/2 > pSize.width)
        {
            setLocation(-getSize().width/2,getLocation().getY());
        }

        //goes off top of screen
        if (getLocation().getY() < -getSize().height/2)
        {
            setLocation(getLocation().getX(),pSize.height+getSize().height/2);
        }

        //goes off bottom
        else if (getLocation().getY()- getSize().height/2 > pSize.height)
        {
            setLocation(getLocation().getX(),-getSize().height/2);
        }
    }

    public float getRotation()
    {
        return angle;
    }

    /**Moves the sprite the specified amount
     *
     * @param x Amount to move along the x dimension
     * @param y Amount to move along the y dimension
     */
    public void move(float x, float y)
    {
        this.location.set(location.getX()+x, location.getY()+y);
    }

    /**Radial move. Moves the sprite the specified distance in the specified direction.
     *
     * @param theta Angle/direction of movement (in radians)
     * @param distance Distance to move
     */
    public void moveRadial(float theta, float distance)
    {
        theta = (float)Math.toRadians(theta);

        //float x = (float)(Math.cos(theta-Math.PI/2f) * distance);
        //float y = (float)(Math.sin(theta-Math.PI/2f) * distance);
        float x = (float)(Math.cos(theta) * distance);
        float y = (float)(Math.sin(theta) * distance);
        location.set(location.x+x,location.y+y);
    }

    public void setTransparency(float t)
    {
        this.transparency = t;
    }

    public float getTransparency()
    {
        return this.transparency;
    }

    public void setVisible(boolean vis)
    {
        this.visible = vis;
    }

    public boolean isVisible()
    {
        return visible;
    }

    protected void update(float interpolation)
    {}

    public void draw(float interpolation)
    {
        
        update(interpolation);

        //System.out.println("Interp="+interpolation);

        MatrixUtils.setVerts(boundsVerts, boundsTransVerts);

        MatrixUtils.translateVerts(-originalSize.x/2, -originalSize.y/2, boundsTransVerts);
        MatrixUtils.rotateVerts(angle, boundsTransVerts);
        MatrixUtils.scaleVerts(scale.x, scale.y, boundsTransVerts);
        MatrixUtils.translateVerts(location.x, location.y, boundsTransVerts);

        updateCollisionShape();
        
        if (!visible) return;

        GL11.glPushMatrix();

        // bind to the appropriate texture for this sprite
        texture.bind();

        //System.out.println("Location="+location);
        //System.out.println("Size="+size);

        //System.out.println("Width="+size.width+ " Height="+size.height);

        //Perform translate, scale, and rotate transformations
        GL11.glTranslatef(location.x, location.y, 0f);
        GL11.glScalef(scale.x, scale.y, 1f);
        GL11.glRotatef(angle, 0, 0, 1f);

        

        //This final translation centers the sprite on the location x,y cooreds
        GL11.glTranslated(-originalSize.x/2, -originalSize.y/2, 0);
        

        if (screenWrap) screenWrap();

        //Set alpha
    	GL11.glColor4f(1,1,1,transparency);

		// draw a quad textured to match the sprite
    	GL11.glBegin(GL11.GL_QUADS);
		{
	      GL11.glTexCoord2f(0, 0);
	      GL11.glVertex2f(0, 0);
	      GL11.glTexCoord2f(0, texture.getHeight());
	      GL11.glVertex2f(0, originalSize.y);
	      GL11.glTexCoord2f(texture.getWidth(), texture.getHeight());
	      GL11.glVertex2f(originalSize.x,originalSize.y);
	      GL11.glTexCoord2f(texture.getWidth(), 0);
	      GL11.glVertex2f(originalSize.x,0);
		}
		GL11.glEnd();

		// restore the model view matrix to prevent contamination
		GL11.glPopMatrix();

        if (isDrawBounds())
        {

            //GL11.glFlush();
            GL11.glDisable(GL11.GL_TEXTURE_2D);

            //Draw bounds rectangle
            GL11.glColor4f(1f,1f,1f,1f);
            //GL11.glLineWidth(2f);
            //GL11.glRectd(boundsRect.getX(), boundsRect.getY(), boundsRect.getWidth(), boundsRect.getHeight());
            GL11.glBegin(GL11.GL_LINE_LOOP);
            {
                GL11.glVertex2f(boundsTransVerts[0].x, boundsTransVerts[0].y);
                GL11.glVertex2f(boundsTransVerts[1].x, boundsTransVerts[1].y);
                GL11.glVertex2f(boundsTransVerts[2].x, boundsTransVerts[2].y);
                GL11.glVertex2f(boundsTransVerts[3].x, boundsTransVerts [3].y);
            }
            GL11.glEnd();

            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        
    }

    private void updateCollisionShape()
    {
        collidePoly.reset();

        for (int i = 0; i < boundsTransVerts.length; i++)
        {
            Vector2f vert = boundsTransVerts[i];
            collidePoly.addPoint((int)vert.x, (int)vert.y);
        }

    }

    public boolean collidesWith(SpriteGL otherSprite)
    {
        if (!this.isVisible() || !otherSprite.isVisible()) return false;

        if (collidePoly.intersects(otherSprite.getCollisionBounds().getBounds2D()))
        {
            return true;
        }
        return false;
    }

    public Shape getCollisionBounds()
    {
        return collidePoly;
    }

    /**Returns true if the sprite is off the paint surface.
     *
     * @param painter The {@link Painter}
     * @return True if offscreen
     */
    public boolean isOffscreen(GLPainter painter)
    {
        boolean returnBool = false;

        Dimension compSize = painter.getSize();

        if ((location.x < 0) ||
                (location.x > compSize.width) ||
                (location.y < 0) ||
                (location.y > compSize.height))
        {
            returnBool = true;
        }
        return returnBool;
    }

    /**
     * @return the drawBounds
     */
    public boolean isDrawBounds() {
        return drawBounds;
    }

    /**
     * @param drawBounds the drawBounds to set
     */
    public void setDrawBounds(boolean drawBounds) {
        this.drawBounds = drawBounds;
    }

}