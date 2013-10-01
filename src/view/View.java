/******************************************************************************
* Title: View.java
* Author: Mike Schoonover
* Date: 3/12/13
*
* Purpose:
*
* This class is the View in a Model-View-Controller architecture.
* It creates and handles all GUI components.
* It knows about the Model, but not the Controller.
* All GUI controls are set up to send their action messages to an
*  ActionListener, a WindowListener, or other valid listener external to the
*  View -- a pointer to this actionListener is provided via the View
*  constructor. The Controller often specifies itself as the listener for all
*  of the possible types of actions.
*
*/

//-----------------------------------------------------------------------------

package view;

//-----------------------------------------------------------------------------

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import model.ADataClass;
import toolkit.Tools;

//-----------------------------------------------------------------------------
// class View
//

public class View
{

    private JFrame mainFrame;
    private JPanel mainPanel;

    private ADataClass aDataClass;

    private MainMenu mainMenu;

    private JTextField dataVersionTField;
    private JTextField dataTArea1;
    private JTextField dataTArea2;

    private GuiUpdater guiUpdater;
    private Log log;
    private ThreadSafeLogger tsLog;
    private Help help;
    private About about;

    private ActionListener actionListener;
    private WindowListener windowListener;

    private Font blackSmallFont, redSmallFont;
    private Font redLargeFont, greenLargeFont, yellowLargeFont, blackLargeFont;

    private JLabel statusLabel, infoLabel;
    private JLabel progressLabel;

//-----------------------------------------------------------------------------
// View::View (constructor)
//

public View(ActionListener pActionListener, WindowListener pWindowListener,
                                                        ADataClass pADataClass)
{

    actionListener = pActionListener;
    windowListener = pWindowListener;
    aDataClass = pADataClass;

}//end of View::View (constructor)
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// View::init
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
    mainFrame.setJMenuBar(mainMenu = new MainMenu(actionListener));

    //create various fonts for use by the program
    createFonts();

    //create user interface: buttons, displays, etc.
    setupGui();

    //arrange all the GUI items
    mainFrame.pack();

    //display the main frame
    mainFrame.setVisible(true);

}// end of View::init
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// View::setupMainFrame
//
// Sets various options and styles for the main frame.
//

public void setupMainFrame()
{

    mainFrame = new JFrame("Content Handler");

    //add a JPanel to the frame to provide a familiar container
    mainPanel = new JPanel();
    mainFrame.getContentPane().add(mainPanel);

    //set the min/max/preferred sizes of the panel to set the size of the frame
    Tools.setSizes(mainPanel, 200, 300);

    mainFrame.addWindowListener(windowListener);

    //turn off default bold for Metal look and feel
    UIManager.put("swing.boldMetal", Boolean.FALSE);

    //force "look and feel" to Java style
    try {
        UIManager.setLookAndFeel(
            UIManager.getCrossPlatformLookAndFeelClassName());
        }
    catch (Exception e) {
        System.out.println("Could not set Look and Feel");
        }

    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    //    setLocation((int)screenSize.getWidth() - getWidth(), 0);

}// end of View::setupMainFrame
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// View::setupGUI
//
// Sets up the user interface on the mainPanel: buttons, displays, etc.
//

