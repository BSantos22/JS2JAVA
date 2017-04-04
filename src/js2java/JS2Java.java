package js2java;

import java.io.File;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.JsonObject;

public class JS2Java {
	public JS2Java() {}
	
	public static void main(String args[]) {
		String varsFilepath;
		String jsFilepath;
		
		if (args.length != 2) {
			Scanner reader = new Scanner(System.in);
			System.out.println("Javascript code file name: ");
			jsFilepath = reader.nextLine();
			System.out.println("Variable types file name: ");
			varsFilepath = reader.nextLine();
			reader.close();
		}
		else {
			jsFilepath = args[0];
			varsFilepath = args[1];
		}
		
		File jsFile = new File("files/" + jsFilepath + ".json");
		JsonReader parser = new JsonReader();
		JsonObject js = parser.parse(jsFile);
		
		File varsFile = new File("files/" + varsFilepath + ".json");
		JsonObject vars = parser.parse(varsFile);
		
		TypeInferrer inferrer = new TypeInferrer(vars);
		inferrer.addTypes(js);
		inferrer.printInfers();
	}
	
	public Map<String, String> test(String jsFile, String typesFile) {
		JsonReader parser = new JsonReader();
		JsonObject js = parser.parse(new File(jsFile));
		JsonObject vars = parser.parse(new File(typesFile));
		TypeInferrer inferrer = new TypeInferrer(vars);
		return inferrer.addTypes(js);
	}
}
