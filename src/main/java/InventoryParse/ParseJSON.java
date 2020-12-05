package InventoryParse;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ParseJSON {

	final static public int VALID_FILE = 0;
	final static public int RECORD_COUNT_MISMATCH = 1;
	final static public int QTY_COUNT_MISMATCH = 2;
	final static public int PREVIOUSLY_PROCESSED = 5;

	private File inputFolder = null;

	public ParseJSON() {

	}

	public ParseJSON(String folderNameStr) throws Exception {

		inputFolder = new File(folderNameStr);

		if (inputFolder == null || !inputFolder.exists() || !inputFolder.isDirectory()) {

			throw new Exception("Invalid input folder");
		}
	}

	public void findParseJSON() throws Exception {

		for (;;) {

			File absoluteFileName = findFile();

			if (absoluteFileName != null) {

				validateJSON(absoluteFileName);

				moveFile(absoluteFileName);

			} else {

				System.out.println("No file found, sleeping.");
				try {
					TimeUnit.SECONDS.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

	}

	/*
	 * Returns a file or null if no file exists
	 */
	private File findFile() throws Exception {

		File absoluteFileName = null;

		for (final File fileEntry : inputFolder.listFiles()) {

			absoluteFileName = fileEntry.getAbsoluteFile();

			if (absoluteFileName == null || !absoluteFileName.isFile()) {
				throw new Exception("Invalid file found, please check input folder");
			}

			System.out.println("File found: " + absoluteFileName.toString());
			break;

		}

		return absoluteFileName;
	}

	/*
	 * Returns true if file is valid TODO: distinguish based on discard reason
	 */
	int validateJSON(File absoluteFileName) throws IOException, ParseException {

		int fileValidatyStatus = VALID_FILE;

		FileReader fileReader = new FileReader(absoluteFileName);

		// parsing file "drop-n.json"
		Object obj = new JSONParser().parse(fileReader);

		// TODO: add function to validate JSON format

		JSONObject jo = (JSONObject) obj;

		JSONArray productsArray = (JSONArray) jo.get("products");

		Long totalRecordcount = 0L;
		Long totalQtysum = 0L;

		for (int i = 0, size = productsArray.size(); i < size; i++) {

			JSONObject objectInArray = (JSONObject) productsArray.get(i);

			String sku = (String) objectInArray.get("sku");
			String description = (String) objectInArray.get("description");
			String category = (String) objectInArray.get("category");
			Double price = ((Number) objectInArray.get("price")).doubleValue();
			String location = (String) objectInArray.get("location");
			Long qty = (Long) objectInArray.get("qty");

			totalRecordcount += 1;
			totalQtysum += qty;

		}

		JSONObject transSum = (JSONObject) jo.get("transmissionsummary");
		String id = (String) transSum.get("id");
		Long transSumRecordcount = (Long) transSum.get("recordcount");
		Long transSumQtysum = (Long) transSum.get("qtysum");

		if (totalRecordcount.longValue() != transSumRecordcount.longValue()) {

			fileValidatyStatus += RECORD_COUNT_MISMATCH;

		}

		if (totalQtysum.longValue() != transSumQtysum) {

			fileValidatyStatus += QTY_COUNT_MISMATCH;

		}

		//System.out.println("######## Closing " + absoluteFileName.getName());

		fileReader.close();

		return fileValidatyStatus;

	}

	/*
	 * Moves JSON from input directory to processed directory
	 */
	private void moveFile(File absoluteFileName) throws Exception {

		// System.out.println("Method moveFile, absoluteFileName: " +
		// absoluteFileName.toString());

		File parentFolder = (absoluteFileName.getParentFile().getParentFile());

		String processedFolderStr = parentFolder.toString() + "/processed-folder";

		File processedFolder = new File(processedFolderStr);

		// System.out.println("Method moveFile, processedFolder: " +
		// processedFolder.toString());

		if (!processedFolder.exists()) {

			// System.out.println("Method moveFile, making processedFolder");

			processedFolder.mkdir();

		}

		String fileNameStr = absoluteFileName.getName();

		String destinationFileStr = processedFolderStr + "/" + fileNameStr;
		File destinationFile = new File(destinationFileStr);
		destinationFileStr = destinationFile.toString();

		System.out.println("Source: " + absoluteFileName.toString());
		System.out.println("Destination: " + destinationFileStr);

		if (absoluteFileName.renameTo(new File(destinationFileStr))) {
			System.out.println("Moved to Processed Directory");
		} else if (destinationFile != null && destinationFile.exists()) {
			System.out.println("Already exists in: " + destinationFileStr);
			absoluteFileName.delete();
		} else {
			System.out.println("Unable to move to Processed Directory");
		}

	}

}