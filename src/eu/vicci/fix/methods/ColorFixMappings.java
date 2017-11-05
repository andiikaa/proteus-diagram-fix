package eu.vicci.fix.methods;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColorFixMappings {
	private static final Logger LOG = LoggerFactory.getLogger(ColorFixMappings.class);
	private static final String folder = "model";
	private static final String colors = "colors";
	
	private static ColorFixMappings instance;
	
	//file name e.g. and.pictograms
	private HashMap<String, List<Color>> maps = new HashMap<>();
	
	private ColorFixMappings(){
		createMappings();
	}
	
	public static ColorFixMappings getInstance(){
		if(instance == null) instance = new ColorFixMappings();
		return instance;
	}
	
	private void createMappings(){
		File root = new File(folder);
		for (File file : root.listFiles()) {
			if(!file.isDirectory() && file.getName().endsWith(".pictograms"))
				map(file);
		}		
	}
	
	private void map(File file){
		LOG.debug("map {}", file.getName());
        SAXReader reader = new SAXReader(); 
        Document document = null;
        try {
        	document = reader.read(file);
		} catch (DocumentException e) {
			LOG.error(e.getLocalizedMessage());
			return;
		}	        
        
        List<Element> nodes = document.getRootElement().elements(colors);
        ArrayList<Color> tmp = new ArrayList<>();
        for (Element node : nodes) {
        	Color c = Color.fromColorElement(node);
        	tmp.add(c);
        	LOG.debug("color: r:{} g:{} b:{}", c.r, c.g, c.b);
		}
        maps.put(file.getName(), tmp);
	}
	
	/**
	 * Finds the color for the given reference e.g. 
	 * ../../../../plugin/eu.vicci.process.graphiti/model/process.pictograms#//@colors.0
	 */
	public Color getColor(String colorReference){
		if(!isSupportedReference(colorReference)){
			LOG.error("unsuported color reference: {}", colorReference);
			return null;
		}
		String[] split = splitColorAndPicto(colorReference);
		List<Color> colors = maps.get(split[0]);
		int index = Integer.parseInt(split[1]);
		return colors.get(index);
	}
	
	//0 contains pictorgram, 1 contains index
	private static String[] splitColorAndPicto(String colorReference){
		String[] split = colorReference.split("eu.vicci.process.graphiti/model/");	
		split = split[1].split("#");
		split[1] = split[1].split("\\.")[1];
		return split;		
	}
	
	private static boolean isSupportedReference(String colorReference){
		return colorReference.contains("eu.vicci.process.graphiti/model/") 
		&& colorReference.contains("pictograms#") && colorReference.contains("//@colors.");		
	}
	
	private static boolean stringEquals(String one, String two){
		if(one != null) return one.equals(two);
		if(two != null) return two.equals(one);
		return true; //if both are null
	}
	
	public static class Color{
		String r;
		String g;
		String b;
		
		public static Color fromColorElement(Element element){
			Color c = new Color();
        	c.r = element.attributeValue("red");
        	c.g = element.attributeValue("green");
        	c.b = element.attributeValue("blue");
        	return c;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj == null) return false;
			if(!(obj instanceof Color)) return false;
			Color two = (Color)obj;
			return stringEquals(r, two.r) && stringEquals(g, two.g) && stringEquals(b, two.b);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(r, g, b);
		}
	}
	
	

}
