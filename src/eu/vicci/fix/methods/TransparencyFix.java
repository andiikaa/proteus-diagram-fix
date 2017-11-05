package eu.vicci.fix.methods;

import java.util.Iterator;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransparencyFix {
	private static final Logger LOG = LoggerFactory.getLogger(TransparencyFix.class);
	private static final String ATT_TRANSPARENCY = "transparency";
	
	public void fix(Document doc){
		recursiveFix(doc.getRootElement());		
	}
	
	private void recursiveFix(Element element){
		Attribute value = element.attribute(ATT_TRANSPARENCY);
		if(value != null){
			convertTransparency(value);
		}
		Iterator<Element> it = element.elementIterator();
		while (it.hasNext()) {
			recursiveFix(it.next());
		}		
	}
	
	private void convertTransparency(Attribute transparency){
		String old = transparency.getValue();
		Float oldFloat = Float.parseFloat(old);
		if(oldFloat <= 1) return;
		Float newFloat = oldFloat / 100;
		String newValue = String.format("%.2f", newFloat).replace(",", ".");
		transparency.setValue(newValue);
		LOG.debug("{} to {}", old, newValue);
	}
	

}
