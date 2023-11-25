/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twodapi.lwjgl;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author bsimpkins
 */
public class SubImageTest
{
    public static void main(String[] args)
    {
        
        URL imageURL = SubImageTest.class.getResource("/images/MapScrollerTestImage.png");
        BufferedImage image = null;

        try {
            image = ImageIO.read(imageURL);
        } catch (IOException ex) {
            System.err.println("Shit!");
            System.exit(1);
        }

        final int cols = image.getWidth()/100;
        final int rows = image.getHeight()/100;
        final BufferedImage[][] images = new BufferedImage[rows][cols];

        for (int i = 0; i < cols; i++)
        {
            for (int j = 0; j < rows; j++)
            {
                images[j][i] = image.getSubimage(i*100, j*100, 100, 100);
            }
        }

        JPanel panel = new JPanel()
        {
            @Override
            public void paintComponent(Graphics g)
            {
                for (int i = 0; i < cols; i++)
                {
                    for (int j = 0; j < rows; j++)
                    {
                        g.drawImage(images[j][i], i*100, j*100, null);
                    }
                }
            }
        };

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024,768);
        frame.getContentPane().add(panel);
        frame.setVisible(true);

    }
}
