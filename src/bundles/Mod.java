package bundles;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.google.gson.JsonObject;

import errorHandling.exceptions.CustomBundleException;
import errorHandling.exceptions.EnglishVanillaBundlesFileException;
import errorHandling.exceptions.NonEnglishVanillaBundlesFileException;
import errorHandling.exceptions.OptionException;
import errorHandling.exceptions.VanillaBundleException;
import items.ItemDatabase;
import util.InputReading;
import util.ReadableJsonFile;
import util.WriteableJsonFile;

public class Mod {

	@SuppressWarnings("unused")
	private class Content {

		private class Change {
			
			private String Action = "EditData";
			private String Target = "Data/Bundles";
			private String FromFile = "Assets/bundles-" + 
					"{{language}}" +  // https://github.com/Pathoschild/StardewMods/blob/stable/ContentPatcher/docs/author-tokens-guide.md#metadata
					".json";
			
		}
		
		String Format = "1.11";
		Change[] Changes;
		
		public Content(String modName) {
			this.Changes = new Change[] {new Change()};
		}

	}

	@SuppressWarnings("unused")
	private class Manifest {
		
		private class ContentPackFor {
			
			private String UniqueID = "Pathoschild.ContentPatcher";
			
		}
		
		private String name;
		private String author;
		private String version = "1.0";
		private String description;
		private String UniqueID;
		private ContentPackFor ContentPackFor = new ContentPackFor();
		
		public Manifest(String author, String modName) {
			this.name = modName + "Bundles";
			this.author = author;
			this.description = author + "'s bundles mod";
			this.UniqueID = author + "." + this.name;
		}
		
	}
	
	private Manifest manifest;
	private Content content;
	private BundleList bundles;
	private BundleTranslations translations;
	private String directoryPath;
	
	public Mod
	(
		String author, String modName, 
		ItemDatabase itemDb, 
		boolean isSbmmFormat, 
		ReadableJsonFile bundlesFile, JsonObject bundles
	) 
		throws EnglishVanillaBundlesFileException, NonEnglishVanillaBundlesFileException, 
				VanillaBundleException, CustomBundleException, OptionException
	{
		this.manifest = new Manifest(author, modName);
		this.content = new Content(modName);
		this.bundles = new BundleList(itemDb, isSbmmFormat, bundlesFile, bundles);
		this.translations = getTranslations();
		this.directoryPath = "Mods/" + modName;
	}

	private BundleTranslations getTranslations() throws NonEnglishVanillaBundlesFileException {
		BundleTranslations translations = new BundleTranslations();
		if (InputReading.getBoolean("Would you like to add non-vanilla bundle names?")) {
			while (true) {
				// Determine the language to edit names for.
				String editLang;
				String[] langIds = translations.getLangIds();
				while (true) {
					System.out.println("Which language would you like to change bundle names for?");
					editLang = InputReading.getString();
					final String finalEditLand = editLang;
					if (!Arrays.stream(langIds).anyMatch(lang -> lang.equals(finalEditLand))) {
						System.out.println("Unrecognised language id. Please enter one of the following:");
						System.out.println(String.join("\n", langIds));
						System.out.println();
						continue;
					}
					break;
				}
				// Determine the bundle name to edit.
				String editBundle;
				while (true) {
					System.out.println("Which bundle would you like to change the name of?");
					editBundle = InputReading.getString().toLowerCase();
					if (!translations.containsKey(editBundle)) {
						System.out.println("Unrecognised bundle name. Please enter one of the following:");
						System.out.println(String.join("\n", translations.keySet()));
						System.out.println();
						continue;
					}
					// Record edit
					System.out.println("Please enter the name that you would like to use.");
					translations.putTranslation(editBundle, editLang, InputReading.getString());
					if (InputReading.getBoolean("Would you like to change the name of another bundle?")) continue;
					break;
				}
				// Loop if more languages should be edited
				if (InputReading.getBoolean("Would you like to change bundle names in other languages?")) {
					continue;
				}
				break;
			}
		}
		return translations;
	}
	
	public void write() {
		writeContent();
		writeManifest();
		writeBundles();
	}
	
	private void writeContent() {
		WriteableJsonFile writeableTranslation = new WriteableJsonFile(this.directoryPath, "content");
		writeableTranslation.write(this.content);
	}
	
	private void writeManifest() {
		WriteableJsonFile writeableTranslation = new WriteableJsonFile(this.directoryPath, "manifest");
		writeableTranslation.write(this.manifest);
	}
	
	private void writeBundles() {
		// Get translations
		HashMap<String, LinkedHashMap<String, String>> bundlesMap = this.bundles.getSvFormatTranslations(this.translations);
		for (String langId: bundlesMap.keySet()) { // For each translation ...
			// Make wrapper map
			HashMap<String, LinkedHashMap<String, String>> wrapperMap = new HashMap<String, LinkedHashMap<String, String>>();
			// Populate wrapper map
			wrapperMap.put("Entries", bundlesMap.get(langId));
			// Write bundle file
			WriteableJsonFile writeableTranslation = new WriteableJsonFile(this.directoryPath + "/Assets", "bundles-" + langId);
			writeableTranslation.write(wrapperMap);
		}
	}

}
