package eu.vicci.fix;

public class Constants {
	public static String ColorIdentifier_Pictogram = "//@colors.";
	public static String ColorIdentifier_Diagram = "/0/@colors.%d";
	public static String Foreground = "foreground";
	public static String Background = "background";
	
	public static String getColorIdentifierForDiagram(int id){
		return String.format(ColorIdentifier_Diagram, id);
	}

}
