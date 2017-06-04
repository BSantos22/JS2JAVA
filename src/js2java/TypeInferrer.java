package js2java;

import java.util.ArrayList;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import variables.Variable;
import variables.Function;
import utils.Utils;

public class TypeInferrer {	
	private ArrayList<Function> functions;
	private ArrayList<Variable> defined;
	
	public TypeInferrer(JsonObject js, JsonObject vars) {
		functions = new ArrayList<Function>();
		defined = new ArrayList<Variable>();
		
		addTypeDef(vars);
		Function global = new Function("global", null);
		functions.add(global);
		addDeclaredVariables(js, global);
		inferScope(global);
		
		String prev_variables = "";
		String variables = "";
		do {
			prev_variables = variables;
			inferTypes(js);
			variables = functions.toString();
		} while(!prev_variables.equals(variables));
	}
	
	public ArrayList<Function> getFunctions() {
		return functions;
	}
	
	// Adds variables in type definition file to defined
	// Ignores variables of invalid type
	public void addTypeDef(JsonObject vars) {
		JsonArray a = (JsonArray) vars.get(Utils.VARS);
		for (int i = 0; i < a.size(); i++) {
			String name = ((JsonObject) a.get(i)).get(Utils.NAME).getAsString();
			String type = ((JsonObject) a.get(i)).get(Utils.TYPE).getAsString();
			
			if (Utils.isValidType(type)) {
				defined.add(new Variable(name));
			}
		}
	}
	
	// Adds the variables declared inside of each function
	// Recursive processes the entire tree looking for functions and variables
	public void addDeclaredVariables(JsonElement node, Function function) {
		if (node instanceof JsonArray) {
			JsonArray array = (JsonArray) node;
			for (int i = 0; i < array.size(); i++) {
				addDeclaredVariables(array.get(i), function);
			}
		}
		else if (node instanceof JsonObject) {
			JsonObject object = (JsonObject) node;
			for (Map.Entry<String, JsonElement> entry: object.entrySet()) {
				String type = "";
				if (entry.getKey().toString().equals(Utils.TYPE)) {
					type = entry.getValue().getAsString();
				}
				else if (entry.getKey().toString().equals(Utils.CALLEE)) { // ignore function calls identifiers
					continue;
				}
				// Ignore array length calls
				else if (object.get(Utils.TYPE).getAsString().equals(Utils.MEMBER_EXPRESSION) && entry.getKey().equals(Utils.PROPERTY) && entry.getValue().getAsJsonObject().get(Utils.NAME).getAsString().equals(Utils.LENGTH)) {
					continue;
				}
				else {
					addDeclaredVariables(object.get(entry.getKey()), function);
				}
				
				
				// Create new functions from function_declarations
				if (type.equals(Utils.FUNCTION_DECLARATION)) {
					String name = ((JsonObject) object.get(Utils.ID)).get(Utils.NAME).getAsString();
					Function next_function = new Function(name, function);
					
					// Get new_function's parameters
					JsonArray array = (JsonArray) object.get(Utils.PARAMS);
					for (int i = 0; i < array.size(); i++) {
						String param_name = ((JsonObject) array.get(i)).get(Utils.NAME).getAsString();
						next_function.addParamaters(new Variable(param_name));
					}
					
					functions.add(next_function);
					addDeclaredVariables(object.get(Utils.BODY), next_function);
					return;
				}
				
				// Add variable declarations to the current function
				else if (type.equals(Utils.VARIABLE_DECLARATOR)) {
					String name = ((JsonObject) object.get(Utils.ID)).get(Utils.NAME).getAsString();
					Variable v = new Variable(name);
					if (!function.getDeclared().contains(v)) {
						function.addDeclaredVariable(v);
					}
				}
				
				// Add variables used to the current function				
				else if (type.equals(Utils.IDENTIFIER) && !node.toString().equals(Utils.ID)) {
					String name = object.get(Utils.NAME).getAsString();
					Variable v = new Variable(name);
					function.addUsedVariable(v);
				}
			}
		}
	}
	
