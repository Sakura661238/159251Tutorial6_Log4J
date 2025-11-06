package nz.ac.vuw.swen301.tuts.log4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// New: Import Log4J Logger class
import org.apache.log4j.Logger;

/**
 * The purpose of this class is to read and merge financial transactions, and print a summary:
 * - total amount
 * - highest/lowest amount
 * - number of transactions
 * @author jens dietrich
 */
public class MergeTransactions {
	private static DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
	private static NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.getDefault());

	// New: Initialize 2 loggers (strictly corresponding to FILE and TRANSACTIONS required by PDF)
	private static final Logger FILE_LOGGER = Logger.getLogger("FILE"); // File access logs
	private static final Logger TRANSACTIONS_LOGGER = Logger.getLogger("TRANSACTIONS"); // Transaction statistics logs

	public static void main(String[] args) {
		List<Purchase> transactions = new ArrayList<Purchase>();

		// Read 4 files (transactions4.csv does not exist, used to trigger exception logs)
		readData("transactions1.csv", transactions);
		readData("transactions2.csv", transactions);
		readData("transactions3.csv", transactions);
		readData("transactions4.csv", transactions);

		// Replacement: // print some info for the user → TRANSACTIONS_LOGGER.INFO
		TRANSACTIONS_LOGGER.info(transactions.size() + " transactions imported");
		TRANSACTIONS_LOGGER.info("total value: " + CURRENCY_FORMAT.format(computeTotalValue(transactions)));
		TRANSACTIONS_LOGGER.info("max value: " + CURRENCY_FORMAT.format(computeMaxValue(transactions)));
	}

	private static double computeTotalValue(List<Purchase> transactions) {
		double v = 0.0;
		for (Purchase p : transactions) {
			v += p.getAmount();
		}
		return v;
	}

	private static double computeMaxValue(List<Purchase> transactions) {
		double v = 0.0;
		for (Purchase p : transactions) {
			v = Math.max(v, p.getAmount());
		}
		return v;
	}

	// Read files and add transaction data
	private static void readData(String fileName, List<Purchase> transactions) {
		File file = new File(fileName);
		String line = null;

		// Replacement: // print info for user → FILE_LOGGER.INFO (file access related information)
		FILE_LOGGER.info("import data from " + fileName);

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			while ((line = reader.readLine()) != null) {
				String[] values = line.split(",");
				Purchase purchase = new Purchase(
						values[0],
						Double.parseDouble(values[1]),
						DATE_FORMAT.parse(values[2])
				);
				transactions.add(purchase);

				// Replacement: // this is for debugging only → FILE_LOGGER.DEBUG (debug transaction details)
				FILE_LOGGER.debug("imported transaction " + purchase);
			}
		}
		catch (FileNotFoundException x) {
			// Replacement: // print warning → FILE_LOGGER.WARN (file not found warning)
			FILE_LOGGER.warn("file " + fileName + " does not exist - skip", x); // Pass exception object to record stack trace
		}
		catch (IOException x) {
			// Replacement: // print error message and details → FILE_LOGGER.ERROR (file reading error)
			FILE_LOGGER.error("problem reading file " + fileName, x);
		}
		catch (ParseException x) {
			// Replacement: // print error message and details → FILE_LOGGER.ERROR (date parsing error)
			FILE_LOGGER.error("cannot parse date from string - please check syntax: " + line, x);
		}
		catch (NumberFormatException x) {
			// Replacement: // print error message and details → FILE_LOGGER.ERROR (amount parsing error)
			FILE_LOGGER.error("cannot parse double from string - please check syntax: " + line, x);
		}
		catch (Exception x) {
			// Replacement: // print error message and details → FILE_LOGGER.ERROR (other exceptions)
			FILE_LOGGER.error("exception reading data from file " + fileName + ", line: " + line, x);
		}
		finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				// Replacement: // print error message and details → FILE_LOGGER.ERROR (stream closing error)
				FILE_LOGGER.error("cannot close reader used to access " + fileName, e);
			}
		}
	}
}