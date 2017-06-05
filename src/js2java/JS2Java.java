package js2java;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.JsonObject;

import utils.Utils;
import variables.Function;
import variables.Variable;

public class JS2Java {
	public JS2Java() {}
	
	public static void main(String args[]) {
		String varsFilepath;
		String jsFilepath;
		String txtFilepath;
		String dir;
		
		if (args.length == 2) {
			jsFilepath = args[0];
			txtFilepath = jsFilepath + ".txt";
			jsFilepath += ".json";
			varsFilepath = args[1] + ".json";
		}
		else if (args.length == 1) {
			jsFilepath = args[0];
			txtFilepath = jsFilepath + ".txt";
			jsFilepath += ".json";
			varsFilepath = null;
		}
		else {
			Scanner reader = new Scanner(System.in);
			System.out.println("Javascript code file name: ");
			jsFilepath = reader.nextLine();
			txtFilepath = jsFilepath + ".txt";
			jsFilepath += ".json";
			dir = checkFileDirectory(jsFilepath);
			while(dir == "") {
				System.out.println("\nCouldn't find given JavaScript code file, try again");
				System.out.println("Javascript code file name: ");
				jsFilepath = reader.nextLine();
				jsFilepath += ".json";
				dir = checkFileDirectory(jsFilepath);
			}
			System.out.println("Variable types file name: ");
			varsFilepath = reader.nextLine() + ".json";
			reader.close();
		}
		
		dir = checkFileDirectory(jsFilepath);
		
		File jsFile = new File(dir + "/" + jsFilepath);
		JsonReader parser = new JsonReader();
		JsonObject js = parser.parse(jsFile);
		
		JsonObject vars;
		if (varsFilepath != null) {
			File varsFile = new File("files/" + varsFilepath);
			try {
				vars = parser.parse(varsFile);
			}
			catch (Exception e) {
				System.err.println("Invalid types file. All variable types will be inferred.");
				vars = null;
			}
		}
		else {
			vars = null;
		}
		
		TypeInferrer inferrer = new TypeInferrer(js, vars);
		if (!checkVariables(inferrer)) {
			return;
		}
		
		String jsCode;
		dir = checkFileDirectory(txtFilepath);
		if (dir == "") {
			jsCode = null;
		}
		else {
			try {
				
				jsCode = new String(Files.readAllBytes(Paths.get(dir + "/" + txtFilepath)), StandardCharsets.UTF_8);
			} catch (IOException e) {
				jsCode = null;
			}
		}
		
		Output output = new Output(js, inferrer, jsFilepath.replace(".json", ""));
		output.start(jsCode);
		
		System.out.println("Generation finished.");
	}
	
	public static String checkFileDirectory(String filename) {
		String dir = "";
		File files = new File("files");
		File[] filesFolders = files.listFiles();
		File[] jsonFiles;
		
		for(int i=0; i < filesFolders.length; i++) {
			if(filesFolders[i].isDirectory()) {
				jsonFiles = filesFolders[i].listFiles();
				
				for(int k=0; k < jsonFiles.length; k++) {
					if(jsonFiles[k].getName().equals(filename)) {
						dir = filesFolders[i].getPath();
						return dir;
					}
				}
			}
		}
		
		
		return "";
	}
	
	public static boolean checkVariables(TypeInferrer inferrer) {
		boolean valid = true;
		for (Function f: inferrer.getFunctions()) {
			ArrayList<Variable> allVariables = new ArrayList<Variable>();
			allVariables.addAll(f.getDeclared());
			allVariables.addAll(f.getUsed());
			allVariables.addAll(f.getParameters());
			
			for (Variable v: allVariables) {
				if (v.getType().equals(Utils.UNDEFINED) || v.getType().equals(Utils.AMBIGUOUS)) {
					String function_name;
					if (f.getName().equals("global")) {
						function_name = "main";
					}
					else {
						function_name = v.getName();
					}
					
					System.err.println("Error: variable \"" + v.getName() + "\" in function \"" + function_name + "\" is " + v.getType());
					valid = false;
				}
			}
		}
		
		return valid;
	}
}
