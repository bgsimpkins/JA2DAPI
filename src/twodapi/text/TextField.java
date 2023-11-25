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
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Line2D;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import twodapi.core.EventDrivenPainter;
import twodapi.core.Frame2D;

import twodapi.core.MouseControls;
import twodapi.core.Mouseable;
import twodapi.core.Paintable;
import twodapi.core.Painter;

/**
 *
 * @author bsimpkins
 */
public class TextField implements Paintable, Mouseable
{
    private Point location = new Point(0,0);
    private Font font = new Font("sanserif",Font.PLAIN,12);

    /**The text stored in this object*/
    private String text = "";
    
    /**The text to be rendered in the box (may be different from text, for example, if text is 
     * wider than field width
     */
    private String renderText = "";
    private Color color = Color.BLACK;

    private boolean visible = true;

    private boolean updateFontMetrics = false;

    private FontMetrics fontMetrics = null;

    private Dimension size = null;

    private Dimension textSize = new Dimension();

    private boolean hasFocus = false;

    private boolean mouseOver = false;

    private int cursorPos = 0;

    private Line2D cursor = new Line2D.Double();

    private BasicStroke cursorStroke = new BasicStroke(2);

    private Rectangle border = null;

    private Painter painter = null;

    private boolean drawBorderAlways = false;

    private boolean drawBorder = false;

    /**If true, text cannot be wider than text field width*/
    private boolean constrainText = true;

    public TextField(Painter painter,MouseControls mControls)
    {
        this.painter = painter;

        initKeyListener(painter.getComponent());
        mControls.addMouseable(this);
    }

    public TextField(Font font, Painter painter, MouseControls mControls)
    {
        this.font = font;
        this.painter = painter;

        initKeyListener(painter.getComponent());
        mControls.addMouseable(this);
    }

    public TextField(String text, Font font, Color color, Point location, Painter painter, MouseControls mControls)
    {
        this.painter = painter;
        this.text = text;
        this.font = font;
        this.color = color;
        this.location = location;

        initKeyListener(painter.getComponent());
        mControls.addMouseable(this);
    }
    
    public void setHasFocus(boolean hasFocus)
    {
        this.hasFocus = hasFocus;
    }

    public boolean hasFocus()
    {
        return hasFocus;
    }
    
    private void initKeyListener(JComponent comp)
    {
        comp.setFocusable(true);
        comp.addKeyListener(new TextKeyListener());
    }

    public void setFont(Font font)
    {
        this.font = font;
        //updateFontMetrics = true;
        updateRenderText();
    }

    public Font getFont()
    {
        return font;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }

    public Color getColor()
    {
        return color;
    }

    public void setText(String text)
    {
        this.text = text;
        
        //updateFontMetrics = true;
        updateRenderText();
        cursorPos = renderText.length();
    }

    public String getText()
    {
        return text;
    }

    public void setLocation(Point loc)
    {
        this.location.setLocation(loc);

        if (border != null)  border.setLocation(loc.x-border.width/2, loc.y-border.height/2);
    }

    public void setLocation(int x, int y)
    {
        this.location.setLocation(x,y);

        if (border != null)  border.setLocation(x-border.width/2, y-border.height/2);
    }

    public Point getLocation()
    {
        return location;
    }

    public Dimension getSize()
    {
        if (size == null)
        {
            return textSize;
        }
        return size;
    }

    public void setSize(Dimension size)
    {
        setSize(size.width,size.height);

    }

    public void setSize(int width, int height)
    {
        if (this.size == null) this.size = new Dimension(width,height);
        else
        {
            this.size.setSize(width,height);
        }

        if (border != null) updateBorder();

        this.updateFontMetrics = true;
        //updateRenderText();
    }
    
    public void drawBorder(boolean draw)
    {
        if (draw) border = new Rectangle(location.x - size.width/2,location.y - size.height/2,size.width,size.height);
        else border = null;

    }

