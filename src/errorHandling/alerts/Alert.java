package errorHandling.alerts;

import java.lang.reflect.InvocationTargetException;

import errorHandling.exceptions.PrettyException;
import util.ReadableFile;

public class Alert extends Exception {
	
	static protected String getProblemPrefix(
		ReadableFile missingFile, String bundleName, String data, String value
	) {
		String problemPrefix = "";
		if (value != null) {
			problemPrefix += "The value '" + value + "' ";
		}
		if (data != null) {
			problemPrefix += problemPrefix.isEmpty()?
				"The data '" + data + "' ":
				"in '" + data + "' ";
		}
		if (bundleName != null) {
			problemPrefix += problemPrefix.isEmpty()?
				"The " + bundleName + "bundle ":
				"in the " + bundleName + "bundle ";
		}
		if (missingFile != null) {
			problemPrefix += problemPrefix.isEmpty()?
				"The file " + missingFile.getPath() + " ":
				"in " + missingFile.getPath() + " ";
		}
		return problemPrefix;
	}
	
	private String problem;
	private String advice;

	public Alert(String problem, String advice) {
		this.problem = problem;
		this.advice = advice;
	}

	public Alert(Alert alert, String extraAdvice) {
		this.problem = alert.getProblem();
		this.advice = alert.getAdvice() + " " + extraAdvice;
	}

	public String getProblem() {
		return this.problem;
	}

	public String getAdvice() {
		return this.advice;
	}
	
	public PrettyException getException(Class<? extends PrettyException> exceptionType) {
		try {
			return exceptionType.getConstructor(Alert.class).newInstance(this);
		} catch (
			InstantiationException | IllegalAccessException | IllegalArgumentException | 
			InvocationTargetException | NoSuchMethodException | SecurityException e
		) {
			throw new RuntimeException(e);
		}
	}

}
