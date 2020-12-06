package InventoryParse;

/*
 * InventoryParse: Main entry into App. 
 */
public class InventoryParse {

	public static void main(String[] args) throws Exception {

		String folderNameStr = null;
		
		if (args == null || args.length != 1) {
			throw new Exception("Usage: <app> <input-folder>");
		}
		
		folderNameStr = args[0];

		//System.out.println("Input Folder: " + folderName);
		
		ParseJSON parseJSON = new ParseJSON(folderNameStr);

		parseJSON.findParseJSON();

	}
}