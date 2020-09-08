package bundles.item;

import java.util.Arrays;

import errorHandling.alerts.ItemCategoryAlert;
import errorHandling.alerts.ItemIdAlert;
import errorHandling.alerts.ItemNameAlert;
import errorHandling.alerts.NumberOfValuesAlert;
import errorHandling.alerts.ValueTypeAlert;
import errorHandling.exceptions.RewardException;
import items.ItemDatabase;
import util.ReadableJsonFile;

public class Reward extends BundleItem {

	public Reward
	(
		ItemDatabase itemDb, 
		ReadableJsonFile bundleFile, String bundleName, 
		boolean isSbmmFormat, String rewardString
	)
		throws RewardException
	{
		if (isSbmmFormat) {
			setSbmmFormatValues(itemDb, bundleFile, bundleName, rewardString);
		} else {
			setSvFormatValues(itemDb, bundleFile, bundleName, rewardString);
		}
	}

	// Format is {quantity name}
	public void setSbmmFormatValues
	(
		ItemDatabase itemDb, 
		ReadableJsonFile bundleFile, String bundleName, 
		String rewardString
	)
		throws RewardException
	{
		// Isolate datums
		String[] rewardValues = rewardString.split(" ");
		if (rewardValues.length < 2)
			// Not enough values for {quantity name} format
			throw new RewardException(new NumberOfValuesAlert(bundleFile, bundleName, rewardValues, " ", 2, null));
		try {
			setQuantity(bundleFile, bundleName, rewardString, rewardValues[0]);
			setId(itemDb, bundleFile, bundleName, String.join(" ", Arrays.copyOfRange(rewardValues, 1, rewardValues.length)));
			setNoQuality();
		} catch (ValueTypeAlert | ItemNameAlert alert) {
			throw new RewardException(alert);
		}
	}

	// Format is {category id quantity}
	public void setSvFormatValues
	(
		ItemDatabase itemDb, 
		ReadableJsonFile bundleFile, String bundleName, 
		String rewardString
	)
		throws RewardException
	{	
		// Isolate datums
		String[] rewardValues = rewardString.split(" ");
		if (rewardValues.length != 3)
			throw new RewardException(new NumberOfValuesAlert(bundleFile, bundleName, rewardValues, " ", 3, 3));
		try {
			setId(itemDb, bundleFile, bundleName, rewardString, rewardValues[0], rewardValues[1]);
			setQuantity(bundleFile, bundleName, rewardString, rewardValues[2]);
			setNoQuality();
		} catch (ValueTypeAlert | ItemCategoryAlert | ItemIdAlert alert) {
			throw new RewardException(alert);
		}
	}

	@Override
	public String getSvFormatValue() {
		return getCategory() + " " + getId() + " " + getQuantity();
	}

}
