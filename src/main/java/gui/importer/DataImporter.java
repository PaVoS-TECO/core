package gui.importer;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Importer for data that should be added to PaVoS. Import takes place for files in a specified folder of the server.
 */
public class DataImporter extends AbstractTableModel implements ActionListener {

	private static final long serialVersionUID = 5187852360487332477L;

	private JFrame frame;
	private Container contentPane;
	private SpringLayout layout;
	private JTable table;
	private JButton chooserButton;
	private JButton importButton;

	private int rows = 0;
	private int columns = 2;
	private ArrayList<File> files = new ArrayList<File>();
	private TreeMap<File, Integer> fileProgress = new TreeMap<File, Integer>();


	/**
     * Default constructor
     */
    public DataImporter() {
    }


    /**
     * Checks for files in the specified import folder and opens a new thread for each of them,
     * where a FileImporter is started to import the contained data.
     */
    public void startImportingFileData() {
    	this.frame = new JFrame("PaVoS Importer");
        this.contentPane = this.frame.getContentPane();
        this.layout = new SpringLayout();
        this.contentPane.setLayout(layout);

        int w = 180;
        int h = 60;

        this.chooserButton = new JButton("Choose Files");
        chooserButton.setName("chooserButton");
        /*chooserButton.setBackground(Static.tableHeaderBG);
        chooserButton.setForeground(Color.white);
        chooserButton.setFont(new Font("Calibri", Font.BOLD, 24));*/
        this.contentPane.add(chooserButton);
        this.layout.putConstraint(SpringLayout.WEST, chooserButton, 20, SpringLayout.WEST, this.contentPane);
        this.layout.putConstraint(SpringLayout.EAST, chooserButton, w + 20, SpringLayout.WEST, this.contentPane);
        this.layout.putConstraint(SpringLayout.NORTH, chooserButton, 20, SpringLayout.NORTH, this.contentPane);
        this.layout.putConstraint(SpringLayout.SOUTH, chooserButton, h + 20, SpringLayout.NORTH, this.contentPane);
        chooserButton.addActionListener(this);

        this.importButton = new JButton("Import Files");
        importButton.setName("importButton");
        /*importButton.setBackground(Static.tableHeaderBG);
        importButton.setForeground(Color.white);
        importButton.setFont(new Font("Calibri", Font.BOLD, 24));*/
        this.contentPane.add(importButton);
        this.layout.putConstraint(SpringLayout.WEST, importButton, -(w + 20), SpringLayout.EAST, this.contentPane);
        this.layout.putConstraint(SpringLayout.EAST, importButton, -20, SpringLayout.EAST, this.contentPane);
        this.layout.putConstraint(SpringLayout.NORTH, importButton, 20, SpringLayout.NORTH, this.contentPane);
        this.layout.putConstraint(SpringLayout.SOUTH, importButton, h + 20, SpringLayout.NORTH, this.contentPane);
        importButton.addActionListener(this);

        this.table = new JTable(this);
        JScrollPane scrollPane = new JScrollPane(this.table);
        this.contentPane.add(scrollPane);
        this.layout.putConstraint(SpringLayout.WEST, scrollPane, 20, SpringLayout.WEST, this.contentPane);
        this.layout.putConstraint(SpringLayout.EAST, scrollPane, -20, SpringLayout.EAST, this.contentPane);
        this.layout.putConstraint(SpringLayout.NORTH, scrollPane, 100, SpringLayout.NORTH, this.contentPane);
        this.layout.putConstraint(SpringLayout.SOUTH, scrollPane, -20, SpringLayout.SOUTH, this.contentPane);

        this.table.getColumnModel().getColumn(0).setMaxWidth(260);
        this.table.getColumnModel().getColumn(0).setMinWidth(260);
        DefaultTableCellRenderer rendererC = new DefaultTableCellRenderer();
        rendererC.setHorizontalAlignment(JLabel.CENTER);
        this.table.getColumnModel().getColumn(1).setCellRenderer(rendererC);
        this.table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        this.frame.setSize(420, 300);
        this.frame.setResizable(false);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // überschreiben wenn der Import läuft
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
    }

    /**
	 * Implements actionPerformed
	 * @param ev is a Button click event
	 */
	public void actionPerformed(ActionEvent ev) {
		String name = ((JButton) ev.getSource()).getName();
		if ("chooserButton".equals(name)) {
			this.chooseFiles();
		} else if ("importButton".equals(name)) {
			this.startImporting();
		}
	}

	private void chooseFiles() {
		JFileChooser opener = new JFileChooser();
		opener.setFileSelectionMode(JFileChooser.FILES_ONLY);
		opener.setMultiSelectionEnabled(true);
        int returnVal = opener.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File[] files = opener.getSelectedFiles();
            for (File file : files) {
            	this.files.add(file);
            	this.fileProgress.put(file, 0);
            }
            this.rows = files.length;
            this.fireTableDataChanged();
        }
	}

	private void startImporting() {
		this.chooserButton.setEnabled(false);
		this.importButton.setEnabled(false);

		for (final File file : this.files) {
			Thread aThread = new Thread(new Runnable() {
	            public void run() {
	                FileImporter importer = new FileImporter();
	                importer.addFileData(file);
	                /*synchronized(this) {
	                	Zustand des Progress zurückgeben
	                }*/
	            }
	        });
	        aThread.start();
		}
	}

	/**
	 * Returns the number of columns in the model. A JTable uses this method to determine how many columns it
	 * should create and display by default.
	 * @return the number of columns in the model
	 */
	public int getColumnCount() {
		return this.columns;
	}

	/**
	 * Returns the number of rows in the model. A JTable uses this method to determine how many rows it should
	 * display. This method should be quick, as it is called frequently during rendering.
	 * @return the number of rows in the model
	 */
	public int getRowCount() {
		return this.rows;
	}

	/**
	 * Returns the value for the cell at columnIndex and rowIndex.
	 * @param rowIndex the row whose value is to be queried
	 * @param columnIndex the column whose value is to be queried
	 * @return the value Object at the specified cell
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		File file = this.files.get(rowIndex);
		if (columnIndex == 0) {
			return file.getName();
		} else if (columnIndex == 1) {
			int percent = this.fileProgress.get(file);
			if (percent < 0) {
				return "unknown type";
			} else {
				if (percent > 100) {
					percent = 100;
				}
				return percent;
			}
		}
		return null;
	}

	@Override
	public String getColumnName(int column) {
		String[] columns = {
        		"File", "Importprogress"
        };
		return columns[column];
	}

}
