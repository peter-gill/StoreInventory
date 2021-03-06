package InventoryParse;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/*
 * ParseJSON: Class that parses JSON files and inserts into mysql DB 
 */
public class ParseJSON {

	final static public int VALID_FILE = 0;
	final static public int RECORD_COUNT_MISMATCH = 1;
	final static public int QTY_COUNT_MISMATCH = 2;
	final static public int PREVIOUSLY_PROCESSED = 5;

	private File inputFolder = null;
	private Connection con = null;

	public ParseJSON() {

	}

	public ParseJSON(String folderNameStr) throws Exception {

		inputFolder = new File(folderNameStr);

		if (inputFolder == null || !inputFolder.exists() || !inputFolder.isDirectory()) {

			throw new Exception("Invalid input folder");
		}
	}

	/*
	 * findParseJSON: Waits until it finds JSON to parse 
	 */
	public void findParseJSON() throws Exception {

		connectToMySqlDB();

		for (;;) {

			File absoluteFileName = findFile();

			if (absoluteFileName != null) {

				validateJSON(absoluteFileName);

				moveFile(absoluteFileName);

			} else {

				// System.out.println("No file found, sleeping.");

				try {
					TimeUnit.SECONDS.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

	}

	/*
	 * findFile: Returns a file or null if no file exists
	 */
	private File findFile() throws Exception {

		File absoluteFileName = null;

		for (final File fileEntry : inputFolder.listFiles()) {

			absoluteFileName = fileEntry.getAbsoluteFile();

			if (absoluteFileName == null || !absoluteFileName.isFile()) {
				throw new Exception("Invalid file found, please check input folder");
			}

			System.out.println("\nProcessing " + absoluteFileName.getName());

			break;

		}

		return absoluteFileName;
	}

	/*
	 * validateJSON: Parses a JSON file and inserts into mysql table. Also deals with invalidate data cases.
	 */
	int validateJSON(File absoluteFileName) throws IOException, ParseException, SQLException {

		int fileValidatyStatus = VALID_FILE;

		FileReader fileReader = new FileReader(absoluteFileName);

		// parsing file "drop-n.json"
		Object obj = new JSONParser().parse(fileReader);

		// TODO: add function to validate JSON format

		JSONObject jo = (JSONObject) obj;

		JSONArray productsArray = (JSONArray) jo.get("products");

		JSONObject transSum = (JSONObject) jo.get("transmissionsummary");
		String id = (String) transSum.get("id");

		Collection<Product> products = new ArrayList<Product>();

		if (isNewID(id)) {

			Long transSumRecordcount = (Long) transSum.get("recordcount");
			Long transSumQtysum = (Long) transSum.get("qtysum");

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

				Product product = new Product(id, sku, description, category, price, location, qty);

				products.add(product);

				totalRecordcount += 1;
				totalQtysum += qty;

			}

			if (totalRecordcount.longValue() != transSumRecordcount.longValue()) {

				System.out.println("Discarding " + absoluteFileName.getName() + ", incorrect recordcount");

				Product product = new Product(id, "", "", "", 0.0, "", 0L);

				products.clear();

				products.add(product);

				fileValidatyStatus += RECORD_COUNT_MISMATCH;

			}

			if (totalQtysum.longValue() != transSumQtysum) {

				System.out.println("Discarding " + absoluteFileName.getName() + ", incorrect qtysum");

				Product product = new Product(id, "", "", "", 0.0, "", 0L);

				products.clear();

				products.add(product);

				fileValidatyStatus += QTY_COUNT_MISMATCH;

			}

		} else {

			System.out.println("Skipped " + absoluteFileName.getName());

			fileValidatyStatus = PREVIOUSLY_PROCESSED;
		}

		if (fileValidatyStatus == VALID_FILE || fileValidatyStatus == RECORD_COUNT_MISMATCH || fileValidatyStatus == QTY_COUNT_MISMATCH) {

			insertProductsToTable(products);

		}

		if (fileValidatyStatus != PREVIOUSLY_PROCESSED) {

			if (fileValidatyStatus != QTY_COUNT_MISMATCH && fileValidatyStatus != RECORD_COUNT_MISMATCH) {
				System.out.println("Completed " + absoluteFileName.getName());
			}

			printAggregateStats();

		}

		// System.out.println("######## Closing " + absoluteFileName.getName());

		fileReader.close();

		return fileValidatyStatus;

	}

	private void insertProductsToTable(Collection<Product> products) throws SQLException {

		Iterator<Product> iterator = products.iterator();

		if (con == null) {

			// System.out.println("insertProductsToTable - establishing connection");
			connectToMySqlDB();
		} else {

			// System.out.println("insertProductsToTable - already connected");

		}

		Statement stmt = con.createStatement();

		while (iterator.hasNext()) {

			Product product = iterator.next();

			String insertQuery = "INSERT INTO products (id, sku, description, category, price, location, qty) VALUES ('"
					+ product.getId() + "', '" + product.getSku() + "', '" + product.getDescription() + "', '"
					+ product.getCategory() + "', " + product.getPrice() + ", '" + product.getLocation() + "', "
					+ product.getQty() + ")";

			// System.out.println("###############" + insertQuery);

			stmt.executeUpdate(insertQuery);

		}

	}

	private boolean isNewID(String id) throws SQLException {

		boolean isNewID = true;

		if (con == null) {

			// System.out.println("isNewID - establishing connection");
			connectToMySqlDB();
		} else {

			// System.out.println("isNewID - already connected");

		}

		Statement stmt = con.createStatement();

		ResultSet rs = stmt.executeQuery("select id from products");

		while (rs.next()) {

			String rsId = rs.getString(1);

			// System.out.println("Test ID: " + id);
			// System.out.println("Result Set ID: " + rsId);

			if (rsId.equals(id)) {

				isNewID = false;
				break;
			}

		}

		return isNewID;
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

		// System.out.println("Source: " + absoluteFileName.toString());
		// System.out.println("Destination: " + destinationFileStr);

		Files.copy(absoluteFileName.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		Files.delete(absoluteFileName.toPath());
	}

	/*
	 * printAggregateStats: Prints aggregated stats. It uses two collections to achieve this.
	 * 						The first collection captures unique Category and Locations. The
	 * 						second collection contains all the data from the products table.
	 * 						The first collection is then iterated on and for each unique
	 * 						Category, Location pair the Qty value is incremented based on matches
	 * 						in the second collection.   
	 */
	private void printAggregateStats() throws SQLException {

		Collection<InventoryStat> inventoryStats = new ArrayList<InventoryStat>();
		Collection<String> uniqueCatLocs = new ArrayList<String>();

		if (con == null) {

			// System.out.println("insertProductsToTable - establishing connection");
			connectToMySqlDB();
		} else {

			// System.out.println("insertProductsToTable - already connected");

		}

		Statement stmt = con.createStatement();

		ResultSet rs = stmt.executeQuery("select category, location, qty from products");

		while (rs.next()) {

			String [] catLocs = rs.getString(1).split(">", 4);
			
			if (catLocs.length == 1) {
				continue;
			}
			
			String category = catLocs[2].trim();
			String location = rs.getString(2);

			String catLoc = category + ">" + location;

			int qty = rs.getInt(3);

			// System.out.println(catLoc + "########" + qty);

			if (!uniqueCatLocs.contains(catLoc)) {
				uniqueCatLocs.add(catLoc);
			}

			InventoryStat inventoryStat = new InventoryStat(catLoc, qty);

			inventoryStats.add(inventoryStat);
		}

		Iterator<String> iteratorCL = uniqueCatLocs.iterator();

		while (iteratorCL.hasNext()) {

			String uniqueCatLoc = iteratorCL.next();
			int qtyCumulative = 0;

			Iterator<InventoryStat> iteratorIS = inventoryStats.iterator();

			while (iteratorIS.hasNext()) {

				InventoryStat inventoryStat = iteratorIS.next();
				String iSCatLoc = inventoryStat.getCatLoc();

				// System.out.println("Unique CatLoc: " + uniqueCatLoc + " InventoryStat CatLoc:
				// " + iSCatLoc);

				if (uniqueCatLoc.equals(iSCatLoc)) {
					qtyCumulative += inventoryStat.getQty();
				}

			}

			System.out.println(uniqueCatLoc.replace(">", " - ") + " - " + qtyCumulative);

		}

	}

	/*
	 * mysql connection method
	 * 
	 * TODO: change so connection details are passed in at runtime, possibly using Azure key vault. Also need to set-up ssl, it's ok for now as this is dev.
	 */
	private void connectToMySqlDB() {

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/company?allowPublicKeyRetrieval=true&useSSL=false", "storeuser",
					"storeuser");

		} catch (Exception e) {
			System.out.println(e);
		}

	}

	/*
	 * Delete data from table
	 */
	public void cleanTable() throws SQLException {
		
		if (con == null) {

			// System.out.println("insertProductsToTable - establishing connection");
			connectToMySqlDB();
		} else {

			// System.out.println("insertProductsToTable - already connected");

		}

		Statement stmt = con.createStatement();
		
		String insertQuery = "DELETE FROM products;";

		stmt.executeUpdate(insertQuery);
		
	}
}
