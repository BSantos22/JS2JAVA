package js2java;

import java.io.File;
import java.util.Iterator;

import com.google.gson.JsonObject;

public class Output {

	private JsonObject inputFile;
	private TypeInferrer varTypes;
	private File outputFile;
	
	public Output(JsonObject varsFile, TypeInferrer inferrer) {
		inputFile = varsFile;
		varTypes = inferrer;
		
		File outputFile = new File("files/java output");
	}
	
	public File getOutputFile() { return outputFile;}
	
	public void start() {
		// > iterator
		
		// for(int i=0; i <)
	}
	
	
	public void inferExpression(JsonObject expr) {
		
	}
}
