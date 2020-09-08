package items;

public class ItemIdentifier {
	
	private String category;
	private int id;
	private String description;
	
	public ItemIdentifier(String category, int id, String description) {
		this.category = category;
		this.id = id;
		this.description = description;
	}
	
	public ItemIdentifier(String category, int id) {
		this.category = category;
		this.id = id;
		this.description = null;
	}

	public String getCategory() {
		return category;
	}

	public int getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}
	
}