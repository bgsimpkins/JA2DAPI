/*
 * GameThread.java
 *
 * Created on December 26, 2007, 9:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package twodapi.core;
import java.util.*;
//import com.sun.j3d.utils.timer.*;

/**This class encapsulates a {@link Thread} that is designed to maintain a specified
 * frame rate, which is intended to make game state update and render calls that can
 * drive smooth animation. An instance of this class is used internally in this API's
 * {@link GamePainter} class, and it is not required to instantiate this class to get
 * an animation-based application running.
 *
 * If, for whatever reason, you do decide to instantiate this class, implement the
 * {@link Animatable} interface for all classes that wish to receive game state and
 * render updates and register them with with this <code>AnimationThread</code> by
 * calling <code>addListener()</code>.
 *
 * This animation framework is designed to maintain a game state update rate
 * as closely as possible. If there are performance issues, a few <code>render()</code>
 * calls may be skipped to ensure a steady update rate.
 *
 * This framework is largely based on the animation framework layed out by Andrew Davison
 * in his "Killer Game Programming for Java" book.
 *
 * @author Andrew Davison, Ben
 */
public class AnimationThread implements Runnable{
    Thread t;
    ArrayList<Animatable> animatables = new ArrayList();
    
    public static int period = 20000000;          //frame period for 50 FPS in nanosecs
    
    /**Number of frames with a delay of 0 ms before the animation thread yields
     to other running threads. */
    public static final int NO_DELAYS_PER_YIELD = 16;
    
    /** record stats every 1 second (roughly)*/
    private static long MAX_STATS_INTERVAL = 1000000000L;
    
    /** no. of frames that can be skipped in any one animation loop
     *i.e the games state is updated but not rendered
     */
    private static int MAX_FRAME_SKIPS = 5;   // was 2;
    
    /** number of FPS values stored to get an average*/
    private static int NUM_FPS = 10;
    
    
    private volatile boolean running = false;
   
    // used for gathering statistics
    private long statsInterval = 0L;    // in ns
    private long prevStatsTime;   
    private long totalElapsedTime = 0L;
    private long gameStartTime;
    private int timeSpentInGame = 0;    //in seconds
    
    private long frameCount = 0;
    private double fpsStore[];
    private long statsCount = 0;
    private double averageFPS = 0.0;
    
    private long framesSkipped = 0L;
    private long totalFramesSkipped = 0L;
    private double upsStore[];
    private double averageUPS = 0.0;

    private boolean collectStats = false;

    private float interpolation = 0f;
    private long timeSinceLast = 0;
    
    /** Creates a new instance of GameThread. A default frame rate of 50 FPS is used.*/
    public AnimationThread() {
        // initialise timing elements
        fpsStore = new double[NUM_FPS];
        upsStore = new double[NUM_FPS];
        for (int i=0; i < NUM_FPS; i++) {
            fpsStore[i] = 0.0;
            upsStore[i] = 0.0;
        }
       
        t = new Thread();
        t.start();
    }
    
    /** Creates a new instance of GameThread
     *@param fps Desired frame rate in frames/sec
     */
    public AnimationThread(int fps) 
    {
        period = (int)Math.round((1/(float)fps)*Math.pow(10,9));
        // initialise timing elements
        fpsStore = new double[NUM_FPS];
        upsStore = new double[NUM_FPS];
        for (int i=0; i < NUM_FPS; i++) {
            fpsStore[i] = 0.0;
            upsStore[i] = 0.0;
        }
       
        t = new Thread();
        t.start();
    }

    /**Sets the frame rate of the animation updates
     *
     * @param fps Frames per second
     */
    public void setFrameRate(int fps)
    {
        period = (int)Math.round((1/(float)fps)*Math.pow(10,9));
    }

    /**Adds a an <code>Animatable</code>, which will subsequently
     * receive <code>udpateState()</code> and <code>render()</code> calls.
     * @param u Listener to receive updates.
     */
    public void addListener(Animatable u){
        
        animatables.add(u);
    }
    
    private void notifyUpdate()
    {
        interpolation = (float)(System.nanoTime() - timeSinceLast)/1000000000f;
        timeSinceLast = System.nanoTime();

        synchronized(animatables){
            Iterator iter = animatables.iterator();
            
            while (iter.hasNext()){
                Animatable u = (Animatable)iter.next();
                u.updateState(interpolation);
            }
        }
    }
    
    private void notifyRender(){
        synchronized(animatables)
        {
            Iterator iter = animatables.iterator();
            
            while (iter.hasNext()){
                Animatable u = (Animatable)iter.next();
                u.render();
            }
        }
    }

    /**If the animation thread is not running, this call
     * creates and starts a new one.
     */
    public void start()
    {
        if (!running)
        {
            t = new Thread(this);
            running = true;
            t.start();
        }
    }

    /**This stops the animation thread from running*/
    public void stop(){
        running = false;
    }

