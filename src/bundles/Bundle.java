package bundles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.AbstractMap.SimpleEntry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import bundles.item.Option;
import bundles.item.Reward;
import errorHandling.alerts.NoValidOptionsAlert;
import errorHandling.alerts.NumberOfValuesAlert;
import errorHandling.alerts.TooManyOptionsAlert;
import errorHandling.alerts.ValueTypeAlert;
import errorHandling.exceptions.CustomBundleException;
import errorHandling.exceptions.OptionException;
import errorHandling.exceptions.PrettyException;
import errorHandling.exceptions.RewardException;
import errorHandling.exceptions.VanillaBundleException;
import items.ItemDatabase;
import util.ReadableJsonFile;

class Bundle {

	// Data

	static private final HashMap<String, String> specialBundles = new HashMap<String, String>() {
		{
			// Contains bundle names -> options for the vault bundles
			put("2,500g", "-1 2500 2500");
			put("5,000g", "-1 5000 5000");
			put("10,000g", "-1 10000 10000");
			put("25,000g", "-1 25000 25000");
		}
	};

	// Bundle

	private String roomId;
	private int spriteIndex;
	private String bundleName;
	private Reward reward;
	private Option[] options;
	private int colorIndex;
	private int requiredOptions;

	public Bundle
	(
		ItemDatabase itemDb,
		boolean isSbmmFormat,
		ReadableJsonFile customBundlesFile, Entry<String, JsonElement> customBundleEntry,
		ReadableJsonFile vanillaEnglishBundlesFile, Entry<String, JsonElement> vanillaBundleEntry
	)
		throws VanillaBundleException, CustomBundleException, OptionException
	{
		HashMap<String, Object> fields;
		// Assign the vanilla file's values to fields
		try {
			fields = getSvFormatBundleValues(itemDb, vanillaEnglishBundlesFile, vanillaBundleEntry, true);
		} catch (NumberOfValuesAlert | ValueTypeAlert | NoValidOptionsAlert | TooManyOptionsAlert a) {
			throw new VanillaBundleException(a);
		} catch (OptionException | RewardException e) {
			throw new VanillaBundleException(e);
		}
		// Try to overwrite the vanilla file's values with the custom file's values
		try {
			try {
				fields.putAll(
					isSbmmFormat?
					getSbmmFormatBundleValues(itemDb, customBundlesFile, customBundleEntry):
					getSvFormatBundleValues(itemDb, customBundlesFile, customBundleEntry, false)
				);
			} catch (NumberOfValuesAlert | ValueTypeAlert | NoValidOptionsAlert | TooManyOptionsAlert a) {
				throw new CustomBundleException(a);
			} catch (RewardException e) {
				throw new CustomBundleException(e);
			}
		} catch (CustomBundleException e) {
			if (e.isFatal()) throw e;
			// Else, use the vanilla bundle data
		}
		this.roomId = (String) fields.get("roomId");
		this.spriteIndex = (int) fields.get("spriteIndex");
		this.bundleName = (String) fields.get("bundleName");
		this.reward = (Reward) fields.get("reward");
		this.options = (Option[]) fields.get("options");
		this.colorIndex = (int) fields.get("colorIndex");
		this.requiredOptions = (int) fields.get("requiredOptions");
	}

	public String getName() {
		return this.bundleName;
	}

	private HashMap<String, Object> getSbmmFormatBundleValues
	(
		ItemDatabase itemDb,
		ReadableJsonFile bundleFile,
		Entry<String, JsonElement> bundleEntry
	)
		throws ValueTypeAlert, OptionException, RewardException, NoValidOptionsAlert
	{
		// Isolate datums in /bundleEntry/
		JsonObject bundleValues = ReadableJsonFile.jsonElementToJsonObject(bundleFile, null, bundleEntry.getValue());
		// Record bundleName for Reward/Option args
		String bundleName = bundleEntry.getKey();
		//
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put("bundleName", bundleName);
		values.put("reward", null);
		if (bundleValues.has("Reward")) {
			String rewardString = ReadableJsonFile.jsonElementToString(bundleFile, bundleName, bundleValues.get("Reward"));
			if (!rewardString.isEmpty()) {
				values.put("reward", new Reward(itemDb, bundleFile, bundleName, true, rewardString));
			}
		}
		if (Bundle.specialBundles.containsKey(bundleEntry.getKey())) {
			values.put("requiredOptions", -1);
			values.put("options", null);
		} else {
			Option[] options = getSbmmFormatBundleOptions(
				itemDb,
				bundleFile, bundleName,
				ReadableJsonFile.jsonElementToJsonArray(bundleFile, bundleName, bundleValues.get("Options"))
			);
			values.put("options", options);
			// Use max options.length in case some options were invalid
			values.put("requiredOptions", Math.min(
				ReadableJsonFile.jsonElementToInt(bundleFile, bundleName, bundleValues.get("Required Options")),
				options.length
			));
		}
		return values;
	}

