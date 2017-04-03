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
	private Map<String, ArrayList<JsonObject>> unknown_types;
	
	public TypeInferrer(JsonObject vars) {
		this.vars = createVarsMap(vars);
	}
	
	public void addTypes(JsonObject js) {
		unknown_types = new HashMap<String, ArrayList<JsonObject>>();
		addKnownTypes(js);
		if (!unknown_types.isEmpty()) {
			inferTypes(js);
		}
		else {
			System.out.println("No variable types to infer");
		}
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
				addKnownTypes(js);
				unknown_types.remove(key);
				System.out.println(key + " - " + type);
			}
			
			if (!it.hasNext()) {
				it = unknown_types.keySet().iterator();
			}
		}
	}
	
	private String inferVariable(String var_name, ArrayList<JsonObject> expr) {
		ArrayList<String> possible_types = new ArrayList<String>();
		
		for (int i = 0; i < expr.size(); i++) {
			String type = expr.get(i).getAsJsonObject().get("type").getAsString();
			
			switch (type) {
			case "VariableDeclarator":
				ArrayList<String> vd = new ArrayList<String>();
				JsonElement init_vd = expr.get(i).getAsJsonObject().get("init");
				if (init_vd.isJsonObject()) {
					vd = getTypes(var_name, init_vd.getAsJsonObject());
				}
				if (vd == null) {
					return null;
				}
				else {
					possible_types.addAll(vd);
				}
				break;
			case "AssignmentExpression":
				ArrayList<String> ae = new ArrayList<String>();
				JsonElement right_ae = expr.get(i).getAsJsonObject().get("right");
				if(right_ae.isJsonObject()) {
					ae = getTypes(var_name, right_ae.getAsJsonObject());
				}
				
				if(ae == null) return null;
				else possible_types.addAll(ae);
				
				break;
			}
			
		}
		
		return getMaxType(possible_types);
	}
	
	private ArrayList<String> getTypes(String var_name, JsonObject expr) {
		ArrayList<String> types = new ArrayList<String>();
		
		for (Map.Entry<String, JsonElement> entry: expr.entrySet()) {
			if (entry.getValue().isJsonObject()) {
				ArrayList<String> a = getTypes(var_name, (JsonObject) entry.getValue());
				if (a == null) {
					return null;
				}
				else {
					types.addAll(a);
				}
			}
			else if (entry.getKey().equals("type")) {
				if (entry.getValue().getAsString().equals("Identifier") && !expr.get("name").getAsString().equals(var_name)) {
					return null;
				}
				else if (entry.getValue().getAsString().equals("Literal")) {
					String v = expr.get("raw").getAsString();
			
					if (v.equals("true") || v.equals("false")) {
						types.add("boolean");
					}
					else if (v.contains(".")) {
						types.add("double");
					}
					else {
						types.add("int");
					}
				}
				else if (expr.has("name")){
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
		
		return null;
	}
}
