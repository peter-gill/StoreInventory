package InventoryParse;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class ParseJSONTest {

	private File drop1File = null, drop2File = null, drop3File = null;

	@Test
	public void shouldBeAValidFile() throws Exception {

		setTestData();

		ParseJSON parseJSON = new ParseJSON();

		int fileValidatyStatus = parseJSON.validateJSON(drop1File);

		if (fileValidatyStatus == ParseJSON.VALID_FILE) {

			printTestResult("should Be A Valid File", fileValidatyStatus);
		}

		assertEquals(ParseJSON.VALID_FILE, fileValidatyStatus);

	}

	@Test
	public void recordCountShouldNotMatchSummary() throws Exception {

		setTestData();

		ParseJSON parseJSON = new ParseJSON();

		int fileValidatyStatus = parseJSON.validateJSON(drop2File);

		if (fileValidatyStatus == ParseJSON.RECORD_COUNT_MISMATCH) {

			printTestResult("record Count Should Not Match Summary", fileValidatyStatus);
		}

		assertEquals(ParseJSON.RECORD_COUNT_MISMATCH, fileValidatyStatus);

	}

	@Test
	public void qtyCountShouldNotMatchSummary() throws Exception {

		setTestData();

		ParseJSON parseJSON = new ParseJSON();

		int fileValidatyStatus = parseJSON.validateJSON(drop3File);

		if (fileValidatyStatus == ParseJSON.QTY_COUNT_MISMATCH) {

			printTestResult("qty Count Should Not Match Summary", fileValidatyStatus);
		}

		assertEquals(ParseJSON.QTY_COUNT_MISMATCH, fileValidatyStatus);

	}

	private void setTestData() throws Exception {

		drop1File = new File("./src/test/resources/drop-1.json");
		drop2File = new File("./src/test/resources/drop-2.json");
		drop3File = new File("./src/test/resources/drop-3.json");

		if (drop1File == null || drop2File == null || drop3File == null || !drop1File.exists() || !drop2File.exists()
				|| !drop3File.exists()) {
			throw new Exception("Issue accessing test data.");
		}
	}

	private void printTestResult(String testName, int fileValidityStatus) {

		String fileValidatyStatusStr = "";

		if (fileValidityStatus == ParseJSON.VALID_FILE) {

			fileValidatyStatusStr = "File is valid";

		} else if (fileValidityStatus == ParseJSON.RECORD_COUNT_MISMATCH) {

			fileValidatyStatusStr = "Mismatch in record count";

		} else if (fileValidityStatus == ParseJSON.QTY_COUNT_MISMATCH) {

			fileValidatyStatusStr = "Mismatch in qty count";

		} else if (fileValidityStatus == (ParseJSON.RECORD_COUNT_MISMATCH + ParseJSON.QTY_COUNT_MISMATCH)) {

			fileValidatyStatusStr = "Mismatch in record and qty count";

		}

		System.out.println("Test: " + testName + " Outcome: " + fileValidatyStatusStr);

	}

}
