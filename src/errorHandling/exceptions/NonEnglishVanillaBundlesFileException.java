package errorHandling.exceptions;

import config.Configuration;
import errorHandling.alerts.Alert;

public class NonEnglishVanillaBundlesFileException extends VanillaBundlesFileException {

	static private Configuration config;

	static public void setConfiguration(Configuration config) {
		NonEnglishVanillaBundlesFileException.config = config;
	}

	public NonEnglishVanillaBundlesFileException(Alert alert) {
		super(
			alert,
			"No bundle file will been generated for the missing language.",
			NonEnglishVanillaBundlesFileException.config
		);
	}

}
