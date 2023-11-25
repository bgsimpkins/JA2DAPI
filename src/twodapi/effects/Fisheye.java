/*
 * Fisheye.java
 *
 * Created on September 11, 2008, 12:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package twodapi.effects;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

/**
 *
 * @author Ben
 */

public class Fisheye implements MouseMotionListener, MouseListener{
    
    public static final int LINEAR_MAGNIFY = 1;
    public static final int LINEAR_FISHEYE = 2;
    
    private int mode = 2;
    
    private ArrayList<Fisheyeable> fishObjects;
    private ArrayList<Dimension> standardSizes;
    private ArrayList<Point> standardPositions;
    private JComponent comp;
    private int influenceRadius = 200;
    
    /**Creates a new instance of Fisheye
     *@param comp JComponent fisheye objects are being painted on*/
    public Fisheye(JComponent comp)
    {
        this.comp = comp;
        fishObjects = new ArrayList();
        comp.addMouseMotionListener(this);
        comp.addMouseListener(this);
        standardSizes = new ArrayList();
        standardPositions = new ArrayList();
    }
    
    /**Creates a new instance of Fisheye and initializes Fisheye objects
     *@param comp JComponent fisheye objects are being painted on
     *@param fishObjects List of Fisheyeable objects*/
    public Fisheye(ArrayList<Fisheyeable> fishObjects,JComponent comp) {
        this.fishObjects = fishObjects;
        this.comp = comp;
        comp.addMouseMotionListener(this);
        
        standardSizes = new ArrayList();
        standardPositions = new ArrayList();
        
        for (int i = 0; i < fishObjects.size();i++)
         {
            setStandards(fishObjects.get(i));
        }
    }
    
    public void addFishObject(Fisheyeable fishObj)
    {
        fishObjects.add(fishObj);
        setStandards(fishObj);
    }
    
    private void setStandards(Fisheyeable fishObj)
    {
                  
         Dimension size = new Dimension(fishObj.getSizeFisheye());
         standardSizes.add(size);

         Point loc = new Point(fishObj.getPositionFisheye());
         standardPositions.add(loc);
        
    }
    
    public ArrayList<Point> getStandardPositions()
    {
        return standardPositions;
    }
    
    public void setMode(int mode)
    {
        this.mode = mode;
    }
    
    public void setInfluenceRadius(int radius)
    {
        this.influenceRadius = radius;
    }
    
    public int getInfluenceRadius()
    {
        return influenceRadius;
    }
    
    private double calculateDistance(Point point1, Point point2)
    {
        double X1 = point1.getX();
        double Y1 = point1.getY();
        double X2 = point2.getX();
        double Y2 = point2.getY();
        
        return(Math.sqrt(Math.pow(X2-X1,2) + Math.pow(Y2-Y1,2)));        
    }
    
    private void linearMagnify(Point mousePoint)
    {
        for (int i = 0; i < fishObjects.size();i++){
            Fisheyeable fishObject = fishObjects.get(i);
            double aspectRatio = standardSizes.get(i).getWidth()/standardSizes.get(i).getHeight();
            
            //distance from mousepoint to object
            //double dist = calculateDistance(fishObject.getPosition(),mousePoint);
            double dist = calculateDistance(standardPositions.get(i),mousePoint);

            
            Dimension size = null;
            int compWidth = comp.getSize().width;
            if (dist < influenceRadius)
            {
                /*scale height as a linear function of distance
                 *( ( lowerLimit - distance ) / ( lowerLimit ) ) * ( height of object at center magnification )
                 */
                int height = (int)Math.round(((influenceRadius-dist)/(influenceRadius))*(influenceRadius/2d));
                
                //use aspect ratio to get width
                int width = (int)Math.round(height*aspectRatio);
                size = new Dimension(width,height);
            }
            else {
                size = standardSizes.get(i);
                
            }
            fishObject.setSizeFisheye(size);
            fishObject.setPositionFisheye(standardPositions.get(i));
        }
    }
 
