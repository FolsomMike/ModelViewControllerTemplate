/******************************************************************************
* Title: MainView.java
* Author: Mike Schoonover
* Date: 3/12/13
*
* Purpose:
*
* This class is the Main View in a Model-View-Controller architecture.
* It creates and handles all GUI components.
* It knows about the Model, but not the Controller.
* 
* There may be many classes in the view package which handle different aspects
* of the GUI.
*
* All GUI control events, including Timer events are caught by this object
* and passed on to the "Controller" object pointed by the class member
* "eventHandler" for final handling.
*
*/

//-----------------------------------------------------------------------------

package view;

//-----------------------------------------------------------------------------

import controller.EventHandler;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import mksystems.mswing.MFloatSpinner;
import model.ADataClass;
import toolkit.Tools;

//-----------------------------------------------------------------------------
// class MainView
//

public class MainView implements ActionListener, WindowListener, ChangeListener
{

    private JFrame mainFrame;
    private JPanel mainPanel;

    private final ADataClass aDataClass;

    private MainMenu mainMenu;

    private JTextField dataVersionTField;
    private JTextField dataTArea1;
    private JTextField dataTArea2;

    private GuiUpdater guiUpdater;
    private Log log;
    private ThreadSafeLogger tsLog;
    private Help help;
    private About about;

    private javax.swing.Timer mainTimer;

    private final EventHandler eventHandler;

    private Font blackSmallFont, redSmallFont;
    private Font redLargeFont, greenLargeFont, yellowLargeFont, blackLargeFont;

