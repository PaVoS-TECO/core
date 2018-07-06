package gui;

import java.util.*;

/**
 * This Class holds the main functionality of the PaVoS program. It starts/stops the whole System and manages the export/import.
 */
public class MainFrame extends JFrame {

    /**
     * Default constructor
     */
    public MainFrame() {
    }

    /**
     * This Button is a Toggle Button which starts the PaVoS Programm and then the Button will toggle to a stop Button. Then you can stop the Programm with the Button.
     */
    private JToggleButton start_Button;

    /**
     * This Button will open an new Dialog where you can enter a new Topic Stream or a complete new Source Topic
     */
    private JButton add_Stream_Button;

    /**
     * This Button will open a File Chooser Instance where you can input a File, which will be importet to the Programm
     */
    private JButton import_Stream_Button;

    /**
     * This Button will open a new SensorFrame Instance and you will see all Sensors in the System
     */
    private JButton view_Sensor_Button;

    /**
     * This Button will open a new Browser Instance with the PaVoS View.
     */
    private JButton open_View_Button;

    /**
     * This Label just hold some Basic Informations about the Programm
     */
    private JLabel info_Label;

    /**
     * This a JMenu Instance which contains different JMenuItems which has some Controll Functionality
     */
    private JMenu controll_Menu;

    /**
     * This a JMenu Instance which contains different JMenuItems which has some Edit Functionality
     */
    private JMenu edit_Menu;

    /**
     * This a JMenu Instance which contains different JMenuItems which has some DataFunctionality
     */
    private JMenu data_Menu;

    /**
     * This MenuItem is a Toggle Button which starts the PaVoS Programm and then the Button will toggle to a stop Button. Then you can stop the Programm with the Button.
     */
    private JMenuItem start_MenuItem;

    /**
     * This MenuItem will open an new Dialog where you can enter a new Topic Stream or a complete new Source Topic
     */
    private JMenuItem add_Stream_MenuItem;

    /**
     * This MenuItem will open an new Dialog where you can delete Topic Stream or a complete new Source Topic
     */
    private JMenuItem delete_Stream_MenuItem;

    /**
     * This MenuItem will open a File Chooser Instance where you can input a File, which will be importet to the Programm
     */
    private JMenuItem import_Data_MenuItem;

    /**
     * This MenuItem will open a File Chooser Instance where you can input a File, which export the hole Data out of the Programm
     */
    private JMenuItem export_Data_MenuItem;

    /**
     * This MenuItem will open a new Browser Instance with the PaVoS View.
     */
    private JMenuItem open_View_MenuItem;

    /**
     * This MenuItem will open a new SensorFrame Instance and you will see all Sensors in the System
     */
    private JMenuItem view_Sensor_MenuItem;

    /**
     * This Table will be an overView of all Input and output Topics in the Programm
     */
    private JTable stream_Table;

    /**
     * This is a simble instance of the Controller Class.
     */
    private Controller controller;





}