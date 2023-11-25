/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twodapi.text;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import twodapi.core.Paintable;
import twodapi.core.GamePainter;

/**A basic text box that displays a wrapped string of text. Text all has to be one font,
 * but can be wrapped by full words and left justified or centered. The newline character '\n' can
 * be used to add new lines.
 *
 * @author bsimpkins
 */
public class TextBox implements Paintable
{
    public static final int DEFAULT = 1;
    public static final int ROUNDED = 2;
    private int type = DEFAULT;
    private Color borderColor = Color.BLACK;
    private Color backgroundColor = Color.WHITE;
    private Color fontColor = Color.BLACK;
    private Font font = new Font("sanserif",Font.PLAIN,12);
    private Point location = new Point(0,0);
    private Dimension size = new Dimension(100,100);
    private FontMetrics fontMetrics = null;
    private Graphics g;

    private String textString = "";
    private int vertSpacing = 2;

    private boolean wrapFullWords = true;

    private ArrayList<String> lineList = new ArrayList();

    private boolean toUpdateBox = false;

    private BasicStroke border = new BasicStroke(2);

    private boolean paintBackground = true;

    private boolean visible = true;

    private boolean centered = false;

    private boolean toUpdateToAccomodate = false;

    public TextBox(){}

    public TextBox(Font font, Color fontColor, Dimension size)
    {
        this.font = font;
        this.fontColor = fontColor;
        this.size = size;
    }

    public void setHeightToAccomodateText()
    {
        if (fontMetrics == null)
        {
            toUpdateToAccomodate = true;
        }
        else
        {
            int fontHeight = vertSpacing + fontMetrics.getHeight();
            int goodHeight = fontHeight*lineList.size() + 2*vertSpacing;
            setSize(getSize().width,goodHeight);
        }
        
    }

    public void update(float interpolation) {}

    public void paintObject(Graphics g)
    {
        this.g = g;

        g.setFont(font);
        g.setColor(fontColor);

        //Make sure we have font metrics before painting
        if (g.getFontMetrics() != null)
        {
            fontMetrics = g.getFontMetrics();
        }
        else return;

        if (!visible) return;

        

        //draw background, if necessary
        if (paintBackground)
        {
            g.setColor(backgroundColor);
            g.fillRect(location.x, location.y, size.width, size.height);
        }


        int lineStartX = 0;

        if (!centered)
        {
            lineStartX = (int)(location.x+border.getLineWidth()/2);
        }

        int fontHeight = fontMetrics.getHeight() + vertSpacing;
        int lineStartY = 0;

        //draw text lines
        g.setColor(fontColor);       
        for (int i = 0; i < lineList.size(); i++)
        {
            String line = lineList.get(i);

            lineStartY = (i+1)*fontHeight + this.location.y;

            if (lineStartY > location.y + this.size.height) break;

            if (centered)
            {
                int lineWidth = SwingUtilities.computeStringWidth(fontMetrics, line);
                lineStartX = location.x+size.width/2 - lineWidth/2;
            }
            
           
            g.drawString(line,lineStartX,lineStartY);
            
        }

        //draw border

        if (borderColor != null)
        {
            g.setColor(borderColor);
            Graphics2D g2 = (Graphics2D)g;
            g2.setStroke(border);
            g2.drawRect(location.x, location.y, size.width, size.height);
        }
        


        if (toUpdateBox)
        {
            updateBox();
            toUpdateBox = false;
        }

        if (toUpdateToAccomodate)
        {
            setHeightToAccomodateText();
            toUpdateToAccomodate = false;
        }
    }

