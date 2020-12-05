package InventoryParse;

/*
 * List of TODO:
 * 
 * Clean-up exception handling - DONE
 * Validate code is written defensively - DONE
 * Add comments at sub-method level - DONE
 * Implement "Discard" functionality - DONE
 * Dockerize Java App - DONE
 * Add unit tests - DONE
 * Move the file to a different folder after they have completed processing - DONE
 * Create database running in Docker - DONE
 * Connect Java App running in Docker to database - DONE
 * 
 * Ensure that the same file is not processed twice
 * Insert processed file into database
 * Print some statistics after each record import is completed successfully
 * Print an aggregate of L3 categories and total qty stock per store
 * If you requeue a file share output
 * Ensure we can pass the folder names into the container
 * Write a SQL query to calculate the stats
 * 
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