	// Remove duplicate variables (same variable become same object)
	public void inferScope(Function f) {
		for (Variable v: f.getUsed()) {
			// find variable declaration for variable in this function and its parents
			Function scope = f;
			boolean found = false;
			while (scope != null) {
				for (Variable d: scope.getDeclared()) {
					if (d.equals(v)) {
						f.replaceUsedVariable(v, d);
						found = true;
						break;
					}
				}
				
				if (found) {
					break;
				}
				
				scope = scope.getParent();
			}
			
			if (!found && scope == null && !f.getParameters().contains(v)) {
				f.addDeclaredVariable(v);
			}
			
		}
		
		// replace used and declared variables with corresponding parameters
		for (Variable p: f.getParameters()) {
			for (Variable u: f.getUsed()) {
				if (u.equals(p)) {
					f.replaceUsedVariable(u, p);
				}
			}
			
			for (Variable d: f.getDeclared()) {
				if (d.equals(p)) {
					f.replaceDeclaredVariable(d, p);
				}
			}
		}
		
		for (Function child: f.getChildren()) {
			inferScope(child);
		}
	}

	// Infer types for each use of a variable
	public void inferTypes(JsonObject js) {		
		if (js.get(Utils.TYPE).getAsString().equals(Utils.PROGRAM)) {
			block((JsonArray) js.get(Utils.BODY), functions.get(0));
		}
	}
	
	private void block(JsonArray content, Function function) {
		if (content.isJsonNull()) {
			return;
		}
		
		for (JsonElement expression: content) {			
			switch (((JsonObject) expression).get(Utils.TYPE).getAsString()) {
				case Utils.EXPRESSION_STATEMENT:
					expression_statement((JsonObject) expression, function);
					break;
				case Utils.VARIABLE_DECLARATION:
					variable_declaration((JsonObject) expression, function);
					break;
				case Utils.IF_STATEMENT:
					if_statement((JsonObject) expression, function);
					break;
				case Utils.WHILE_STATEMENT:
					while_statement((JsonObject) expression, function);
					break;
				case Utils.DOWHILE_STATEMENT:
					dowhile_statement((JsonObject) expression, function);
					break;
				case Utils.FOR_STATEMENT:
					for_statement((JsonObject) expression, function);
					break;
				case Utils.FUNCTION_DECLARATION:
					function_declaration((JsonObject) expression, function);
					break;
				case Utils.RETURN_STATEMENT:
					return_statement((JsonObject) expression, function);
					break;
			}
		}
	}
	
	private void expression_statement(JsonObject statement, Function function) {
		JsonObject expression = statement.get(Utils.EXPRESSION).getAsJsonObject();
		if (expression.get(Utils.TYPE).getAsString().equals(Utils.SEQUENCE_EXPRESSION)) {
			JsonArray exs = expression.get(Utils.EXPRESSIONS).getAsJsonArray();
			for (JsonElement asgn: exs) {
				assignment_expression((JsonObject) asgn, function);
			}
		}
		else if (expression.get(Utils.TYPE).getAsString().equals(Utils.CALL_EXPRESSION)) {
			call_expression(expression, function);
		}
		else {
			assignment_expression(expression, function);
		}
	}
	
	private String variable_declaration(JsonObject expression, Function function) {
		JsonArray declarations = expression.get(Utils.DECLARATIONS).getAsJsonArray();
		
		for (JsonElement declaration: declarations) {
			JsonObject dec = declaration.getAsJsonObject();
			
			if (dec.get(Utils.TYPE).getAsString().equals(Utils.VARIABLE_DECLARATOR)) {
				JsonObject id = (JsonObject) dec.get(Utils.ID);
				Variable assign;
				if (id.get(Utils.TYPE).getAsString().equals(Utils.IDENTIFIER)) {
					assign = identifier(id, function);
				}
				else {
					continue;
				}
				
				if (dec.get(Utils.INIT).isJsonNull()) {
					assign.addType(Utils.UNDEFINED);
					continue;
				}
				
				JsonObject init = (JsonObject) dec.get(Utils.INIT);
				String type = expression(init, function);
				
				assign.addType(type);
			}	
		}
		
		return Utils.UNDEFINED;
	}
	
