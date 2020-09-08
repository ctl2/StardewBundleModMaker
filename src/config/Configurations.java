package config;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import errorHandling.alerts.JsonKeyAlert;
import errorHandling.alerts.JsonSyntaxAlert;
import errorHandling.alerts.MissingFileAlert;
import errorHandling.alerts.ValueTypeAlert;
import errorHandling.exceptions.CustomBundleException;
import errorHandling.exceptions.ConfigException;
import errorHandling.exceptions.ItemFileException;
import errorHandling.exceptions.NonEnglishVanillaBundlesFileException;
import errorHandling.exceptions.OptionException;
import errorHandling.exceptions.PrettyException;
import util.WriteableJsonFile;
import util.ReadableJsonFile;

public class Configurations extends HashMap<Class<? extends PrettyException>, Configuration> {

	static final HashMap<String, Class<? extends PrettyException>> configKeyToExceptionClass =
			new HashMap<String, Class<? extends PrettyException>>() {
				{
					put("language files", NonEnglishVanillaBundlesFileException.class);
					put("item files", ItemFileException.class);
					put("options", OptionException.class);
					put("bundles", CustomBundleException.class);
				}
			};

	public Configurations() throws ConfigException {
		super();
		try {
			init();
		} catch (ConfigException toThrow) {
			if (toThrow.isFatal()) throw toThrow;
			// Make a new config.json file
			WriteableJsonFile writeableConfigFile = new WriteableJsonFile(null, "config");
			writeableConfigFile.write(
				"{\r\n" +
				"    \"language files\": {\r\n" +
				"        \"config\": \"conceal\",\r\n" +
				"        \"__HANDLED BY__\": \"excluding bundle name translations for missing languages from mods\"\r\n" +
				"    }, \r\n" +
				"    \"item files\": {\r\n" +
				"        \"config\": \"conceal\",\r\n" +
				"        \"__HANDLED BY__\": \"generating an incomplete dataset of items\"\r\n" +
				"    }, \r\n" +
				"    \"options\": {\r\n" +
				"        \"config\": \"ask\",\r\n" +
				"        \"__HANDLED BY__\": \"discarding invalid options\"\r\n" +
				"    }, \r\n" +
				"    \"bundles\": {\r\n" +
				"        \"config\": \"ask\",\r\n" +
				"        \"__HANDLED BY__\": \"replacing invalid bundles with vanilla bundles\"\r\n" +
				"    }, \r\n" +
				"    \"__CONFIGURATIONS__\": [\r\n" +
				"        \"conceal    Don't notify the user. Handle the problem.\",\r\n" +
				"        \"tell       Notify the user. Handle the problem.\", \r\n" +
				"        \"ask        Notify the user. Ask how to respond.\",\r\n" +
				"        \"abort      Notify the user. Terminate the program.\"\r\n" +
				"    ]\r\n" +
				"}\r\n"
			);
			// Retry
			try {
				init();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private void init() throws ConfigException {
		ReadableJsonFile readableConfigFile = new ReadableJsonFile(null, "config");
		try {
			// Get the user's configurations
			JsonObject configurations = readableConfigFile.getJson();
			// Populate /this/
			for (Entry<String, Class<? extends PrettyException>> entry: configKeyToExceptionClass.entrySet()) {
				try {
					this.put(
						entry.getValue(),
						Configuration.getEnumConfiguration(
							ReadableJsonFile.jsonElementToString(
								readableConfigFile,
								null,
								ReadableJsonFile.jsonElementToJsonObject(
									readableConfigFile,
									null,
									configurations.get(entry.getKey())
								)
								.get("config")
							)
						)
					);
				} catch (NullPointerException e3) {
					// readableConfigFile is missing a config
					throw new ConfigException(
						new JsonKeyAlert(readableConfigFile, entry.getKey())
					);
				} catch (ValueTypeAlert a) {
					throw new ConfigException(a);
				}
			}
		} catch (FileNotFoundException e1) {
			throw new ConfigException(
				new MissingFileAlert(readableConfigFile)
			);
		} catch (JsonSyntaxException e2) {
			throw new ConfigException(
				new JsonSyntaxAlert(readableConfigFile)
			);
		}
	}

	public void configure() {
		this.entrySet()
				.stream()
				.forEach(configuration -> {
					try {
						configuration.getKey()
								.getMethod("setConfiguration", Configuration.class)
								.invoke(null, configuration.getValue());
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
							| NoSuchMethodException | SecurityException e) {
						throw new RuntimeException(e);
					}
				});
	}

}
