import java.io.FileNotFoundException;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import bundles.BundleList;
import bundles.Mod;
import config.Configurations;
import errorHandling.exceptions.ConfigException;
import errorHandling.exceptions.ItemFileException;
import errorHandling.exceptions.PrettyException;
import items.ItemDatabase;
import util.InputReading;
import util.ReadableJsonFile;
import util.WriteableJsonFile;

public class Interface {

	private ItemDatabase itemDb;

	public Interface() throws ItemFileException, ConfigException {
		Configurations configs = new Configurations();
		configs.configure();
		this.itemDb = new ItemDatabase();
	}

	public void start() throws PrettyException {
		System.out.println("Welcome to Stardew Bundle Mod Maker!");
		System.out.println();
		while (true) {
			System.out.println("Enter a command (e.g. help).");
			while (true) {
				String input = InputReading.getString().toLowerCase().trim();
				switch (input) {
					case "sbmm":
						makeSbmmFormatFile();
						break;
					case "sv":
						makeSvFormatFile();
						break;
					case "m":
						makeMod();
						break;
					case "q":
						System.exit(0);
						break;
					case "help":
						System.out.println("Command | Description");
						System.out.println("--------+-----------------------------------------------------------------------");
						System.out.println(" sbmm   | Convert a JSON bundles file from Stardew Valley format to SBMM format.");
						System.out.println(" sv     | Convert a JSON bundles file from SBMM format to Stardew Valley format.");
						System.out.println(" m      | Make a mod.");
						System.out.println(" q      | Terminate the program.");
						break;
					default:
						System.out.println("Unrecognised command. Please type 'help' for a list of valid commands.");
						continue;
				}
				System.out.println();
				break;
			}
		}
	}

	private void makeSbmmFormatFile() throws PrettyException {
		// Get input bundles json
		ReadableJsonFile inputFile;
		JsonObject inputJson;
		while (true) {
			inputFile = new ReadableJsonFile("Bundles/SvFormat", null);
			try {
				inputJson = inputFile.getJson();
				break;
			} catch (JsonSyntaxException e) {
				System.out.println("Your file does not contain valid JSON.\n");
				continue;
			} catch (FileNotFoundException e) {
				System.out.println("File not found.\n");
				continue;
			}
		}
		// Make bundle objects
		BundleList bundles = new BundleList(this.itemDb, false, inputFile, inputJson);
		// Make the output file
		WriteableJsonFile outputFile = new WriteableJsonFile("Bundles/SbmmFormat", inputFile.getBaseName());
		outputFile.write(bundles.getSbmmFormatBundles());
		// Inform user of success
		System.out.println("Your file is ready! You can find it at " + outputFile.getDirectoryPath() + "\n");
	}

	private void makeSvFormatFile() throws PrettyException {
		// Get input bundles json
		ReadableJsonFile inputFile;
		JsonObject inputJson;
		while (true) {
			inputFile = new ReadableJsonFile("Bundles/SbmmFormat", null);
			try {
				inputJson = inputFile.getJson();
				break;
			} catch (JsonSyntaxException e) {
				System.out.println("Your file does not contain valid JSON.\n");
				continue;
			} catch (FileNotFoundException e) {
				System.out.println("File not found.\n");
				continue;
			}
		}
		// Make bundle objects
		BundleList bundles = new BundleList(this.itemDb, true, inputFile, inputJson);
		// Make the output file
		WriteableJsonFile outputFile = new WriteableJsonFile("Bundles/SvFormat", inputFile.getBaseName());
		outputFile.write(bundles.getSvFormatBundles());
		// Inform user of success
		System.out.println("Your file is ready! You can find it at " + outputFile.getDirectoryPath() + "\n");
	}

	private void makeMod() throws PrettyException {
		ReadableJsonFile inputFile;
		boolean isSbmmFormat;
		while (true) {
			// Get input file name
			ReadableJsonFile sbmmFile = new ReadableJsonFile("Bundles/SbmmFormat", null);
			ReadableJsonFile svFile = new ReadableJsonFile("Bundles/SvFormat", sbmmFile.getBaseName());
			boolean sbmmFileExists = sbmmFile.exists();
			boolean svFileExists = svFile.exists();
			if (sbmmFileExists ^ svFileExists) {
				// One exists
				if (sbmmFileExists) {
					inputFile = sbmmFile;
					isSbmmFormat = true;
				} else {
					inputFile = svFile;
					isSbmmFormat = false;
				}
			} else if (sbmmFileExists) {
				// Both exist
				while (true) {
					// Determine which file the user wants to use
					System.out.println("Is your file in SBMM or SV format?");
					String format = InputReading.getString().toLowerCase();
					switch (format) {
						case "sbmm":
							inputFile = sbmmFile;
							isSbmmFormat = true;
							break;
						case "sv":
							inputFile = svFile;
							isSbmmFormat = false;
							break;
						default:
							System.out.println("Unrecognised response. Please enter 'sbmm' or 'sv'.\n");
							continue;
					}
					break;
				}
			} else {
				// Neither exist
				System.out.println("File not found.\n");
				continue;
			}
			break;
		}
		try {
			// Make the mod
			String modName = inputFile.getBaseName();
			Mod mod = new Mod("StardewBundleModMaker", modName, this.itemDb, isSbmmFormat, inputFile, inputFile.getJson());
			// Create output files
			mod.write();
			// Inform user of success
			System.out.println("Your mod is ready! You can find it at Mods/" + modName + "\n");
		} catch (FileNotFoundException e1) {
			throw new RuntimeException("File '" + inputFile + "' not found after positive check.");
		} catch (JsonSyntaxException e2) {
			System.out.println("That file does not contain valid JSON.\n");
		}
	}

	public static void main(String[] args) {
		try {
			Interface i = new Interface();
			i.start();
		} catch (PrettyException e) {
			// Allow the user to read the error message before the program terminates
			System.out.println("Press enter to terminate the program.");
			InputReading.getVoid();
			// Terminate
			System.exit(0);
		} catch (RuntimeException programmerError) {
			System.err.println("PROGRAMMER ERROR: " + programmerError.getMessage());
			programmerError.printStackTrace();
		}
	}

}
