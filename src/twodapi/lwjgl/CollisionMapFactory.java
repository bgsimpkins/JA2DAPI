/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twodapi.lwjgl;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

/**
 *
 * @author bsimpkins
 */
public class CollisionMapFactory
{

    private int grainSize;
    private Dimension imageSize, tileSize;
    private int lineWidth, numLines;
    private float grainSelectThreshold = .5f;
    private boolean[][] mapVals;
    private BufferedImage[][] tiles;
    private BufferedImage image;

    private int buildProgress = 0;


    public CollisionMapFactory(File inputFile, int grainSize)
    {
        this.grainSize= grainSize;
        

        image = null;
        try {
             image = ImageIO.read(inputFile);
             tileSize = new Dimension(image.getWidth(),image.getHeight());
        } catch (IOException ex)
        {
            System.err.println("CollisionMapFactory: Could not load image!");
            ex.printStackTrace();
        }

        initialize();
    }

    public CollisionMapFactory(BufferedImage image, int grainSize)
    {
        this.grainSize= grainSize;
        this.image = image;
        tileSize = new Dimension(image.getWidth(),image.getHeight());

        initialize();
    }

    private void initialize()
    {
        

        imageSize = new Dimension(image.getWidth(),image.getHeight());

        if (imageSize.width % grainSize != 0 || imageSize.height % grainSize != 0)
        {
            System.err.println("CollisionMapFactory: Grain size does not evenly go into image size!! Collision data may be lost!");
        }

        lineWidth = imageSize.width/grainSize;
        numLines = imageSize.height/grainSize;
        //PixelGrabber pGrabber = new PixelGrabber(image,0,0,10,10,true);
        //PixelGrabber pGrabber = null;

        mapVals = new boolean[numLines][lineWidth];


    }

    public void makeMap()
    {
        buildProgress = 0;


        System.out.println("CollisionMapFactory: Building map....");
        int[] pixData = new int[grainSize*grainSize*4];
        for (int i = 0; i < numLines; i++)
        {
            for (int j = 0; j < lineWidth; j++)
            {
                buildProgress++;

                    Raster raster = image.getData();
                    raster.getPixels(j*grainSize, i*grainSize, grainSize,grainSize, pixData);
                    int numSolidPix = 0;
                    for (int p = 0; p < pixData.length; p++)
                    {
                        int pData = pixData[p];
                        if ((p+1) % 4 == 0) //if mod 4, is alpha value
                        {
                            if (pData > 0) numSolidPix++;
                        }

                    }

                    //System.out.println("Solidity="+(float)numSolidPix/(grainSize*grainSize));
                    if ((float)numSolidPix/(grainSize*grainSize) > grainSelectThreshold)
                    {
                        mapVals[i][j] = true;
                    }
                    else
                    {
                        mapVals[i][j] = false;
                    }

                }
            }
        /*System.out.println("CollisionMapFactory: Building tiles....");
        if (image.getWidth() % tileSize.width != 0)
        {
            System.err.println("CollisionMapFactory: Tile Width ("+tileSize.width+") is not evenly divisible by image width ("+
                    image.getWidth()+").");
        }
        if (image.getHeight() % tileSize.height != 0)
        {
            System.err.println("CollisionMapFactory: Tile Height ("+tileSize.height+") is not evenly divisible by image height ("+
                    image.getHeight()+").");
        }

        int numCols = image.getWidth()/tileSize.width;
        int numRows = image.getHeight()/tileSize.height;

        tiles = new BufferedImage[numRows][numCols];

        for (int i = 0; i < numRows; i++)
        {
            for (int j = 0; j < numCols; j++)
            {
                tiles[i][j] = image.getSubimage(j*tileSize.width, i*tileSize.height, tileSize.width, tileSize.height);
            }
        }*/

    }

