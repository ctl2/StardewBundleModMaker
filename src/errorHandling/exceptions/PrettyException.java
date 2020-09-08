package errorHandling.exceptions;

import config.Configuration;
import errorHandling.alerts.Alert;
import util.InputReading;

public abstract class PrettyException extends Exception {
	
	static private final String lineDivider = "\n\t";
	
	private Alert alert;
	private boolean isConfigurable;
	private boolean isFatal;
	private String[] messageBody;
	
	public PrettyException(Alert alert) {
		this.alert = alert;
		this.isConfigurable = false;
		setConfigDependentFields(Configuration.ABORT, null);
		printMessage();
	}
	
	public PrettyException(Alert alert, String solution, Configuration config) {
		super();
		this.alert = alert;
		this.isConfigurable = true;
		setConfigDependentFields(config, solution);
		printMessage();
	}
	
	public PrettyException(PrettyException cause) {
		this.alert = cause.getAlert();
		this.isConfigurable = false;
		setConfigDependentFields(Configuration.ABORT, null);
	}
	
	public PrettyException(PrettyException cause, String solution, Configuration config) {
		this.alert = cause.getAlert();
		this.isConfigurable = cause.isConfigurable;
		setConfigDependentFields(config, solution);
	}
	
	private void setConfigDependentFields(Configuration config, String solution) {
		switch (config) {
			case CONCEAL:
				this.isFatal = false;
				this.messageBody = null;
				break;
			case TELL:
				this.isFatal = false;
				this.messageBody = new String[] {alert.getProblem(), solution};
				break;
			case ASK:
	            System.out.println(alert.getProblem());
	            if (InputReading.getBoolean("Would you like to handle this problem yourself?")) {
	    			this.isFatal = true;
	    			this.messageBody = new String[] {alert.getAdvice()};
	            } else {
	    			this.isFatal = false;
	    			this.messageBody = new String[] {solution};
	            }
				break;
			case ABORT:
				this.isFatal = true;
				this.messageBody = new String[] {alert.getProblem(), alert.getAdvice()};
		}
	}
	
	private void printMessage() {
		String message = getMessage();
		if (message == null) return;
		System.out.println(message);
	}

	protected Alert getAlert() {
		return this.alert;
	}
	
	public boolean isFatal() {
		return this.isFatal;
	}
	
	@Override
	public String getMessage() {
		if (this.messageBody == null) return null;
		// Get exception message header
		String header = (
			this.isFatal?
			"ERROR":
			"ALERT"
		) + ": " + this.getClass().getSimpleName();
		// Return a pretty message UwU
		return String.join(lineDivider, new String[] {header, String.join(lineDivider, messageBody)}) + "\n";
	}

}
