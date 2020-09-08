package errorHandling.alerts;

public abstract class FileContentAlert extends Alert {
	
	public FileContentAlert(String problem) {
		super(problem, "Undo any recent edits to the file or remake it.");
	}

}
