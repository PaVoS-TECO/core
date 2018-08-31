package edu.teco.pavos.pim;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;

/**
 * The table of the window
 * @author Jean Baumgarten
 */
public class DataTable extends AbstractTableModel {
	
	private static final long serialVersionUID = 8025853545067957119L;

	private ArrayList<String> files;
	private HashMap<String, Integer> fileProgress;
	int finished = 0;

	private JButton chooser;
	private JButton importer;

	/**
	 * Default Constructor
	 * @param chooser is a button
	 * @param importer is a button
	 */
	public DataTable(JButton chooser, JButton importer) {
		
		this.chooser = chooser;
		this.importer = importer;
		this.files = new ArrayList<String>();
		this.fileProgress = new HashMap<String, Integer>();
		
	}
	
	/**
	 * Sends this file is finished importing
	 */
	public void sendFinished() {
		
		this.finished++;
		if (this.finished >= this.files.size()) {
			this.chooser.setEnabled(true);
			this.importer.setEnabled(true);
		}
		
	}
	
	/**
	 * Sets the files for the table
	 * @param files are the files to be imported
	 */
	public void setFiles(ArrayList<String> files) {
		
		this.files = files;
		this.fileProgress = new HashMap<String, Integer>();
		
		for (String file : files) {
			this.fileProgress.put(file, 0);
		}
		
	}
	
	/**
	 * Sets the progress for the table
	 * @param file is the file to be changed
	 * @param percent is the percent to be changed
	 */
	public void setProgress(String file, int percent) {
		
		this.fileProgress.put(file, percent);
		this.fireTableDataChanged();
		
	}

	/**
	 * Returns the number of columns in the model. A JTable uses this method to determine how many columns it
	 * should create and display by default.
	 * @return the number of columns in the model
	 */
	public int getColumnCount() {
		
		return 2;
		
	}

	/**
	 * Returns the number of rows in the model. A JTable uses this method to determine how many rows it should
	 * display. This method should be quick, as it is called frequently during rendering.
	 * @return the number of rows in the model
	 */
	public int getRowCount() {
		
		return this.fileProgress.size();
		
	}

	/**
	 * Returns the value for the cell at columnIndex and rowIndex.
	 * @param rowIndex the row whose value is to be queried
	 * @param columnIndex the column whose value is to be queried
	 * @return the value Object at the specified cell
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		String file = this.files.get(rowIndex);
		if (columnIndex == 0) {
			
			return file;
			
		} else if (columnIndex == 1) {
			
			int percent = this.fileProgress.get(file);
			if (percent < 0) {
				
				return "unknown type";
				
			} else {
				
				if (percent > 100) {
					percent = 100;
				}
				
				return "" + percent + " %";
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
