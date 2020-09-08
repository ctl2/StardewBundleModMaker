package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import util.WriteableFile;

public class WriteableJsonFile extends WriteableFile {
	
	static private final Gson converter = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	
	public WriteableJsonFile(String directoryPath, String baseName) {
		super(directoryPath, baseName, "json");
	}

	@Override
	public void write(Object jsonObject) {
        super.write(
    		jsonObject instanceof String?
			jsonObject:
			converter.toJson(jsonObject)
		);
    }

}
