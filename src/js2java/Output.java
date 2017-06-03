package js2java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import utils.Utils;
import variables.Function;

public class Output {

	private JsonObject inputFile;
	private TypeInferrer varTypes;
	private PrintWriter outputFile;
	private String outputFileName;

	public Output(JsonObject js, TypeInferrer inferrer, String fileName) {
		inputFile = js;
		varTypes = inferrer;
		outputFileName = fileName.substring(0, 1).toUpperCase() + fileName.substring(1);
		
		try {
			outputFile = new PrintWriter("output/"+outputFileName+".java", "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start() {
		Function global = varTypes.getFunctions().get(0);
		function_declaration(inputFile, global);
		
	}

	private void function_declaration(JsonObject expression, Function function) {
		if (function.getName().equals("global")) {
			outputFile.println("public class " + outputFileName + " {");
		}
		else {
			outputFile.println("public class " + function.getName() + " {");
		}
		
		
		
		
		outputFile.println("}");
	}
	
	/*
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