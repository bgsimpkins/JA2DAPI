package twodapi.ai;

/*
 * AStar.java
 *
 * Created on July 3, 2009, 11:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import java.io.*;
import java.util.ArrayList;
import java.awt.Dimension;
import java.awt.Point;

//TODO: Set class to handle cases where a path/solution can't be found

/**AStar best-first (least cost) pathfinding algorithm. This implementation of the algorithm uses
 * the Manhattan method heuristic to estimate H. 
 *
 * There are two modes in which this class can be used. The standard mode is the
 * init&getSolution mode in which the init data are read in and the algorithm is
 * run through to the end until a solution is found. The step-wise mode can be used by
 * extending this method and calling the <code>loopAlgorithm()</code> method to perform
 * iterations of the algorithm's main loop.
 * 
 * @author bsimpkins
 */
public class AStar 
{    
    
    int numRows, numCols;
    private Node[][] nodes;
    
    private ArrayList<Node> openList = new ArrayList();
    private ArrayList<Node> closedList = new ArrayList();
    private Node currentNode;
    
    private Node startNode, endNode;
    
    private boolean solutionFound = false;
    
    private String[][] initData;
    
    /**Creates a new instance of AStar. Loads init file containing data representing
     * the search space in the following format:
     * <p> 
     * '-'=walkable node 'O'=wall node (unwalkable) 'S'=start 'E'=end
     * <p> 
     * This constructor only initializes the data. To find a solution, call <code>getSolution()</code> 
     * @param fileName Path to init file in proper format
     */
    public AStar(String fileName) 
    {
        initData = loadInitFile(fileName);
        
        //initNodes();
    }
    
    /** Creates a new instance of AStar. Takes data from 2D String array with elements
     *representing the search area in the following format:
     * <p> 
     * '-'=walkable node 'O'=wall node (unwalkable) 'S'=start 'E'=end
     * <p> 
     */
    public AStar(int numRows, int numCols,String[][] initData) 
    {
        this.numRows = numRows;
        this.numCols = numCols;
        this.initData = initData;
                
        //initNodes();                
    }
    
    private String[][] loadInitFile(String fileName)
    {
        String returnArray[][] = null;
        try
        {
            FileReader reader = new FileReader(fileName);
            BufferedReader bufRead = new BufferedReader(reader);
                        
            ArrayList<String> lines = new ArrayList(); 
            String line = bufRead.readLine();
            
            //IMPORTANT: First line must not be blank
            numCols = line.length();
            
            while(line != null)
            {
                //make sure blank lines are not added
                if (!line.trim().equals("")){
                    lines.add(line);
                    if (line.length() != numCols)
                    {
                        System.err.println("All rows have to be same length. Also, make sure the first line of the file is not blank. Exiting!");
                        System.exit(1);
                    }
                }
                    
                line = bufRead.readLine();                
            }
            numRows = lines.size();
            
            returnArray = new String[lines.size()][lines.get(0).length()];
        
            for (int i = 0; i < lines.size();i++)
            {
                line = lines.get(i);
                char[] lineArray = line.toCharArray();
                for (int j = 0; j < lineArray.length;j++)
                {
                    returnArray[i][j] = ""+lineArray[j];
                }
            }                        
            
        } catch(FileNotFoundException e){
            System.err.println("File "+fileName+" not found! Exiting.");
            System.exit(1);
            e.printStackTrace();
        }
        catch(IOException e)
        {
            System.err.println("IOException when loading "+fileName+". Exiting.");
            System.exit(1);
            e.printStackTrace();
        }
        
        return returnArray;
    }
    
    protected void initNodes()
    {
        nodes = new Node[numRows][numCols];
        
        //init all nodes
        for (int i = 0; i < numRows; i++)
        {
            for (int j = 0; j < numCols;j++)
            {
                Node node = new Node(j,i);
                
                if (initData[i][j].equalsIgnoreCase("S")) 
                {
                    node.setStartingNode(true);
                    
                    //set starting node as current node of interest
                    currentNode = node;
                    startNode = node;
                    
                    //add starting node to closed list
                    //node.setInClosedList(true);
                    closedList.add(node);                    
                    
                }
                else if (initData[i][j].equalsIgnoreCase("E")) 
                {
                    node.setEndingNode(true);
                    endNode = node;
                }
                else if (initData[i][j].equalsIgnoreCase("O")) node.setWallNode(true);

                nodes[i][j] = node;
            }
        }
    }
    
