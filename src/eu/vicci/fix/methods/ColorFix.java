package eu.vicci.fix.methods;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.vicci.fix.methods.ColorFixMappings.Color;

//relative colors happens only for foreground and background elements
public class ColorFix {
	private static final Logger LOG = LoggerFactory.getLogger(ColorFix.class);
	private final ColorFixMappings mappings = ColorFixMappings.getInstance();
	
	private Element diagram;
	private List<Color> colors;
	
	public void fix(Document document){		
		colors = new ArrayList<>();
		diagram = document.getRootElement().element("Diagram");	
		initExistingColors();
		iterate(diagram);
	}
	
	private void iterate(Element element){
		Iterator<Element> it = element.elementIterator();
		while (it.hasNext()) {
			Element next = (Element) it.next();
			String colorRef = getColorReference(next);
			if(colorRef == null) iterate(next);
			else changeColor(next, colorRef);
		}		
	}
	
	private void changeColor(Element element, String colorRef){
		Element parent = element.getParent();
		Color color = mappings.getColor(colorRef);
		int index = colors.lastIndexOf(color);
		if(index < 0){
			index = colors.size();
			colors.add(color);
			addColorsElement(color);
		}
		String newColorRef = "/0/@colors." + index;
		parent.addAttribute(element.getName(), newColorRef);
		parent.remove(element);
		LOG.debug("changed color ref: {}", colorRef);
		LOG.debug("		to {}", newColorRef);
	}
	
	private Element addColorsElement(Color color){
		Element ce = diagram.addElement("colors");
		if(color.r != null)ce.addAttribute("red", color.r);
		if(color.g != null)ce.addAttribute("green", color.g);
		if(color.b != null)ce.addAttribute("blue", color.b);
		LOG.debug("added color: r:{} g:{} b:{}", color.r, color.g, color.b);
		return ce;
	}
	
	/**
	 * returns null if no back or foreground element
	 */
	private String getColorReference(Element element){
		if(!"foreground".equals(element.getName()) && !"background".equals(element.getName()))
				return null;
		return element.attributeValue("href");
	}
	
	private void initExistingColors(){
		List<Element> nodes = diagram.elements("colors");
        for (Element node : nodes) {
        	Color c = Color.fromColorElement(node);
        	colors.add(c);
        	LOG.debug("existing color: r:{} g:{} b:{}", c.r, c.g, c.b);
		}		
	}
	
}
