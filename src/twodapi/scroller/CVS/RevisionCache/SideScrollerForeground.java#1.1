/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twodapi.scroller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import twodapi.core.KeyControls;
import twodapi.core.Paintable;
import twodapi.core.GamePainter;
import twodapi.core.Painter;

/**This is the scrollable layer that the player interacts with (e.g., blocks in Super Mario Bros). Therefore, this class
 * contains methods for collision checking.
 *
 * The foreground is a set of tiles. The locations of the tiles are read in from a specified input file. The tile images
 * are specified in an array parameter in the constructor. The tile image names must be in the form {tilename}_{tileID}.{extension}
 * such as "tile_1.gif" or "image_F.jpg". The <code>tileID</code> must be a single character.
 * An example of an
 * input file is:
 * <br><br> &nbsp;&nbsp;&nbsp; 22
 * <br>11111111
 *<p>
 * So, here, there could be 2 tile image files: "tile_1.gif" drawn for each '1' and "tile_2.gif" for each '2' in the input file.
 *
 * If a {@link ScrollerManager} is being used, it's internal <code>ScrollerForeground</code> must be retrieved to do
 * collision checking.
 *
 * @author bsimpkins
 */
public class SideScrollerForeground implements Paintable
{

    private Dimension tileSize = null;

    private char[][] indices;

    private HashMap<Character,BufferedImage> tiles = new HashMap();

    private float toScroll = 0f;

    protected float scroll = 0f;

    private int yLoc = 0;

    //private JComponent comp = null;
    private Painter painter;

    private int numLines = 0;

    private int lineWidth = 0;

    protected int xStart = 0;

    protected int xEnd = 0;

    private boolean wrap = false;

    public boolean scrolling = true;

    private ArrayList<Integer> yReturn = new ArrayList();

    private Rectangle tileBound = new Rectangle();

    /**Creates a new ScrollerForeground
     *
     * @param inputFile the filename of the tile input file
     * @param imagePaths The filenames of the tile images
     * @param comp The component on which this foreground is being painted
     */
    public SideScrollerForeground(String inputFile, String[] imagePaths, Painter painter)
    {
        this.painter = painter;

        try
        {
            initialize(inputFile, imagePaths);

        } catch (IOException ex)
        {
            System.err.println("ScrollLayer: Could not load or read layer image!");
        }
    }

