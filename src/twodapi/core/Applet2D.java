/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twodapi.core;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JPanel;
import twodapi.audio.SimpleAudioPlayer;

/**
 *
 * @author bsimpkins
 */
public class Applet2D extends JApplet{

    private Painter painter = null;

    private SimpleAudioPlayer aPlayer = null;

    private JPanel panel = null;

    public static final int REG_PAINTER = 1;
    public static final int EVENT_DRIVEN_PAINTER = 2;

    private int painterType;


    @Override
    public void init()
    {
        this.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent e)
            {
                returnFocusToApplet();
            }
        });
    }

    public void setSimpleAudioPlayer(SimpleAudioPlayer player)
    {
        this.aPlayer = player;
    }

    public Painter makePainter(int type)
    {
        if (type==REG_PAINTER)
        {
            //panel = new JPanel();
            painter = new GamePainter((JComponent)getContentPane());
            ((GamePainter)painter).setFrameRate(30);
           // getContentPane().add(panel);
        }
        else if (type==EVENT_DRIVEN_PAINTER)
        {
            painter = new EventDrivenPainter();
            getContentPane().add(painter.getComponent());
        }
        return painter;
    }

    public Painter getPainter()
    {
        return painter;
    }

    @Override
    public void destroy()
    {
        if (aPlayer == null) return;

        ArrayList<String> soundList = aPlayer.getSoundList();

        for (int i = 0; i < soundList.size();i++)
        {
            aPlayer.stopSound(soundList.get(i));
        }

        aPlayer = null;
    }

    private void returnFocusToApplet()
    {

        System.out.println("Attempting to return focus to applet");
        this.requestFocus();
        panel.requestFocus();
        panel.requestFocusInWindow();

    }
}
