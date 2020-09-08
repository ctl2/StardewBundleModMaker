package errorHandling.exceptions;

import config.Configuration;
import errorHandling.alerts.Alert;

public class ItemFileException extends PrettyException {

	static private Configuration config;
	
	static public void setConfiguration(Configuration config) {
		ItemFileException.config = config;
	}
	
	public ItemFileException(Alert alert) {
		super(alert, "Items in the excluded dataset will not be recognised.", ItemFileException.config);
	}

}