    /**Performs one iteration of the algorithm loop. Primarily meant for internal use
     *(<code>getSolution()</code>), but can be overriden if step-by-step tracking of the
     *process is needed..*/
    protected void loopAlgorithm()
    {
        int currentX = currentNode.getCoord().x;
        int currentY = currentNode.getCoord().y;
        System.out.println("Current node:"+currentNode.toString());
        
        //start lowest F at impossibly high number
        double lowestF = 10*(numRows*numCols);
        Node lowestFNode = null;
        
        //Compute F cost (G+H) for every node in open list
        for (int i = 0; i < openList.size();i++)
        {
            Node node = openList.get(i);                        
            
            System.out.println(node.toString());
            
            //if F is lower than lowest F, make it lowest
            if (node.getF() != -1 && node.getF() < lowestF)
            {
                lowestF = node.getF();
                lowestFNode = node;               
            }
        }
        
        //need to check mostly because lowestF is null in initial state
        if (lowestFNode != null)
        {
            System.out.println("Lowest F: "+lowestFNode.getCoord().toString());

            currentNode = lowestFNode;

            //lowestFNode.setInClosedList(true);
            openList.remove(lowestFNode);
            closedList.add(lowestFNode);

            System.out.println("New node: "+currentNode.getCoord().toString());


            currentX = currentNode.getCoord().x;
            currentY = currentNode.getCoord().y;
        }

        //check adjacent squares for activation and better parent
        
        for (int i = currentY - 1;i <= currentY + 1;i++)
        {
            for (int j = currentX - 1;j <= currentX + 1;j++)
            {
                //make sure we don't go out of bounds
                if (i >= 0 && j >= 0 && i < numRows && j < numCols)
                {
                    Node node = nodes[i][j];
                    //Ignore wall nodes and ignore closed list
                    if (!node.isWallNode() && !closedList.contains(node))
                    {                               
                        //If already in open list, check to see if current node is better parent (ie., path through current node is less costly)
                        if (openList.contains(node))
                        {
                            //use G as criterion for better
                            int newG = node.getG();

                            //calc G for node
                            
                            //if node is diagonal from current position
                            if (Math.abs(currentX - node.getCoord().x) == 1 && Math.abs(currentY - node.getCoord().y) == 1)
                            {
                                newG = currentNode.getG()+14;                
                            }

                             //if node is orthogonal from current position
                            else if (Math.abs(currentX - node.getCoord().x) == 1 || Math.abs(currentY - node.getCoord().y) == 1)
                            {
                                newG = currentNode.getG()+10;
                            }

                            //if path through current node to node is less costly, set current node as parent
                            if (newG < node.getG() || node.getG() == -1)
                            {
                                node.setParent(currentNode);

                                //calculate H using Manhattan method heuristic
                                int xDist = 10*Math.abs(node.getCoord().x-endNode.getCoord().x);
                                int yDist = 10*Math.abs(node.getCoord().y-endNode.getCoord().y); 

                                node.setG(newG);
                                node.setH(xDist+yDist);
                                node.setF(newG + (xDist + yDist));
                            }
                        }
                        else         //adjacent node not in open list
                        {
                            //node.setInOpenList(true);
                            node.setParent(currentNode);
                            openList.add(node);

                            //set F (=G+H)
                            
                            int newG = 0;

                            //if node is diagonal from current position
                            if (Math.abs(currentX - node.getCoord().x) == 1 && Math.abs(currentY - node.getCoord().y) == 1)
                            {
                                newG = currentNode.getG()+14;                
                            }

                             //if node is orthogonal from current position
                            else if (Math.abs(currentX - node.getCoord().x) == 1 || Math.abs(currentY - node.getCoord().y) == 1)
                            {
                                newG = currentNode.getG()+10;
                            }
                            
                            //calculate H using Manhattan method heuristic
                            int xDist = 10*Math.abs(node.getCoord().x-endNode.getCoord().x);
                            int yDist = 10*Math.abs(node.getCoord().y-endNode.getCoord().y); 

                            node.setG(newG);
                            node.setH(xDist+yDist);
                            node.setF(newG + (xDist + yDist));
                            
                        }                             
                    }
                }
            }
        }
        
        if (currentNode == endNode)
        {
            solutionFound = true;
        }
    }
    
