/*
 * Asteroid.java
 *
 * Created on August 3, 2009, 10:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package GameTest;

import twodapi.audio.SimpleAudioPlayer;
import twodapi.core.*;

/**
 *
 * @author bsimpkins
 */
public class AsteroidSprite extends Sprite2D
{
    int moveAmt = 0;
    double moveTheta = 0d;

    double rotAmt = 0;

    private TestGame game;

    private static final float SMALL_SCALE=.5f;

    private static final float MED_SCALE=.8f;

    private static final int SMALL = 1;
    private static final int MED = 2;
    private static final int LARGE = 3;

    private int relSize = LARGE;
    
    /** Creates a new instance of Asteroid */
    public AsteroidSprite(TestGame game)
    {
        super("/images/Asteroid.gif");

        this.game = game;

        double randomAngle = 2*Math.PI*Math.random();
        double randomLength = Math.random();
        
        moveAmt = 50+(int)Math.round(300*randomLength);


        moveTheta = randomAngle;

        rotAmt = Math.PI/12*Math.random();

        setVisible(false);
    }

    public void explode()
    {
        setVisible(false);

        SimpleAudioPlayer aPlayer = game.getSoundPlayer();

        //If this asteroid is MED or LARGE, break it up into three smaller ones
        if (relSize > SMALL)
        {
            aPlayer.playSound("sounds/explosion.wav");

            /////We need to loop through the available asteroids and see which ones are availabe for
            //breakaway asteroids and update them

            AsteroidSprite[]asters = game.getAsteroids();

            //Count of the number of asteroids we have updated (catch max at 3)
            int asterCount = 0;

            //Incremented 2PI/3 each time an asteroid is found (so all breakaway asteroids are travelling in diff directions
            double thetaStart = 0;

            int size = this.relSize - 1;

            //find available asteroids
            for (int i = 0; i < asters.length; i++)
            {
                AsteroidSprite a = asters[i];

                if (!a.isVisible)
                {
                    //Determine direction of movement semi-randomly
                    double theta = thetaStart + Math.random()*2*Math.PI/3;
                    updateAsteroid(a,theta,size);
                    
                    thetaStart += 2*Math.PI/3;

                    asterCount++;
                    if (asterCount==3) break;
                }
            }

            //System.out.println("num asters="+newAsters.size());

        }
        else
        {
            aPlayer.playSound("sounds/explosion_small.wav");
        }

        game.showExplosion(getLocation().x, getLocation().y);
        

    }

    private void updateAsteroid(AsteroidSprite a, double theta, int size)
    {

        a.setRelativeSize(size);
        if (size == SMALL) a.setScale(SMALL_SCALE);
        else if (size == MED) a.setScale(MED_SCALE);

        a.setLocation(this.getLocation().x,this.getLocation().y);

        a.setMoveAngle(theta);

        a.update(0);
        a.setVisible(true);
    }

    public void setMoveAngle(double moveTheta)
    {
        this.moveTheta = moveTheta;
    }

    public void setRelativeSize(int relSize)
    {
        this.relSize = relSize;
    }

    @Override
    public void updateState(float interpolation)
    {
        if (isVisible())
        {
            move(moveTheta,(int)(moveAmt*interpolation));
            rotate(rotAmt);
        }
        

    }
    
}
