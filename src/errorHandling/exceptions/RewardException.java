package errorHandling.exceptions;

import errorHandling.alerts.FileContentAlert;

public class RewardException extends PrettyException {
	
	public RewardException(FileContentAlert alert) {
		super(alert);
	}

}