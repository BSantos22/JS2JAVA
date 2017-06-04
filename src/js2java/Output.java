package js2java;

import java.io.PrintWriter;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import utils.Utils;
import variables.Function;
import variables.Variable;

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
		function_declaration(inputFile, global, 0);
		outputFile.close();
	}

	private void function_declaration(JsonObject expression, Function function, int ind) {
		if (function.getName().equals("global")) {
			println("public class " + outputFileName + " {", ind);
		}
		else {
			println("public static class " + function.getName().substring(0, 1).toUpperCase() + function.getName().substring(1) + " {", ind);
		}
		
		for (Variable v: function.getDeclared()) {
			println("public static " + v.getType() + " " + v.getName() + ";", ind+1);
		}
		
		ArrayList<JsonObject> nestedFunctions;
		if (function.getName().equals("global")) {
			println("", 0);
			println("public static void main(String args[]) {", ind+1);
			nestedFunctions = processFunction(expression.get(Utils.BODY).getAsJsonArray(), function, ind+2);
		}
		else {
			println("", 0);
			String parameters = "";
			for (int i = 0; i < function.getParameters().size(); i++) {
				Variable param = function.getParameters().get(i);
				
				if (i != 0) {
					parameters += ", ";
				}
				
				parameters += param.getType() + " " + param.getName();
			}
			
			println("public static " + function.getReturnType() + " " + function.getName() + "(" + parameters + ") {", ind+1);
			nestedFunctions = processFunction(expression.get(Utils.BODY).getAsJsonObject().get(Utils.BODY).getAsJsonArray(), function, ind+2);
		}
		
		println("}", ind+1);
		
		for (JsonObject f: nestedFunctions) {
			String name = f.get(Utils.ID).getAsJsonObject().get(Utils.NAME).getAsString();
			for (Function child: function.getChildren()) {
				if (child.getName().equals(name)) {
					function_declaration(f, child, ind+1);
				}
			}
		}
		
		println("}", ind);
	}
	
	private ArrayList<JsonObject> processFunction(JsonArray expression, Function function, int ind) {
		ArrayList<JsonObject> nested = block(expression, function, ind);		
		return nested;
	}
	
	private ArrayList<JsonObject> block(JsonArray content, Function function, int ind) {
		ArrayList<JsonObject> nested = new ArrayList<JsonObject>();
		
		if (content.isJsonNull()) {
			return nested;
		}
		
		for (JsonElement expression: content) {			
			switch (((JsonObject) expression).get(Utils.TYPE).getAsString()) {
				case Utils.EXPRESSION_STATEMENT:
					expression_statement((JsonObject) expression, function, ind);
					break;
				case Utils.VARIABLE_DECLARATION:
					variable_declaration((JsonObject) expression, function, ind, true);
					break;
				case Utils.IF_STATEMENT:
					if_statement((JsonObject) expression, function, ind, false);
					break;
				case Utils.WHILE_STATEMENT:
					while_statement((JsonObject) expression, function, ind);
					break;
				case Utils.DOWHILE_STATEMENT:
					dowhile_statement((JsonObject) expression, function, ind);
					break;
				case Utils.FOR_STATEMENT:
					for_statement((JsonObject) expression, function, ind);
					break;
				case Utils.FUNCTION_DECLARATION:
					nested.add((JsonObject) expression);
					break;
				case Utils.RETURN_STATEMENT:
					return_statement((JsonObject) expression, function, ind);
					break;
			}
		}
		
		return nested;
	}
	
	private void expression_statement(JsonObject statement, Function function, int ind) {
		JsonObject expression = statement.get(Utils.EXPRESSION).getAsJsonObject();
		if (expression.get(Utils.TYPE).getAsString().equals(Utils.SEQUENCE_EXPRESSION)) {
			JsonArray exs = expression.get(Utils.EXPRESSIONS).getAsJsonArray();
			for (JsonElement asgn: exs) {
				println(assignment_expression((JsonObject) asgn, function)+";", ind);
			}
		}
		else if (expression.get(Utils.TYPE).getAsString().equals(Utils.CALL_EXPRESSION)) {
			println(call_expression(expression, function)+";", ind);
		}
		else {
			println(assignment_expression(expression, function)+";", ind);
		}
	}	
	
	private String variable_declaration(JsonObject expression, Function function, int ind, boolean print) {
		JsonArray declarations = expression.get(Utils.DECLARATIONS).getAsJsonArray();
		String s = "";
		
		for (int i = 0; i < declarations.size(); i++) {
			JsonObject dec = declarations.get(i).getAsJsonObject();
			
			if (dec.get(Utils.TYPE).getAsString().equals(Utils.VARIABLE_DECLARATOR)) {
				JsonObject id = (JsonObject) dec.get(Utils.ID);
				if (id.get(Utils.TYPE).getAsString().equals(Utils.IDENTIFIER) && !dec.get(Utils.INIT).isJsonNull()) {
					String var_name = id.get(Utils.NAME).getAsString();
					Variable var = function.getVariable(var_name);
					
					JsonObject init = (JsonObject) dec.get(Utils.INIT);
					s += var.getName() + " = " + expression(init, function);
					if (print) {
						s += ";\n";
					}
				}
			}
		}
		
		if (print) {
			println(s, ind);
		}
		
		return s;	
	}
	
	private void if_statement(JsonObject expression, Function function, int ind, boolean elseif) {
		// Condition
		JsonObject test = expression.get(Utils.TEST).getAsJsonObject();
		if (elseif) {
			println("if (" + expression(test, function) + ") {", 0);
		}
		else {
			println("if (" + expression(test, function) + ") {", ind);
		}
		
		// Then
		JsonObject consequent = expression.get(Utils.CONSEQUENT).getAsJsonObject();
		if (consequent.get(Utils.TYPE).getAsString().equals(Utils.BLOCK_STATEMENT)) {
			block(consequent.get(Utils.BODY).getAsJsonArray(), function, ind+1);
		}
		
		println("}", ind);
		
		// Else
		if (!expression.get(Utils.ALTERNATE).isJsonNull()) {
			JsonObject alternate = expression.get(Utils.ALTERNATE).getAsJsonObject();
			if (alternate.get(Utils.TYPE).getAsString().equals(Utils.BLOCK_STATEMENT)) {
				println("else {", ind);
				
				block(alternate.get(Utils.BODY).getAsJsonArray(), function, ind+1);
				
				println("}", ind);
			}
			// Else if
			else if (alternate.get(Utils.TYPE).getAsString().equals(Utils.IF_STATEMENT)) {
				print("else ", ind);
				if_statement(alternate, function, ind, true);
			}
		}
	}
	
	private void while_statement(JsonObject expression, Function function, int ind) {
		// Condition
		JsonObject test = expression.get(Utils.TEST).getAsJsonObject();
		println("while (" + expression(test, function) + ") {", ind);
		
		// Then
		JsonObject body = expression.get(Utils.BODY).getAsJsonObject();
		if (body.get(Utils.TYPE).getAsString().equals(Utils.BLOCK_STATEMENT)) {
			block(body.get(Utils.BODY).getAsJsonArray(), function, ind+1);
		}
		
		println("}", ind);
	}
	
	private void dowhile_statement(JsonObject expression, Function function, int ind) {
		println("do {", ind);
		
		// Then
		JsonObject body = expression.get(Utils.BODY).getAsJsonObject();
		if (body.get(Utils.TYPE).getAsString().equals(Utils.BLOCK_STATEMENT)) {
			block(body.get(Utils.BODY).getAsJsonArray(), function, ind+1);
		}
		
		// Condition
		JsonObject test = expression.get(Utils.TEST).getAsJsonObject();
		println("while (" + expression(test, function) + ");", ind);
	}
	
	private void for_statement(JsonObject expression, Function function, int ind) {
		// Init
		JsonObject init = expression.get(Utils.INIT).getAsJsonObject();
		String i = expression(init, function);
		
		// Condition
		JsonObject test = expression.get(Utils.TEST).getAsJsonObject();
		String c = expression(test, function);
		
		// Update
		JsonObject update = expression.get(Utils.UPDATE).getAsJsonObject();
		String u = expression(update, function);
		
		println("for (" + i + "; " + c + "; " + u + ") {",ind);
		
		// Body
		JsonObject body = expression.get(Utils.BODY).getAsJsonObject();
		if (body.get(Utils.TYPE).getAsString().equals(Utils.BLOCK_STATEMENT)) {
			block(body.get(Utils.BODY).getAsJsonArray(), function, ind+1);
		}
		
		println("}", ind);
	}
	
	private void return_statement(JsonObject expression, Function function, int ind) {
		JsonObject argument = expression.get(Utils.ARGUMENT).getAsJsonObject();
		println("return " + expression(argument, function) + ";", ind);
	}
	
	private String expression(JsonObject expression, Function function) {
		switch (((JsonObject) expression).get(Utils.TYPE).getAsString()) {
			case Utils.VARIABLE_DECLARATION:
				return variable_declaration(expression, function, 0, false);
			case Utils.CALL_EXPRESSION:
				return call_expression(expression, function);
			case Utils.ASSIGNMENT_EXPRESSION:
				return assignment_expression(expression, function);
			case Utils.LOGICAL_EXPRESSION:
				return logical_expression(expression, function);
			case Utils.UNARY_EXPRESSION:
				return unary_expression(expression, function);
			case Utils.UPDATE_EXPRESSION:
				return update_expression(expression, function);
			case Utils.BINARY_EXPRESSION:
				return binary_expression(expression, function);
			case Utils.ARRAY_EXPRESSION:
				return array_expression(expression, function);
			case Utils.MEMBER_EXPRESSION:
				return member_expression(expression, function);
			case Utils.IDENTIFIER:	
				return identifier(expression, function);
			case Utils.LITERAL:
				return literal(expression);				
			default:
				return "";
		}
	}
	
	private String call_expression(JsonObject expression, Function function) {
		String function_name, class_name;
		
		if (expression.get(Utils.CALLEE).getAsJsonObject().get(Utils.TYPE).getAsString().equals(Utils.MEMBER_EXPRESSION)) {
			function_name = expression.get(Utils.CALLEE).getAsJsonObject().get(Utils.PROPERTY).getAsJsonObject().get(Utils.NAME).getAsString();
			class_name = expression.get(Utils.CALLEE).getAsJsonObject().get(Utils.OBJECT).getAsJsonObject().get(Utils.NAME).getAsString();
		}
		else {
			function_name = expression.get(Utils.CALLEE).getAsJsonObject().get(Utils.NAME).getAsString();
			class_name = function_name.substring(0, 1).toUpperCase() + function_name.substring(1);
		}
		
		String exp = "";
		if (class_name.equals(Utils.CONSOLE) && function_name.equals(Utils.LOG)) {
			exp += "System.out.println(";
		}
		else {
			exp += class_name + "." + function_name + "(";
		}
		
		JsonArray arguments = expression.get(Utils.ARGUMENTS).getAsJsonArray();
		
		// Add type of each parameter to called function
		for (int i = 0; i < arguments.size(); i++) {
			if (i != 0) {
				exp += ", ";
			}
			exp += expression(arguments.get(i).getAsJsonObject(), function);
		}
		
		exp += ")";
		
		return exp;
	}

	private String assignment_expression(JsonObject expression, Function function) {
		String exp = "";
		
		JsonObject left = (JsonObject) expression.get(Utils.LEFT);
		if (left.get(Utils.TYPE).getAsString().equals(Utils.MEMBER_EXPRESSION)) {
			exp += member_expression(left, function);
		}
		else {
			exp += identifier(left, function);
		}
		
		JsonObject right = (JsonObject) expression.get(Utils.RIGHT);
		exp += " = " + expression(right, function);
		
		return exp;
	}
	
	private String logical_expression(JsonObject expression, Function function) {
		String operator = expression.get(Utils.OPERATOR).getAsString();		
		JsonObject left = (JsonObject) expression.get(Utils.LEFT);
		JsonObject right = (JsonObject) expression.get(Utils.RIGHT);
		String exp = expression(left, function);
		exp += " " + operator + " ";
		exp += expression(right, function);
		
		return exp;
	}
	
	private String unary_expression(JsonObject expression, Function function) {
		String exp = expression.get(Utils.OPERATOR).getAsString();
		JsonObject argument = (JsonObject) expression.get(Utils.ARGUMENT);
		exp += expression(argument, function);
		return exp;
	}

	private String update_expression(JsonObject expression, Function function) {
		JsonObject argument = (JsonObject) expression.get(Utils.ARGUMENT);
		String exp = variable(argument, function);
		exp += expression.get(Utils.OPERATOR).getAsString();	
		return exp;
	}
	
	private String binary_expression(JsonObject expression, Function function) {
		String operator = expression.get(Utils.OPERATOR).getAsString();		
		JsonObject left = (JsonObject) expression.get(Utils.LEFT);
		JsonObject right = (JsonObject) expression.get(Utils.RIGHT);
		String exp = expression(left, function);
		exp += " " + operator + " ";
		exp += expression(right, function);
		
		return exp;
	}
	
	private String variable(JsonObject argument, Function function) {
		if (argument.get(Utils.TYPE).getAsString().equals(Utils.MEMBER_EXPRESSION)) {
			return member_expression(argument, function);
		}
		else if (argument.get(Utils.TYPE).getAsString().equals(Utils.IDENTIFIER)) {
			return identifier(argument, function);
		}
		
		return "";
	}
	
	private String array_expression(JsonObject expression, Function function) {
		String  exp = "{";
		
		JsonArray elements = expression.get(Utils.ELEMENTS).getAsJsonArray();
		for (int i = 0; i < elements.size(); i++) {
			JsonObject o = elements.get(i).getAsJsonObject();
			
			if (i != 0) {
				exp += ", " + expression(o, function);
			}
			else {
				exp += expression(o, function);
			}
		}
		
		exp += "}";
		exp = "new " + varTypes.array_expression(expression, function) + exp;
		
		return exp;
	}
	
	private String member_expression(JsonObject expression, Function function) {
		String exp = expression.get(Utils.OBJECT).getAsJsonObject().get(Utils.NAME).getAsString();
		if (expression.get(Utils.PROPERTY).getAsJsonObject().get(Utils.NAME).getAsString().equals(Utils.LENGTH)) {
			exp += ".length";
		}
		else {
			exp += "[";
			exp += expression(expression.get(Utils.PROPERTY).getAsJsonObject(), function);
			exp += "]";
		}
		
		return exp;
	}
	
	private String identifier(JsonObject expression, Function function) {
		return expression.get(Utils.NAME).getAsString();
	}
	
	private String literal(JsonObject expression) {
		return expression.get(Utils.RAW).getAsString();
	}
	
	// Print functions
	private void println(String s, int i) {
		String ind = new String(new char[i]).replace("\0", "\t");
		outputFile.println(ind + s);
	}
	
	private void print(String s, int i) {
		String ind = new String(new char[i]).replace("\0", "\t");
		outputFile.print(ind + s);
	}
}