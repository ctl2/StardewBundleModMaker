package errorHandling.alerts;

import util.ReadableJsonFile;

// Should only be passed to the BundleException class
public class NumberOfValuesAlert extends FileContentAlert {
	
	public NumberOfValuesAlert
	(
		ReadableJsonFile jsonFile, String bundleName, 
		String[] values, String delimiter, 
		Integer minValues, Integer maxValues
	) {
		super(
			Alert.getProblemPrefix(jsonFile, bundleName, String.join(delimiter, values), null) + 
			"consists of " + values.length + " values. " + (
				maxValues == null?
					minValues + " or more":
				minValues == null?
					maxValues + " or fewer":
				minValues == maxValues?
					minValues:
					"Between " + minValues + " and " + maxValues
			) + " values are expected."
		);
	}

}