    protected void initialize(String file, String[] imagePaths) throws IOException
    {
        //FileReader input = null;

        //input = new FileReader(file);

        if (!file.startsWith("/")) file = "/"+file;

        InputStream input = this.getClass().getResourceAsStream(file);

        BufferedReader bufRead = new BufferedReader(new InputStreamReader(input));

        String line;
        numLines = 0;
        //Width of widest line
        lineWidth = 0;
        ArrayList<String> lineList = new ArrayList();

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

            BufferedImage tile = ImageIO.read(this.getClass().getResource(path));
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

    private boolean lineEmpty(String line)
    {
        if (line.isEmpty()) 
        {
            return true;
        }

        else 
        {

            char[] lineArray = line.toCharArray();

            for (int i = 0; i < lineArray.length; i++)
            {
                if (lineArray[i] != ' ')
                {                   
                    return false;
                }
            }
            return true;
        }
    }
    /**Override of the {@link Paintable} method. Do not call directly*/
    public void update(float interpolation)
    {
        if (toScroll != 0f)
        {
            //System.out.println("To scroll="+toScroll);
            //Keep window from moving out of level area
            if (scroll + toScroll >= 0f && scroll + toScroll <= lineWidth*tileSize.width - painter.getSize().width)
            {
                scrolling = true;
                //It is more intuitive to make positive scroll to the right
                scroll += toScroll;

            }
            else{
                scrolling = false;
            }

            toScroll = 0;
        }
    }

    /**For a given screen x position, returns an ArrayList of y coordinates where there
     * tiles.
     * @param xPos X position on the screen
     * @return ArrayList of y positions where there are tiles.
     */
    public ArrayList getTileYPos(int xPos)
    {
        //if (numLines == 0 || this.lineWidth == 0) return yReturn;

        yReturn.clear();

        int x = this.xPosToIndex(xPos);

        //for x tile index, get all y tiles that are not empty (' ')

        for (int y = 0; y < numLines; y++)
        {
            //System.out.println("x="+x+" y="+y);
            if (indices[y][x] != ' ') yReturn.add(y*tileSize.height + this.yLoc);
        }

        return yReturn;
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
        int boundXOrigin = this.xPosToIndex(shape.getBounds().x);
        int boundYOrigin = (int)Math.floor((shape.getBounds().getY() - yLoc)/tileSize.getHeight());

        double boundWidth = shape.getBounds().getWidth();
        double boundHeight = shape.getBounds().getHeight();

        //# of tiles shape could hit
        int numTilesX = (int)Math.ceil(boundWidth/tileSize.getWidth());
        int numTilesY = (int)Math.ceil(boundHeight/tileSize.getHeight());

        //System.out.println("Checking tile area x="+boundXOrigin+" y="+boundYOrigin+" width="+numTilesX+" height="+numTilesY);

        //These checks make sure we are not going outside the array bounds, but are checking all likely tiles
        if (boundXOrigin < 0) boundXOrigin = 0;
        else if (boundXOrigin + numTilesX >= lineWidth)
        {
            numTilesX = lineWidth - boundXOrigin - 1;
        }
        
        if (boundYOrigin < 0) boundYOrigin = 0;
        else if (boundYOrigin + numTilesY >= numLines)
        {
            numTilesY = numLines - boundYOrigin - 1;
        }

        try{

            for (int x = boundXOrigin; x <= boundXOrigin + numTilesX; x++)
            {
                for (int y = boundYOrigin; y <= boundYOrigin + numTilesY; y++)
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
        int boundXOrigin = this.xPosToIndex(shape.getBounds().x);
        int boundYOrigin = (int)Math.floor((shape.getBounds().getY() - yLoc)/tileSize.getHeight());

        double boundWidth = shape.getBounds().getWidth();
        double boundHeight = shape.getBounds().getHeight();

        //# of tiles shape could hit
        int numTilesX = (int)Math.ceil(boundWidth/tileSize.getWidth());
        int numTilesY = (int)Math.ceil(boundHeight/tileSize.getHeight());

        //These checks make sure we are not going outside the array bounds, but are checking all likely tiles
        if (boundXOrigin < 0) boundXOrigin = 0;
        else if (boundXOrigin + numTilesX >= lineWidth)
        {
            numTilesX = lineWidth - boundXOrigin - 1;
        }

        if (boundYOrigin < 0) boundYOrigin = 0;
        else if (boundYOrigin + numTilesY >= numLines)
        {
            numTilesY = numLines - boundYOrigin - 1;
        }


        //System.out.println("Checking tile area x="+boundXOrigin+" y="+boundYOrigin+" width="+numTilesX+" height="+numTilesY);

        try
        {

            for (int x = boundXOrigin; x <= boundXOrigin + numTilesX; x++)
            {
                for (int y = boundYOrigin; y <= boundYOrigin + numTilesY; y++)
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

   /**Override of the {@link Paintable} method. Do not call directly*/
    public void paintObject(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;

        //Only want to paint tiles that are on the screen. Get tile index range for these.
        xStart = (int)Math.floor(scroll/tileSize.width);
        xEnd = (int)Math.ceil((scroll + painter.getSize().width)/tileSize.width);

        if (xStart < 0)
        {
            //TODO: Insert code for wrapping!
            xStart = 0;
           
        }

        //Tile set might me smaller than screen size. If so, set xEnd to end of tiles.
        if (xEnd > lineWidth) 
        {
            //TODO: Insert code for wrapping!

            xEnd = lineWidth;    
        }

        for (int x = xStart; x < xEnd; x++)
        {
            for (int y = 0; y < numLines; y++)
            {
                int xPos = xIndexToPos(x);
                int yPos = this.yLoc + y*tileSize.height;

                BufferedImage tile = tiles.get(indices[y][x]);

                g2.drawImage(tile, xPos, yPos, null);
            }
        }

    }

    /**Converts an x tile index to x location in the panel
     *
     * @param index The tile x index
     * @return panel x location
     */
    public int xIndexToPos(int index)
    {
        return (int)Math.round(index*tileSize.width - scroll);
    }

    /**Converts an x panel location to an x tile index
     *
     * @param xPos panel x location
     * @return tile x index
     */
    public int xPosToIndex(int xPos)
    {
        //for now, returns floor
        return (int)Math.floor(((float)xPos+scroll)/tileSize.getWidth());
    }

    /**returns the bounds of a tile. Position is in world coordinates (not panel coordinates
     * because x position has not been adjusted for scroll.
     * @param xIndex
     * @param yIndex
     * @return bounds of tile
     */
    public Rectangle getTileBound(int xIndex, int yIndex)
    {
        int x = xIndexToPos(xIndex);
        int y = yIndex*tileSize.height + yLoc;

        tileBound.setLocation(x, y);

        return tileBound;
    }


    public Dimension getTileSize()
    {
        return tileSize;
    }

    public char[][] getTileIndices()
    {
        return indices;
    }

    public int getWorldWidthInTiles()
    {
        return lineWidth;
    }

    public int getWorldHeightInTiles()
    {
        return numLines;
    }

    public void setYLocation(int yLoc)
    {
        this.yLoc = yLoc;
    }

    public int getYLocation()
    {
        return yLoc;
    }

    public void scroll(float amount)
    {
        toScroll = amount;
    }

    public float getScroll()
    {
        return scroll;
    }

    public void setScroll(float scroll)
    {
        this.scroll = scroll;
    }

    public void stopScroll()
    {
        toScroll = 0;
    }

    public Dimension getSize()
    {
        return new Dimension(lineWidth*tileSize.width,numLines*tileSize.height);
    }

    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,600);



        JPanel panel = new JPanel();
        final KeyControls control = new KeyControls(panel);
        control.set(KeyEvent.VK_RIGHT);
        control.set(KeyEvent.VK_LEFT);

        GamePainter painter = new GamePainter(panel);



        String[] paths = {"images/tile_0.gif","images/tile_1.gif","images/tile_2.gif"};
        final SideScrollerForeground scroller = new SideScrollerForeground("files/testFile.txt",paths,painter)
        {
            @Override
            public void update(float interpolation)
            {
                if (control.isPressed(KeyEvent.VK_RIGHT))
                {
                    scroll(10f);
                }
                else if (control.isPressed(KeyEvent.VK_LEFT))
                {
                    scroll(-10f);
                }

                super.update(0);
            }
            @Override
            public void paintObject(Graphics g)
            {
                super.paintObject(g);

                g.setColor(Color.WHITE);

                //g.drawString("XStart="+this.xStart+" XEnd="+this.xEnd,10,40);
                //g.drawString("Scroll="+this.scroll,10,60);
            }

        };

        painter.addToPaintList(scroller);

        frame.getContentPane().add(panel);

        frame.setVisible(true);

        scroller.setYLocation(panel.getSize().height - scroller.getSize().height);

    }


}
