package InventoryParse;

/*
 * List of TODO:
 * 
 * 1. Clean-up exception handling
 * 2. Validate code is written defensively
 * 3. Add comments at method level
 * 4. Implement "Discard" functionality
 * 5. Ensure that the same file is not processed twice
 * 6. Dockerize Java App
 * 7. Create database running in Docker
 * 8. Connect Java App running in Docker to database
 * 9. Insert processed file into database
 * 10. Print some statistics after each record import is completed successfully
 * 11. Print an aggregate of L3 categories and total qty stock per store
 * 12. Move the file to a different folder after they have completed processing
 * 13. If you requeue a file share output
 * 14. Ensure we can pass the folder names into the container
 * 15. Write a SQL query to calculate the stats
 * 16. Add unit tests
 * 
 */


public class InventoryParse {

	public static void main(String[] args) throws Exception {

		String folderName = args[0];

		System.out.println("Input Folder: " + folderName);
		
		ParseJSON parseJSON = new ParseJSON(folderName);

		parseJSON.findParseJSON();

	}
}