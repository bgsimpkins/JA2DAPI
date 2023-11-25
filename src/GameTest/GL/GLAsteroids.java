/*
 * GameTest.java
 *
 * Created on August 3, 2009, 10:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package GameTest.GL;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import twodapi.audio.SimpleAudioPlayer;
import twodapi.lwjgl.FlipBookSpriteGL;
import twodapi.lwjgl.GLGame;
import twodapi.lwjgl.GLPainter;
import twodapi.lwjgl.GameController;
import twodapi.lwjgl.KeyControlsGL;
import twodapi.lwjgl.MouseControlsGL;
import twodapi.lwjgl.PolygonGL;
import twodapi.lwjgl.SpriteGL;
import twodapi.lwjgl.TextGL;


/**
 *
 * @author bsimpkins
 */
public class GLAsteroids extends GLGame
{
    private final static int MENU_STATE = 0;
    private final static int CONTROLLER_CONFIG_STATE = 1;
    private final static int GAME_STATE = 2;
    private final static int GAME_OVER_STATE = 3;

    private int state;

    private ShipSprite ship;
    private AsteroidSprite[] asteroids;
    private Blast[] blastList;
    private Explosion[] explosions;
    private int lives;
    private BufferedImage background;
    private int level;
    private SpriteGL lilShip;
    private TextGL numLivesDisplay;
    private TextGL levelDisplay;
    private SimpleAudioPlayer aPlayer;
    private PolygonGL safeCircle;
    private TextGL levelAnnounce;
    private float announceTimer = 0f;
    private GameController gc;

    private GLPainter painter;

    public GLAsteroids(boolean fullscreen)
    {
        painter = createPainter(1024,768);
        painter.setFullscreen(fullscreen);
        start();
    }

    @Override
    public void initGame()
    {
        Display.setTitle("Never Tell Me the Odds!!!");
        painter.setBackground("/images/purple-galaxy-stars.jpg");

        gc = new GameController();
        gc.setActions(new String[]{"Rotate Left","Rotate Right","Jet Forward","Jet Backwards","Fire Blast"});

        state = this.MENU_STATE;
        makeTitleState();
        

        //initGameState();
    }


    private void makeTitleState()
    {
        painter.removeAll();

        SpriteGL title = new SpriteGL("/images/Asteroids_title.png");
        title.setLocation(painter.getSize().width/2,painter.getSize().height/2-50);
        painter.addToPaintList(title,1);
        TextGL pressButton = new TextGL("Press Enter to Start",new Font("serif",Font.PLAIN,24),Color.BLUE);
        pressButton.setCenterText(true);
        pressButton.setLocation(painter.getSize().width/2,painter.getSize().height/2+50);
        painter.addToPaintList(pressButton,1);
    }

    private void initGameState()
    {
        painter.removeAll();

        level = 1;
        lives = 10;

        lilShip = new SpriteGL("/images/ship.gif");
        numLivesDisplay = new TextGL(""+lives,new Font("sanserif",Font.PLAIN,20),Color.GREEN);
        levelDisplay = new TextGL("Level:  1",new Font("sanserif",Font.PLAIN,20),Color.GREEN);

        if (aPlayer == null)
        {
            aPlayer = new SimpleAudioPlayer(new String[]{
                "sounds/explosion.wav",
                "sounds/explosion_high.wav",
                "sounds/explosion_small.wav",
                "sounds/laserfire3.wav",
                "sounds/music.wav"
            });
        }

        blastList = new Blast[10];
        explosions = new Explosion[8];
        initAsteroids();
        initBlasts(painter);
        initExplosions(painter);
        ship = new ShipSprite(blastList,this);
        //ship.setVisible(false);
        painter.addToPaintList(ship,2);

        lilShip.setScale(.5f,.5f);
        lilShip.setLocation(50, 40);
        painter.addToPaintList(lilShip,3);
        numLivesDisplay.setLocation(80,28);
        painter.addToPaintList(numLivesDisplay,3);
        levelDisplay.setLocation(300,28);
        painter.addToPaintList(levelDisplay,3);
        safeCircle = new PolygonGL();
        safeCircle.setColor(50, 50, 50);
        safeCircle.createCircle(125, 125, null);
        safeCircle.setVisible(false);
        safeCircle.setTransparency(.4f);
        painter.addToPaintList(safeCircle,2);

        ship.temporarilyHide();
        aPlayer.loopSound("sounds/music.wav");

        levelAnnounce = new TextGL("Level 1",new Font("sanserif",Font.PLAIN,56),new Color(30,90,255));
        levelAnnounce.setTransparency(.6f);
        levelAnnounce.setCenterText(true);
        levelAnnounce.setLocation(painter.getSize().width/2, painter.getSize().height/2);
        //levelAnnounce.setVisible(false);
        painter.addToPaintList(levelAnnounce,4);
    }

