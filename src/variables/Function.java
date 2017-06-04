package variables;

import java.util.ArrayList;

import utils.Utils;

public class Function {
	private String name;
	private Function parent;
	private ArrayList<Function> children;
	private ArrayList<Variable> declared; // variables declared within this function
	private ArrayList<Variable> used; // variables used in this function
	private ArrayList<Variable> parameters;
	private ArrayList<String> returns;
	
	public Function(String name, Function parent) {
		this.name = name;
		this.parent = parent;
		children = new ArrayList<Function>();
		declared = new ArrayList<Variable>();
		used = new ArrayList<Variable>();
		parameters = new ArrayList<Variable>();
		returns = new ArrayList<String>();
		if (parent != null) {
			parent.addChild(this);
		}
	}
	
	// Get
	public String getName() { return name; }
	public Function getParent() { return parent; }
	public ArrayList<Function> getChildren() { return children; }
	public ArrayList<Variable> getDeclared() { return declared; }
	public ArrayList<Variable> getUsed() { return used; }
	public ArrayList<Variable> getParameters() { return parameters; }
	public ArrayList<Variable> getReturns() { return parameters; }
	public Variable getVariable(String name) {
		for (Variable v: used) {
			if (v.equals(name)) {
				return v;
			}
		}
		
		return null;
	}
	
	public Variable getParameter(String name) {
		for (Variable v: parameters) {
			if (v.equals(name)) {
				return v;
			}
		}
		
		return null;
	}
	
	public Variable getParameter(int i) {
		return parameters.get(i);
	}
	
	// Get actual type the return is going to be
	public String getReturnType() {
		int number = 0, bool = 0, str = 0, ch = 0, array = 0;
		
		String type_name = Utils.UNDEFINED;
		int priority_status = -1;
		for (String s: returns) {
			if (s.equals(Utils.STRING)) {
				str = 1;
				type_name = s;
			}
			else if (s.equals(Utils.BOOLEAN)) {
				bool = 1;
				type_name = s;
			}
			else if (s.equals(Utils.CHAR)) {
				ch = 1;
				type_name = s;
			}			
			else if (Utils.NUMERIC.contains(s) && Utils.NUMERIC.indexOf(s) > priority_status) {
				number = 1;
				type_name = s;
				priority_status = Utils.NUMERIC.indexOf(s);
			}
			else if (s.contains("[]")) {
				array = 1;
				String array_type = s.replaceAll("\\[", "");
				array_type = s.replaceAll("\\]", "");
				if (!Utils.NUMERIC.contains(array_type)) {
					type_name = s;
				}
				else if (Utils.NUMERIC.contains(array_type) && Utils.NUMERIC.indexOf(array_type) > priority_status) {
					type_name = s;
					priority_status = Utils.NUMERIC.indexOf(s);
				}
			}
		}
		
		if (number + bool + str + ch + array> 1) {
			return Utils.DYNAMIC;
		}
		else {
			return type_name;
		}
	}
	
	
	// Add variables
	public void addChild(Function f) { children.add(f); }
	public void addDeclaredVariable(Variable v) { declared.add(v); }
	public void addUsedVariable(Variable v) { used.add(v); }
	public void addParamaters(Variable v) { parameters.add(v); }
	public void addReturn(String r) { returns.add(r); }
	
	// Remove
	public void removeUsedVariable(Variable v) { used.remove(v); }
	
	// Replace
	public void replaceUsedVariable(Variable oldVar, Variable newVar) {
		for (int i = 0; i < used.size(); i++) {
			if (used.get(i).equals(oldVar)) {
				used.set(i, newVar);
			}
		}
	}
	
	public void replaceDeclaredVariable(Variable oldVar, Variable newVar) {
		for (int i = 0; i < declared.size(); i++) {
			if (declared.get(i).equals(oldVar)) {
				declared.set(i, newVar);
			}
		}
	}
	
	// Utils
	public String toString() {
		String s = name + "\n";
		s += "Declared: " + declared + "\n";
		s += "Used: " + used + "\n";
		s += "Param: " + parameters + "\n";
		return s;
	}
}
