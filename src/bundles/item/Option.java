package bundles.item;

import java.util.Arrays;

import errorHandling.alerts.ItemCategoryAlert;
import errorHandling.alerts.ItemIdAlert;
import errorHandling.alerts.ItemNameAlert;
import errorHandling.alerts.ItemQualityAlert;
import errorHandling.alerts.NumberOfValuesAlert;
import errorHandling.alerts.ValueTypeAlert;
import errorHandling.exceptions.OptionException;
import items.ItemDatabase;
import util.ReadableJsonFile;

public class Option extends BundleItem {

	public Option
	(
		ItemDatabase itemDb,
		ReadableJsonFile bundleFile, String bundleName,
		boolean isSbmmFormat, String optionString
	)
		throws OptionException
	{
		if (isSbmmFormat) {
			setSbmmFormatValues(itemDb, bundleFile, bundleName, optionString);
		} else {
			setSvFormatValues(itemDb, bundleFile, bundleName, optionString);
		}
	}

	// Format is {quantity[ quality] name}
	protected void setSbmmFormatValues
	(
		ItemDatabase itemDb,
		ReadableJsonFile bundleFile, String bundleName,
		String optionString
	)
		throws OptionException
	{
		// Isolate datums
		String[] optionValues = optionString.split(" ");
		if (optionValues.length < 2)
			// Not enough values for {quantity name} format
			throw new OptionException(new NumberOfValuesAlert(bundleFile, bundleName, optionValues, " ", 2, null));
		try {
			// Do format-independent setting
			setQuantity(bundleFile, bundleName, optionString, optionValues[0]);
		} catch (ValueTypeAlert quantityAlert) {
			throw new OptionException(quantityAlert);
		}
		try {
			// Check whether the string is in {quantity name} format
			setId(itemDb, bundleFile, bundleName, String.join(" ", Arrays.copyOfRange(optionValues, 1, optionValues.length)), "O");
			// Check passed
			setNoQuality();
		} catch (ItemNameAlert | ItemCategoryAlert noQualityNameAlert) {
			// Check whether the string is in {quantity quality name} format
			if (optionValues.length < 3)
				// Not enough values for {quantity quality name} format
				throw new OptionException(noQualityNameAlert);
			try {
				setQuality(bundleFile, bundleName, optionString, optionValues[1], true);
			} catch (ItemQualityAlert | ValueTypeAlert qualityAlert) {
				// No valid quality value found
				throw new OptionException(noQualityNameAlert);
			}
			// Check passed
			try {
				setId(itemDb, bundleFile, bundleName, String.join(" ", Arrays.copyOfRange(optionValues, 2, optionValues.length)), "O");
			} catch (ItemNameAlert | ItemCategoryAlert qualityNameAlert) {
				throw new OptionException(qualityNameAlert);
			}
		}
	}

	// Format is {id quantity quality}
	protected void setSvFormatValues
	(
		ItemDatabase itemDb,
		ReadableJsonFile bundleFile, String bundleName,
		String optionString
	)
		throws OptionException
	{
		// Isolate datums
		String[] optionValues = optionString.split(" ");
		if (optionValues.length != 3)
			throw new OptionException(new NumberOfValuesAlert(bundleFile, bundleName, optionValues, " ", 3, 3));
		try {
			setId(itemDb, bundleFile, bundleName, optionString, "O", optionValues[0]);
			setQuantity(bundleFile, bundleName, optionString, optionValues[1]);
			setQuality(bundleFile, bundleName, optionString, optionValues[2], false);
		} catch (ValueTypeAlert | ItemQualityAlert | ItemCategoryAlert | ItemIdAlert alert) {
			throw new OptionException(alert);
		}
	}

	@Override
	public String getSvFormatValue() {
		return getId() + " " + getQuantity() + " " + getQuality();
	}

}