	private void if_statement(JsonObject expression, Function function) {
		// Condition
		JsonObject test = expression.get(Utils.TEST).getAsJsonObject();
		expression(test, function);
		
		// Then
		JsonObject consequent = expression.get(Utils.CONSEQUENT).getAsJsonObject();
		if (consequent.get(Utils.TYPE).getAsString().equals(Utils.BLOCK_STATEMENT)) {
			block(consequent.get(Utils.BODY).getAsJsonArray(), function);
		}
		
		// Else
		if (!expression.get(Utils.ALTERNATE).isJsonNull()) {
			JsonObject alternate = expression.get(Utils.ALTERNATE).getAsJsonObject();
			if (alternate.get(Utils.TYPE).getAsString().equals(Utils.BLOCK_STATEMENT)) {
				block(alternate.get(Utils.BODY).getAsJsonArray(), function);
			}
			// Else if
			else if (alternate.get(Utils.TYPE).getAsString().equals(Utils.IF_STATEMENT)) {
				if_statement(alternate, function);
			}
		}
	}
	
	private void while_statement(JsonObject expression, Function function) {
		// Condition
		JsonObject test = expression.get(Utils.TEST).getAsJsonObject();
		expression(test, function);
		
		// Then
		JsonObject body = expression.get(Utils.BODY).getAsJsonObject();
		if (body.get(Utils.TYPE).getAsString().equals(Utils.BLOCK_STATEMENT)) {
			block(body.get(Utils.BODY).getAsJsonArray(), function);
		}
	}
	
	private void dowhile_statement(JsonObject expression, Function function) {
		// Condition
		JsonObject test = expression.get(Utils.TEST).getAsJsonObject();
		expression(test, function);
		
		// Then
		JsonObject body = expression.get(Utils.BODY).getAsJsonObject();
		if (body.get(Utils.TYPE).getAsString().equals(Utils.BLOCK_STATEMENT)) {
			block(body.get(Utils.BODY).getAsJsonArray(), function);
		}
	}
	
	private void for_statement(JsonObject expression, Function function) {
		// Init
		JsonObject init = expression.get(Utils.INIT).getAsJsonObject();
		expression(init, function);
		
		// Condition
		JsonObject test = expression.get(Utils.TEST).getAsJsonObject();
		expression(test, function);
		
		// Update
		JsonObject update = expression.get(Utils.UPDATE).getAsJsonObject();
		expression(update, function);
		
		// Body
		JsonObject body = expression.get(Utils.BODY).getAsJsonObject();
		if (body.get(Utils.TYPE).getAsString().equals(Utils.BLOCK_STATEMENT)) {
			block(body.get(Utils.BODY).getAsJsonArray(), function);
		}
	}
	
	private void function_declaration(JsonObject expression, Function function) {
		String name = expression.get(Utils.ID).getAsJsonObject().get(Utils.NAME).getAsString();
		
		Function new_function = null;
		for (Function f: functions) {
			if (f.getName().equals(name)) {
				new_function = f;
			}
		}
		
		if (new_function != null) {
			/*
			JsonArray params = expression.get(Utils.PARAMS).getAsJsonArray();
			for (JsonElement e : params) {
				Variable param = new_function.getParameter(e.getAsJsonObject().get(Utils.NAME).getAsString());				
			}
			*/
			
			JsonObject body = expression.get(Utils.BODY).getAsJsonObject();
			if (body.get(Utils.TYPE).getAsString().equals(Utils.BLOCK_STATEMENT)) {
				block(body.get(Utils.BODY).getAsJsonArray(), new_function);
			}
		}
	}
	
