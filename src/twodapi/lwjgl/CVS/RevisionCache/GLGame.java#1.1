/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twodapi.lwjgl;

/**
 *
 * @author ben
 */
public abstract class GLGame
{
    private GLPainter painter;
    private boolean running = false;
    private MouseControlsGL mControls;
    private GameController cControls;

    protected abstract void initGame();
    protected void update(float interpolation) {}

    public GLPainter createPainter(int width, int height)
    {
        painter = new GLPainter(width,height)
        {
            @Override
            public void gameInit()
            {
                initGame();
            }
            @Override
            public void gameUpdate(float interpolation)
            {
                update(interpolation);
            }
        };

        return painter;
    }

    public void start()
    {
        if (!running)
        {
            running = true;
            painter.start();
        }
        
    }

    public boolean isRunning()
    {
        return running;
    }

    public void setFullscreen()
    {
        painter.setFullscreen(true);
    }

    public GLPainter getPainter()
    {
        return painter;
    }

    public KeyControlsGL createKeyControls()
    {
        return painter.createKeyControls();
    }

    public KeyControlsGL getKeyControls()
    {
        return painter.getKeyControls();
    }

    public MouseControlsGL createMouseControls()
    {
        mControls = new MouseControlsGL();
        return mControls;
    }

    public MouseControlsGL getMouseControls()
    {
        return mControls;
    }

    public GameController createGameController()
    {
        cControls = new GameController();
        return cControls;
    }

    public GameController getGameController()
    {
        return cControls;
    }
}