    private JLabel statusLabel, infoLabel;
    private JLabel progressLabel;

//-----------------------------------------------------------------------------
// MainView::MainView (constructor)
//

public MainView(EventHandler pEventHandler, ADataClass pADataClass)
{

    eventHandler = pEventHandler;
    aDataClass = pADataClass;

}//end of MainView::MainView (constructor)
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// MainView::init
//
// Initializes the object.  Must be called immediately after instantiation.
//

public void init()
{

    setupMainFrame();

    //create a window for displaying messages and an object to handle updating
    //it in threadsafe manner
    log = new Log(mainFrame); log.setLocation(230, 0);

    tsLog = new ThreadSafeLogger(log.textArea);

    //create an object to handle thread safe updates of GUI components
    guiUpdater = new GuiUpdater(mainFrame);
    guiUpdater.init();

    tsLog.appendLine("Hello"); tsLog.appendLine("");

    //add a menu to the main form, passing this as the action listener
    mainFrame.setJMenuBar(mainMenu = new MainMenu(this));

    //create various fonts for use by the program
    createFonts();

    //create user interface: buttons, displays, etc.
    setupGui();

    //arrange all the GUI items
    mainFrame.pack();

    //display the main frame
    mainFrame.setVisible(true);

}// end of MainView::init
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// MainView::setupMainFrame
//
// Sets various options and styles for the main frame.
//

public void setupMainFrame()
{

    mainFrame = new JFrame("Model View Controller Template");

    //add a JPanel to the frame to provide a familiar container
    mainPanel = new JPanel();
    mainFrame.setContentPane(mainPanel);

    //set the min/max/preferred sizes of the panel to set the size of the frame
    Tools.setSizes(mainPanel, 200, 350);

    mainFrame.addWindowListener(this);

    //turn off default bold for Metal look and feel
    UIManager.put("swing.boldMetal", Boolean.FALSE);

    //force "look and feel" to Java style
    try {
        UIManager.setLookAndFeel(
            UIManager.getCrossPlatformLookAndFeelClassName());
        }
    catch (ClassNotFoundException | InstantiationException |
            IllegalAccessException | UnsupportedLookAndFeelException e) {
        System.out.println("Could not set Look and Feel");
        }

    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    //    setLocation((int)screenSize.getWidth() - getWidth(), 0);

}// end of MainView::setupMainFrame
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// MainView::setupGUI
//
// Sets up the user interface on the mainPanel: buttons, displays, etc.
//

private void setupGui()
{

    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
    
    JPanel controlsPanel = new JPanel();
    
    controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));

    addVerticalSpacer(controlsPanel, 20);

    //create a label to display good/warning/bad system status
    statusLabel = new JLabel("Status");
    statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    controlsPanel.add(statusLabel);

    addVerticalSpacer(controlsPanel, 20);

    //create a label to display miscellaneous info
    infoLabel = new JLabel("Info");
    infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    controlsPanel.add(infoLabel);

    addVerticalSpacer(controlsPanel, 20);

    //add text field
    dataVersionTField = new JTextField("unknown");
    dataVersionTField.setAlignmentX(Component.LEFT_ALIGNMENT);
    Tools.setSizes(dataVersionTField, 100, 24);
    //text fields don't have action commands or action listeners
    dataVersionTField.setToolTipText("The data format version.");
    controlsPanel.add(dataVersionTField);

    addVerticalSpacer(controlsPanel, 3);
    
    //add text field
    dataTArea1 = new JTextField("");
    dataTArea1.setAlignmentX(Component.LEFT_ALIGNMENT);
    Tools.setSizes(dataTArea1, 100, 24);
    //text fields don't have action commands or action listeners
    dataTArea1.setToolTipText("A data entry.");
    controlsPanel.add(dataTArea1);
    
    addVerticalSpacer(controlsPanel, 3);    

    //add text field
    dataTArea2 = new JTextField("");
    dataTArea2.setAlignmentX(Component.LEFT_ALIGNMENT);
    Tools.setSizes(dataTArea2, 100, 24);
    //text fields don't have action commands or action listeners
    dataTArea2.setToolTipText("A data entry.");
    controlsPanel.add(dataTArea2);

    addVerticalSpacer(controlsPanel, 20);

    //add button
    JButton loadBtn = new JButton("Load");
    loadBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
    loadBtn.setActionCommand("Load Data From File");
    loadBtn.addActionListener(this);
    loadBtn.setToolTipText("Load data from file.");
    controlsPanel.add(loadBtn);

    addVerticalSpacer(controlsPanel, 10);
    
    //add a button
    JButton saveBtn = new JButton("Save");
    saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
    saveBtn.setActionCommand("Save Data To File");
    saveBtn.addActionListener(this);
    saveBtn.setToolTipText("Save data to file.");
    controlsPanel.add(saveBtn);

    addVerticalSpacer(controlsPanel, 10);    

    progressLabel = new JLabel("Progress");
    progressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    controlsPanel.add(progressLabel);

    addVerticalSpacer(controlsPanel, 10);    

    //set this spinner up for use with doubles
    //the format string "##0" has decimal places
    //use intSpinner1.getIntValue() to retrieve the value as an integer
    
    MFloatSpinner doubleSpinner1 = 
            new MFloatSpinner(5.5, 1.1, 9.9, 0.1, "##0.0", 60, 20);
    doubleSpinner1.setName("Double Spinner 1 -- used for doubles");
    doubleSpinner1.addChangeListener(this);
    doubleSpinner1.setToolTipText("This is float spinner #1!");
    controlsPanel.add(doubleSpinner1);

    addVerticalSpacer(controlsPanel, 10);
    
    //set this spinner up for use with integers
    //the format string "##0" has no decimal places
    //use intSpinner1.getIntValue() to retrieve the value as an integer
    
    MFloatSpinner intSpinner1 = 
            new MFloatSpinner(1, 1, 100000, 1, "##0", 60, 20);
    intSpinner1.setName("Integer Spinner 1 -- used for integers");
    intSpinner1.addChangeListener(this);
    intSpinner1.setToolTipText("This is float spinner #1!");
    controlsPanel.add(intSpinner1);
 
    mainPanel.add(controlsPanel);
    
}// end of MainView::setupGui
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// MainView::addVerticalSpacer
//
// Adds a vertical spacer of pNumPixels height to JPanel pTarget.
//

private void addVerticalSpacer(JPanel pTarget, int pNumPixels)
{

    pTarget.add(Box.createRigidArea(new Dimension(0,pNumPixels)));
    
}// end of MainView::addVerticalSpacer
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// MainView::createFonts
//
// Creates fonts for use by the program.
//

public void createFonts()
{

    //create small and large red and green fonts for use with display objects
    HashMap<TextAttribute, Object> map = new HashMap<>();

    blackSmallFont = new Font("Dialog", Font.PLAIN, 12);

    map.put(TextAttribute.FOREGROUND, Color.RED);
    redSmallFont = blackSmallFont.deriveFont(map);

    //empty the map to use for creating the large fonts
    map.clear();

    blackLargeFont = new Font("Dialog", Font.PLAIN, 20);

    map.put(TextAttribute.FOREGROUND, Color.GREEN);
    greenLargeFont = blackLargeFont.deriveFont(map);

    map.put(TextAttribute.FOREGROUND, Color.RED);
    redLargeFont = blackLargeFont.deriveFont(map);

    map.put(TextAttribute.FOREGROUND, Color.YELLOW);
    yellowLargeFont = blackLargeFont.deriveFont(map);

}// end of MainView::createFonts
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// MainView::displayLog
//
// Displays the log window. It is not released after closing as the information
// is retained so it can be viewed the next time the window is opened.
//

