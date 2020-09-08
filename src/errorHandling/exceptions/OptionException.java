package errorHandling.exceptions;

import config.Configuration;
import errorHandling.alerts.FileContentAlert;

public class OptionException extends PrettyException {

	static private Configuration config;

	static public void setConfiguration(Configuration config) {
		OptionException.config = config;
	}

	public OptionException(FileContentAlert alert) {
		super(alert, "Your options have been replaced with the vanilla options for this bundle.", OptionException.config);
	}

}
