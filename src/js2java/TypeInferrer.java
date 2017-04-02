package js2java;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class TypeInferrer {
	private Map<String, String> vars;
	
	public TypeInferrer(JsonObject vars) {
		this.vars = createVarsMap(vars);
	}
	
	public void addTypes(JsonObject js) {
		addKnownTypes(js);
		
		//inferTypes(js);
	}
	
	private void addKnownTypes(JsonObject js) {
		
		for (Map.Entry<String, JsonElement> entry: js.entrySet()) {
			if (entry.getKey().equals("type") && entry.getValue().getAsString().equals("Identifier")) {
				String var = js.get("name").getAsString();
				if (vars.containsKey(var)) {
					entry.setValue(new JsonPrimitive(vars.get(var)));
				}
			}
			
			if (entry.getValue() instanceof JsonArray) {
				addKnownTypes((JsonArray) entry.getValue());
			}
			else if (entry.getValue() instanceof JsonObject) {
				addKnownTypes((JsonObject) entry.getValue());
			}
		}
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
		
	}
}
