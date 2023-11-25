/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twodapi.lwjgl;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.lwjgl.input.Controller;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;


/**
 *
 * @author bsimpkins
 */
public class GameController
{
    //for quick lookup of button indices
    private ArrayList<HashMap<String,Integer>> buttonList;

    private HashMap<String,MappedAction> actionMap;

    private ControllerConfig configScreen;

    private MouseControlsGL mouseControls;

    private String[] actionList;
    
    public GameController()
    {
        if (!Controllers.isCreated()) try {
            Controllers.create();

            actionMap = new HashMap();
            initButtonList();

            configScreen = new ControllerConfig();

            /*System.out.println("Number of controllers="+Controllers.getControllerCount());
            Controller c= getController(0);
            System.out.println("\n*************Controller="+c.getName());
            System.out.println("Num axes="+c.getAxisCount());
            for (int i = 0; i < c.getButtonCount(); i++)
            {
                String name = c.getButtonName(i);
                System.out.println("Button "+i+"="+name);
            }*/

        } catch (LWJGLException ex)
        {
            System.err.println("GameController: Could not create Controllers!");
            ex.printStackTrace();
        }
    }

    private void initButtonList()
    {
        buttonList = new ArrayList();

        for (int i = 0; i < Controllers.getControllerCount(); i++)
        {
            HashMap<String,Integer> buttonMap = new HashMap();
            Controller c = Controllers.getController(i);
            for (int j = 0; j < c.getButtonCount(); j++)
            {
                String buttonName = c.getButtonName(j);
                buttonMap.put(buttonName,j);
            }
            buttonList.add(buttonMap);
        }
    }

    public void setActions(String[] actions)
    {
        actionList = actions;
        configScreen.setActions(actions);

        actionMap = new HashMap();
        for (int i = 0; i < actions.length; i++)
        {

            MappedAction ma = new MappedAction();
            actionMap.put(actions[i], ma);

        }
    }

    public String[] getActions()
    {
        return actionList;
    }

    public PaintableGL createConfigScreen(MouseControlsGL mouseControls)
    {
        this.mouseControls = mouseControls;

        return configScreen;
    }

    public boolean isPressed(int controller, String buttonName)
    {
        int buttonI = buttonList.get(controller).get(buttonName);
        return Controllers.getController(controller).isButtonPressed(buttonI);
    }

    public boolean isPressed(int controller, int buttonIndex)
    {
        return Controllers.getController(controller).isButtonPressed(buttonIndex);
    }

    public boolean isPressed(String action)
    {
        MappedAction ma = actionMap.get(action);

        //if ma == -1, action hasn't been mapped yet
        if (ma.index == -1) return false;
        
        if (ma.isButton)
        {
            return Controllers.getController(ma.controller).isButtonPressed(ma.index);
        }
        else     //is axis
        {
            int axisVal= getAxisValueInt(ma.controller,ma.index);
            return (axisVal==ma.axisVal);
        }
    }

    public boolean isConfigFinished()
    {
        return configScreen.finished;
    }

    public void setConfigFinished(boolean finished)
    {
        configScreen.finished = finished;
    }

    public int getAxisValueInt(int controller ,int axisIndex)
    {

        float axisVal = Controllers.getController(controller).getAxisValue(axisIndex);
        
        if (axisVal <= -.9f) return -1;
        else if (axisVal >= .9f) return 1;
        else return 0;
    }

    public float getAxisValue(int controller ,int axisIndex)
    {
        return Controllers.getController(controller).getAxisValue(axisIndex);
    }

    public int getNumButtons(int controller)
    {
        return Controllers.getController(controller).getButtonCount();
    }

    public String getButtonName(int controller,int buttonIndex)
    {
        return Controllers.getController(controller).getButtonName(buttonIndex);
    }

    public Controller getController(int index)
    {
        return Controllers.getController(index);
    }

    public String getControllerName(int index)
    {
        return Controllers.getController(index).getName();
    }

    public int getNumControllers()
    {
        return Controllers.getControllerCount();
    }

    public int getNumAxes(int controller)
    {
        return Controllers.getController(controller).getAxisCount();
    }

