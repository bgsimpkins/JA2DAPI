/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twodapi.scroller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import twodapi.core.Frame2D;
import twodapi.core.GamePainter;
import twodapi.core.KeyControls;
import twodapi.core.Paintable;
import twodapi.core.Painter;
import twodapi.core.Sprite2D;

/**
 *
 * @author bsimpkins
 */
public class GeneralScrollManager implements Paintable
{
    private Painter painter;

    private Dimension tileSize = null;

    private char[][] indices;

    private HashMap<Character,BufferedImage> tiles = new HashMap();

    private int numLines = 0;

    private int lineWidth = 0;

    private Rectangle tileBound = new Rectangle();

    private float xScroll = 0f;
    private float yScroll = 0f;

    private float toScrollX = 0f;
    private float toScrollY = 0f;

    private boolean scrollingX = false;
    private boolean scrollingY = false;

    /**If true, scroll will automatically stop if scroll window reaches beginning or end of tiles*/
    private boolean scrollStopX = false;
    private boolean scrollStopY = false;

    protected Point start = new Point();
    protected Point end = new Point();


    //Used for repeated calcs
    private final Point convertPoint = new Point();
    private final Point boundOrigin = new Point();
    private final Point tileBoundPoint = new Point();

    private float parallax = 1f;

    private ArrayList<BackgroundLayer> background = new ArrayList();

    public GeneralScrollManager(String inputFile, String[] imagePaths, Painter painter)
    {
        this.painter = painter;

        initialize(inputFile, imagePaths);

    }




    private void initialize(String file,String[] imagePaths)
    {
        if (!file.startsWith("/")) file = "/"+file;

        InputStream input = this.getClass().getResourceAsStream(file);

        BufferedReader bufRead = new BufferedReader(new InputStreamReader(input));

        String line = null;
        numLines = 0;
        //Width of widest line
        lineWidth = 0;
        ArrayList<String> lineList = new ArrayList();
        try {
            //Read first line
            line = bufRead.readLine();


            //read in lines
            while(line != null)
            {
                //lineEmpty(line);
                if (line.length() > lineWidth) lineWidth = line.length();
                lineList.add(line);

                line = bufRead.readLine();
                numLines++;
            }

            bufRead.close();
        } catch (IOException ex)
        {
            System.err.println("GeneralScrollManager: Could not read input file!! Exiting!");
            ex.printStackTrace();
            System.exit(1);
        }

        //put data in char array for easy processing

        System.out.println("Lines="+numLines+ " Width="+lineWidth);

        indices = new char[numLines][lineWidth];

        for (int i = 0; i < lineList.size(); i++)
        {
            line = lineList.get(i);

            for (int j = 0; j < lineWidth; j++)
            {
                if (j < line.length())
                {
                    indices[i][j] = line.charAt(j);
                }
                else{
                    indices[i][j] = ' ';
                }
                System.out.print(indices[i][j]);
            }
            System.out.print("|");
            System.out.println("");
        }

        //load images & catalogue with char index
        tiles = new HashMap();
        for (int i = 0; i < imagePaths.length; i++)
        {
            String path = imagePaths[i];

            if (!path.startsWith("/")) path = "/"+path;

            BufferedImage tile = null;
            try {
                tile = ImageIO.read(this.getClass().getResource(path));
            } catch (IOException ex)
            {
                System.err.println("GeneralScrollManager: Could not read tile images! Exiting!");
                ex.printStackTrace();
                System.exit(1);
            }
            int indexPos = path.indexOf("_") + 1;
            char index = path.charAt(indexPos);
            tiles.put(index,tile);

            //Assumption is all tiles are same size. Set class var tileSize based on first tile.
            if (i == 0)
            {
                this.tileSize = new Dimension(tile.getWidth(),tile.getHeight());
                tileBound.setSize(tileSize);
            }

            System.err.println("Loaded image="+path);
            System.err.println("Image height="+tile.getHeight());
        }
    }

    public void addLayer(String file, Point pos)
    {
        BackgroundLayer layer = new BackgroundLayer(file,painter);

        layer.setXScroll(-pos.x);
        layer.setYScroll(-pos.y);

        background.add(layer);
    }


     public void addLayer(String file, Point pos, boolean wrapX, boolean wrapY)
    {
        BackgroundLayer layer = new BackgroundLayer(file,painter);
        layer.setWrap(wrapX, wrapY);

        layer.setXScroll(-pos.x);
        layer.setYScroll(-pos.y);

        background.add(layer);
    }

    public void setScroll(float xScroll, float yScroll)
    {
        this.xScroll = xScroll;
        this.yScroll = yScroll;
    }

    public void scroll(float xAmt, float yAmt)
    {
        toScrollX += xAmt;
        toScrollY += yAmt;
    }