    private void updateBox()
    {
        if (fontMetrics == null) {
            toUpdateBox = true;
            return;
        }

        lineList.clear();

        //boolean finished = false;
        //char[] textArray = textString.toCharArray();
        int maxCharsRow = (int)Math.ceil((size.width-border.getLineWidth())/fontMetrics.getMaxAdvance());

        if (maxCharsRow >= textString.length()) maxCharsRow = textString.length()-1;


        int i = 0;


        while (i+maxCharsRow < getTextString().length())
        {
            //System.out.println("to Substring: "+i+","+(i+maxCharsRow));

            String subStr = textString.substring(i, i+maxCharsRow);
            int rowWidth = SwingUtilities.computeStringWidth(fontMetrics, subStr);

            //System.out.println("Substr="+subStr);


            boolean newLineFound = false;

            //Check for newline in subStr
            for (int j = 0; j < maxCharsRow; j++)
            {
                char ch = subStr.charAt(j);

                if (ch == '\n')
                {
                    i += (j+1);
                    lineList.add(subStr.substring(0, j));
                    //lineList.add("");

                    newLineFound = true;
                    break;
                }
            }

            if (newLineFound) continue;

            int rowI = maxCharsRow;


            //Strech row out until end of box is reached or end of text string
            while (i+rowI < textString.length())
            {

                //Test the width of the row with the next character added
                subStr = textString.substring(i,i+(rowI+1));
                //System.out.println("Substr="+subStr);
                rowWidth = SwingUtilities.computeStringWidth(fontMetrics, subStr);
                //System.out.println("Row width="+rowWidth);

                //Check for newline
                if (subStr.charAt(subStr.length()-1) == '\n')
                {
                    lineList.add(subStr);

                    i = i+subStr.length();
                    //lineList.add("");
                    newLineFound = true;

                    break;
                }

                int rowEnd = i + rowI;
  
                //when it goes over, go back to the previous character and use it as the end point
                if (rowWidth >= size.width-border.getLineWidth())
                {
                    if (wrapFullWords)
                    {
                        rowEnd = getRowIndexForWordWrap(i,i+rowI);

                        //if space was found, go past it (so next line doesn't start with a space)
                        if (rowEnd != rowI) rowI = rowEnd-i+1;
                    }
                    

                    subStr = textString.substring(i,rowEnd);
                    break;
                }

                rowI++;


            }

            if (newLineFound) continue;

            lineList.add(subStr);
            i += rowI;

            //if line is shorter than maxCharsRow, make sure we get it (e.g., last line).
            if (i+maxCharsRow >= getTextString().length() && i < getTextString().length()) maxCharsRow = getTextString().length() -1 - i;
        }
        

    }

    private int getRowIndexForWordWrap(int rowBegin, int rowEnd)
    {
        int returnI = rowEnd;

        while(rowEnd > rowBegin)
        {
            char ch = textString.charAt(rowEnd);
            if (ch == ' ') {
                returnI = rowEnd;
                break;
            }
            rowEnd--;

        }


        return returnI;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the borderColor
     */
    public Color getBorderColor() {
        return borderColor;
    }

    /**
     * @param borderColor the borderColor to set
     */
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public void setBorderWidth(int width)
    {
        border = new BasicStroke(width);
        updateBox();
    }

    public int getBorderWidth()
    {
        return (int)border.getLineWidth();
    }

    /**
     * @return the backgroundColor
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * @param backgroundColor the backgroundColor to set
     */
    public void setBackgroundColor(Color backgroundColor)
    {
        if (backgroundColor == null)
        {
            this.paintBackground = false;
        }
        this.backgroundColor = backgroundColor;
    }

    /**
     * @return the font
     */
    public Font getFont() {
        return font;
    }

    /**
     * @param font the font to set
     */
    public void setFont(Font font) {
        this.font = font;
        //g.setFont(font);
        this.toUpdateBox = true;
    }

    /**
     * @return the location
     */
    public Point getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(Point location) {
        this.location.setLocation(location);
    }

    public void setLocation(int x, int y)
    {
        this.location.setLocation(x, y);
    }

    /**
     * @return the size
     */
    public Dimension getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(Dimension size) {
        this.size.setSize(size);
        updateBox();
    }

    public void setSize(int width, int height)
    {
        this.size.setSize(width,height);
        updateBox();
    }

    public void setVisible(boolean vis)
    {
        this.visible = vis;
    }

    public boolean isVisible()
    {
        return visible;
    }

    /**
     * @return the wrapFullWords
     */
    public boolean wrapFullWords() {
        return wrapFullWords;
    }

    /**
     * @param wrapFullWords the wrapFullWords to set
     */
    public void setWrapFullWords(boolean wrapFullWords) {
        this.wrapFullWords = wrapFullWords;
        updateBox();
    }

    /**
     * @return the fontColor
     */
    public Color getFontColor() {
        return fontColor;
    }

    /**
     * @param fontColor the fontColor to set
     */
    public void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
    }


    /**
     * @return the textString
     */
    public String getTextString() {
        return textString;
    }

    /**
     * @param textString the textString to set
     */
    public void setTextString(String textString) {
        this.textString = textString;
        this.updateBox();
    }

    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,600);

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel);

        GamePainter painter = new GamePainter(panel);
        TextBox tb = new TextBox();
        tb.setCentered(true);
        tb.setSize(new Dimension(400,300));
        tb.setLocation(10,10);
        tb.setFont(new Font("sanserif",Font.PLAIN,20));
        tb.setBorderWidth(2);
        //tb.setWrapFullWords(false);
        tb.setTextString("This is a text box. That's all it is.  \n\nIf you wanted more, then I'm sorry to have disappointed you.");
        painter.addToPaintList(tb);

        frame.setVisible(true);

        tb.setHeightToAccomodateText();

    }

    /**
     * @return the centered
     */
    public boolean isCentered() {
        return centered;
    }

    /**
     * @param centered the centered to set
     */
    public void setCentered(boolean centered) {
        this.centered = centered;
    }

}
