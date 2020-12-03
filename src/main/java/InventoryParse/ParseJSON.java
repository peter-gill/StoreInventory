package InventoryParse;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ParseJSON {

	private String inputFolder = null;

	public ParseJSON(String folderName) {
		inputFolder = folderName;
	}

	public void findParseJSON() {

		for (;;) {

			String fileName = findFile();

			if (fileName != null) {

				validateJSON(fileName);

				moveFile(fileName);

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

	private String findFile() {

		String fileName = null;

		File folder = new File(inputFolder);

		for (final File fileEntry : folder.listFiles()) {

			fileName = fileEntry.getName();
			System.out.println("File found: " + fileName);
			break;

		}

		return fileName;

	}

	private boolean validateJSON(String fileName) {

		boolean discard = false;

		try {

			FileReader fileReader = new FileReader(inputFolder + "\\" + fileName);

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

			if (totalRecordcount.longValue() == transSumRecordcount.longValue()
					|| totalQtysum.longValue() == transSumQtysum) {

				System.out.println("Valid record");

			} else {

				System.out.println("Invalid record");

				discard = true;

			}

			fileReader.close();

			return discard;

		}

		catch (Exception e) {
			System.out.println(e.toString());
			return true;
		}

	}

	private void moveFile(String fileNameStr) {

		File fileName = new File(fileNameStr);
		File parentFolder = (new File(inputFolder)).getParentFile();

		if (parentFolder.exists()) {

			String processedFolderStr = parentFolder.toString() + "/processed-folder";

			File processedFolder = new File(processedFolderStr);

			if (!processedFolder.exists()) {

				processedFolder.mkdir();

			}

			fileName = new File(inputFolder + "/" + fileNameStr);
			fileNameStr = new File(inputFolder + "/" + fileNameStr).toString();

			String destinationFileStr = processedFolderStr + "/" + fileName.getName();
			File destinationFile = new File(destinationFileStr);
			destinationFileStr = destinationFile.toString();

			System.out.println("Source: " + fileName.toString());
			System.out.println("Destination: " + destinationFileStr);

			if (fileName.renameTo(new File(destinationFileStr)) && fileName.delete()) {
				System.out.println("Moved to Processed Directory");
			} else {
				System.out.println("Unable to move to Processed Directory");
			}

		}

	}

}