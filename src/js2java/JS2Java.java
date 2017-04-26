package js2java;

import java.io.File;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.JsonObject;

public class JS2Java {
	public JS2Java() {}
	
	public static void main(String args[]) {
		String varsFilepath;
		String jsFilepath;
		
		if (args.length != 2) {
			Scanner reader = new Scanner(System.in);
			System.out.println("Javascript code file name: ");
			jsFilepath = reader.nextLine();
			jsFilepath += ".json";
			System.out.println("Variable types file name: ");
			varsFilepath = reader.nextLine();
			reader.close();
		}
		else {
			jsFilepath = args[0];
			varsFilepath = args[1];
		}
		
		String dir = checkFileDirectory(jsFilepath);
		if(dir == "") {
			System.out.println("\nCouldn't find such a file");
			return;
		}
		
		File jsFile = new File(dir + "/" + jsFilepath);
		JsonReader parser = new JsonReader();
		JsonObject js = parser.parse(jsFile);
		
		/*
		File jsFile = new File("files/" + jsFilepath + ".json");
		JsonReader parser = new JsonReader();
		JsonObject js = parser.parse(jsFile);
		*/
		
		File varsFile = new File("files/" + varsFilepath + ".json");
		JsonObject vars = parser.parse(varsFile);
		
		TypeInferrer inferrer = new TypeInferrer(vars);
		inferrer.addTypes(js);
		inferrer.printInfers();
	}
	
	public Map<String, String> test(String jsFile, String typesFile) {
		JsonReader parser = new JsonReader();
		JsonObject js = parser.parse(new File(jsFile));
		JsonObject vars = parser.parse(new File(typesFile));
		TypeInferrer inferrer = new TypeInferrer(vars);
		return inferrer.addTypes(js);
	}
	
	public static String checkFileDirectory(String filename) {
		System.out.println("\n\tChecking for " + filename + "\n");
		String dir = "";
		File files = new File("files");
		File[] filesFolders = files.listFiles();
		File[] jsonFiles;
		
		for(int i=0; i < filesFolders.length; i++) {
			if(filesFolders[i].isDirectory()) {
				System.out.println("\n" + filesFolders[i].getName() + " is directory");
				jsonFiles = filesFolders[i].listFiles();
				
				for(int k=0; k < jsonFiles.length; k++) {
					System.out.println(jsonFiles[k].getName() + " tested");
					
					if(jsonFiles[k].getName() == filename) {
						dir = filesFolders[i].getName();
						System.out.println("\tFound");
						return dir;
					}
				}
			}
		}
		
		
		return "";
	}
}
