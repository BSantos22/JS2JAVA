package js2java;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Output {

	private JsonObject inputFile;
	private TypeInferrer varTypes;
	private File outputFile;
	private String outputString;
	private Map<String, ArrayList<JsonObject>> expressions;


	public Output(JsonObject js, TypeInferrer inferrer) {
		inputFile = js;
		varTypes = inferrer;

		//File outputFile = new File("files/java output");
	}

	public File getOutputFile() { return outputFile;}

	public void start() {
		iterateJsonObj(inputFile);
	}

	public void iterateJsonObj(JsonObject js) {
		int i=-1;
		for (Map.Entry<String, JsonElement> entry: js.entrySet()) {
			i++;
			System.out.println("\n" + i);
			System.out.println(entry.getKey() + " - " + entry.getValue());

			if(entry.getValue() instanceof JsonObject)
				iterateJsonObj((JsonObject) entry.getValue());

			else if (entry.getValue() instanceof JsonArray)
				iterateJsonArray((JsonArray) entry.getValue());

			
		}
	}

	public void iterateJsonArray(JsonArray jay) {
		for (int i = 0; i < jay.size(); i++) {
			if (jay.get(i) instanceof JsonObject) {
				iterateJsonObj((JsonObject) jay.get(i));
			}
		}
	}

	/*
	public void inferExpression(Map.Entry<String, JsonElement> expr) {
		String e = expr.getKey();

		switch(e) {
		case "VariableDeclaration":
			variableDeclaration((JsonObject) expr.getValue());
			break;
		}
	}

	public void variableDeclaration(JsonObject expr) {
		String line = "";
		for (Map.Entry<String, JsonElement> entry: expr.entrySet()) {
			System.out.println(entry.getKey() + " ------ " + entry.getValue());
			if(entry.getKey().equals("type"))
				line += entry.getValue().getAsString();
			if(entry.getKey().equals("name"))
				line += " " + entry.getValue().getAsString() + " =";
			if(entry.getKey().equals("init"))
				aux_variableDeclaration((JsonObject) entry.getValue(), line);
		}

		System.out.println("LINE: " + line);
	}

	public void aux_variableDeclaration(JsonObject expr, String line) {
		for (Map.Entry<String, JsonElement> entry: expr.entrySet()) {
			if(entry.getKey().equals("raw"))
				line += " " + entry.getValue().getAsString();
		}
		line += ";";
	}
	*/
}