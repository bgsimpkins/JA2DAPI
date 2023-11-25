/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twodapi.effects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import twodapi.core.Frame2D;
import twodapi.core.KeyControls;
import twodapi.core.Paintable;
import twodapi.core.GamePainter;
import twodapi.core.Sprite2D;

/**A basic explosion particle system. The parameters used to initialize the system are number of particles, origin, base speed,
 * variability (in speed), and maximum radius. The particles can be points or <code>Sprite2D</code> objects.
 * If sprites are used, individual sprite states can be tweaked each update by overriding the <code>updateSprites()</code>
 * function.
 *
 * @author bsimpkins
 */
public class ParticleExplosion implements Paintable
{
    private boolean visible = false;
    private int maxRadius = 100;
    private float speed = 200;
    private float variability = 10;
    private int numParticles = 50;

    public static final int POINTS = 1;
    public static final int SPRITES = 2;

    private int type = 0;

    private float pointX[] = null;
    private float pointY[] = null;

    private int moveX[] = null;
    private int moveY[] = null;

    private Sprite2D[] sprites = null;

    private boolean exploding = false;

    private Point origin = new Point();

    private Color color = Color.BLACK;

    private int particleSize = 5;

    private boolean disappearAfterExplosion = true;

    private String spriteImage = null;

    /**Creates a new instance of ParticleExplosion. Inits particles as <code>POINTS</code> and the number of particles
     * specified and uses defaults for all other parameters.
     *
     * @param numParticles The number of particles in the explosion.
     */
    public ParticleExplosion(int numParticles)
    {
        this.numParticles = numParticles;
        setType(POINTS);
    }

    /**Creates a new instance of ParticleExplosion. Inits particles as <code>SPRITES</code> which are {@link Sprite2D}
     * objects created from the specified image file.
     *
     * @param image Filename of the image used to create the sprites.
     * @param numParticles The number of particles used in the explosion.
     */
    public ParticleExplosion(String image, int numParticles)
    {
        this.spriteImage = image;
        this.numParticles = numParticles;
        setType(SPRITES);
    }

    /**Creates a new instance of ParticleExplosion. Inits particles with the specified parameters.
     *
     * @param origin Origin of the explosion
     * @param maxRadius Once all particles pass beyond this radius, the explosion disappears
     * @param speed The base speed of the particles
     * @param variability The variability in speed of the particles. Uses a flat distribution for this.
     * @param type Particle type: <code>POINTS</code> or <code>SPRITES</code>
     * @param numParticles The number of particles in the explosion.
     */
    public ParticleExplosion(Point origin, int maxRadius,float speed, float variability,int type, int numParticles)
    {

        this.origin = origin;
        this.maxRadius = maxRadius;
        this.speed = speed;
        this.variability = variability;
        this.numParticles = numParticles;
        setType(type);
        
    }

    private void init()
    {
        double radIncrement = 2*Math.PI/getNumParticles();
        double incI = 0;

        for (int i = 0; i < getNumParticles(); i++)
        {
            moveX[i] = (int)Math.round(Math.cos(incI) * getVariableSpeed(speed));
            moveY[i] = (int)Math.round(Math.sin(incI) * getVariableSpeed(speed));

            incI += radIncrement;
        }
    }

    private void createSprites(String image)
    {
        for (int i = 0; i < numParticles; i++)
        {
            final int fI = i;
            Sprite2D sprite = new Sprite2D(image)
            {
                @Override
                public void updateState(float interpolation)
                {
                    updateSprites(this,fI,interpolation);
                }

            };

            sprites[i] = sprite;
        }
    }

    /**Sets all the relevant particle explosion parameters. NOTE: This does not create another explosion system.
     * Call <code>refreshParticles()</code> to do this.
     *
     * @param origin Origin of the explosion
     * @param maxRadius Once all particles pass beyond this radius, the explosion disappears
     * @param speed The base speed of the particles
     * @param variability The variability in speed of the particles. Uses a flat distribution for this.
     * @param numParticles The number of particles in the explosion.
     */
    public void setAttributes(Point origin, int maxRadius,float speed,float variability,int numParticles)
    {
        this.setOrigin(origin);
        this.maxRadius = maxRadius;
        this.speed = speed;
        this.variability = variability;
        this.setNumParticles(numParticles);
    }

    /**Re-inits the particle system so a new explosion is created.*/
    public void refreshParticles()
    {
        init();
    }