	private void return_statement(JsonObject expression, Function function) {
		JsonObject argument = expression.get(Utils.ARGUMENT).getAsJsonObject();
		String return_type = expression(argument, function);
		function.addReturn(return_type);
	}
	
	private String expression(JsonObject expression, Function function) {
		switch (((JsonObject) expression).get(Utils.TYPE).getAsString()) {
			case Utils.VARIABLE_DECLARATION:
				return variable_declaration(expression, function);
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
				String type = member_expression(expression, function).getType();
				if (type.contains("[]")) {
					type = type.replaceAll("\\[", "");
					type = type.replaceAll("\\]", "");
				}
				return type;
			case Utils.IDENTIFIER:	
				return identifier(expression, function).getType();
			case Utils.LITERAL:
				return literal(expression);				
			default:
				return Utils.UNDEFINED;
		}
	}
	
	private String call_expression(JsonObject expression, Function function) {
		String function_name;
		if (expression.get(Utils.CALLEE).getAsJsonObject().get(Utils.NAME) == null) {
			function_name = "";
		}
		else {
			function_name = expression.get(Utils.CALLEE).getAsJsonObject().get(Utils.NAME).getAsString();
		}
		
		JsonArray args = expression.get(Utils.ARGUMENTS).getAsJsonArray();
		for (JsonElement arg: args) {
			expression(arg.getAsJsonObject(), function);
		}
		
		for (Function f: functions) {
			if (f.getName().equals(function_name)) {
				JsonArray arguments = expression.get(Utils.ARGUMENTS).getAsJsonArray();
				
				// Add type of each parameter to called function
				for (int i = 0; i < arguments.size(); i++) {
					f.getParameter(i).addType(expression(arguments.get(i).getAsJsonObject(), function));
				}
				
				return f.getReturnType();
			}
		}
		
		return Utils.UNDEFINED;
	}
	
	private String assignment_expression(JsonObject expression, Function function) {		
		JsonObject left = (JsonObject) expression.get(Utils.LEFT);
		Variable assign;
		if (left.get(Utils.TYPE).getAsString().equals(Utils.MEMBER_EXPRESSION)) {
			assign = member_expression(left, function);
		}
		else {
			assign = identifier(left, function);
		}
		
		JsonObject right = (JsonObject) expression.get(Utils.RIGHT);
		String type = expression(right, function);
		
		assign.addType(type);
		
		return type;
	}
	
	private String logical_expression(JsonObject expression, Function function) {
		return Utils.BOOLEAN;
	}
	
	private String unary_expression(JsonObject expression, Function function) {
		String operator = expression.get(Utils.OPERATOR).getAsString();
		JsonObject argument = expression.get(Utils.ARGUMENT).getAsJsonObject();
		if (operator.equals(Utils.OP_EXC)) {
			return Utils.BOOLEAN;
		}
		else if (operator.equals(Utils.OP_DIF)) {
			return expression(argument, function);
		}
		
		return Utils.UNDEFINED;
	}

	private String update_expression(JsonObject expression, Function function) {
		String operator = expression.get(Utils.OPERATOR).getAsString();
		JsonObject argument = expression.get(Utils.ARGUMENT).getAsJsonObject();
		if (operator.equals(Utils.OP_INC) || operator.equals(Utils.OP_DEC)) {
			return variable(argument, function);
		}
		
		return Utils.UNDEFINED;
	}
	