    public void setDrawBorderAlways(boolean draw)
    {
        drawBorderAlways = draw;

        if (drawBorderAlways) drawBorder = true;
    }

    public void updateBorder()
    {
        border.setRect(location.x - size.width/2,location.y - size.height/2,size.width,size.height);
    }

    private void updateCursorPosition()
    {
        if (fontMetrics == null) return;

        //System.out.println("Updating cursor pos!");

        String subStr = text.substring(0,cursorPos);
        int subStrWidth = SwingUtilities.computeStringWidth(fontMetrics, subStr);

        double x = location.x - size.width/2 + subStrWidth;

        cursor.setLine(x, location.y - textSize.height/2, x, location.y + textSize.height/2);

        if (painter instanceof EventDrivenPainter)  ((EventDrivenPainter)painter).update();

    }

    public void update(float interpolation){}

    public void paintObject(Graphics g)
    {
        if (!visible) return;

        Graphics2D g2 = (Graphics2D)g;
        g2.setFont(font);
        g2.setColor(color);

        if (fontMetrics == null)
        {
            
            fontMetrics = g2.getFontMetrics();
            int width = SwingUtilities.computeStringWidth(fontMetrics, text);
            int height = fontMetrics.getHeight();
            //size.setSize(width,height);
            textSize.setSize(width,height);
            updateRenderText();
        }

        if (updateFontMetrics)
        {
            fontMetrics = g2.getFontMetrics();
            int width = SwingUtilities.computeStringWidth(fontMetrics, text);
            int height = fontMetrics.getHeight();
            //size.setSize(width,height);
            textSize.setSize(width,height);

            updateRenderText();
            updateFontMetrics = false;

        }

        g2.drawString(renderText, location.x - size.width/2,location.y -size.height/2 +textSize.height);

        g2.setColor(Color.BLACK);

        //Draw border
        if (border != null && (drawBorder || hasFocus))
        {

            g2.draw(border);
        }

        //Draw cursor
        
        if (hasFocus)
        {

            Stroke oldStroke = g2.getStroke();
            g2.setStroke(cursorStroke);
            
            g2.draw(cursor);

            g2.setStroke(oldStroke);
        }

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

    private boolean addCharacter(char ch)
    {

        String before = text.substring(0,cursorPos);
        String after = text.substring(cursorPos);
        String tempText = before+ch+after;

        if (SwingUtilities.computeStringWidth(fontMetrics, tempText) >= size.width)
        {
            return false;
        }
        else
        {
            text = tempText;
            updateRenderText();
            return true;
        }

        

    }

    private void updateRenderText()
    {
        if (fontMetrics == null) return;

        String tempText = text;

        while(SwingUtilities.computeStringWidth(fontMetrics, tempText) > size.width)
        {
            tempText = tempText.substring(0, tempText.length()-1);
        }
        renderText = tempText;
    }

    private void backspace()
    {
        String before = "";
        if (cursorPos > 0) before = text.substring(0,cursorPos-1);
        String after = text.substring(cursorPos);
        text=before+after;

        updateRenderText();

        if (cursorPos > 0) cursorPos--;
        updateCursorPosition();


    }

    public void mouseButtonUpdate(Point mousePoint, int button, boolean pressed) 
    {
        if (!pressed && mouseOver)
        {
            hasFocus = true;

            if (!painter.getComponent().isFocusOwner()) painter.getComponent().requestFocus();

            updateCursorPosition();
        }
        else if (!pressed && !mouseOver && hasFocus)
        {
            hasFocus = false;

            fieldFinalized();
            if (painter instanceof EventDrivenPainter)  ((EventDrivenPainter)painter).update();
            
        }
    }

    public boolean isSkippedKey(int keyCode)
    {
       boolean skipped = false;

       if (keyCode == KeyEvent.VK_CONTROL) skipped = true;
       else if (keyCode == KeyEvent.VK_SHIFT) skipped = true;
       else if (keyCode == KeyEvent.VK_CAPS_LOCK) skipped = true;
       else if (keyCode == KeyEvent.VK_TAB) skipped = true;

       return skipped;
    }

    public void mouseMoveUpdate(Point mousePoint, int button, boolean pressed) {}

    public void mouseBorderUpdate(Point mousePoint, boolean entered) 
    {
        if (entered)
        {
            //System.out.println("Entered!");
            mouseOver = true;

            if (!drawBorderAlways)
            {
                drawBorder = true;
                if (painter instanceof EventDrivenPainter)  ((EventDrivenPainter)painter).update();
            }
        }
        else
        {
            //System.out.println("Exited!");
            mouseOver = false;

            if (!drawBorderAlways)
            {
                drawBorder = false;
                if (painter instanceof EventDrivenPainter)  ((EventDrivenPainter)painter).update();
            }
        }
    }

    /**Returns true if text is constrained to box width (ie., can't enter more text 
     * than box width).
     *
     * @return True if text is constrained.
     */
    public boolean isConstrainingText() {
        return constrainText;
    }

    /**Set to true if text is to be constrained to box width (ie., can't enter more text 
     * than box width).
     *
     * @param constrainText True if text is constrained.
     */
    public void setConstrainText(boolean constrainText) {
        this.constrainText = constrainText;
    }

    /**Is fired if a character is added or deleted from the text field. Override to receive
     * event.
     */
    protected void fieldEdited(){}


    /**Is fired if the <code>Enter</code> button is pressed or the mouse is clicked outside of the field
     * (essentially, when it loses focus). Override to receive event.
     */
    protected void fieldFinalized(){}

    class TextKeyListener implements KeyListener
    {

        public void keyTyped(KeyEvent e) {}

        public void keyPressed(KeyEvent e) 
        {
            if (hasFocus)
            {
                if (e.getKeyCode()==KeyEvent.VK_LEFT)
                {
                    if (cursorPos > 0) cursorPos--;
                    updateCursorPosition();
                }
                else if (e.getKeyCode()==KeyEvent.VK_RIGHT)
                {
                    if (cursorPos < text.length() ) cursorPos++;
                    updateCursorPosition();
                }
                else if (e.getKeyCode()==KeyEvent.VK_BACK_SPACE)
                {
                    backspace();
                    fieldEdited();
                    //updateFontMetrics = true;
                }
                else if (e.getKeyCode()==KeyEvent.VK_ENTER)
                {
                    hasFocus = false;

                    fieldFinalized();
                    if (painter instanceof EventDrivenPainter)  ((EventDrivenPainter)painter).update();
                    
                }
                else if (isSkippedKey(e.getKeyCode())){}
                else {
                    boolean added = addCharacter(e.getKeyChar());
                    fieldEdited();
                    //updateFontMetrics = true;

                    if (added)
                    {
                        cursorPos++;
                        updateCursorPosition();
                    }
                    
                }
            }
        }

        public void keyReleased(KeyEvent e) {}

    }

    public static void main(String[] args)
    {

        EventDrivenPainter painter = new EventDrivenPainter(5,true);
        Frame2D frame = new Frame2D(painter,400,300,false);

//        EventDrivenPainter painter = (EventDrivenPainter)frame.getPainter();


        System.out.println("Painter size="+painter.getSize().toString());

        MouseControls mControls = new MouseControls(painter);
        mControls.setUseAWTThread(true);

        TextField field = new TextField(new Font("sanserif",Font.PLAIN,12),painter,mControls)
        {
            @Override
            public void fieldEdited()
            {
                System.out.println("Field edited!");
            }
            @Override
            public void fieldFinalized()
            {
                System.out.println("Field finalized!");
            }
        };
        field.setText("Some Text");
        field.setLocation(100, 100);
        
        field.setSize(100,20);
        field.drawBorder(true);

        field.setLocation(150, 100);
        //field.setDrawBorderAlways(true);

        painter.addToPaintList(field);
        
        
        painter.update();
        
    }

}