    private float getVariableSpeed(float speedIn)
    {
        float lowerBound = speed - variability;

        return lowerBound + (float)Math.random()*variability*2;
    }

    private void setParticlesToOrigin()
    {
        if (type == POINTS)
        {
            for (int i = 0; i < getNumParticles(); i++)
            {
                pointX[i] = getOrigin().x;
                pointY[i] = getOrigin().y;
            }
        }
        else if (type == SPRITES)
        {
            for (int i = 0; i < getNumParticles(); i++)
            {
                pointX[i] = getOrigin().x;
                pointY[i] = getOrigin().y;

                Sprite2D sprite = sprites[i];

                sprite.setLocation(origin);
            }

        }

    }

    /**Override of the {@link Paintable} method. Do not call directly*/
    public void update(float interpolation) 
    {
        if (!isVisible()) return;

        if (!exploding) return;

        int nOutofBounds = 0;
      
        for (int i = 0; i < getNumParticles(); i++)
        {
            double distance = getDistance(pointX[i],pointY[i],getOrigin().x,getOrigin().y);

            if (distance >= maxRadius)
            {
                nOutofBounds++;

            }

            pointX[i] += (float)moveX[i]*interpolation;
            pointY[i] += (float)moveY[i]*interpolation;


            if (type == SPRITES)
            {

                sprites[i].setLocation((int)Math.round(pointX[i]), (int)Math.round(pointY[i]));

                sprites[i].update(interpolation);
            }

        }
        if (nOutofBounds == getNumParticles())
        {
            exploding = false;
            if (disappearAfterExplosion()) visible = false;
        }
        
        
        
    }

    private double getDistance(float x1, float y1, float x2, float y2)
    {
        return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }

    /**Override of the {@link Paintable} method. Do not call directly*/
    public void paintObject(Graphics g) 
    {
        if (!isVisible()) return;

        g.setColor(getColor());

        if (type == POINTS)
        {
            for (int i = 0; i < getNumParticles(); i++)
            {
                g.fillOval((int)Math.round(pointX[i])-getParticleSize()/2, (int)Math.round(pointY[i])-getParticleSize()/2, getParticleSize(), getParticleSize());
            }
        }
        else if (type == SPRITES)
        {
            for (int i = 0; i < getNumParticles(); i++)
            {
                sprites[i].paintObject(g);
            }
        }

        
    }

    /**Sets the particles back at the origin and causes a new explosion*/
    public void explode()
    {       
        
        setParticlesToOrigin();
        exploding = true;

    }

    /**Return the max radius. During an explosion, if all particles past beyond this radious and <code>disappearAfterExplosion</code>
     * is set to true, the explosion will disappear.
     *
     * @return the maxRadius The max radious.
     */
    public int getMaxRadius() {
        return maxRadius;
    }

    /**Sets the max radius. During an explosion, if all particles past beyond this radious and <code>disappearAfterExplosion</code>
     * @param maxRadius the maxRadius to set
     */
    public void setMaxRadius(int maxRadius) {
        this.maxRadius = maxRadius;
    }

    /**Returns the base speed of the particles.
     * @return Base speed of the particles.
     */
    public float getSpeed() {
        return speed;
    }

    /**Sets the base speed of the particles.
     * @param speed  The base speed to set.
     */
    public void setSpeed(float speed) {
        this.speed = speed;

        init();
    }

    /**Returns the variability of the speed among the particles.
     * @return the variability
     */
    public float getVariability() {
        return variability;
    }

    /**Sets the variability of speed among the particles.
     * @param variability the variability to set
     */
    public void setVariability(float variability) {
        this.variability = variability;

        init();
    }

    /**Returns the particle type(<code>POINTS</code> or <code>SPRITES</code>).
     * @return the particle type
     */
    public int getType() {
        return type;
    }

    /**Sets the particle type(<code>POINTS</code> or <code>SPRITES</code>).
     * @param type the type to set
     */
    public void setType(int type)
    {
        if (type != POINTS && type != SPRITES)
        {
            System.err.println(this.getClass().getName()+": Invalid type for particle explosion. Please use ParticleExplosion type constants)");
            System.exit(1);
        }

        if (type == POINTS && this.type != POINTS)
        {
            pointX = new float[getNumParticles()];
            pointY = new float[getNumParticles()];
        }
        else if (type == SPRITES && this.type != SPRITES)
        {
            pointX = new float[getNumParticles()];
            pointY = new float[getNumParticles()];

            if (sprites == null)
            {
                sprites = new Sprite2D[getNumParticles()];
                createSprites(spriteImage);
            }
            

        }

        moveX = new int[getNumParticles()];
        moveY = new int[getNumParticles()];
        
        this.type = type;

        init();

    }