public void displayLog()
{

    log.setVisible(true);

}//end of MainView::displayLog
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// MainView::displayHelp
//
// Displays help information.
//

public void displayHelp()
{

    help = new Help(mainFrame);
    help = null;  //window will be released on close, so point should be null

}//end of MainView::displayHelp
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// MainView::displayAbout
//
// Displays about information.
//

public void displayAbout()
{

    about = new About(mainFrame);
    about = null;  //window will be released on close, so point should be null

}//end of MainView::displayAbout
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// MainView::displayErrorMessage
//
// Displays an error dialog with message pMessage.
//

public void displayErrorMessage(String pMessage)
{

    Tools.displayErrorMessage(pMessage, mainFrame);

}//end of MainView::displayErrorMessage
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// MainView::updateGUIDataSet1
//
// Updates some of the GUI with data from the model.
//

public void updateGUIDataSet1()
{

    dataVersionTField.setText(aDataClass.getDataVersion());

    dataTArea1.setText(aDataClass.getDataItem(0));

    dataTArea2.setText(aDataClass.getDataItem(1));

}//end of MainView::updateGUIDataSet1
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// MainView::drawRectangle
//
// Draws a rectangle on mainPanel
//

public void drawRectangle()
{

    
    Graphics2D g2 = (Graphics2D)mainPanel.getGraphics();

     // draw Rectangle2D.Double
    g2.draw(new Rectangle2D.Double(20, 10,10, 10));
        
}//end of MainView::drawRectangle
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// MainView::updateModelDataSet1
//
// Updates some of the model data with values in the GUI.
//

public void updateModelDataSet1()
{

    aDataClass.setDataVersion(dataVersionTField.getText());

    aDataClass.setDataItem(0, dataTArea1.getText());

    aDataClass.setDataItem(1, dataTArea2.getText());

}//end of MainView::updateModelDataSet1
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// MainView::setupAndStartMainTimer
//
// Prepares and starts a Java Swing timer.
//

public void setupAndStartMainTimer()
{

    //main timer has 2 second period
    mainTimer = new javax.swing.Timer (2000, this);
    mainTimer.setActionCommand ("Timer");
    mainTimer.start();

}// end of MainView::setupAndStartMainTimer
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// MainView::setTextForDataTArea1
//
// Sets the text value for text box.
//

public void setTextForDataTArea1(String pText)
{

    dataTArea1.setText(pText);

}// end of MainView::setTextForDataTArea1
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// MainView::setTextForDataTArea2
//
// Sets the text value for text box.
//

public void setTextForDataTArea2(String pText)
{

    dataTArea2.setText(pText);

}// end of MainView::setTextForDataTArea2
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// MainView::actionPerformed
//
// Responds to events and passes them on to the "Controller" (MVC Concept)
// objects.
//

@Override
public void actionPerformed(ActionEvent e)
{

    eventHandler.actionPerformed(e);

}//end of MainView::actionPerformed
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// MainView::stateChanged
//

@Override
public void stateChanged(ChangeEvent ce) {
   
    eventHandler.stateChanged(ce);
    
}//end of MainView::stateChanged
//-----------------------------------------------------------------------------



//-----------------------------------------------------------------------------
// MainView::windowClosing
//
// Handles actions necessary when the window is closing
//

@Override
public void windowClosing(WindowEvent e)
{

    eventHandler.windowClosing(e);

}//end of Controller::windowClosing
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// MainView::(various window listener functions)
//
// These functions are implemented per requirements of interface WindowListener
// but do nothing at the present time.  As code is added to each function, it
// should be moved from this section and formatted properly.
//

@Override
public void windowActivated(WindowEvent e){}
@Override
public void windowDeactivated(WindowEvent e){}
@Override
public void windowOpened(WindowEvent e){}
//@Override
//public void windowClosing(WindowEvent e){}
@Override
public void windowClosed(WindowEvent e){}
@Override
public void windowIconified(WindowEvent e){}
@Override
public void windowDeiconified(WindowEvent e){}

//end of MainView::(various window listener functions)
//-----------------------------------------------------------------------------

}//end of class MainView
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