    public void printActionMap()
    {
        Iterator iter = actionMap.entrySet().iterator();
        System.out.println("Size of action map="+actionMap.size());
        while(iter.hasNext())
        {
            Map.Entry me = (Map.Entry)iter.next();
            String action = (String)me.getKey();
            MappedAction ma = (MappedAction)me.getValue();
            System.out.println("Action="+action+" Button="+ma.isButton+" index="+ma.index+" Axis val="+ma.axisVal);
        }
    }

    public static void main(String[] args)
    {
        /*GameController gc = new GameController();
        Controller c= gc.getController(0);
        System.out.println("\n*************Controller="+c.getName());
        System.out.println("Num axes="+c.getAxisCount());
        for (int i = 0; i < c.getButtonCount(); i++)
        {
            String name = c.getButtonName(i);
            System.out.println("Button "+i+"="+name);
        }*/

        //Test controller monitor
//        GLPainter painter = new GLPainter(800,600)
//        {
//            GameController gc;
//            ControllerMonitor monitor;
//
//            public void gameInit()
//            {
//                gc = new GameController();
//                monitor= new ControllerMonitor(this,gc);
//            }
//            public void gameUpdate(float interp)
//            {
//                monitor.update();
//            }
//        };

        //Test ControllerConfig
        GLPainter painter = new GLPainter(800,600)
        {
            KeyControlsGL kc;
            GameController gc;

            public void gameInit()
            {
                gc = new GameController();
                gc.setActions(new String[]{"Run right","Run left","Jump","Flames out ass","Duck like a wuss"});
                PaintableGL cs = gc.createConfigScreen(new MouseControlsGL());
                kc = createKeyControls();
                kc.set(Keyboard.KEY_SPACE);
                addToPaintList(cs);
            }
            public void gameUpdate(float interpolation)
            {
                if (kc.isTapped(Keyboard.KEY_SPACE))
                {
                    gc.printActionMap();
                }

                String[] actions = gc.getActions();
                for (int i = 0; i < actions.length;i++)
                {
                    boolean pressed = gc.isPressed(actions[i]);

                    if (pressed) System.out.println("Action="+actions[i]+" pressed="+pressed);

                }
            }
        };
        painter.start();

    }

    class MappedAction
    {
        /**True if button, false if axis*/
        boolean isButton;
        int axisVal;
        int index = -1;
        int controller;

        public MappedAction(boolean isButton, int index, int axisVal)
        {
            this.index = index;
            this.isButton = isButton;
            this.axisVal = axisVal;
        }

        public MappedAction(){}
    }

    class ControllerConfig implements PaintableGL
    {
        private int controllerI;
        private TextGL[] actionTexts;
        private TextGL[] actionVals;
        private int selectedRow;

        private TextGL titleText, controllerText, controllerField, noControlsText;
        private SpriteGL leftArrow, rightArrow;

        private boolean finished ;
        private TextGL finishedButton;

        /*ActiveVal box that is being edited. Also, determines if we're in edit mode
          If we are, editVal is not null. If we aren't, it's null.*/
        private TextGL editVal;

        public ControllerConfig()
        {
            controllerI = 0;
            editVal = null;
            selectedRow = -1;
            finished = false;

            titleText = new TextGL("Controller Configuration",new Font("sanserif",Font.BOLD,24),Color.LIGHT_GRAY);
            titleText.setLocation(10, 10);

            noControlsText = new TextGL("No Controllers Available!",new Font("sanserif",Font.BOLD,24),Color.RED);
            noControlsText.setLocation(10,80);

            controllerText = new TextGL("Controller:",new Font("sanserif",Font.BOLD,18),Color.YELLOW);
            controllerText.setLocation(20,60);

            controllerField = new TextGL("0",new Font("sanserif",Font.BOLD,18),Color.YELLOW);
            controllerField.setLocation(175,60);
            //controllerField.setDrawBounds(true);

            leftArrow = new SpriteGL("/images/arrow_left.png");
            leftArrow.setLocation(controllerField.getLocation().x-18,70);
            rightArrow = new SpriteGL("/images/arrow_right.png");
            rightArrow.setLocation(controllerField.getLocation().x+controllerField.getSize().width+18,70);

            finishedButton = new TextGL("Finished",new Font("sanserif",Font.BOLD,18),Color.GREEN);
            finishedButton.setDrawBounds(true);
            finishedButton.setLocation(300,60);

            updateControllerTracking();
        }

