/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twodapi.lwjgl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Hashtable;
import javax.swing.SwingUtilities;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

/**
 *
        throw new UnsupportedOperationException("Not supported yet.");

 * @author ben
 */
public class TextGL implements PaintableGL,MouseableGL
{
    private String text = "";
    private Font font;
    private Color color;
    private FontMetrics fm;

    private Texture texture;

    private static BufferedImage tempImage;

    /** The colour model including alpha for the GL image */
    private static ColorModel glAlphaColorModel;

    private Dimension size = new Dimension();

    private float angle = 0f;

    private boolean visible = true;

    private Vector2f location = new Vector2f();

    private ByteBuffer imageBuffer;

    private float alpha = 1f;

    private boolean drawBounds = false;

    private Vector2f[] boundsVerts = new Vector2f[4];
    private Vector2f[] boundsTransVerts = new Vector2f[4];

    private boolean centerText = false;

    public TextGL(String text, Font font, Color color)
    {
        this.text = text;
        this.font = font;
        this.color = color;

        glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                new int[] {8,8,8,8},
                true,
                false,
                ComponentColorModel.TRANSLUCENT,
                DataBuffer.TYPE_BYTE);

        init();
    }

    private void init()
    {
        int target = GL11.GL_TEXTURE_2D; // target
        int dstPixelFormat = GL11.GL_RGBA;     // dst pixel format
        int minFilter = GL11.GL_LINEAR; // minification filter
        int magFilter = GL11.GL_LINEAR;  //magnification filter

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

        //Use temp image to set font and get font metrics
        tempImage = new BufferedImage(1,1,BufferedImage.TRANSLUCENT);
        Graphics g = tempImage.getGraphics();
        g.setFont(font);
        fm = g.getFontMetrics();

        int texWidth = SwingUtilities.computeStringWidth(fm, text);
        int texHeight = fm.getHeight();
        int texAsc = fm.getAscent();

        getSize().setSize(texWidth,texHeight);
        initBounds(texWidth,texHeight);

        texture.setWidth(texWidth);
        texture.setHeight(texHeight);

        WritableRaster raster;
        BufferedImage texImage;

        srcPixelFormat = GL11.GL_RGBA;
        
        int width = get2Fold(texWidth);
        int height = get2Fold(texHeight);       

        texture.setTextureHeight(height);
        texture.setTextureWidth(width);

        raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,width,height,4,null);
        texImage = new BufferedImage(glAlphaColorModel,raster,false,new Hashtable());

        g = texImage.getGraphics();
        g.setColor(new Color(0f,0f,0f,0f));
        g.fillRect(0,0,width,height);
        g.setColor(color);
        g.setFont(font);
        g.drawString(text, 0,texAsc);

        byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();
       
        imageBuffer =  ByteBuffer.allocateDirect(data.length);
        imageBuffer.order(ByteOrder.nativeOrder());
        imageBuffer.put(data, 0, data.length);
        imageBuffer.flip();

        if (target == GL11.GL_TEXTURE_2D)
        {
            GL11.glTexParameteri(target, GL11.GL_TEXTURE_MIN_FILTER, minFilter);
            GL11.glTexParameteri(target, GL11.GL_TEXTURE_MAG_FILTER, magFilter);
        }

        // produce a texture from the byte buffer
        GL11.glTexImage2D(target,
                      0,
                      dstPixelFormat,
                      get2Fold(texImage.getWidth()),
                      get2Fold(texImage.getHeight()),
                      0,
                      srcPixelFormat,
                      GL11.GL_UNSIGNED_BYTE,
                      imageBuffer );

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

    public void draw(float interpolation) 
    {
        if (!isVisible()) return;

        GL11.glPushMatrix();

        GL11.glTranslatef(location.x, location.y, 0);   
        GL11.glRotatef(angle, 0, 0, 1f);

        if (centerText) GL11.glTranslatef(-size.width/2, -size.height/2, 0);
         
        // bind to the appropriate texture for this sprite
        texture.bind();

        GL11.glColor4f(1,1,1,alpha);
        
         // draw a quad textured to match the sprite
    	GL11.glBegin(GL11.GL_QUADS);
		{
            GL11.glTexCoord2f(0, 0);
            GL11.glVertex2f(0, 0);
            GL11.glTexCoord2f(0, texture.getHeight());
            GL11.glVertex2f(0, getSize().height);
            GL11.glTexCoord2f(texture.getWidth(), texture.getHeight());
            GL11.glVertex2f(getSize().width,getSize().height);
            GL11.glTexCoord2f(texture.getWidth(), 0);
            GL11.glVertex2f(getSize().width,0);
		}
		GL11.glEnd();

        MatrixUtils.setVerts(boundsVerts, boundsTransVerts);

        //MatrixUtils.translateVerts(-size.width/2, -size.height/2, boundsTransVerts);
        if (centerText) MatrixUtils.translateVerts(-size.width/2, -size.height/2, boundsTransVerts);
        MatrixUtils.rotateVerts(angle, boundsTransVerts);
        MatrixUtils.translateVerts(location.x, location.y, boundsTransVerts);
        

        // restore the model view matrix to prevent contamination
		 GL11.glPopMatrix();

        if (drawBounds())
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

    public void setText(String text)
    {
        this.text = text;
        init();
    }

    public String getText()
    {
        return text;
    }

    public void setFont(Font font)
    {
        this.font = font;
        init();

    }

    public void setFont(String name, int style, int size)
    {
        font = new Font(name,style,size);
        init();
    }


    public Font getFont()
    {
        return font;
    }

    public void setColor(Color color)
    {
        this.color = color;
        init();
    }

    public void setColor(float red, float green, float blue)
    {
        this.color = new Color(red,green,blue);
        init();
    }

    public void setTransparency(float alpha)
    {
        this.alpha = alpha;
    }

    public float getTransparency()
    {
        return alpha;
    }

    /**
     * @return the location
     */
    public Vector2f getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(Vector2f location) {
        this.location = location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(float x, float y) {
        this.location.set(x, y);
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

    public float getRotation()
    {
        return angle;
    }

    public static void main(String[] args)
    {
        GLPainter test = new GLPainter(1024,768)
        {
            TextGL text;
            public void gameInit()
            {
                 this.setBackground("/images/purple-galaxy-stars.jpg");
                 text = new TextGL("Some text and stupf",new Font("serif",Font.PLAIN,50),Color.RED);
                 text.setCenterText(true);
                 //text.setDrawBounds(true);
                 text.setLocation(100,100);
                 text.setTransparency(.8f);
                 addToPaintList(text);
                 System.out.println("Size="+text.getSize());

            }
            public void gameUpdate(float interpolation)
            {
                text.setLocation(text.getLocation().x+1,text.getLocation().y+1);
                text.rotate(2f);

                if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && !text.getText().equals("More text")) text.setText("More text");
            }

        };
        test.start();
        
    }

    /**
     * @return the drawBounds
     */
    public boolean drawBounds() {
        return drawBounds;
    }

    /**
     * @param drawBounds the drawBounds to set
     */
    public void setDrawBounds(boolean drawBounds) {
        this.drawBounds = drawBounds;
    }

    /**
     * @return the centerText
     */
    public boolean isCenteringText() {
        return centerText;
    }

    /**
     * @param centerText the centerText to set
     */
    public void setCenterText(boolean centerText) {
        this.centerText = centerText;
    }

    /**
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible the visible to set
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * @return the size
     */
    public Dimension getSize() {
        return size;
    }
}
