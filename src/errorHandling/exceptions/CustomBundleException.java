package errorHandling.exceptions;

import config.Configuration;
import errorHandling.alerts.FileContentAlert;

public class CustomBundleException extends PrettyException {

	static private Configuration config;

	static public void setConfiguration(Configuration config) {
		CustomBundleException.config = config;
	}

	public CustomBundleException(FileContentAlert alert) {
		super(
			alert,
			"Your bundle has been replaced with the vanilla bundle.",
			CustomBundleException.config
		);
	}
	
	public CustomBundleException(PrettyException cause) {
		super(cause);
	}

}
