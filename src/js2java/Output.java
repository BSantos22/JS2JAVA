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
			outputFile = new PrintWriter("src/output/"+outputFileName+".java", "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start(String jsCode) {
		if (jsCode != null) {
			println("/*", 0);
			println("JS code", 0);
			println("", 0);
			println(jsCode, 0);		
			println("*/", 0);
		}
		println("", 0);
		println("package output;", 0);
		println("", 0);
		println("import java.util.ArrayList;", 0);
		println("", 0);
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
			String type = v.getType();
			if (v.getType().equals(Utils.STRING)) {
				type = "String";
			}
			else if (v.getType().contains("[]")) {
				type = v.getType().replaceAll("\\[", "");
				type = type.replaceAll("\\]", "");
				type = toObject(type);				
				type = "ArrayList<" + type + ">";
			}
			
			println("public static " + type + " " + v.getName() + ";", ind+1);
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
		else if (expression.get(Utils.TYPE).getAsString().equals(Utils.UPDATE_EXPRESSION)) {
			println(update_expression(expression, function)+";", ind);
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
					s += var.getName() + " = " + expression(init, function, varTypes.getExpression(dec.hashCode()));
					if (print) {
						s += ";";
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
			println("if (" + expression(test, function, Utils.BOOLEAN) + ") {", 0);
		}
		else {
			println("if (" + expression(test, function, Utils.BOOLEAN) + ") {", ind);
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
		println("while (" + expression(test, function, Utils.BOOLEAN) + ") {", ind);
		
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
		println("while (" + expression(test, function, Utils.BOOLEAN) + ");", ind);
	}
	
	private void for_statement(JsonObject expression, Function function, int ind) {
		// Init
		JsonObject init = expression.get(Utils.INIT).getAsJsonObject();
		String i = expression(init, function, Utils.UNDEFINED);
		
		// Condition
		JsonObject test = expression.get(Utils.TEST).getAsJsonObject();
		String c = expression(test, function, Utils.UNDEFINED);
		
		// Update
		JsonObject update = expression.get(Utils.UPDATE).getAsJsonObject();
		String u = expression(update, function, Utils.UNDEFINED);
		
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
		println("return " + expression(argument, function, function.getReturnType()) + ";", ind);
	}
	
	// type - the type this expression should be
	private String expression(JsonObject expression, Function function, String type) {
		String t;
		String expressionType = varTypes.getExpression(expression.hashCode());
		
		switch (((JsonObject) expression).get(Utils.TYPE).getAsString()) {
			case Utils.VARIABLE_DECLARATION:
				t = variable_declaration(expression, function, 0, false);
				break;
			case Utils.CALL_EXPRESSION:
				t = call_expression(expression, function);
				break;
			case Utils.ASSIGNMENT_EXPRESSION:
				t = assignment_expression(expression, function);
				break;
			case Utils.LOGICAL_EXPRESSION:
				t = logical_expression(expression, function);
				break;
			case Utils.UNARY_EXPRESSION:
				t = unary_expression(expression, function);
				break;
			case Utils.UPDATE_EXPRESSION:
				t = update_expression(expression, function);
				break;
			case Utils.BINARY_EXPRESSION:
				t = binary_expression(expression, function);
				break;
			case Utils.ARRAY_EXPRESSION:
				t = array_expression(expression, function, type);
				break;
			case Utils.MEMBER_EXPRESSION:
				t = member_expression(expression, function);
				break;
			case Utils.IDENTIFIER:	
				t = identifier(expression, function);
				break;
			case Utils.LITERAL:
				t = literal(expression);
				break;
			default:
				t = "";
				break;
		}
		
		if (type.equals(Utils.BOOLEAN)) {
			if (expressionType.equals(Utils.STRING)) {
				t = "(" + t + ".equals(\"\") ? false: true" + ")";
			}
			else if (Utils.NUMERIC.contains(expressionType)) {
				t = "(" + t + "== 0 ? false: true" + ")";
			}
		}
		else if (Utils.NUMERIC.contains(type)) {
			if (expressionType.equals(Utils.STRING)) {
				t = "(" + t + ".matches(\"-?\\\\d+(\\\\.\\\\d+)?\") ? Double.parseDouble(" + t + ") : Double.MAX_VALUE" + ")";
			}
			else if (expressionType.equals(Utils.BOOLEAN)) {
				t = "(" + t + " ? 1: 0" + ")";
			}
		}
		
		return t;
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
		ArrayList<Variable> params = new ArrayList<Variable>();
		if (class_name.equals(Utils.CONSOLE) && function_name.equals(Utils.LOG)) {
			exp += "System.out.println(";
		}
		else if (function_name.equals(Utils.PUSH)) {
			exp += class_name + "." + "add(";
			params = varTypes.getParams(function_name);
		}
		else {
			exp += class_name + "." + function_name + "(";
			params = varTypes.getParams(function_name);
		}
		
		JsonArray arguments = expression.get(Utils.ARGUMENTS).getAsJsonArray();
		
		// Add type of each parameter to called function
		for (int i = 0; i < arguments.size(); i++) {
			if (i != 0) {
				if (class_name.equals(Utils.CONSOLE) && function_name.equals(Utils.LOG)) {
					exp += "System.out.println(";
				}
				else {
					exp += ", ";
				}
			}
			
			JsonObject arg = arguments.get(i).getAsJsonObject();
			if (i < params.size() && params.get(i) != null && params.get(i).getType() != null) {
				exp += expression(arg, function, params.get(i).getType());
			}
			else {
				exp += expression(arg, function, Utils.UNDEFINED);
			}
			
			if (i != arguments.size()-1 && class_name.equals(Utils.CONSOLE) && function_name.equals(Utils.LOG)) {
				exp += "); ";
			}
		}
		
		exp += ")";
		
		return exp;
	}

	private String assignment_expression(JsonObject expression, Function function) {
		String exp = "";
		String operator = expression.get(Utils.OPERATOR).getAsString();
		JsonObject left = (JsonObject) expression.get(Utils.LEFT);
		if (left.get(Utils.TYPE).getAsString().equals(Utils.MEMBER_EXPRESSION)) {
			exp += member_expression(left, function);
		}
		else {
			exp += identifier(left, function);
		}
		
		JsonObject right = (JsonObject) expression.get(Utils.RIGHT);
		exp += " " + operator + " " + expression(right, function, varTypes.getExpression(left.hashCode()));
		
		return exp;
	}
	
	private String logical_expression(JsonObject expression, Function function) {
		String operator = expression.get(Utils.OPERATOR).getAsString();		
		JsonObject left = (JsonObject) expression.get(Utils.LEFT);
		JsonObject right = (JsonObject) expression.get(Utils.RIGHT);
		String exp = "(" + expression(left, function, Utils.BOOLEAN);
		exp += " " + operator + " ";
		exp += expression(right, function, Utils.BOOLEAN) + ")";
		
		return exp;
	}
	
	private String unary_expression(JsonObject expression, Function function) {
		String exp = expression.get(Utils.OPERATOR).getAsString();
		JsonObject argument = (JsonObject) expression.get(Utils.ARGUMENT);
		exp += expression(argument, function, varTypes.getExpression(expression.hashCode()));
		
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
		
		String exp = "";
		if (operator.equals(Utils.OP_TEQ) || operator.equals(Utils.OP_TNEQ) || operator.equals(Utils.OP_EQ) || operator.equals(Utils.OP_NEQ) || operator.equals(Utils.OP_MIN)  || operator.equals(Utils.OP_MAX) || operator.equals(Utils.OP_MINEQ) || operator.equals(Utils.OP_MAXEQ)) {
			String l = varTypes.getExpression(left.hashCode());
			String r = varTypes.getExpression(right.hashCode());
			
			if (l.equals(Utils.STRING) && r.equals(Utils.STRING) && operator.equals(Utils.OP_NEQ)) {
				exp = "!(";
			}
			else {
				exp = "(";
			}
			
			// Left
			String tl = expression(left, function, Utils.UNDEFINED);
			if (l.equals(Utils.BOOLEAN)) {
				exp += "(" + tl + " ? 1: 0" + ")";
			}
			else if ((l.equals(Utils.STRING) && !r.equals(Utils.STRING))
					|| (l.equals(Utils.STRING) && r.equals(Utils.STRING) && !operator.equals(Utils.OP_EQ) && !operator.equals(Utils.OP_NEQ))) {
				exp += "(" + tl + ".matches(\"-?\\\\d+(\\\\.\\\\d+)?\") ? Double.parseDouble(" + tl + ") : Double.MAX_VALUE" + ")";
			}
			else {
				exp += tl;
			}
			
			
			if (l.equals(Utils.STRING) && r.equals(Utils.STRING) && (operator.equals(Utils.OP_EQ) || operator.equals(Utils.OP_NEQ))) {
				exp += ".equals(";
			}
			else {
				exp += " " + operator + " ";
			}
			
			
			// Right
			String tr = expression(right, function, Utils.UNDEFINED);
			if (r.equals(Utils.BOOLEAN)) {
				exp += "(" + tr + " ? 1: 0" + ")";
			}
			else if ((r.equals(Utils.STRING) && !l.equals(Utils.STRING))
					|| (l.equals(Utils.STRING) && r.equals(Utils.STRING) && !operator.equals(Utils.OP_EQ) && !operator.equals(Utils.OP_NEQ))) {
				exp += "(" + tr + ".matches(\"-?\\\\d+(\\\\.\\\\d+)?\") ? Double.parseDouble(" + tr + ") : Double.MAX_VALUE" + ")";
			}
			else {
				exp += tr;
			}
			
			if (l.equals(Utils.STRING) && r.equals(Utils.STRING) && (operator.equals(Utils.OP_EQ) || operator.equals(Utils.OP_NEQ))) {
				exp += ")";
			}
			
			exp += ")";
		}
		else {
			exp = "(" + expression(left, function, varTypes.getExpression(expression.hashCode()));
			exp += " " + operator + " ";
			exp += expression(right, function, varTypes.getExpression(expression.hashCode())) + ")";
		}
		
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
	
	private String array_expression(JsonObject expression, Function function, String objType) {
		String expType = objType;
		expType = expType.replace("[", "");
		expType = expType.replace("]", "");
		objType = toObject(expType);		
		
		String exp = "new ArrayList<" + objType + ">(){{";		
		JsonArray elements = expression.get(Utils.ELEMENTS).getAsJsonArray();
		for (int i = 0; i < elements.size(); i++) {
			JsonObject o = elements.get(i).getAsJsonObject();
			exp += "add(" + expression(o, function, expType) + "); ";
		}
		
		exp += "}}";
		
		return exp;
	}
	
	private String toObject(String expType) {
		if (expType.equals(Utils.STRING)) {
			return "String";
		}
		else if (expType.equals(Utils.BOOLEAN)) {
			return "Boolean";
		}
		else if (expType.equals(Utils.BYTE)) {
			return "Byte";
		}
		else if (expType.equals(Utils.SHORT)) {
			return "Short";
		}
		else if (expType.equals(Utils.INT)) {
			return "Integer";
		}
		else if (expType.equals(Utils.LONG)) {
			return "Long";
		}
		else if (expType.equals(Utils.FLOAT)) {
			return "Float";
		}
		else if (expType.equals(Utils.DOUBLE)) {
			return "Double";
		}
		else if (expType.equals(Utils.CHAR)) {
			return "Character";
		}
		
		return Utils.UNDEFINED;
	}
	
	private String member_expression(JsonObject expression, Function function) {
		String exp = expression.get(Utils.OBJECT).getAsJsonObject().get(Utils.NAME).getAsString();
		JsonElement name = expression.get(Utils.PROPERTY).getAsJsonObject().get(Utils.NAME);
		if (name != null) {
			if (name.getAsString().equals(Utils.LENGTH)) {
				exp += ".length";
			}
		}
		else {
			JsonObject expType = expression.get(Utils.PROPERTY).getAsJsonObject();
			exp += ".get(";
			exp += expression(expType, function, Utils.INT);
			exp += ")";
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