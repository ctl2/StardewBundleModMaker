package errorHandling.alerts;

import util.ReadableJsonFile;

// Invalid/unexpected value
public class ItemNameAlert extends FileContentAlert {
	
	public ItemNameAlert(ReadableJsonFile bundlesFile, String bundleName, String itemName) {
		super(
			Alert.getProblemPrefix(bundlesFile, bundleName, null, itemName) + 
			"is not a recognised item name."
		);
	}
	
}
