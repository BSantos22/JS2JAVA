package js2java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class TypeInferrer {
	private Map<String, String> vars;
	private Map<String, String> infers;
	private Map<String, ArrayList<JsonObject>> unknown_types;
	
	public TypeInferrer(JsonObject vars) {
		this.vars = createVarsMap(vars);
	}
	
	public Map<String, String> addTypes(JsonObject js) {
		unknown_types = new HashMap<String, ArrayList<JsonObject>>();
		addKnownTypes(js);
		if (!unknown_types.isEmpty()) {
			inferTypes(js);
		}
		else {
			System.out.println("No variable types to infer");
		}
		
		return vars;
	}
	
	private String addKnownTypes(JsonObject js) {
		String no_type = null;
		
		for (Map.Entry<String, JsonElement> entry: js.entrySet()) {
			if (entry.getKey().equals("type") && entry.getValue().getAsString().equals("Identifier")) {
				String var = js.get("name").getAsString();
				if (vars.containsKey(var)) {
					entry.setValue(new JsonPrimitive(vars.get(var)));
				}
				else {
					no_type = var;
				}
			}
			
			if (entry.getValue() instanceof JsonArray) {
				addKnownTypes((JsonArray) entry.getValue());
			}
			else if (entry.getValue() instanceof JsonObject) {
				String r = addKnownTypes((JsonObject) entry.getValue());
				if (r != null) {
					if (unknown_types.containsKey(r)) {
						unknown_types.get(r).add(js);
					}
					else {
						ArrayList<JsonObject> a = new ArrayList<JsonObject>();
						a.add(js);
						unknown_types.put(r, a);
					}
				}
			}
		}
		
		return no_type;
	}
	
	private void addKnownTypes(JsonArray list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof JsonObject) {
				addKnownTypes((JsonObject) list.get(i));
			}
		}
	}
	
	private Map<String, String> createVarsMap(JsonObject vars) {
		HashMap <String,String> v = new HashMap<String,String>();
		if (vars.isJsonObject()) {
			JsonArray a = (JsonArray) vars.get("vars");
			for (int i = 0; i < a.size(); i++) {
				v.put(((JsonObject) a.get(i)).get("name").getAsString(), ((JsonObject) a.get(i)).get("type").getAsString());
			}
		}
		
		return v;
	}
	
	private void inferTypes(JsonObject js) {
		Iterator<String> it = unknown_types.keySet().iterator();
		
		while(it.hasNext()) {
			String key = it.next();
			ArrayList<JsonObject> op = unknown_types.get(key);
			
			String type = inferVariable(key, op);
			if (type != null) {
				vars.put(key, type);
				infers.put(key, type);
				addKnownTypes(js);
				it.remove();
			}
			
			if (!it.hasNext()) {
				it = unknown_types.keySet().iterator();
			}
			
			
		}
	}
	
	private String inferVariable(String var_name, ArrayList<JsonObject> expr) {
		ArrayList<String> possible_types = new ArrayList<String>();
		
		for (int i = 0; i < expr.size(); i++) {
			JsonObject obj = expr.get(i).getAsJsonObject();
			String type = obj.get("type").getAsString();
			ArrayList<String> t = new ArrayList<String>();
			
			t = getTypes(var_name, obj, type);
			if (t != null) {
				possible_types.addAll(t);
			}
		}
		
		return getMaxType(possible_types);
	}
	
	private ArrayList<String> getTypes(String var_name, JsonObject expr, String expression_type) {
		ArrayList<String> types = new ArrayList<String>();
		
		for (Map.Entry<String, JsonElement> entry: expr.entrySet()) {
			if (entry.getValue().isJsonObject()) {
				ArrayList<String> a = getTypes(var_name, (JsonObject) entry.getValue(), expression_type);
				if (a == null) {
					return null;
				}
				else {
					types.addAll(a);
				}
			}
			else if (entry.getKey().equals("type")) {
				if (entry.getValue().getAsString().equals("Literal")) {
					String v = expr.get("raw").getAsString();
			
					if (v.equals("true") || v.equals("false")) {
						types.add("boolean");
					}
					else if (v.contains("\"")) {
						types.add("String");
					}
					else if (v.contains(".")) {
						types.add("double");
					}
					else {
						types.add("int");
					}
				}
				else if (entry.getValue().getAsString().equals("ArrayExpression")) {
					types.add("Object[]");
				}
				else if (entry.getValue().getAsString().equals("MemberExpression")) {					
					JsonObject isIndex = expr.get("property").getAsJsonObject();
					if (isIndex.has("name") && isIndex.get("name").getAsString().equals(var_name)) {
						types.add("int");
					}
					else {
						types.add("Object");
					}
				}
				else if (expression_type.equals("WhileStatement") || expression_type.equals("IfStatement") || expression_type.equals("LogicalExpression")) {
					types.add("boolean");
				}
				else if (entry.getValue().getAsString().equals("Identifier") && !expr.get("name").getAsString().equals(var_name)) {
					return null;
				}
				else if (expr.has("name") && !entry.getValue().getAsString().equals("Identifier")){
					types.add(entry.getValue().getAsString());
				}
			}
		}
		
		return types;
	}
	
	private String getMaxType(ArrayList<String> types) {
		if (types.contains("double")) {
			return "double";
		}
		if (types.contains("float")) {
			return "float";
		}
		if (types.contains("long")) {
			return "long";
		}
		if (types.contains("int")) {
			return "int";
		}
		if (types.contains("short")) {
			return "short";
		}
		if (types.contains("char")) {
			return "char";
		}
		if (types.contains("byte")) {
			return "byte";
		}
		if (types.contains("boolean")) {
			return "boolean";
		}
		if (types.contains("String")) {
			return "String";
		}
		if (types.contains("Object")) {
			return "Object";
		}
		if (types.contains("Object[]")) {
			return "Object[]";
		}
		
		return null;
	}
	
	public void printTypes() {
		Iterator<String> it = vars.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			String type = vars.get(key);
			if(type != null)
				System.out.println(key + " - " + type);
		}
	}
	
	public void printInfers() {
		Iterator<String> it = infers.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			String type = infers.get(key);
			if(type != null)
				System.out.println(key + " - " + type);
		}
	}
}
