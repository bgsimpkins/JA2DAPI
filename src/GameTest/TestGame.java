/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package GameTest;

import twodapi.audio.SimpleAudioPlayer;
import twodapi.core.GamePainter;

/**
 *
 * @author bsimpkins
 */
public interface TestGame {

    public AsteroidSprite[] getAsteroids();
    public void showExplosion(int x, int y);
    public GamePainter getPainter();
    public int getNumLives();
    public void removeLife();
    public void gameOver();
    public SimpleAudioPlayer getSoundPlayer();

}