	private Option[] getSbmmFormatBundleOptions
	(
		ItemDatabase itemDb,
		ReadableJsonFile bundleFile, String bundleName,
		JsonArray optionsJson
	)
		throws OptionException, NoValidOptionsAlert, ValueTypeAlert
	{
		List<Option> options = new ArrayList<Option>();
		for (JsonElement optionJson: optionsJson) {
			try {
				options.add(
					new Option(
						itemDb, bundleFile, bundleName, true,
						ReadableJsonFile.jsonElementToString(bundleFile, bundleName, optionJson)
					)
				);
			} catch (OptionException e) {
				if (e.isFatal()) throw e;
				// Else, Just skip that option
			}
		}
		if (options.isEmpty()) throw new NoValidOptionsAlert(bundleFile, bundleName);
		return options.toArray(new Option[options.size()]);
	}

	// Format is {roomID/spriteIndex}: {bundleName/reward/options/colorIndex[/numberOfItems[/translatedName]]}
	private HashMap<String, Object> getSvFormatBundleValues
	(
		ItemDatabase itemDb,
		ReadableJsonFile bundleFile,
		Entry<String, JsonElement> customBundleEntry,
		boolean isVanilla
	)
		throws NumberOfValuesAlert, ValueTypeAlert, OptionException, RewardException, NoValidOptionsAlert, TooManyOptionsAlert
	{
		// Isolate datums in /bundleEntry/
		String[] bundleKey = customBundleEntry.getKey().split("/");
		if (bundleKey.length != 2) {
			throw new NumberOfValuesAlert(bundleFile, null, bundleKey, "/", 2, 2);
		}
		String[] bundleValues;
		bundleValues = ReadableJsonFile.jsonElementToString(bundleFile, null, customBundleEntry.getValue()).split("/");
		if (bundleValues.length < 4 || bundleValues.length > 6) {
			throw new NumberOfValuesAlert(bundleFile, null, bundleKey, "/", 4, 6);
		}
		// Record bundleName for arg passing
		String bundleName = bundleValues[0];
		// Make the hashmap
		HashMap<String, Object> values = new HashMap<String, Object>();
		// Record the data held in the bundle entry's key
		values.put("roomId", bundleKey[0]);
		try {
			values.put("spriteIndex", Integer.parseInt(bundleKey[1]));
		} catch (NumberFormatException e) {
			throw new ValueTypeAlert(bundleFile, bundleName, customBundleEntry.getKey(), bundleKey[1], Integer.class);
		}
		// Record the data held in the bundle entry's value
		values.put("bundleName", bundleValues[0]);
		if (bundleValues[1].equals("")) {
			values.put("reward", null);
		} else {
			values.put("reward", new Reward(itemDb, bundleFile, bundleName, false, bundleValues[1]));
		}
		values.put("colorIndex", Integer.parseInt(bundleValues[3]));
		if (Bundle.specialBundles.containsKey(bundleValues[0])) {
			values.put("requiredOptions", -1);
			values.put("options", null);
		} else {
			if (
				bundleValues[0].equals("Animal") &&
				bundleValues[2].equals("186 1 0 182 1 0 174 1 0 438 1 0 440 1 0 442 1 0 639 1 0 640 1 0 641 1 0 642 1 0 643 1 0")
			) {
				// The vanilla 'animals' bundle contains item ids which don't correspond to actual items.
				// I remove them to avoid throwing exceptions.
				bundleValues[2] = "186 1 0 182 1 0 174 1 0 438 1 0 440 1 0 442 1 0";
			}
			Option[] options = getSvFormatBundleOptions(
				itemDb,
				bundleFile, bundleName,
				bundleValues[2].split(" "),
				isVanilla
			);
			if (options.length == 0) {
				throw new NoValidOptionsAlert(bundleFile, bundleName);
			}
			values.put("options", options);
			if (bundleValues.length > 4) {
				try {
					// Use max options.length in case some options were invalid
					values.put("requiredOptions", Math.min(
						Integer.parseInt(bundleValues[4]),
						options.length
					));
				} catch (NumberFormatException e) {
					throw new ValueTypeAlert(bundleFile, bundleName, String.join("/", bundleValues), bundleValues[4], Integer.class);
				}
			} else {
				// If no number is given, all options are required
				values.put("requiredOptions", options.length);
			}
		}
		return values;
	}