    /**Returns the solution in the form of an array of points with the first element
     * being the start point and the last being the end.
     * @return The result of the AStar pathfinding algorithm
     */
    public Point[] getSolution()
    {
        ArrayList<Point> solutionList = new ArrayList();
        
        initNodes();
        
        solutionFound = false;
        
        while (!solutionFound)
        {
            loopAlgorithm();
        }
        
        Node node = endNode;
        node.setIsPath(true);
        solutionList.add(endNode.getCoord());
        
        while (node != startNode)
        {            
            if (node.getParent() != null)
            {                
                node = node.getParent();
                node.setIsPath(true);
                solutionList.add(node.getCoord());
            }
        }
        
        //flip list around (so it's start to end)
        Point[] solution = new Point[solutionList.size()];
        
        int flipI = 0;
        
        for (int i = solutionList.size() - 1; i >= 0; i--)
        {
            solution[flipI++] = solutionList.get(i);
        }
        
        return solution;
    }
    
    /**Returns The dimensions of the matrix in x,y (column,row) format
     *@return Dimensions of the matrix*/
    public Dimension getDimensions()
    {
        return new Dimension(numCols,numRows);
    }
    
    /**Returns nodes of 2d A* matrix
     *@return 2d array of nodes (row,column)
     */
    public Node[][] getNodes()
    {
        return nodes;
    }
    
    /**Returns the open list (nodes currently being considered)
     *
     * @return The openlist
     */
    public ArrayList<Node> getOpenList()
    {
        return openList;
    }
    
    /**Returns the closed list (nodes closed for consideration)
     * 
     * @return The closedlist
     */
    public ArrayList<Node> getClosedList()
    {
        return closedList;
    }
    
    /**Returns the start node.
     * 
     * @return The start node
     */
    public Node getStartNode()
    {
        return startNode;
    }
    
    /**Returns the end node.
     * 
     * @return The end node
     */
    public Node getEndNode()
    {
        return endNode;
    }
    
    /**Returns the node currently being examined
     * 
     * @return Node currently being examined
     */
    public Node getCurrentNode()
    {
        return currentNode;
    }
    
    public static void main(String[] args)
    {
        AStar aStar = new AStar("init.props");
        long start = System.nanoTime();
        Point[] solution = aStar.getSolution();
        long end = System.nanoTime();
        
        double differenceMillis = (end - start)/(Math.pow(10, 6));
        
        System.out.println("Solution found in "+differenceMillis+" milliseconds");
        
        for (int i = 0; i < solution.length; i++)
        {
            System.out.println("Node #"+(i+1)+": "+solution[i].toString());
        }
    }

    public class Node
    {
        private boolean startingNode = false;
        private boolean endingNode = false;
        private boolean wallNode = false;
        //private boolean inOpenList = false;
        //private boolean inClosedList = false;
        private Point coord;
        private Node parent;

        private boolean isPath = false;

        private int g = -1;
        private int h = -1;
        private int f = -1;
        public Node(int x, int y)
        {
            coord = new Point(x,y);
        }

        public boolean isStartingNode() {
            return startingNode;
        }

        public void setStartingNode(boolean startingNode) {
            this.startingNode = startingNode;
        }

        public boolean isEndingNode() {
            return endingNode;
        }

        public void setEndingNode(boolean endingNode) {
            this.endingNode = endingNode;
        }

        /*public boolean isInOpenList() {
            return inOpenList;
        }

        public void setInOpenList(boolean inOpenList) {
            this.inOpenList = inOpenList;
            this.inClosedList = !inOpenList;
        }

        public boolean isInClosedList() {
            return inClosedList;
        }

        public void setInClosedList(boolean inClosedList) {
            this.inClosedList = inClosedList;
            this.inOpenList = !inClosedList;
        }*/

        public Point getCoord() {
            return coord;
        }

        public void setCoord(Point coord) {
            this.coord = coord;
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public boolean isWallNode() {
            return wallNode;
        }

        public void setWallNode(boolean wallNode) {
            this.wallNode = wallNode;
        }

        public int getG() {
            return g;
        }

        public void setG(int g) {
            this.g = g;
        }

        public int getH() {
            return h;
        }

        public void setH(int h) {
            this.h = h;
        }

        public int getF() {
            return f;
        }

        public void setF(int f) {
            this.f = f;
        }

        public boolean isPath() {
            return isPath;
        }

        public void setIsPath(boolean isPath) {
            this.isPath = isPath;
        }

        @Override
        public String toString()
        {
            if (parent != null)
            {
                return("x="+coord.x+" y="+coord.y+" G="+g+" H="+h+" F="+f+" Parent="+parent.getCoord().x+","+parent.getCoord().y);
            }
            else
            {
                return("x="+coord.x+" y="+coord.y+" G="+g+" H="+h+" F="+f+" Parent=null");
            }
        }
    }
}