    public void setScrollStops(boolean xStop, boolean yStop)
    {
        scrollStopX = xStop;
        scrollStopY = yStop;
    }

    /**Override of the {@link Paintable} method. Do not call directly*/
    public void update(float interpolation)
    {
        if (toScrollX != 0f)
        {


            //System.out.println("To scrollX="+toScrollX);

            if (scrollStopX)
            {
                //Keep window from moving out of level area
                if (xScroll + toScrollX >= 0f && xScroll + toScrollX <= lineWidth*tileSize.width - painter.getSize().width)
                {
                    scrollingX = true;
                    //It is more intuitive to make positive scroll to the right
                    xScroll += toScrollX;

                }
                else{
                    //scrolling = false;
                }
            }
            else
            {
                scrollingX = true;
                //It is more intuitive to make positive scroll to the right
                xScroll += toScrollX;
            }


            //update background
            float prevScroll = toScrollX;

            //Update background front to back. Scroll is based on parallax function (scroll=parallax^layer)
            for (int i = 0; i < background.size(); i++)
            {
                BackgroundLayer layer = background.get(i);

                prevScroll = prevScroll * parallax;
                layer.scrollX(prevScroll);
                //layer.scroll((float)(p+1)*(toScroll*parallax));


                layer.update(0);
            }

            toScrollX = 0;

        }
        else
        {
            scrollingX = false;
        }

        if (toScrollY != 0f)
        {
            //System.out.println("To scroll="+toScroll);

            if (scrollStopY)
            {
                //Keep window from moving out of level area
                if (yScroll + toScrollY >= 0f && yScroll + toScrollY <= numLines*tileSize.height - painter.getSize().height)
                {
                    scrollingY = true;
                    //It is more intuitive to make positive scroll to the right
                    yScroll += toScrollY;

                }
                else{
                    //scrolling = false;
                }
            }
            else
            {
                scrollingY = true;
                //It is more intuitive to make positive scroll to the right
                yScroll += toScrollY;
            }

            //update background
            float prevScroll = toScrollY;

            //Update background front to back. Scroll is based on parallax function (scroll=parallax^layer)
            for (int i = 0; i < background.size(); i++)
            {
                BackgroundLayer layer = background.get(i);

                prevScroll = prevScroll * parallax;
                layer.scrollY(prevScroll);
                //layer.scroll((float)(p+1)*(toScroll*parallax));


                layer.update(0);
            }

            toScrollY = 0;
        }
        else
        {
            scrollingY = false;
        }
    }

    public void paintObject(Graphics g) 
    {
        Graphics2D g2 = (Graphics2D)g;

        //Paint background back to front
        for (int i = background.size() - 1; i >= 0; i--)
        {
            background.get(i).paintObject(g);
        }

        //Only want to paint tiles that are on the screen. Get tile index range for these.
        start.x = (int)Math.floor(xScroll/tileSize.width);
        end.x = (int)Math.ceil((xScroll + painter.getSize().width)/tileSize.width);

        start.y = (int)Math.floor(yScroll/tileSize.height);
        end.y = (int)Math.ceil((yScroll + painter.getSize().height)/tileSize.height);

        if (start.x < 0){ //TODO: Insert code for wrapping!
            start.x = 0;
        }

        //Tile set might me smaller than screen size. If so, set xEnd to end of tiles.
        if (end.x > lineWidth){//TODO: Insert code for wrapping!
            end.x = lineWidth;
        }

        if (start.y < 0){ //TODO: Insert code for wrapping!
            start.y = 0;
        }

        //Tile set might me smaller than screen size. If so, set xEnd to end of tiles.
        if (end.y > numLines){//TODO: Insert code for wrapping!
            end.y = numLines;
        }

        for (int x = start.x; x < end.x; x++)
        {
            for (int y = start.y; y < end.y; y++)
            {
                indexToPos(x,y,convertPoint);
                //System.out.println("Drawing tile at x="+convertPoint.x);

                BufferedImage tile = tiles.get(indices[y][x]);

                g2.drawImage(tile, convertPoint.x, convertPoint.y, null);
            }
        }

        
    }