    public void writeMapToFile(String fileName)
    {
        System.out.println("CollisionMapFactory: Writing map to file "+fileName);
        //Make indices array a writable string

        String outString = "grainSize="+grainSize+"\n";
        outString += "numLines="+numLines+"\n";
        outString += "lineWidth="+lineWidth+"\n";

        for (int i = 0; i < numLines; i++)
        {
            for (int j = 0; j < lineWidth;j++)
            {
                if (!mapVals[i][j])
                {
                    outString += 0;
                }
                else{
                    outString += 1;
                }
            }
            outString +="\n";
        }

        //Create tar.gz archive and write collision map and tiles as entries
        /*FileOutputStream fos;
        try {
            fos = new FileOutputStream(new File(fileName+".jcm"));
            TarGzOutputStream tgz = new TarGzOutputStream(fos);

            //Write collision map to archive
            TarEntry entry = new TarEntry(new File("collsionMap.txt"));
            tgz.putNextEntry(entry);
            tgz.write(outString.getBytes());
            tgz.closeEntry();

            //Write tile images to archive
            int numCols = image.getWidth()/tileSize.width;
            int numRows = image.getHeight()/tileSize.height;

            for (int i = 0; i < numRows; i++)
            {
                for (int j = 0; j < numCols; j++)
                {
                    BufferedImage bi = tiles[i][j];
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(bi, "png", baos);
                    entry = new TarEntry(new File(j+"_"+i+".png"));
                    tgz.putNextEntry(entry);
                    tgz.write(baos.toByteArray());
                    tgz.closeEntry();
                }
            }

            tgz.close();

        } catch (FileNotFoundException ex){}
        catch(IOException ex){}*/

        BufferedWriter bufWrite = null;
        try
        {
            FileWriter output = new FileWriter(fileName);
            bufWrite = new BufferedWriter(output);
            bufWrite.write(outString);

            bufWrite.close();
        }catch (IOException e)
        {
            System.err.println("CollisionMapFactory.writeMaptoFile(): Couldn't write map to file: "+fileName);
            e.printStackTrace();
        }
    }

    public boolean[][] getMap()
    {
        return mapVals;
        
    }

    public int getBuildProgress()
    {
        return (int)((float)buildProgress/(numLines*lineWidth)*100);
    }

    public void setTileSize(int width, int height)
    {
        tileSize.setSize(width, height);
    }
    public Dimension getTileSize()
    {
        return tileSize;
    }

    public void setGrainSelectThreshold(float thresh)
    {
        if (thresh < 0 || thresh > 1)
        {
            System.err.println("CollisionMapFactory: Couldn't set grain threshold. Must be 0 <= x <= 1");
        }
        else
        {
            this.grainSelectThreshold = thresh;
        }
    }

    public float getGrainSelectThreshold()
    {
        return grainSelectThreshold;
    }