     private void initAsteroids()
    {
        asteroids = new AsteroidSprite[level*9];
        for (int i = 0; i < asteroids.length; i++)
        {
            asteroids[i] = new AsteroidSprite(this);
            asteroids[i].setScreenWrap(true, painter);
            painter.addToPaintList(asteroids[i],2);

            if (i < level)
            {
               //set location randomly
                int x = (int)(Math.random()*painter.getSize().width);
                int y = (int)(Math.random()*painter.getSize().height);
                asteroids[i].setLocation(x, y);
                asteroids[i].setVisible(true);
            }
        }
    }

    AsteroidSprite[] getAsteroids()
    {
        return asteroids;
    }

    public void showExplosion(float x, float y)
    {
        Explosion expl = null;
        for (int i = 0; i < explosions.length; i++)
        {
            if (!explosions[i].isVisible())
            {
                expl = explosions[i];
                break;
            }
        }
        if (expl != null)
        {
            expl.setLocation((int)x, (int)y);
            expl.setVisible(true);
            expl.animateOnce();
        }
        else
        {
            System.out.println("Warning: Couldn't get an explosion from the queue to show!");
        }
    }

    private void initBlasts(GLPainter painter)
    {
        for (int i = 0; i < blastList.length; i++)
        {
            blastList[i] = new Blast(painter);
            blastList[i].setVisible(false);
            painter.addToPaintList(blastList[i],2);
        }
    }

    private void initExplosions(GLPainter painter)
    {
        for (int i = 0; i < explosions.length; i++)
        {
            explosions[i] = new Explosion();
            painter.addToPaintList(explosions[i],1);
        }
    }

    private void printSoundList()
    {
        ArrayList list = aPlayer.getSoundList();
        System.out.println("\nSound list\n_______________________________");
        for (int i = 0; i < list.size(); i++)
        {
            System.out.println("   "+list.get(i));
        }
        System.out.println("");
    }

    @Override
    public void update(float interpolation)
    {
        if (state == GAME_STATE) updateGameState(interpolation);

        else if (state == GAME_OVER_STATE && Keyboard.isKeyDown(Keyboard.KEY_SPACE))
        {
            state = MENU_STATE;
            makeTitleState();
        }

        else if (state == CONTROLLER_CONFIG_STATE && gc.isConfigFinished())
        {
            initGameState();
            state = GAME_STATE;
        }
        else if (state == MENU_STATE && Keyboard.isKeyDown(Keyboard.KEY_RETURN))
        {
            painter.removeAll();

            if (gc.getNumControllers() > 0)
            {
                painter.addToPaintList(gc.createConfigScreen(new MouseControlsGL()));
                state = CONTROLLER_CONFIG_STATE;
            }
            else
            {
                state = GAME_STATE;
                initGameState();
            }
        }

        
    }

    private void updateGameState(float interpolation)
    {
        if (levelAnnounce.isVisible())
        {
            announceTimer += interpolation;
            if (announceTimer > 3f)
            {
                levelAnnounce.setVisible(false);
                announceTimer = 0f;
            }
        }

        //check for ship/asteroid collisions
        if ( ship.isVisible())
        {
            for (int i = 0; i < asteroids.length; i++)
            {
                if (asteroids[i].isVisible() && ship.collidesWith(asteroids[i]))
                {
                    aPlayer.playSound("sounds/explosion_high.wav");
                    ship.explode();
                }

                //check for blast/asteroid collisions
                for (int j = 0; j < blastList.length; j++)
                {
                    if (blastList[j].isVisible() && blastList[j].collidesWith(asteroids[i]))
                    {
                        blastList[j].setVisible(false);
                        asteroids[i].explode();

                        checkForNextLevel();
                        break;
                    }
                }
            }
        }
    }

