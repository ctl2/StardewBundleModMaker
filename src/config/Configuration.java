package config;

import errorHandling.exceptions.ConfigException;

public enum Configuration {
	
	CONCEAL, 
	TELL, 
	ASK, 
	ABORT;
	
	static public Configuration getEnumConfiguration(String config) throws ConfigException {
		switch (config) {
			case "conceal":
				return Configuration.CONCEAL;
			case "tell":
				return Configuration.TELL;
			case "ask":
				return Configuration.ASK;
			case "abort":
				return Configuration.ABORT;
		}
		return null;
	}
	
}