    /**Returns true if the animation thread is running
     *
     * @return true if the animation thread is running
     */
    public boolean isRunning(){
        return running;
    }
    
    public void run()
    {
        long beforeTime,afterTime,timeDiff,sleepTime;
        long overSleepTime = 0L;
        int noDelays = 0;
        long excess = 0L;
        
        gameStartTime = System.nanoTime();
        prevStatsTime = gameStartTime;
        beforeTime = gameStartTime;

        afterTime = System.nanoTime();
        timeSinceLast = System.nanoTime();
        while (running)
        {
            
            notifyUpdate();
            notifyRender();
            
            afterTime = System.nanoTime();
            timeDiff = afterTime - beforeTime;
            sleepTime = period - timeDiff - overSleepTime;
            
            if (sleepTime > 0){
                try{
                    Thread.sleep(sleepTime/1000000L);
                }catch(InterruptedException e){}
                overSleepTime = System.nanoTime() - afterTime - sleepTime;
            }
            else{
                excess -= sleepTime;
                overSleepTime = 0L;
                
                if (++noDelays >= NO_DELAYS_PER_YIELD){
                    Thread.yield();
                    noDelays = 0;
                }
            }
            beforeTime = System.nanoTime();
             
            /*if frame animation is taking too long, update the game state
             *without rendering it, to get the updates/sec nearer to the 
             *required FPS*/
            int skips = 0;
            while((excess > period) && (skips < MAX_FRAME_SKIPS)){
                excess -= period;
                notifyUpdate();
                skips++;
            }
            framesSkipped += skips;

            if (collectStats) storeStats();
        }
    }

    /**If set to true, FPS and UPS stats will be collected (warning:
     * this does make the thread a bit less efficient).
     *
     * @param collect true if stats are to be collected
     */
    public void setCollectStats(boolean collect)
    {
        this.collectStats = collect;
    }

    /**Returns true if FPS and UPS stats are being collected
     *
     * @return true if stats are to be collected
     */
    public boolean isCollectingStats()
    {
        return collectStats;
    }
    
    private void storeStats()
  /* The statistics:
       - the summed periods for all the iterations in this interval
         (period is the amount of time a single frame iteration should take), 
         the actual elapsed time in this interval, 
         the error between these two numbers;

       - the total frame count, which is the total number of calls to run();

       - the frames skipped in this interval, the total number of frames
         skipped. A frame skip is a game update without a corresponding render;
         
       - the FPS (frames/sec) and UPS (updates/sec) for this interval, 
         the average FPS & UPS over the last NUM_FPSs intervals.

     The data is collected every MAX_STATS_INTERVAL  (1 sec).
  */
  { 
    frameCount++;
    statsInterval += period;

    if (statsInterval >= MAX_STATS_INTERVAL) {     // record stats every MAX_STATS_INTERVAL
      long timeNow = System.nanoTime();
      timeSpentInGame = (int) ((timeNow - gameStartTime)/1000000000L);  // ns --> secs

      long realElapsedTime = timeNow - prevStatsTime;   // time since last stats collection
      totalElapsedTime += realElapsedTime;

      double timingError = 
         ((double)(realElapsedTime - statsInterval) / statsInterval) * 100.0;

      totalFramesSkipped += framesSkipped;

      double actualFPS = 0;     // calculate the latest FPS and UPS
      double actualUPS = 0;
      if (totalElapsedTime > 0) {
        actualFPS = (((double)frameCount / totalElapsedTime) * 1000000000L);
        actualUPS = (((double)(frameCount + totalFramesSkipped) / totalElapsedTime) 
                                                             * 1000000000L);
      }

      // store the latest FPS and UPS
      fpsStore[ (int)statsCount%NUM_FPS ] = actualFPS;
      upsStore[ (int)statsCount%NUM_FPS ] = actualUPS;
      statsCount = statsCount+1;

      double totalFPS = 0.0;     // total the stored FPSs and UPSs
      double totalUPS = 0.0;
      for (int i=0; i < NUM_FPS; i++) {
        totalFPS += fpsStore[i];
        totalUPS += upsStore[i];
      }

      if (statsCount < NUM_FPS) { // obtain the average FPS and UPS
        averageFPS = totalFPS/statsCount;
        averageUPS = totalUPS/statsCount;
      }
      else {
        averageFPS = totalFPS/NUM_FPS;
        averageUPS = totalUPS/NUM_FPS;
      }
      
      framesSkipped = 0;
      prevStatsTime = timeNow;
      statsInterval = 0L;   // reset
    }
  }  // end of storeStats()

  /**If stats are being collected, this returns the FPS (averaged
   * over a 1-second window).
   * @return frames/second
   */
  public double getAverageFPS(){
      return averageFPS;
  }

  /**If stats are being collected, this returns the UPS (averaged
   * over a 1-second window).
   * @return updates/second
   */
  public double getAverageUPS(){
      return averageUPS;
  }
  
    
}