	private String binary_expression(JsonObject expression, Function function) {
		String operator = expression.get(Utils.OPERATOR).getAsString();
		if (operator.equals(Utils.OP_EQ) || operator.equals(Utils.OP_NEQ) || operator.equals(Utils.OP_MIN)  || operator.equals(Utils.OP_MAX) || operator.equals(Utils.OP_MINEQ) || operator.equals(Utils.OP_MAXEQ)) {
			return Utils.BOOLEAN;
		}
		
		JsonObject left = (JsonObject) expression.get(Utils.LEFT);
		JsonObject right = (JsonObject) expression.get(Utils.RIGHT);
		String type1 = expression(left, function);
		String type2 = expression(right, function);
		
		// Get lowest representation for a numeric operation
		int priority1 = Utils.NUMERIC.indexOf(type1);
		int priority2 = Utils.NUMERIC.indexOf(type2);
		if (priority1 != -1 && priority2 != -1) {
			if (operator.equals(Utils.OP_DIV)) {
				if (!type1.equals(Utils.DOUBLE) && !type2.equals(Utils.DOUBLE)) {
					return Utils.FLOAT;					
				}
				else {
					return Utils.DOUBLE;
				}
			}
			
			if (priority1 > priority2) {
				return type1;
			}
			else {
				return type2;
			}
		}
		
		// Boolean values are treated as numeric on +, -, *, /
		if ((Utils.NUMERIC.contains(type1) && type2.equals(Utils.BOOLEAN))
				|| (Utils.NUMERIC.contains(type2) && type1.equals(Utils.BOOLEAN))
				|| (type1.equals(Utils.BOOLEAN) && type2.equals(Utils.BOOLEAN))) {
			
			return Utils.INT;
		}
		
		// Strings
		if ((type1.equals(Utils.STRING) || type2.equals(Utils.STRING))) {
			if (operator.equals(Utils.OP_SUM)) {
				return Utils.STRING;
			}
			else if (operator.equals(Utils.OP_DIV) || operator.equals(Utils.OP_DIF) || operator.equals(Utils.OP_MUL)) {
				return Utils.DOUBLE;
			}
		}
		
		return Utils.UNDEFINED;
	}
	
	private String variable(JsonObject argument, Function function) {
		if (argument.get(Utils.TYPE).getAsString().equals(Utils.MEMBER_EXPRESSION)) {
			String type = member_expression(argument, function).getType();
			if (type.contains("[]")) {
				type = type.replaceAll("\\[", "");
				type = type.replaceAll("\\]", "");
			}
			
			if (type.equals(Utils.STRING) || type.equals(Utils.BOOLEAN) || type.equals(Utils.CHAR)) {
				return Utils.UNDEFINED;
			}
			else {
				return type;
			}
		}
		else if (argument.get(Utils.TYPE).getAsString().equals(Utils.IDENTIFIER)) {
			String type = identifier(argument, function).getType();
			if (type.equals(Utils.STRING) || type.equals(Utils.BOOLEAN) || type.equals(Utils.CHAR)) {
				return Utils.UNDEFINED;
			}
			else {
				return type;
			}
		}
		
		return Utils.UNDEFINED;
	}
	
	public String array_expression(JsonObject expression, Function function) {
		JsonArray elements = expression.get(Utils.ELEMENTS).getAsJsonArray();
		Variable temp = new Variable("temp");
		for (JsonElement e: elements) {
			JsonObject o = (JsonObject) e;
			String type = expression(o, function);
			temp.addType(type);
		}
		String type;
		
		if(temp.getType() == "int")
			type = "Integer";
		else
			type = temp.getType();
		
		return "ArrayList<" + type + ">";
	}
	
	private Variable member_expression(JsonObject expression, Function function) {
		String name = expression.get(Utils.OBJECT).getAsJsonObject().get(Utils.NAME).getAsString();
		return function.getVariable(name);
	}
	
	private Variable identifier(JsonObject expression, Function function) {
		String identifier = expression.get(Utils.NAME).getAsString();
		return function.getVariable(identifier);
	}
	
	private String literal(JsonObject expression) {
		String value = expression.get(Utils.RAW).getAsString();
		
		if (value.contains("\"") || value.contains("\'")) {
			return Utils.STRING;
		}
		else if (value.equals("true") || value.equals("false")) {
			return Utils.BOOLEAN;
		}
		else {
			try {
				Integer.parseInt(value);
				return Utils.INT;
			}
			catch (NumberFormatException e) {
				try {
					Double.parseDouble(value);
					return Utils.DOUBLE;
				}
				catch (NumberFormatException ee) {
					return Utils.UNDEFINED;
				}
			}
			
		}
	}
}