    /**Returns true if this particle system is visible.
     * @return True if visible.
     */
    public boolean isVisible() {
        return visible;
    }

    /**Set to true if particles are to be visible
     * @param visible Tru if visible.
     */
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    
    public static void main(String[] args)
    {
        Frame2D frame = new Frame2D(Frame2D.REG_PAINTER,1024,768,true);
        GamePainter painter = (GamePainter)frame.getPainter();

        //final ParticleExplosion partExpl = new ParticleExplosion(new Point(400,300),400,200,100,ParticleExplosion.POINTS,100);

        int numParticles = 50;

        final float[] thetas = new float[numParticles];
        float lowerBound = -(float)Math.PI/16f;
        float var = (float)Math.PI/8;

        for ( int i = 0 ; i < numParticles; i++)
        {
            thetas[i] = lowerBound + (float)Math.random()*var;
        }

        /*final ParticleExplosion partExpl = new ParticleExplosion("/images/gas.png",numParticles)
        {

            @Override
            public void updateSprites(Sprite2D sprite,int index,float interpolation)
            {
                //sprite.setComponent(panel);
                sprite.rotate(thetas[index]);
                //sprite.scale(.97);

                //if (sprite.getScale() <= .1)
                //{
                //    this.setVisible(false);
                //    sprite.setScale(1);
                //}
            }
        };*/

        final ParticleExplosion partExpl = new ParticleExplosion(numParticles);

        partExpl.setMaxRadius(300);
        partExpl.setSpeed(280);
        partExpl.setVariability(200);
        partExpl.setOrigin(new Point(512,384));


        KeyControls controls = new KeyControls(painter.getComponent())
        {
            @Override
            public void keyPressedEvent(KeyEvent e)
            {
                if (e.getKeyCode()==KeyEvent.VK_SPACE)
                {
                    partExpl.setVisible(true);
                    partExpl.explode();

                    //Init particles again so we get a different explosion
                    partExpl.refreshParticles();
                }
            }
        };

        controls.set(KeyEvent.VK_SPACE);

        painter.setBackground(Color.WHITE);
        //painter.setUseVolatileBackbuffer(true);
        
        painter.addToPaintList(partExpl);


        //partExpl.explode();
    }

    protected void updateSprites(Sprite2D sprite,int index, float interpolation)
    {}

    /**Returns the number of particles
     * @return Number of particles
     */
    public int getNumParticles() {
        return numParticles;
    }

    /**Sets the number of particles.
     * @param numParticles the numParticles to set
     */
    public void setNumParticles(int numParticles)
    {
        

        moveX = new int[numParticles];
        moveY = new int[numParticles];

        if (type == POINTS)
        {
            pointX = new float[numParticles];
            pointY = new float[numParticles];
        }
        else if (type == SPRITES)
        {
            sprites = new Sprite2D[numParticles];
            createSprites(spriteImage);
        }

        this.numParticles = numParticles;

        init();
    }

    /**Returns the color of the particles (only relevant if particle type is <code>POINTS</code>).
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**Sets the color of the particles (only relevant if particle type is <code>POINTS</code>).
     * @param color the color to set
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * @return the particleSize
     */
    public int getParticleSize() {
        return particleSize;
    }

    /**Sets the size of the particles (only relevant if type is <code>SPRITES</code>
     * @param particleSize the particleSize to set
     */
    public void setParticleSize(int particleSize)
    {
        this.particleSize = particleSize;

        if (type == SPRITES)
        {

            for (int i= 0; i < numParticles; i++)
            {
                sprites[i].setScale(particleSize);
            }
        }

    }

    /**Returns true if particles are to disappear after explosion
     * @return True if particles disappear after explosion
     */
    public boolean disappearAfterExplosion() {
        return disappearAfterExplosion;
    }

    /**Set to true if particles are to disappear after explosion
     * @param disappearAfterExplosion True if particles disappear after explosion
     */
    public void setDisappearAfterExplosion(boolean disappearAfterExplosion) {
        this.disappearAfterExplosion = disappearAfterExplosion;
    }

    /**Returns the origin of the explosion
     * @return the origin
     */
    public Point getOrigin() {
        return origin;
    }

    /**Sets the origin of the explosion
     * @param origin The origin
     */
    public void setOrigin(Point origin) {
        this.origin = origin;
    }

}
