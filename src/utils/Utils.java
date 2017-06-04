package utils;

import java.util.ArrayList;
import java.util.Arrays;

public class Utils {
	// JS functions
	public static final String CONSOLE = "console";
	public static final String LENGTH = "length";
	public static final String LOG = "log";
	
	// Common types
	public static final String STRING = "string";
	public static final String BOOLEAN = "boolean";
	
	// JS types
	public static final String NUMBER = "number";
	public static final String NULL = "null";
	public static final String UNDEFINED = "undefined";

	// Java types
	public static final String BYTE = "byte";
	public static final String SHORT = "short";
	public static final String INT = "int";
	public static final String LONG = "long";
	public static final String FLOAT = "float";
	public static final String DOUBLE = "double";
	public static final String CHAR = "char";
	public static final ArrayList<String> NUMERIC = new ArrayList<String>(Arrays.asList(BYTE, SHORT, INT, LONG, FLOAT, DOUBLE));
	
	public static final String DYNAMIC = "Dynamic";
	
	// Check Java valid type
	public static boolean isValidType(String s) {
		ArrayList<String> types = new ArrayList<>(Arrays.asList(STRING, BOOLEAN, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, CHAR));
		if (types.contains(s)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	// AST Blocks and Tokens
	public static final String VARS = "vars";
	public static final String NAME = "name";
	public static final String TYPE = "type";
	public static final String ID = "id";
	public static final String BODY = "body";
	public static final String PARAMS = "params";
	public static final String CALLEE = "callee";
	public static final String PROGRAM = "Program";
	public static final String EXPRESSION = "expression";
	public static final String EXPRESSIONS = "expressions";
	public static final String OPERATOR = "operator";
	public static final String LEFT = "left";
	public static final String RIGHT = "right";
	public static final String OBJECT = "object";
	public static final String ARGUMENT = "argument";
	public static final String RAW = "raw";
	public static final String DECLARATIONS = "declarations";
	public static final String INIT = "init";
	public static final String ELEMENTS = "elements";
	public static final String TEST = "test";
	public static final String CONSEQUENT = "consequent";
	public static final String ALTERNATE = "alternate";
	public static final String UPDATE = "update";
	public static final String ARGUMENTS = "arguments";
	public static final String PROPERTY = "property";
	
	public static final String IDENTIFIER = "Identifier";
	public static final String LITERAL = "Literal";
	
	public static final String FUNCTION_DECLARATION = "FunctionDeclaration";
	public static final String VARIABLE_DECLARATION = "VariableDeclaration";
	public static final String VARIABLE_DECLARATOR = "VariableDeclarator";
	
	public static final String EXPRESSION_STATEMENT = "ExpressionStatement";
	public static final String IF_STATEMENT = "IfStatement";
	public static final String WHILE_STATEMENT = "WhileStatement";
	public static final String DOWHILE_STATEMENT = "DoWhileStatement";
	public static final String FOR_STATEMENT = "ForStatement";
	public static final String RETURN_STATEMENT = "ReturnStatement";
	public static final String BLOCK_STATEMENT= "BlockStatement";
	
	public static final String ASSIGNMENT_EXPRESSION = "AssignmentExpression";
	public static final String SEQUENCE_EXPRESSION = "SequenceExpression";
	public static final String MEMBER_EXPRESSION = "MemberExpression";
	public static final String ARRAY_EXPRESSION = "ArrayExpression";
	
	public static final String CALL_EXPRESSION = "CallExpression";
	public static final String LOGICAL_EXPRESSION = "LogicalExpression";
	public static final String UNARY_EXPRESSION = "UnaryExpression";
	public static final String UPDATE_EXPRESSION = "UpdateExpression";
	public static final String BINARY_EXPRESSION = "BinaryExpression";
	
	// AST OPERATORS
	public static final String OP_EXC = "!";
	public static final String OP_INC = "++";
	public static final String OP_DEC = "--";
	public static final String OP_EQ = "==";
	public static final String OP_NEQ = "!=";
	public static final String OP_MAX = ">";
	public static final String OP_MIN = "<";
	public static final String OP_MAXEQ = ">=";
	public static final String OP_MINEQ = "<=";
	public static final String OP_SUM = "+";
	public static final String OP_DIF = "-";
	public static final String OP_MUL = "*";
	public static final String OP_DIV = "/";
	
}
