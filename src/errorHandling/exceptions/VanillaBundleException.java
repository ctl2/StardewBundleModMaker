package errorHandling.exceptions;

import errorHandling.alerts.FileContentAlert;

public class VanillaBundleException extends PrettyException {
	
	public VanillaBundleException(FileContentAlert alert) {
		super(alert);
	}
	
	public VanillaBundleException(PrettyException cause) {
		super(cause);
	}

}
