package util;

import java.io.FileNotFoundException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import errorHandling.alerts.ValueTypeAlert;

public class ReadableJsonFile extends ReadableFile {
	
	static public JsonObject jsonElementToJsonObject
	(
		ReadableJsonFile bundleFile, String bundleName, 
		JsonElement element
	) 
		throws ValueTypeAlert 
	{
		if (element.isJsonObject()) return element.getAsJsonObject();
		throw new ValueTypeAlert(bundleFile, bundleName, null, element.toString(), JsonObject.class);
	}
	
	static public JsonArray jsonElementToJsonArray
	(
		ReadableJsonFile bundleFile, String bundleName, 
		JsonElement element
	) 
		throws ValueTypeAlert 
	{
		if (element.isJsonArray()) return element.getAsJsonArray();
		throw new ValueTypeAlert(bundleFile, bundleName, null, element.toString(), JsonArray.class);
	}
	
	static public String jsonElementToString
	(
		ReadableJsonFile bundleFile, String bundleName, 
		JsonElement element
	) 
		throws ValueTypeAlert 
	{
		if (element.isJsonPrimitive())
			if (element.getAsJsonPrimitive().isString())
				return element.getAsString();
		throw new ValueTypeAlert(bundleFile, bundleName, null, element.toString(), String.class);
	}
	
	static public int jsonElementToInt
	(
		ReadableJsonFile bundleFile, String bundleName, 
		JsonElement element
	) 
		throws ValueTypeAlert 
	{
		if (element.isJsonPrimitive())
			if (element.getAsJsonPrimitive().isNumber())
				return element.getAsInt();
		throw new ValueTypeAlert(bundleFile, bundleName, null, element.toString(), Integer.class);
	}
	
	static private Gson jsonReader = new Gson();

	public ReadableJsonFile(String directoryPath, String baseName) {
		super(directoryPath, baseName, "json");
	}

	public JsonObject getJson() throws FileNotFoundException, JsonSyntaxException {
		JsonObject json = jsonReader.fromJson(getFileContents(), JsonObject.class);
		return json.has("content")? 
				json.getAsJsonObject("content"): // Trim xnb metadata if present
				json;
	}
	
	public boolean isValidJsonFile() {
		try {
			getJson();
			return true;
		} catch (JsonSyntaxException | FileNotFoundException e) {
			return false;
		}
	}

}
