package errorHandling.exceptions;

import config.Configuration;
import errorHandling.alerts.Alert;

public class ConfigException extends PrettyException {

	public ConfigException(Alert alert) {
		super(
			new Alert(
				alert,
				"If unrecoverable, redownload the file."
			), 
			"A new config.json file has been generated with default settings.",
			Configuration.ASK
		);
	}

}
