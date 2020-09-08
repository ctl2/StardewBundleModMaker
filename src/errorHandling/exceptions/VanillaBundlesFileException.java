package errorHandling.exceptions;

import config.Configuration;
import errorHandling.alerts.Alert;

public abstract class VanillaBundlesFileException extends PrettyException {

	public VanillaBundlesFileException(Alert alert) {
		super(
			new Alert(
				alert, 
				"See Instructions.txt for advice on generating JSON versions of the vanilla bundles files."
			)
		);
	}

	public VanillaBundlesFileException(Alert alert, String solution, Configuration config) {
		super(
			new Alert(
				alert, 
				"See Instructions.txt for advice on generating JSON versions of the vanilla bundles files."
			), 
			solution, config
		);
	}

}
