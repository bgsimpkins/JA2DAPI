/*
 * ShipSprite.java
 *
 * Created on August 3, 2009, 10:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package GameTest;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;

import twodapi.core.AnimationThread;
import twodapi.core.KeyControls;
import twodapi.core.Painter;
import twodapi.core.Sprite2D;

/**
 *
 * @author bsimpkins
 */
public class ShipSprite extends Sprite2D
{
    private KeyControls keyControls;

    private Blast[] blasts;

    private TestGame game;

    private Ellipse2D safeBounds = new Ellipse2D.Double(0,0,250,250);

    private boolean tempHiding = false;

    private int hideDelay = 1;

    private float timer = 0f;

    private static final AlphaComposite transparent = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .3f);

    private static final Color hideColor = new Color(200,200,200);

    //double time = 0;

    //int update = 0;
    
    /** Creates a new instance of ShipSprite */
    public ShipSprite(Painter painter, Blast[] blasts,TestGame game)
    {
        super("/images/ship.gif");

        this.game = game;
        this.blasts = blasts;

        setPainter(painter);
        
        keyControls = new KeyControls(painter.getComponent())
        {
            //To pause game, we must catch key press from AWT thread
            @Override
            public void keyPressedEvent(KeyEvent e)
            {
                processAWTKeyPress(e);
            }
        };
        keyControls.set(KeyEvent.VK_LEFT);
        keyControls.set(KeyEvent.VK_RIGHT);
        keyControls.set(KeyEvent.VK_UP);
        keyControls.set(KeyEvent.VK_DOWN);
        keyControls.set(KeyEvent.VK_SPACE);
        keyControls.set(KeyEvent.VK_P);
        
        setLocation(200,200);

  
        setScale(.8);

        setScreenWrap(true,painter);

        //this.setBoundsVisible(true);
    }

    private void processAWTKeyPress(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_P)
        {
            //To pause game
            AnimationThread at = game.getPainter().getAnimationThread();
            if (at.isRunning())
            {
                at.stop();
            }
            else at.start();
        }
    }

    public void explode()
    {
        setVisible(false);
        game.showExplosion(getLocation().x, getLocation().y);

        game.removeLife();

        if (game.getNumLives() < 0)
        {
            //Do game over sequence
            game.gameOver();
        }
        else
        {
            hideDelay = 2;
            temporarilyHide();
        }

    }

    public void temporarilyHide()
    {
        safeBounds.setFrame(getLocation().x-125, getLocation().y-125, 250, 250);
        this.setVisible(false);
        timer = 0;
        tempHiding = true;
    }

    @Override
    public void updateState(float interpolation)
    {
        //System.out.println("Time="+(time += interpolation) + " Update="+(update+=1));

        if (tempHiding) {
            timer += interpolation;

            if (timer > hideDelay)
            {
                updateTempHiding();
                return;
            }
           
        }

        if (keyControls.isPressed(KeyEvent.VK_LEFT))
        {
            rotate(-Math.PI/12);
        }
        else if (keyControls.isPressed(KeyEvent.VK_RIGHT))
        {
            rotate(Math.PI/12);
        }

        if (keyControls.isPressed(KeyEvent.VK_UP))
        {
            move(getRotation(),8);
        }
        else if (keyControls.isPressed(KeyEvent.VK_DOWN))
        {
            move(getRotation(),-8);
        }

        if (keyControls.isTapped(KeyEvent.VK_SPACE))
        {
            fireBlast();
        }
        
    }

    private void updateTempHiding()
    {
        AsteroidSprite[] asteroids = game.getAsteroids();

        boolean clear = true;

        for (int i = 0; i < asteroids.length; i++)
        {
            AsteroidSprite aster = asteroids[i];
            if (aster.isVisible() && safeBounds.intersects(aster.getCollisionBounds().getBounds2D()))
            {
                clear = false;
                break;
            }
        }

        if (clear) {
            tempHiding = false;
            setVisible(true);
        }
    }

    @Override
    public void paintObject(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;

        if (tempHiding) 
        {
            Color oldColor = g2.getColor();
            Composite oldComposite = g2.getComposite();

            g2.setComposite(transparent);
            g2.setColor(hideColor);

            g2.fill(safeBounds);

            g2.setColor(oldColor);
            g2.setComposite(oldComposite);
        }

        

        super.paintObject(g);
    }

    public void fireBlast()
    {
        if (isVisible())
        {
            //search for invisible blast to fire
            for (int i = 0; i < blasts.length; i++)
            {
                Blast blast = blasts[i];

                if (!blast.isVisible())
                {
                    game.getSoundPlayer().playSound("sounds/laserfire3.wav");

                    blast.setVisible(true);
                    blast.setLocation(getLocation());
                    blast.move(getRotation(), 30);                    
                    blast.setTheta(getRotation());
                    return;
                }
            }
        }
    }

}