	private Option[] getSvFormatBundleOptions
	(
		ItemDatabase itemDb,
		ReadableJsonFile bundleFile, String bundleName,
		String[] optionsValues,
		boolean isVanilla
	)
		throws OptionException, TooManyOptionsAlert
	{
		Option[] options = new Option[optionsValues.length / 3];
		for (int i = 0; i < optionsValues.length; i += 3) {
			String[] optionValues = Arrays.copyOfRange(optionsValues, i, i+3);
			try {
				options[i / 3] = new Option(itemDb, bundleFile, bundleName, false, String.join(" ", optionValues));
			} catch (OptionException e) {
				if (e.isFatal() || isVanilla) throw e;
				// Else, Just skip that option
			}
		}
		if (options.length > 12)
			throw new TooManyOptionsAlert(bundleFile, bundleName, options.length);
		return options;
	}

	public Entry<String, LinkedHashMap<String, Object>> getSbmmFormatEntry() throws PrettyException {
		return new SimpleEntry<String, LinkedHashMap<String, Object>>(getSbmmFormatKey(), getSbmmFormatValue());
	}

	private String getSbmmFormatKey() {
		return this.bundleName;
	}

	private LinkedHashMap<String, Object> getSbmmFormatValue() throws PrettyException {
		// Get the value as an ordered map
		LinkedHashMap<String, Object> sbmmFormatValue = new LinkedHashMap<String, Object>();
		if (this.reward == null) {
			sbmmFormatValue.put("Reward", "");
		} else {
			sbmmFormatValue.put("Reward", this.reward.getSbmmFormatValue());
		}
		if (!Bundle.specialBundles.containsKey(this.bundleName)) {
			sbmmFormatValue.put("Required Options", this.requiredOptions);
			String[] optionArray = new String[this.options.length];
			for (int i = 0; i < this.options.length; i++) {
				optionArray[i] = (options[i].getSbmmFormatValue());
			}
			sbmmFormatValue.put("Options", optionArray);
		}
		// Return the value as a json string
		return sbmmFormatValue;
	}

	public SimpleEntry<String, String> getSvFormatEntry(String bundleNameTranslation) {
		return new SimpleEntry<String, String>(getSvFormatKey(), getSvFormatValue() + "/" + bundleNameTranslation);
	}

	public SimpleEntry<String, String> getSvFormatEntry() {
		return new SimpleEntry<String, String>(getSvFormatKey(), getSvFormatValue());
	}

	private String getSvFormatKey() {
		return this.roomId + "/" + this.spriteIndex;
	}

	private String getSvFormatValue() {
		String svFormatRewardValue;
		if (this.reward == null) {
			svFormatRewardValue = "";
		} else {
			svFormatRewardValue = this.reward.getSvFormatValue();
		}
		if (Bundle.specialBundles.containsKey(this.bundleName))
			return this.bundleName + "/" + this.reward.getSvFormatValue() + "/" +
			Bundle.specialBundles.get(this.bundleName) + "/" + this.colorIndex;
		String svFormatOptionValue = String.join(" ",
				Arrays.stream(this.options, 0, options.length).map(
						option -> option.getSvFormatValue()).collect(Collectors.toList()));
		return this.bundleName + "/" + svFormatRewardValue + "/" + svFormatOptionValue + "/" +
				this.colorIndex + "/" + this.requiredOptions;
	}

}
