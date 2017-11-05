package eu.vicci.fix;

import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.vicci.fix.methods.ColorFix;
import eu.vicci.fix.methods.TransparencyFix;

public class FixDiagrams {
	private static final String processFolder = "processes";
	private static final String fixedFolder = "FixedDiagrams";
	private static final Logger LOG = LoggerFactory.getLogger(FixDiagrams.class);
	
	public static void main(String[] args)throws Exception {
		new FixDiagrams().run();
	}
	
	public void run()throws Exception{
		File rootFolder = new File(processFolder);	    
        handleFolder(rootFolder);
	}
	
	private void handleFolder(File folder)throws Exception {
		if(!folder.isDirectory()){
			LOG.error("not a folder: {}", folder.getAbsolutePath());
			return;
		}
		for (File file : folder.listFiles()) {
			if(file.isDirectory()) handleFolder(file);
			else fixFile(file);
			
		}			
	}
	
	
	private void fixFile(File file) throws Exception{
		if(file.isDirectory()){
			LOG.error("not a file: {}", file.getAbsolutePath());
			return;
		}
		
        SAXReader reader = new SAXReader(); 
        ColorFix colorFix = new ColorFix();
        TransparencyFix transparencyFix = new TransparencyFix();
        
        Document document = reader.read(file);
        LOG.debug("##########################################");
        LOG.debug("Fix {}", file.getName());
        transparencyFix.fix(document);
        colorFix.fix(document);
        saveDocument(document, file);
        LOG.debug("##########################################\n");
	}
	
	private void saveDocument(Document document, File file) throws Exception{	
		File outFolder = new File(fixedFolder);
		String filePath = file.getAbsolutePath();
		String outPath = outFolder.getAbsolutePath();
		int index = outPath.lastIndexOf(fixedFolder);
		String relativePath = filePath.replaceFirst(Pattern.quote(outPath.substring(0, index)), "");
		relativePath = relativePath.replaceFirst(processFolder, fixedFolder);
		String relativeFolders = relativePath.substring(0, relativePath.lastIndexOf(File.separator));
		File folders = new File(relativeFolders);
		folders.mkdirs();		
		
		FileOutputStream fos = new FileOutputStream(relativePath);
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("ASCII");
		XMLWriter writer = new XMLWriter(fos, format);
		writer.write(document);
		writer.flush();		

		LOG.debug("saved to {}", relativePath);
	}

}