        private void updateControllerTracking()
        {

            if (controllerI - 1 >= 0) leftArrow.setTransparency(1f);
            else leftArrow.setTransparency(.5f);

            if (controllerI + 1 < getNumControllers()) rightArrow.setTransparency(1f);
            else rightArrow.setTransparency(.5f);
        }

        public void setActions(String[] actions)
        {
            actionTexts = new TextGL[actions.length];
            actionVals = new TextGL[actions.length];
            Font font = new Font("sanserif",Font.PLAIN,18);

            int y = 100;

            int width = 0;

            for (int i = 0; i < actions.length; i++)
            {
                String text = actions[i];
                if (text.length() > 20) text = text.substring(0, 20);

                TextGL action = new TextGL(text,font,Color.LIGHT_GRAY);
                if (action.getSize().width > width) width = action.getSize().width;
                actionTexts[i] = action;
                action.setLocation(20, y);
                y+= 30;
            }

            y = 100;

            for (int i = 0; i < actions.length; i++)
            {
                TextGL val = new TextGL("    ",font,Color.WHITE);
                val.setDrawBounds(true);
                actionVals[i] = val;
                val.setLocation(40+width,y);
                y+=30;
            }

        }

        public void draw(float interpolation)
        {
            titleText.draw(interpolation);

            if (getNumControllers() == 0)
            {
                noControlsText.draw(interpolation);
                finishedButton.setLocation(120, 150);
                finishedButton.draw(interpolation);
                return;
            }

            
            controllerText.draw(interpolation);
            controllerField.draw(interpolation);
            leftArrow.draw(interpolation);
            rightArrow.draw(interpolation);
            finishedButton.draw(interpolation);

            for (int i = 0; i < actionTexts.length; i++)
            {
                actionTexts[i].draw(interpolation);
            }

            for (int i = 0; i < actionVals.length; i++)
            {
                actionVals[i].draw(interpolation);
            }

            updateMapEdits();

            if (mouseControls.buttonPressed(MouseControlsGL.LEFT_BUTTON) && mouseControls.mouseOver(finishedButton))
            {
                finished = true;
            }
        }

        private void updateMapEdits()
        {
            if (mouseControls.buttonPressed(MouseControlsGL.LEFT_BUTTON))
            {
                boolean selected = false;
                //Activate button config mode if mouse over actionVal
                for (int i = 0; i < actionVals.length; i++)
                {
                    if (mouseControls.mouseOver(actionVals[i]))
                    {
                        editVal = actionVals[i];
                        editVal.setColor(Color.RED);
                        editVal.setText("Press Button");
                        selected = true;
                        selectedRow = i;
                        break;
                    }
                }
                if (!selected) editVal = null;

            }

            if (editVal != null)
            {
                int button = searchForPressedButton();

                if (button != -1)
                {
                    editVal.setColor(Color.GREEN);
                    editVal.setText(""+controllerI+button);
                    editVal.setDrawBounds(false);
                    editVal = null;

                    //map button to action
                    String actionName = actionTexts[selectedRow].getText();
                    MappedAction ma = actionMap.get(actionName);
                    ma.index = button;
                    ma.isButton = true;
                    ma.controller = controllerI;
                }
                else
                {
                    AxisValue aVal = new AxisValue(-1,0);
                    int axis = searchForAxisVector(aVal);
                    if (axis != -1)
                    {
                        editVal.setColor(Color.GREEN);
                        editVal.setText("A"+axis+aVal.value);
                        editVal.setDrawBounds(false);
                        editVal = null;

                        //map axis to action
                        String actionName = actionTexts[selectedRow].getText();
                        MappedAction ma = actionMap.get(actionName);
                        ma.index = axis;
                        ma.isButton = false;
                        ma.axisVal = aVal.value;
                        ma.controller = controllerI;
                    }
                }
            }
        }

        /**Returns button index if button is pressed. Returns -1 if no button is down*/
        private int searchForPressedButton()
        {

            for (int i = 0; i < getNumButtons(controllerI); i++)
            {
                if (isPressed(controllerI,i))
                {
                    return i;
                }
            }


            return -1;
        }