    private void checkForNextLevel()
    {
        boolean nextLevel = true;
        for (int i = 0; i < asteroids.length; i++)
        {
            if (asteroids[i].isVisible())
            {
                nextLevel = false;
                break;
            }
        }
        if (nextLevel)
        {
            level++;
            ship.temporarilyHide();
            initAsteroids();
            levelAnnounce.setText("Level "+level);
            levelAnnounce.setVisible(true);
            levelDisplay.setText("Level:  "+level);
        }
    }

    public SimpleAudioPlayer getSoundPlayer()
    {
        return aPlayer;
    }

    public GameController getGameController()
    {
        return gc;
    }

    public int getNumLives()
    {
        return lives;
    }

    public void removeLife()
    {
            lives--;
            if (lives >= 0) numLivesDisplay.setText(""+lives);
    }

    public void gameOver()
    {
        Dimension size = painter.getSize();
        TextGL gameEndText = new TextGL("Game Over",new Font("sanserif",Font.BOLD,60),Color.RED);
        gameEndText.setCenterText(true);
        gameEndText.setLocation(size.width/2, size.height/2);
        painter.addToPaintList(gameEndText,4);

    }

    public static void main(String[] args)
    {
        boolean fullscreen = true;

        if (args != null && args.length > 0)
        {
            if (args[0].equalsIgnoreCase("-fs") || args[0].equalsIgnoreCase("-fullscreen")) fullscreen = true;
        }

        GLAsteroids gameTest = new GLAsteroids(fullscreen);
    }

    class AsteroidSprite extends SpriteGL
    {
        int moveAmt = 0;
        float moveTheta = 0f;
        double rotAmt = 0;
        private GLAsteroids game;
        private static final float SMALL_SCALE=.5f;
        private static final float MED_SCALE=.8f;
        private static final int SMALL = 1;
        private static final int MED = 2;
        private static final int LARGE = 3;
        private int relSize = LARGE;

