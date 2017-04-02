package js2java;

import java.io.File;

import com.google.gson.JsonObject;

public class JS2Java {
	public static void main(String args[]) {
		if (args.length != 2) {
			System.err.println("Usage: java JS2Java <js_code_file> <var_type_file>");
			return;
		}
		
		File jsFile = new File(args[0]);
		JsonReader parser = new JsonReader();
		JsonObject js = parser.parse(jsFile);
		
		File varsFile = new File(args[1]);
		JsonObject vars = parser.parse(varsFile);
		
		TypeInferrer inferrer = new TypeInferrer(vars);
		inferrer.addTypes(js);
		System.out.println(js);
	}
}
