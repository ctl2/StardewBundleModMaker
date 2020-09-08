package items;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import errorHandling.alerts.ItemCategoryAlert;
import errorHandling.alerts.ItemIdAlert;
import errorHandling.alerts.ItemNameAlert;
import errorHandling.alerts.MissingFileAlert;
import errorHandling.alerts.ValueTypeAlert;
import errorHandling.exceptions.ItemFileException;
import util.ReadableJsonFile;

public class ItemDatabase {

	private HashMap<String, HashMap<Integer, String>> svFormatDict =
			new HashMap<String, HashMap<Integer, String>>(); // category -> id -> name
	private HashMap<String, List<ItemIdentifier>> sbmmFormatDict =
			new HashMap<String, List<ItemIdentifier>>(); // name -> [category, id, description]

	public ItemDatabase() throws ItemFileException {
		init("O", "ObjectInformation", Pattern.compile("^(?!Ring).*$"), 5);
		init("R", "ObjectInformation", Pattern.compile("^Ring$"), 5);
		init("BO", "BigCraftablesInformation", Pattern.compile(".*"), 4);
		init("F", "Furniture", Pattern.compile(".*"), 0);
		init("H", "hats", Pattern.compile(".*"), 1);
		init("C", "ClothingInformation", Pattern.compile(".*"), 2);
	}

	private void init
	(
		String category, String fileName, Pattern typeRegex,
		int descriptionIndex
	)
		throws ItemFileException
	{
		// Create outer hashmap for svFormatDict
		HashMap<Integer, String> categorySvFormatDict = new HashMap<Integer, String>();
		// Create inner hashmaps
		ReadableJsonFile itemFile = new ReadableJsonFile("Items", fileName);
		try {
			try {
				JsonObject itemFileJson = itemFile.getJson();
				for (Entry<String, JsonElement> itemEntry: itemFileJson.entrySet()) {
					if (
						typeRegex.matcher(
							ReadableJsonFile.jsonElementToString(
								itemFile, 
								null, 
								itemEntry.getValue()
							)
							.split("/")[3]
						)
						.find()
					) {
						// itemEntry is in the right category
						String[] objectValues = ReadableJsonFile.jsonElementToString(
							itemFile, 
							null, 
							itemEntry.getValue()
						)
						.split("/");
						String objectName = objectValues[0].toLowerCase();
						int objectId = Integer.parseInt(itemEntry.getKey());
						categorySvFormatDict.put(objectId, objectName);
						sbmmFormatDict.putIfAbsent(objectName, new ArrayList<ItemIdentifier>());
						sbmmFormatDict.get(objectName).add(new ItemIdentifier(category, objectId, objectValues[descriptionIndex]));
					}
				}
				svFormatDict.put(category, categorySvFormatDict);
			} catch (FileNotFoundException e) {
				throw new ItemFileException(new MissingFileAlert(itemFile));
			} catch (ValueTypeAlert a) {
				throw new ItemFileException(a);
			}
		} catch (ItemFileException e) {
			if (e.isFatal()) throw e;
		}
	}

	public List<ItemIdentifier> getItems
	(
		ReadableJsonFile bundleFile, String bundleName,
		String itemName
	)
		throws ItemNameAlert
	{
		if (!sbmmFormatDict.containsKey(itemName))
				throw new ItemNameAlert(bundleFile, bundleName, itemName);
		return sbmmFormatDict.get(itemName);
	}

	public List<ItemIdentifier> getItems
	(
		ReadableJsonFile bundleFile, String bundleName,
		String itemName, String itemCategory
	)
		throws ItemNameAlert, ItemCategoryAlert
	{
		List<ItemIdentifier> matches = getItems(bundleFile, bundleName, itemName)
				.stream()
				.filter(item -> item.getCategory().equals(itemCategory))
				.collect(Collectors.toList());
		if (matches.size() == 0)
				throw new ItemCategoryAlert(bundleFile, bundleName, itemName, itemCategory);
		return matches;
	}

	public String getName
	(
		ReadableJsonFile bundleFile, String bundleName,
		String itemCategory, int itemId
	)
		throws ItemCategoryAlert, ItemIdAlert
	{
		if (!svFormatDict.containsKey(itemCategory))
				throw new ItemCategoryAlert(bundleFile, bundleName, itemCategory);
		HashMap<Integer, String> categoryItems = svFormatDict.get(itemCategory);
		if (!categoryItems.containsKey(itemId))
				throw new ItemIdAlert(bundleFile, bundleName, itemCategory, itemId);
		return categoryItems.get(itemId);
	}

}
