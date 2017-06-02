package js2java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonReader {
	final public static String DEFAULT_CHAR_SET = "UTF-8";
	
	public JsonReader() {}
	
	public JsonObject parse(File file) {
		JsonElement root = (new JsonParser()).parse(read(file));
		return root.getAsJsonObject();
	}
	
	private String read(File file) {
		if (file == null) {
			Logger.getLogger("info").info("Input file is null");
			return null;
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		
		try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), DEFAULT_CHAR_SET))) {
            int intChar = bufferedReader.read();
            while (intChar != -1) {
                char character = (char) intChar;
                stringBuilder.append(character);
                intChar = bufferedReader.read();
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger("info").info("FileNotFoundException: " + ex.getMessage());
            return null;

        } catch (IOException ex) {
            Logger.getLogger("info").info("IOException: " + ex.getMessage());
            return null;
        }

        return stringBuilder.toString();
	}
}
