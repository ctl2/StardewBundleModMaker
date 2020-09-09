package bundles;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import errorHandling.alerts.JsonKeyAlert;
import errorHandling.alerts.JsonSyntaxAlert;
import errorHandling.alerts.MissingFileAlert;
import errorHandling.alerts.ValueTypeAlert;
import errorHandling.exceptions.CustomBundleException;
import errorHandling.exceptions.EnglishVanillaBundlesFileException;
import errorHandling.exceptions.NonEnglishVanillaBundlesFileException;
import errorHandling.exceptions.OptionException;
import errorHandling.exceptions.PrettyException;
import errorHandling.exceptions.VanillaBundleException;
import items.ItemDatabase;
import util.ReadableJsonFile;

public class BundleList extends ArrayList<Bundle> {

	static private final ReadableJsonFile vanillaEnglishBundlesFile = new ReadableJsonFile("Bundles/Vanilla", "Bundles");

	public BundleList
	(
		ItemDatabase itemDb,
		boolean isSbmmFormat,
		ReadableJsonFile customBundlesFile, JsonObject customBundles
	)
		throws EnglishVanillaBundlesFileException, NonEnglishVanillaBundlesFileException,
				VanillaBundleException, CustomBundleException, OptionException
	{
		super(31);
		// Read the english vanilla bundles file
		JsonObject vanillaBundles;
		try {
			vanillaBundles = BundleList.vanillaEnglishBundlesFile.getJson();
		} catch (FileNotFoundException e1) {
			throw new EnglishVanillaBundlesFileException(new MissingFileAlert(BundleList.vanillaEnglishBundlesFile));
		} catch(JsonSyntaxException e) {
			throw new EnglishVanillaBundlesFileException(new JsonSyntaxAlert(BundleList.vanillaEnglishBundlesFile));
		}
		// Populate /this/
		HashMap<String, Entry<String, JsonElement>> customBundleEntries = new HashMap<String, Entry<String, JsonElement>>();
		customBundles.entrySet().forEach(
			entry -> customBundleEntries.put(entry.getKey(), entry)
		);
		for (Entry<String, JsonElement> vanillaBundleEntry: vanillaBundles.entrySet()) {
			String key;
			try {
				key = (
					isSbmmFormat?
					ReadableJsonFile.jsonElementToString(
						BundleList.vanillaEnglishBundlesFile, 
						null, 
						vanillaBundleEntry.getValue()
					)
					.split("/")[0]:
					vanillaBundleEntry.getKey()
				);
			} catch (ValueTypeAlert a) {
				throw new EnglishVanillaBundlesFileException(a);
			}
			Entry<String, JsonElement> customBundleEntry = customBundleEntries.get(key);
			if (customBundleEntry == null)
					throw new CustomBundleException(new JsonKeyAlert(customBundlesFile, key));
			this.add(new Bundle(
				itemDb,
				isSbmmFormat,
				customBundlesFile, customBundleEntry,
				BundleList.vanillaEnglishBundlesFile, vanillaBundleEntry
			));
		}
	}

	public LinkedHashMap<String, Object> getSbmmFormatBundles() throws PrettyException {
		LinkedHashMap<String, Object> sbmmFormatBundles = new LinkedHashMap<String, Object>();
		for (Bundle bundle: this) {
			Entry<String, LinkedHashMap<String, Object>> sbmmFormatBundle = bundle.getSbmmFormatEntry();
			sbmmFormatBundles.put(sbmmFormatBundle.getKey(), sbmmFormatBundle.getValue());
		};
		return sbmmFormatBundles;
	}

	public HashMap<String, LinkedHashMap<String, String>> getSvFormatTranslations
	(
		HashMap<String, LinkedHashMap<String, String>> translations
	) {
		HashMap<String, LinkedHashMap<String, String>> svFormatTranslations = new HashMap<String, LinkedHashMap<String, String>>();
		translations.entrySet()
				.stream()
				.forEach(translation -> {
					LinkedHashMap<String, String> svFormatTranslation = new LinkedHashMap<String, String>();
					this.stream()
					.forEach(bundle -> {
						SimpleEntry<String, String> svFormatEntry = 
								bundle.getSvFormatEntry(translation.getValue().get(bundle.getName()));
						svFormatTranslation.put(svFormatEntry.getKey(), svFormatEntry.getValue());
					});
					svFormatTranslations.put(translation.getKey(), svFormatTranslation);
				});
		return svFormatTranslations;
	}

	public LinkedHashMap<String, String> getSvFormatBundles() {
		LinkedHashMap<String, String> svFormatBundles = new LinkedHashMap<String, String>();
		// Populate /bundlesObjects/
		for (Bundle bundle: this) {
			SimpleEntry<String, String> svFormatEntry = bundle.getSvFormatEntry();
			svFormatBundles.put(svFormatEntry.getKey(), svFormatEntry.getValue());
		}
		// Return /bundlesObjects/
		return svFormatBundles;
	}

}