    public static void main(String[] args)
    {
        new CollisionMapMaker();
        /*
         //Test tar library
         try {
            
            FileOutputStream fos = new FileOutputStream(new File("testfile.tar.gz"));
            TarGzOutputStream tgz = new TarGzOutputStream(fos);

            BufferedImage testImage = ImageIO.read(CollisionMapFactory.class.getResource("/images/purple-galaxy-stars.jpg"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(testImage, "jpg", baos);

            //FileWriter fw = new FileWriter("testFile.txt");
            //BufferedWriter bw = new BufferedWriter(fw);
            //bw.write("You are a silly bastard!");
            //bw.close();

            TarEntry entry = new TarEntry(new File("testFile.txt"));
            tgz.putNextEntry(entry);
            tgz.write("Holy shitter".getBytes());
            tgz.closeEntry();

            entry = new TarEntry(new File("testImage.jpg"));
            tgz.putNextEntry(entry);
            tgz.write(baos.toByteArray());
            tgz.closeEntry();

            tgz.close();

        } catch (FileNotFoundException ex) { }
        catch (IOException ex) {}*/
    }

}
class CollisionMapMaker implements Runnable
    {
        JProgressBar progressBar;
        JDialog dialog;
        JLabel label;
        boolean running = false;
        CollisionMapFactory mf;
        String fileoutName = "MapFactoryOut";

        public CollisionMapMaker()
        {

            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Choose Foreground Image");
            fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fc.setCurrentDirectory(new File("."));

            File file = null;
            int returnVal = fc.showOpenDialog(null);
            if(returnVal == JFileChooser.APPROVE_OPTION)
            {
                file = fc.getSelectedFile();
            }
            else
            {
                System.exit(1);
            }

            //System.out.println("File to load: "+file.getAbsolutePath());

            BufferedImage image = null;
            try {
                image = ImageIO.read(file);
            } catch (IOException ex) {
                System.err.println("CollisionMapFactory.CollisionMapMaker: Could not load image "+file.getAbsolutePath());
            }

            boolean ok = false;
            int grain = 0;

            while(!ok)
            {
                try{
                    String inputString = JOptionPane.showInputDialog("Please Enter Grain Size (in pixels):");
                    if (inputString == null) System.exit(0);
                    grain = Integer.parseInt(inputString);
                    if (image.getWidth() % grain == 0 && image.getHeight() % grain == 0)
                    {
                        ok = true;
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, "Please enter grain value with which image size divides evenly. Image Size= "+image.getWidth()+"X"+image.getHeight());
                    }
                }catch (NumberFormatException e)
                {
                    JOptionPane.showMessageDialog(null, "Please enter positive integer for grain value!");
                }
            }

            /*int response = JOptionPane.showConfirmDialog(null, "Do you wish to set custom tile size?","Tile Size" , JOptionPane.YES_NO_OPTION);

            
            int width = image.getWidth();
            int height = image.getHeight();
            if(response == JOptionPane.YES_OPTION)
            {
                ok = false;
                while(!ok)
                {
                    try{
                        String inputString = JOptionPane.showInputDialog("Please enter tile width");
                        //if close, just go with default vals
                        if (inputString == null) ok = true;

                        int val = Integer.parseInt(inputString);
                        if (image.getWidth() % val == 0) {
                            width = val;
                            ok = true;
                        }
                        else  JOptionPane.showMessageDialog(null, "Please enter tile width value with which image width divides evenly. Image Width= "+image.getWidth());
                        
                    }catch(NumberFormatException ex)
                    {
                        JOptionPane.showMessageDialog(null, "Please enter positive integer for tile width!");
                    }
                }
                
                ok = false;
                while(!ok)
                {
                    try{
                        String inputString = JOptionPane.showInputDialog("Please enter tile height");
                        //if close, just go with default vals
                        if (inputString == null) ok = true;
                        
                        int val = Integer.parseInt(inputString);
                        if (image.getHeight() % val == 0) {
                            height = val;
                            ok = true;
                        }
                        else  JOptionPane.showMessageDialog(null, "Please enter tile height value with which image height divides evenly. Image height= "+image.getHeight());
                        
                    }catch(NumberFormatException ex)
                    {
                        JOptionPane.showMessageDialog(null, "Please enter positive integer for tile height!");
                    }
                }


            }*/

            fileoutName = JOptionPane.showInputDialog("Enter filename for saved map:");

            mf = new CollisionMapFactory(image,grain);
            //mf.setTileSize(width, height);
            dialog = new JDialog();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setSize(300,200);
            dialog.setLocationRelativeTo(null);
            Container contentPane = dialog.getContentPane();

            contentPane.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            label = new JLabel("Creating Collision Map!");
            contentPane.add(label,c);
            c.insets = new Insets(10,0,10,0);
            c.gridy = 1;

            progressBar = new JProgressBar();
            progressBar.setValue(0);
            contentPane.add(progressBar,c);

            dialog.setVisible(true);


            Thread thread = new Thread(this);
            thread.start();
            running = true;
            while(running)
            {              
                progressBar.setValue(mf.getBuildProgress());
                contentPane.repaint();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {}
            }
        }

        public void run()
        {
            
            
            mf.makeMap();
            mf.writeMapToFile(fileoutName);
            label.setText("Collision Map Created!");
            running = false;
        }
    }