        private int searchForAxisVector(AxisValue value)
        {
            for (int i = 0; i < getNumAxes(controllerI);i++)
            {
                int val = getAxisValueInt(controllerI,i);
                if  (val != 0)
                {
                    value.axis = i;
                    value.value = val;
                    return i;
                }
            }

            return -1;
        }
    }

}

class AxisValue
{
    int axis, value;

    public AxisValue(int axis, int value)
    {
        this.axis = axis;
        this.value = value;
    }
}

class ControllerMonitor
{
    private GLPainter painter;
    private GameController gc;
    private TextGL[] buttonStates, axesStates;
    private int controllerI;

    public ControllerMonitor(GLPainter painter, GameController gc)
    {
        this.painter = painter;
        this.gc = gc;
        controllerI = 0;
        initDisplay();
    }

    private void initDisplay()
    {

        if (gc.getNumControllers() <= controllerI)
        {
            addLabel("Controller Not Available!",new Font("sanserif",Font.PLAIN,18),10,20,Color.RED);

            return;
        }

        int y = 40;
        addLabel("Controller "+controllerI,new Font("sanserif",Font.PLAIN,18),10,y,Color.BLUE);
        y=80;

        //Buttons
        Font font = new Font("sanserif",Font.PLAIN,14);     
        addLabel("Buttons:",font,10,y,Color.BLUE);
        y=120;

        for (int i = 0; i < gc.getNumButtons(controllerI); i++)
        {
            String buttonName = gc.getButtonName(controllerI, i);
            addLabel(buttonName,font,10,y,Color.LIGHT_GRAY);
            y+=20;
        }

        y=120;

        buttonStates = new TextGL[gc.getNumButtons(controllerI)];

        for (int i = 0; i < gc.getNumButtons(controllerI); i++)
        {
            TextGL label = addLabel("false",font,120,y,Color.WHITE);
            buttonStates[i] = label;
            y+=20;
        }
        
        //Axes
        y=80;
        addLabel("Axes:",font,240,y,Color.BLUE);
        
        y=120;
        addLabel("X Axis",font,240,y,Color.LIGHT_GRAY);
        addLabel("Y Axis",font,240,y+=20,Color.LIGHT_GRAY);
        addLabel("Z Axis",font,240,y+=20,Color.LIGHT_GRAY);
        addLabel("RX Axis",font,240,y+=20,Color.LIGHT_GRAY);
        addLabel("RY Axis",font,240,y+=20,Color.LIGHT_GRAY);
        addLabel("RZ Axis",font,240,y+=20,Color.LIGHT_GRAY);

        y=120;

        axesStates = new TextGL[6];
        for (int i = 0; i < 6; i++)
        {
            TextGL label = addLabel("0",font,350,y,Color.WHITE);
            axesStates[i] = label;
            y+=20;
        }
    }

    private TextGL addLabel(String name, Font font, int x, int y, Color color)
    {
        TextGL label = new TextGL(name,font,color);
        label.setLocation(x, y);
        painter.addToPaintList(label,1);
        return label;
    }

    public void update()
    {
        if (gc.getNumControllers() <= controllerI) return;

        for (int i = 0; i < gc.getNumButtons(controllerI); i++)
        {
            boolean pressed = Controllers.getController(controllerI).isButtonPressed(i);
            String pressedText =  (pressed)?"true":"false";
            TextGL state = buttonStates[i];
            if (!state.getText().equals(pressedText)) state.setText(pressedText );
        }

        Controller controller = Controllers.getController(controllerI);

        String val = "" + controller.getXAxisValue();
        setTextIfChanged(axesStates[0],val);
        val = "" + controller.getYAxisValue();
        setTextIfChanged(axesStates[1],val);
        val = "" + controller.getZAxisValue();
        setTextIfChanged(axesStates[2],val);
        val = "" + controller.getRXAxisValue();
        setTextIfChanged(axesStates[3],val);
        val = "" + controller.getRYAxisValue();
        setTextIfChanged(axesStates[4],val);
        val = "" + controller.getRZAxisValue();
        setTextIfChanged(axesStates[5],val);

    }

    private void setTextIfChanged(TextGL textGL, String text)
    {
        if (!textGL.getText().equals(text)) textGL.setText(text);
    }
}
