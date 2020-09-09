package bundles.item;

import java.util.List;

import items.ItemIdentifier;
import items.ItemDatabase;
import errorHandling.alerts.ItemCategoryAlert;
import errorHandling.alerts.ItemIdAlert;
import errorHandling.alerts.ItemNameAlert;
import errorHandling.alerts.ItemQualityAlert;
import errorHandling.alerts.ValueTypeAlert;
import util.InputReading;
import util.ReadableJsonFile;

abstract class BundleItem {

	private String name;
	private String category;
	private int id;
	private int quality;
	private int quantity;

	public String getSbmmFormatValue() {
		switch (this.getQuality()) {
			case 0:
				return getQuantity() + " " + getName();
			case 1:
				return getQuantity() + " silver " + getName();
			case 2:
				return getQuantity() + " gold " + getName();
			case 3:
				// Quality actually goes from 2 for gold to 4 for iridium,
				// but that's kind of unintuitive so I'm assuming that both mean iridium.
				return getQuantity() + " iridium " + getName();
			case 4:
				return getQuantity() + " iridium " + getName();
		}
		throw new RuntimeException("Invalid quality value (" + this.getQuality() + ") passed validation.");
	}

	public abstract String getSvFormatValue();

	protected String getName() {
		return this.name;
	}

	protected String getCategory() {
		return this.category;
	}

	protected int getId() {
		return this.id;
	}

	protected int getQuality() {
		return quality;
	}

	protected int getQuantity() {
		return quantity;
	}
	
	protected void setId
	(
		ItemDatabase itemDb, 
		ReadableJsonFile bundleFile, String bundleName, 
		String itemName, String itemCategory
	) 
		throws ItemNameAlert, ItemCategoryAlert
	{
		this.name = itemName;
		setId(disambiguateIdentifier(
			bundleName, itemName, 
			itemDb.getItems(bundleFile, bundleName, itemName, itemCategory)
		));
	}
	
	protected void setId
	(
		ItemDatabase itemDb, 
		ReadableJsonFile bundleFile, String bundleName, 
		String itemName
	) 
		throws ItemNameAlert
	{
		this.name = itemName;
		setId(disambiguateIdentifier(
			bundleName, itemName, 
			itemDb.getItems(bundleFile, bundleName, itemName)
		));
	}
	
	protected void setId
	(
		ItemDatabase itemDb, 
		ReadableJsonFile bundleFile, String bundleName, 
		String itemString, String itemCategory, String itemId
	) 
		throws ValueTypeAlert, ItemCategoryAlert, ItemIdAlert 
	{
		try {
			this.id = Integer.parseInt(itemId);
			this.name = itemDb.getName(bundleFile, bundleName, itemCategory, this.id);
			this.category = itemCategory;
		} catch (NumberFormatException e) {
			throw new ValueTypeAlert(bundleFile, bundleName, itemString, itemId, Integer.class);
		}
	}
	
	private void setId(ItemIdentifier id) {
		this.category = id.getCategory();
		this.id = id.getId();
	}
	
	protected void setQuantity
	(
		ReadableJsonFile bundleFile, String bundleName, 
		String itemString, String quantity
	) 
		throws ValueTypeAlert
	{
		try {
			this.quantity = Integer.parseInt(quantity);
		} catch (NumberFormatException e) {
			throw new ValueTypeAlert(bundleFile, bundleName, itemString, quantity, Integer.class);
		}
	}
	
	protected void setQuality
	(
		ReadableJsonFile bundleFile, String bundleName, 
		String itemString, String quality, 
		boolean isSbmmFormat
	) 
		throws ItemQualityAlert, ValueTypeAlert
	{
		this.quality = (
			isSbmmFormat?
				getSbmmFormatQuality(bundleFile, bundleName, quality):
				getSvFormatQuality(bundleFile, bundleName, itemString, quality)
		);
	}
	
	protected void setNoQuality() {
		this.quality = 0;
	}

	private ItemIdentifier disambiguateIdentifier
	(
		String bundleName, String itemName, 
		List<ItemIdentifier> candidates
	) {
		if (candidates.size() == 0) throw new RuntimeException("Empty candidates list passed to disambiguateIdentifier.");
		if (candidates.size() == 1) return candidates.get(0);
		System.out.println("Multiple items found with name '" + itemName + "'.");
		System.out.println("Which of the following " + itemName + " descriptions is correct for the " + bundleName + " bundle?");
		for (int i = 0; i < candidates.size(); i++) {
			System.out.println(i + ": " + candidates.get(i).getDescription());
		}
		while (true) {
			ItemIdentifier chosenItem = null;
			while (true) {
				String input = InputReading.getString();
				for (int i = 0; i < candidates.size(); i++) {
					if (input.equals("" + i)) {
						chosenItem = candidates.get(i);
						break;
					}
				}
				if (chosenItem != null) return chosenItem;
				System.out.println("Please enter a number from 0 to " + (candidates.size() - 1) + ".");
			}
		}
	}
	
	private int getSbmmFormatQuality
	(
		ReadableJsonFile bundleFile, String bundleName, 
		String quality
	)
		throws ItemQualityAlert
	{
		switch (quality) {
			case "iridium":
				return 4;
			case "gold":
				return 2;
			case "silver":
				return 1;
			default:
				throw new ItemQualityAlert(bundleFile, bundleName, quality);
		}
	}

	private int getSvFormatQuality(
		ReadableJsonFile bundleFile, String bundleName, 
		String optionString, 
		String quality
	) 
		throws ItemQualityAlert, ValueTypeAlert
	{
		try {
			int qualityInt = Integer.parseInt(quality);
			if (
				qualityInt == 0 || 
				qualityInt == 1 || 
				qualityInt == 2 || 
				qualityInt == 4
			) {
				return qualityInt;
			}
			if (qualityInt == 3) {
				// Quality actually goes from 2 for gold to 4 for iridium,
				// but that's kind of unintuitive so I'm assuming that both mean iridium.
				return 4;
			}
			throw new ItemQualityAlert(bundleFile, bundleName, optionString, quality);
		} catch (NumberFormatException e) {
			throw new ValueTypeAlert(bundleFile, bundleName, optionString, quality, Integer.class);
		}
	}

}
