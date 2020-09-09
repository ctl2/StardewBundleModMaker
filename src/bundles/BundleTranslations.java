package bundles;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import errorHandling.alerts.JsonSyntaxAlert;
import errorHandling.alerts.MissingFileAlert;
import errorHandling.alerts.NumberOfValuesAlert;
import errorHandling.alerts.ValueTypeAlert;
import errorHandling.exceptions.NonEnglishVanillaBundlesFileException;
import util.ReadableJsonFile;

// langId -> bundleEnglishName -> bundleLangName
public class BundleTranslations extends HashMap<String, LinkedHashMap<String, String>> {

	static private final String[][] langs = {{"en", "EN"}, {"de", "DE"}, {"es", "ES"}, {"fr", "FR"}, {"hu", "HU"}, {"it", "IT"},
			{"ja", "JP"}, {"ko", "KR"}, {"pt", "BR"}, {"ru", "RU"}, {"tr", "TR"}, {"zh", "CN"}};

	public BundleTranslations() throws NonEnglishVanillaBundlesFileException {
		// Populate /this/ with hashmaps of bundle name translations for every language
		for (String[] lang: langs) {
			boolean isEnglish = lang[0].equals("en");
			// Initialise hashmap
			LinkedHashMap<String, String> translations;
			// Populate hashmap
			translations = new LinkedHashMap<String, String>();
			// Read the bundles file for the current language
			ReadableJsonFile readableBundlesFile = (
				isEnglish?
				new ReadableJsonFile("Bundles/Vanilla", "Bundles"):
				new ReadableJsonFile("Bundles/Vanilla", "Bundles." + lang[0] + "-" + lang[1])
			);
			try {
				try {
					JsonObject bundles = readableBundlesFile.getJson();
					// Add bundleEnglishName -> bundleLangName entries
					for (Entry<String, JsonElement> bundleEntry: bundles.entrySet()) {
						String bundleName = getName(readableBundlesFile, bundleEntry);
						translations.put(
							bundleName, (
								isEnglish?
								bundleName:
								getNameTranslation(readableBundlesFile, bundleEntry)
							)
						);
					}
				} catch (FileNotFoundException e1) {
					throw new NonEnglishVanillaBundlesFileException(
						new MissingFileAlert(readableBundlesFile)
					);
				} catch (JsonSyntaxException e2) {
					throw new NonEnglishVanillaBundlesFileException(
						new JsonSyntaxAlert(readableBundlesFile)
					);
				} catch (ValueTypeAlert | NumberOfValuesAlert a) {
					throw new NonEnglishVanillaBundlesFileException(a);
				}
			} catch (NonEnglishVanillaBundlesFileException e) {
				if (e.isFatal()) throw e;
				// If not fatal, skip file
			}
			// Add hashmap to /this/
			put(lang[0], translations);
		}
	}

	private String getName(ReadableJsonFile bundleFile, Entry<String, JsonElement> bundle) throws ValueTypeAlert {
		return ReadableJsonFile.jsonElementToString(
			bundleFile, 
			null, 
			bundle.getValue()
		)
		.split("/")[0];
	}

	private String getNameTranslation(ReadableJsonFile bundleFile, Entry<String, JsonElement> bundle) throws ValueTypeAlert, NumberOfValuesAlert {
		String[] bundleValues = ReadableJsonFile.jsonElementToString(
			bundleFile, 
			null, 
			bundle.getValue()
		)
		.split("/");
		return bundleValues[bundleValues.length - 1];
	}

	private String toCapitalised(String string) {
		return String.join(
			" ",
			Arrays.stream(string.split(" "))
					.map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
					.collect(Collectors.toList())
		);
	}

	public String putTranslation(String bundleName, String langId, String newName) {
		return get(toCapitalised(bundleName)).put(langId, newName);
	}

	public String get(String langId, String bundleName) {
		return get(langId).get(toCapitalised((String) bundleName));
	}

	public boolean containsBundle(String bundleName) {
		return get(langs[1][0]).containsKey(toCapitalised(bundleName));
	}

	public String[] getLangIds() {
		String[] langIds = new String[langs.length];
		for (int i = 0; i < langs.length; i++) {
			langIds[i] = langs[i][0];
		}
		return langIds;
	}

}