    /**Returns true if the specified <code>Shape</code> collides with the foreground
     *
     * @param shape The <code>Shape</code> object to be checked against the foreground.
     * @return true if there is a collision
     */
    public boolean collidesWith(Shape shape)
    {
        //System.err.println("Shape bounds="+shape.getBounds().toString());
        //The origin of the shape in tile index coords

        posToIndex(shape.getBounds().x,shape.getBounds().y,boundOrigin);

        double boundWidth = shape.getBounds().getWidth();
        double boundHeight = shape.getBounds().getHeight();

        //# of tiles shape could hit
        int numTilesX = (int)Math.ceil(boundWidth/tileSize.getWidth());
        int numTilesY = (int)Math.ceil(boundHeight/tileSize.getHeight());

        //System.out.println("Checking tile area x="+boundXOrigin+" y="+boundYOrigin+" width="+numTilesX+" height="+numTilesY);

        //These checks make sure we are not going outside the array bounds, but are checking all likely tiles
        if (boundOrigin.x < 0) boundOrigin.x = 0;
        else if (boundOrigin.x + numTilesX >= lineWidth)
        {
            numTilesX = lineWidth - boundOrigin.x - 1;
        }

        if (boundOrigin.y < 0) boundOrigin.y = 0;
        else if (boundOrigin.y + numTilesY >= numLines)
        {
            numTilesY = numLines - boundOrigin.y - 1;
        }

        try{

            for (int x = boundOrigin.x; x <= boundOrigin.x + numTilesX; x++)
            {
                for (int y = boundOrigin.y; y <= boundOrigin.y + numTilesY; y++)
                {
                    if (indices[y][x] != ' ')
                    {
                        if (shape.getBounds().intersects(getTileBound(x,y))) return true;
                    }
                }
            }
        }catch(ArrayIndexOutOfBoundsException e)
        {

            return false;
        }

        return false;
    }

    /**Returns true if the specified <code>Shape</code> collides with the foreground. Also, the
     * supplied <code>Rectangle</code> object will be updated with the bounds of the tile that
     * is being collided with.
     *
     * @param shape The <code>Shape</code> object to be checked against the foreground.
     * @param collideTile bounds of tile that is collided with (to be updated)
     * @return true if there is a collision
     */
    public boolean collidesWith(Shape shape, Rectangle collideTile)
    {

        //System.err.println("Shape bounds="+shape.getBounds().toString());
        //The origin of the shape in tile index coords
        posToIndex(shape.getBounds().x,shape.getBounds().y,boundOrigin);

        double boundWidth = shape.getBounds().getWidth();
        double boundHeight = shape.getBounds().getHeight();

        //# of tiles shape could hit
        int numTilesX = (int)Math.ceil(boundWidth/tileSize.getWidth());
        int numTilesY = (int)Math.ceil(boundHeight/tileSize.getHeight());

        //These checks make sure we are not going outside the array bounds, but are checking all likely tiles
        if (boundOrigin.x < 0) boundOrigin.x = 0;
        else if (boundOrigin.x + numTilesX >= lineWidth)
        {
            numTilesX = lineWidth - boundOrigin.x - 1;
        }

        if (boundOrigin.y < 0) boundOrigin.y = 0;
        else if (boundOrigin.y + numTilesY >= numLines)
        {
            numTilesY = numLines - boundOrigin.y - 1;
        }


        //System.out.println("Checking tile area x="+boundXOrigin+" y="+boundYOrigin+" width="+numTilesX+" height="+numTilesY);

        try
        {

            for (int x = boundOrigin.x; x <= boundOrigin.x + numTilesX; x++)
            {
                for (int y = boundOrigin.y; y <= boundOrigin.y + numTilesY; y++)
                {
                    if (indices[y][x] != ' ')
                    {
                        if (shape.getBounds().intersects(getTileBound(x,y)))
                        {
                            if (collideTile == null) collideTile = new Rectangle();
                            collideTile.setRect(tileBound);
                            //System.out.println("COllideTile="+collideTile.toString());
                            return true;
                        }
                    }
                }
            }
        }catch(ArrayIndexOutOfBoundsException e)
        {

            return false;
        }

        return false;
    }

    /**Converts an tile index to location in the panel
     *
     * @param xIndex The tile x index
     * * @param yIndex The tile y index
     * @return panel location
     */
    public Point indexToPos(int xIndex, int yIndex)
    {
        Point point = new Point();


        point.setLocation((int)Math.round(xIndex*tileSize.width - xScroll),
                (int)Math.round(yIndex*tileSize.height - yScroll));

        return point;
    }

    /**Converts an tile index to location in the panel (this version doesn't create
     * a new return <code>Point</code> object, but instead returns conversion results
     * in specified <code>returnPoint</code>. Best for repeated calculations.
     *
     * @param xIndex The tile x index
     * * @param yIndex The tile y index
     * @param returnPoint non-null Point object that will capture resulting position
     */
    public void indexToPos(int xIndex, int yIndex, Point returnPoint)
    {

        returnPoint.setLocation((int)Math.round(xIndex*tileSize.width - xScroll),
                (int)Math.round(yIndex*tileSize.height - yScroll));
    }