        /** Creates a new instance of Asteroid */
        public AsteroidSprite(GLAsteroids game)
        {
            super("/images/Asteroid.gif");
            this.game = game;
            double randomAngle = 2*Math.PI*Math.random();
            double randomLength = Math.random();
            moveAmt = 50+(int)Math.round(300*randomLength);
            moveTheta = (float)Math.toDegrees(randomAngle);
            rotAmt = Math.toDegrees((4*Math.PI)*Math.random());
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
                    if (!a.isVisible())
                    {
                        //Determine direction of movement semi-randomly
                        double theta = thetaStart + Math.random()*2*Math.PI/3;
                        updateAsteroid(a,(float)Math.toDegrees(theta),size);
                        thetaStart += 2*Math.PI/3;
                        asterCount++;
                        if (asterCount==3) break;
                    }
                }
            }
            else
            {
                aPlayer.playSound("sounds/explosion_small.wav");
            }
            game.showExplosion((int)getLocation().x, (int)getLocation().y);
        }

        private void updateAsteroid(AsteroidSprite a, float theta, int size)
        {
            a.setRelativeSize(size);
            if (size == SMALL) a.setScale(SMALL_SCALE,SMALL_SCALE);
            else if (size == MED) a.setScale(MED_SCALE,MED_SCALE);
            a.setLocation(this.getLocation().x,this.getLocation().y);
            a.setMoveAngle(theta);
            //a.update(0);
            //force a draw so transformation matrix is updated when set to visible
            a.draw(0);
            a.setVisible(true);
        }

        public void setMoveAngle(float moveTheta)
        {
            this.moveTheta = moveTheta;
        }

        public void setRelativeSize(int relSize)
        {
            this.relSize = relSize;
        }

        public void update(float interpolation)
        {
            if (isVisible())
            {
                moveRadial(moveTheta,moveAmt*interpolation);
                rotate((float)rotAmt*interpolation);
            }
        }
    }

    class ShipSprite extends SpriteGL
    {
        private KeyControlsGL keyControls;
        private Blast[] blasts;
        private GLAsteroids game;
        private Ellipse2D safeBounds = new Ellipse2D.Double(0,0,250,250);
        private boolean tempHiding = false;
        private int hideDelay = 1;
        private float timer = 0f;
        private final Color hideColor = new Color(200,200,200);
        private float blastTimer = 0f;

        /** Creates a new instance of ShipSprite */
        public ShipSprite(Blast[] blasts,GLAsteroids game)
        {
            super("/images/ship.gif");
            this.game = game;
            this.blasts = blasts;
            keyControls = createKeyControls();
            keyControls.set(Keyboard.KEY_LEFT);
            keyControls.set(Keyboard.KEY_RIGHT);
            keyControls.set(Keyboard.KEY_UP);
            keyControls.set(Keyboard.KEY_DOWN);
            keyControls.set(Keyboard.KEY_SPACE);
            keyControls.set(Keyboard.KEY_P);
            setLocation(200,200);
            setScale(.8f,.8f);
            setScreenWrap(true,painter);
            //this.setDrawBounds(true);
        }

        public void explode()
        {
            setVisible(false);
            game.showExplosion(getLocation().x, getLocation().y);
            game.removeLife();
            if (game.getNumLives() < 0)
            {
                //Do game over sequence
                state = GAME_OVER_STATE;
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
            safeCircle.setLocation(this.getLocation());
            safeCircle.setVisible(true);
            tempHiding = true;
        }

        @Override
        public void update(float interpolation)
        {
            if (tempHiding) {
                timer += interpolation;
                if (timer > hideDelay)
                {
                    updateTempHiding();
                    return;
                }
                return;
            }
            GameController gc = game.getGameController();

            if (blastTimer < .2f) blastTimer += interpolation;

            if (keyControls.isPressed(Keyboard.KEY_LEFT) || gc.isPressed("Rotate Left"))
            {
                rotate(-500*interpolation);
            }
            else if (keyControls.isPressed(Keyboard.KEY_RIGHT) || gc.isPressed("Rotate Right"))
            {
                rotate(500*interpolation);
            }
            if (keyControls.isPressed(Keyboard.KEY_UP) || gc.isPressed("Jet Forward"))
            {
                moveRadial(getRotation()-90,300*interpolation);
            }
            else if (keyControls.isPressed(Keyboard.KEY_DOWN) || gc.isPressed("Jet Backwards"))
            {
                moveRadial(getRotation()-90,-300*interpolation);
            }

            if ((blastTimer >= .1f && ((gc.isPressed("Fire Blast")) || keyControls.isPressed(Keyboard.KEY_SPACE))))
            {
                fireBlast();
                blastTimer = 0f;
            }

//            if (keyControls.isPressed(Keyboard.KEY_LEFT) && keyControls.isPressed(Keyboard.KEY_UP) /*&& keyControls.isPressed(Keyboard.KEY_SPACE)*/)
//            {
//                //System.out.println("sTUFF");
//                fireBlast();
//                blastTimer = 0f;
//            }
            
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
                safeCircle.setVisible(false);
                setVisible(true);
            }
            //TODO: draw transparent circle
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
                        blast.setLocation(this.getLocation());
                        blast.setRotation(this.getRotation()-90);
                        blast.moveRadial(this.getRotation()-90, 30);
                        blast.setTheta(this.getRotation()-90);
                        //force a draw so transformation matrix is updated when set to visible
                        blast.draw(0f);
                        blast.setVisible(true);
                        //blast.animateLoop();                     
                        return;
                    }
                }
            }
        }

    }

    class Blast extends SpriteGL
    {
        private GLPainter painter;
        private float theta = 0f;

        public Blast(GLPainter painter)
        {
            super("/images/fireball.gif");
            this.painter = painter;
            //setDrawBounds(true);
        }

        public void setTheta(float theta)
        {
            this.theta = theta;
        }

        @Override
        public void update(float interpolation)
        {
            if (isVisible() && isOffscreen(painter)) setVisible(false);
            if (isVisible())
            {
                moveRadial(theta, interpolation*800);
            }
        }
    }

    class Explosion extends FlipBookSpriteGL
    {
        public Explosion()
        {
            super("/images/Explosion/Explosion.gif");
            setFrameDuration(.02f);
            setScale(3f,3f);
            setVisible(false);
        }

        @Override
        public void update(float interpolation)
        {
            if (!isVisible()) return;
            //if last frame is shown, make explosion disappear
            if (this.getFrameIndex() >= 15)
            {
                setVisible(false);
            }
        }
    }
}
