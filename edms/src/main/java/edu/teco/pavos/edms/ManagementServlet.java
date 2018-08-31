package edu.teco.pavos.edms;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Management Servlet for the export and download functionality
 * @author Jean Baumgarten
 */
public class ManagementServlet extends HttpServlet {

	private static final long serialVersionUID = -4176701096580683392L;
	private HashSet<String> parameters;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		
		Enumeration<String> enumeration = req.getParameterNames();
		this.parameters = new HashSet<String>();
		while (enumeration.hasMoreElements()) {
			this.parameters.add(enumeration.nextElement());
		}
		
		if (this.parameters.contains("requestType")) {
			
			String type = req.getParameter("requestType");
			
			if (type.equals("setIDReady")) {
				AlterableDownloadState ads = new AlterableDownloadState(req.getParameter("downloadID"));
				ads.setFileReadyForDownload();
				String path = "/usr/pke/exports/" + ads.getID() + ".csv";
				ads.setFilePath(new File(path));
				ads.savePersistent();
				PrintWriter writer = res.getWriter();
				writer.println(ads.getDownloadState());
				writer.close();
			}
			
			if (type.equals("newExport")) {
				
				this.export(req, res);
				
			} else if (type.equals("getStatus")) {
				
				this.status(req, res);
				
			} else if (type.equals("tryDownload")) {
				
				this.download(req, res);
				
			} else if (type.equals("getExtensions")) {
				
				this.extensions(req, res);
				
			}
			
		} else {
			
			res.sendError(HttpServletResponse.SC_BAD_REQUEST);
			
		}
		
	}
	
	private void export(HttpServletRequest req, HttpServletResponse res) 
			throws IOException {
		
		boolean check = this.parameters.contains("downloadID")
				&& this.parameters.contains("extension")
				&& this.parameters.contains("timeFrame")
				&& this.parameters.contains("observedProperties")
				&& this.parameters.contains("clusters");
		
		if (check) {
			
			String dID = req.getParameter("downloadID");
			String ready = (new DownloadState(dID)).getDownloadState();
			
			if (ready.equals("noID") || ready.equals("error")) {
				
				String ext = req.getParameter("extension");
				String tf = req.getParameter("timeFrame");
				String ops = req.getParameter("observedProperties");
				String cIDs = req.getParameter("clusters");
				
				Runtime.getRuntime().exec("java -jar /usr/pke/pke.jar "
						+ ext + " "
						+ tf + " "
						+ ops + " "
						+ cIDs + " "
						+ dID);
				
			}
			
		} else {
			
			res.sendError(HttpServletResponse.SC_BAD_REQUEST);
			
		}
		
	}
	
	private void status(HttpServletRequest req, HttpServletResponse res) 
			throws IOException {
		
		if (this.parameters.contains("downloadID")) {
			
			String dID = req.getParameter("downloadID");
			String ready = (new DownloadState(dID)).getDownloadState();
			
			PrintWriter writer = res.getWriter();
			writer.println(ready);
			writer.close();
			
		} else {
			
			res.sendError(HttpServletResponse.SC_BAD_REQUEST);
			
		}
		
	}
	
	private void download(HttpServletRequest req, HttpServletResponse res) 
			throws IOException {
		
		if (this.parameters.contains("downloadID")) {
			
			String dID = req.getParameter("downloadID");
			DownloadState ds = new DownloadState(dID);
			String ready = ds.getDownloadState();
			
			if (!ready.equals("true")) {
				
				PrintWriter writer = res.getWriter();
				writer.println(ready);
				writer.close();
				
			} else {
				
				File file = ds.getFilePath();
				
				if (file.exists()) {
					
					try {
						
						byte[] zip = zipFile(file);
						ServletOutputStream sos = res.getOutputStream();
			            res.setContentType("application/zip");
			            res.setHeader("Content-Disposition", "attachment; filename=" + ds.getID() + ".zip");
			            sos.write(zip);
			            sos.flush();
			            sos.close();
			            
					} catch (IOException e) {
						
						PrintWriter writer = res.getWriter();
						writer.println("error");
						writer.close();
						
					}
		            
					
				} else {
					
					PrintWriter writer = res.getWriter();
					writer.println("error");
					writer.close();
					
				}
	            
			}
			
		} else {
			
			res.sendError(HttpServletResponse.SC_BAD_REQUEST);
			
		}
		
	}
	
	private void extensions(HttpServletRequest req, HttpServletResponse res) 
			throws IOException {
		
		PrintWriter writer = res.getWriter();
		Properties properties = new Properties();
		BufferedInputStream stream;
		
		try {
			
			stream = new BufferedInputStream(new FileInputStream("/usr/pke/export.properties"));
			properties.load(stream);
			stream.close();
			String extensions = properties.getProperty("extensions");
			writer.println(extensions);
			
		} catch (FileNotFoundException e) {
			
			System.out.println(e.getLocalizedMessage());
			writer.println("error");
			
		} catch (IOException e) {
			
			System.out.println(e.getLocalizedMessage());
			writer.println("error");
			
		}
		
		writer.close();
		
	}

    /**
     * Compress the given directory with all its files.
     */
    private byte[] zipFile(File file)
    		throws IOException {
    	
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        byte[] bytes = new byte[2048];

        FileInputStream fis = new FileInputStream(file.getAbsolutePath());
        BufferedInputStream bis = new BufferedInputStream(fis);
        
        zos.putNextEntry(new ZipEntry(file.getName()));

        int bytesRead;
        while ((bytesRead = bis.read(bytes)) != -1) {
            zos.write(bytes, 0, bytesRead);
        }
        
        zos.closeEntry();
        bis.close();
        fis.close();
        zos.flush();
        baos.flush();
        zos.close();
        baos.close();

        return baos.toByteArray();
        
    }

}