    public void linearFisheye(Point mousePoint)
    {
        for (int i = 0; i < fishObjects.size();i++)
        {
            Fisheyeable fishObject = fishObjects.get(i);
            double aspectRatio = standardSizes.get(i).getWidth()/standardSizes.get(i).getHeight();
            
            //double dist = calculateDistance(fishObject.getPosition(),mousePoint);
            double dist = calculateDistance(standardPositions.get(i),mousePoint);

            Dimension size = null;
            Point position = null;
            int compWidth = comp.getSize().width;
            if (dist < influenceRadius)
            {
                
                /*scale height as a linear function of distance
                 *( ( lowerLimit - distance ) / ( lowerLimit ) ) * ( height of object at center magnification )
                 */
                
                //height at center mag twice the size of standard height
                int height = (int)Math.round(2*((influenceRadius-dist)/(influenceRadius))*(standardSizes.get(i).height));                
                
                //height at center mag inversely related to initial size
                //int height = (int)Math.round(((influenceRadius-dist)/(influenceRadius))*(1/Math.pow(standardSizes.get(i).height,10)));
                
                
                /////////////////////////////
                
                //use aspect ratio to get width
                int width = (int)Math.round(height*aspectRatio);
                size = new Dimension(width,height);                
                
                ////extends distance as a linear function of distance value (closer = push further away)
                                
                //get angle to object
                int offX = standardPositions.get(i).x - mousePoint.x;
                int offY = standardPositions.get(i).y - mousePoint.y;         
                double theta = Math.atan2(offY,offX);
                //if (i == 65){
                //    System.out.println("X: "+offX + " Y: "+offY);
                //    System.out.println("Theta: "+theta);
                //}

                //for objects beyond a certain distance, increase distance for closer objects
                if (!(dist < fishObject.getSizeFisheye().getHeight()/2 || dist < fishObject.getSizeFisheye().getWidth()/2)){
                   
                    double transMultiplier = 3;                    
                    
                    //amount pushed away based on influenceRadius
                    dist = dist + ((influenceRadius/dist)*transMultiplier);

                    //if (i == 65){
                    //    System.out.println("Dist: "+dist);
                    //}
                }
                else {    //pull closer objects in
                    double transMultiplier = 40;
                    dist = dist + ((dist/influenceRadius)*transMultiplier);
                }
               
                //set x and y based on new distance               
                int newX = mousePoint.x + (int)Math.round(dist*Math.cos(theta));
                int newY = mousePoint.y + (int)Math.round(dist*Math.sin(theta));

                position = new Point(newX,newY);
             
            }
            else {
                size = standardSizes.get(i);
                position = standardPositions.get(i);
            }
            fishObject.setSizeFisheye(size);
            fishObject.setPositionFisheye(position);
        }
    }
    
    /**This should be called to force the fisheye to set the size and
     *dimensin of the fisheye objects to their standard values. This is
     *useful if the mouse exits the panel suddenly.*/
    public void setObjectsToStandardSettings()
    {
        for (int i = 0; i < fishObjects.size();i++)
        {
            Fisheyeable fishObject = fishObjects.get(i);
            fishObject.setSizeFisheye(standardSizes.get(i));
            fishObject.setPositionFisheye(standardPositions.get(i));
        }
    }
    
    /**If a scroll scalar or the like is being implemented, need to call this each time
     *the scalar changes*/
    public void fishObjectsMoved()
    {
        
        for (int i = 0; i < fishObjects.size();i++)
        {
            Fisheyeable fishObj = fishObjects.get(i);
            standardPositions.get(i).setLocation(fishObj.getPositionFisheye());
        }
    }
    
    /**If a scale factor is being used  need to call this each time
     *the scale factor changes*/
    public void fishObjectsScaled()
    {
        for (int i = 0; i < fishObjects.size();i++)
        {
            Fisheyeable fishObj = fishObjects.get(i);
            standardSizes.get(i).setSize(fishObj.getSizeFisheye());
        }
    }
    
    private void update(Point mousePoint)
    {
        switch(mode)
        {
            case LINEAR_MAGNIFY: 
            {
                linearMagnify(mousePoint);
                break;
            }
            
            case LINEAR_FISHEYE:
            {
                linearFisheye(mousePoint);
                break;
            }
            
        }
        comp.repaint();
    }
    
    public void mouseMoved(MouseEvent me)
    {
        update(me.getPoint());
    }
    public void mouseDragged(MouseEvent me)
    {
        //if a scroll is being used, need to update standardPositions if screen
        //is scrolled
        fishObjectsMoved();
    }
    
    public void mouseEntered(MouseEvent me){}
    public void mouseExited(MouseEvent me){}
    public void mousePressed(MouseEvent me){}
    public void mouseReleased(MouseEvent me){
        update(me.getPoint());
    }
    public void mouseClicked(MouseEvent me){}
    
}