private void setupGui()
{

    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

    mainPanel.add(Box.createRigidArea(new Dimension(0,20))); //vertical spacer

    //create a label to display good/warning/bad system status
    statusLabel = new JLabel("Status");
    statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    mainPanel.add(statusLabel);

    mainPanel.add(Box.createRigidArea(new Dimension(0,20))); //vertical spacer

    //create a label to display miscellaneous info
    infoLabel = new JLabel("Info");
    infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    mainPanel.add(infoLabel);

    mainPanel.add(Box.createRigidArea(new Dimension(0,20))); //vertical spacer

    //add text field
    dataVersionTField = new JTextField("unknown");
    dataVersionTField.setAlignmentX(Component.LEFT_ALIGNMENT);
    Tools.setSizes(dataVersionTField, 100, 24);
    //text fields don't have action commands or action listeners
    dataVersionTField.setToolTipText("The data format version.");
    mainPanel.add(dataVersionTField);

    mainPanel.add(Box.createRigidArea(new Dimension(0,3))); //vertical spacer

    //add text field
    dataTArea1 = new JTextField("");
    dataTArea1.setAlignmentX(Component.LEFT_ALIGNMENT);
    Tools.setSizes(dataTArea1, 100, 24);
    //text fields don't have action commands or action listeners
    dataTArea1.setToolTipText("A data entry.");
    mainPanel.add(dataTArea1);

    mainPanel.add(Box.createRigidArea(new Dimension(0,3))); //vertical spacer

    //add text field
    dataTArea2 = new JTextField("");
    dataTArea2.setAlignmentX(Component.LEFT_ALIGNMENT);
    Tools.setSizes(dataTArea2, 100, 24);
    //text fields don't have action commands or action listeners
    dataTArea2.setToolTipText("A data entry.");
    mainPanel.add(dataTArea2);

    mainPanel.add(Box.createRigidArea(new Dimension(0,20))); //vertical spacer

    //add button
    JButton loadBtn = new JButton("Load");
    loadBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
    loadBtn.setActionCommand("Load Data From File");
    loadBtn.addActionListener(actionListener);
    loadBtn.setToolTipText("Load data from file.");
    mainPanel.add(loadBtn);

    mainPanel.add(Box.createRigidArea(new Dimension(0,10))); //vertical spacer

    //add a button
    JButton saveBtn = new JButton("Save");
    saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
    saveBtn.setActionCommand("Save Data To File");
    saveBtn.addActionListener(actionListener);
    saveBtn.setToolTipText("Save data to file.");
    mainPanel.add(saveBtn);

    mainPanel.add(Box.createRigidArea(new Dimension(0,10))); //vertical spacer

    progressLabel = new JLabel("Progress");
    progressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    mainPanel.add(progressLabel);

    mainPanel.add(Box.createRigidArea(new Dimension(0,10))); //vertical spacer

}// end of View::setupGui
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// View::createFonts
//
// Creates fonts for use by the program.
//

public void createFonts()
{

    //create small and large red and green fonts for use with display objects
    HashMap<TextAttribute, Object> map = new HashMap<TextAttribute, Object>();

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

}// end of View::createFonts
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// View::displayLog
//
// Displays the log window. It is not released after closing as the information
// is retained so it can be viewed the next time the window is opened.
//

public void displayLog()
{

    log.setVisible(true);

}//end of View::displayLog
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// View::displayHelp
//
// Displays help information.
//

public void displayHelp()
{

    help = new Help(mainFrame);
    help = null;  //window will be released on close, so point should be null

}//end of View::displayHelp
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// View::displayAbout
//
// Displays about information.
//

public void displayAbout()
{

    about = new About(mainFrame);
    about = null;  //window will be released on close, so point should be null

}//end of View::displayAbout
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// View::displayErrorMessage
//
// Displays an error dialog with message pMessage.
//

public void displayErrorMessage(String pMessage)
{

    Tools.displayErrorMessage(pMessage, mainFrame);

}//end of View::displayErrorMessage
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// View::updateGUIDataSet1
//
// Updates some of the GUI with data from the model.
//

public void updateGUIDataSet1()
{

    dataVersionTField.setText(aDataClass.getDataVersion());

    dataTArea1.setText(aDataClass.getDataItem(0));

    dataTArea2.setText(aDataClass.getDataItem(1));

}//end of View::updateGUIDataSet1
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// View::updateModelDataSet1
//
// Updates some of the model data with values in the GUI.
//

public void updateModelDataSet1()
{

    aDataClass.setDataVersion(dataVersionTField.getText());

    aDataClass.setDataItem(0, dataTArea1.getText());

    aDataClass.setDataItem(1, dataTArea2.getText());

}//end of View::updateModelDataSet1
//-----------------------------------------------------------------------------


}//end of class View
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