    /**Converts an panel location to a tile index
     *
     * @param xPos panel x location
     * @param yPos panel y location
     * @return tile index
     */
    public Point posToIndex(int xPos, int yPos)
    {
        Point point = new Point();

        point.x = (int)Math.floor(((float)xPos+xScroll)/tileSize.getWidth());
        point.y = (int)Math.floor(((float)yPos+yScroll)/tileSize.getHeight());

        //for now, returns floor
        return point;
    }

    /**Converts an panel location to a tile index (this version doesn't create
     * a new return <code>Point</code> object, but instead returns conversion results
     * in specified <code>returnPoint</code>. Best for repeated calculations.
     *
     * @param xPos panel x location
     * @param yPos panel y location
     * @param returnPoint non-null Point object that will capture resulting index
     */
    public void posToIndex(int xPos, int yPos, Point returnPoint)
    {

        returnPoint.x = (int)Math.floor(((float)xPos+xScroll)/tileSize.getWidth());
        returnPoint.y = (int)Math.floor(((float)yPos+yScroll)/tileSize.getHeight());

    }

    public void setParallax(float parallax)
    {
        this.parallax = parallax;
    }

    public float getParallax()
    {
        return parallax;
    }

    /**Returns the size of the tiles (in pixels)
     *
     * @return Size of tiles (in pixels)
     */
    public Dimension getTileSize()
    {
        return tileSize;
    }

    /**Returns the character indices used to point to the particular tile images
     * during rendering.
     *
     * @return Tile character indices.
     */
    public char[][] getTileIndices()
    {
        return indices;
    }

    /**Returns the width of the world in tiles
     *
     * @return Width of the world (tile count)
     */
    public int getWorldWidthInTiles()
    {
        return lineWidth;
    }

    /**Returns the height of the world in tiles
     *
     * @return Height of the world (tile count)
     */
    public int getWorldHeightInTiles()
    {
        return numLines;
    }

    /**returns the bounds of a tile. Position is in world coordinates (not panel coordinates
     * because x position has not been adjusted for scroll.
     * @param xIndex
     * @param yIndex
     * @return bounds of tile
     */
    public Rectangle getTileBound(int xIndex, int yIndex)
    {
        indexToPos(xIndex,yIndex,tileBoundPoint);

        tileBound.setLocation(tileBoundPoint);

        return tileBound;
    }

    public static void main(String[] args)
    {
        Frame2D frame = new Frame2D(Frame2D.REG_PAINTER,1024,768,false);

        GamePainter painter = (GamePainter)frame.getPainter();


        painter.setFrameRate(40);

        final KeyControls kc = new KeyControls(painter.getComponent());
        kc.set(KeyEvent.VK_LEFT);
        kc.set(KeyEvent.VK_RIGHT);
        kc.set(KeyEvent.VK_UP);
        kc.set(KeyEvent.VK_DOWN);

        String tileDir = "images/";

        String[] tImages = new String[]{tileDir+"tile_0.gif",tileDir+"tile_1.gif",tileDir+"tile_2.gif",
        tileDir+"tile_3.gif",tileDir+"tile_4.gif",};

        final GeneralScrollManager scroller = new GeneralScrollManager("files/testFile2.txt",tImages,painter);

        scroller.setScrollStops(false, false);

        scroller.setParallax(.5f);
        scroller.addLayer("/images/stone03.jpg",new Point(300,0),false,true);
        scroller.addLayer("/images/fyre002.jpg", new Point(0,0));
        

        painter.addToPaintList(scroller);

        final Sprite2D ship = new Sprite2D("/images/ship.gif");
        ship.setLocation(500, 300);

        painter.addToPaintList(ship);

        final Rectangle bounds = new Rectangle();

        Paintable updater = new Paintable()
        {

            

            public void update(float interpolation)
            {
                if (kc.isPressed(KeyEvent.VK_RIGHT)) scroller.scroll(6f,0f);
                else if (kc.isPressed(KeyEvent.VK_LEFT)) scroller.scroll(-6f,0f);

                if (kc.isPressed(KeyEvent.VK_UP)) scroller.scroll(0,-6f);
                else if (kc.isPressed(KeyEvent.VK_DOWN)) scroller.scroll(0f,6f);

                if (scroller.collidesWith(ship.getCollisionBounds()/*,bounds*/))
                {
                    ship.setBoundsVisible(true);
                }
                else
                {
                    ship.setBoundsVisible(false);
                }
            }
            public void paintObject(Graphics g)
            {
                
                
                Graphics2D g2 = (Graphics2D)g;
                
                Color oldColor = g2.getColor();
                
                g2.setColor(Color.RED);
                //g2.draw(bounds);
                
                g2.setColor(oldColor);
            }

        };

        painter.addToPaintList(updater);

    }

}
