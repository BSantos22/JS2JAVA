package js2java;

import java.io.File;
import java.util.Scanner;

import com.google.gson.JsonObject;

public class JS2Java {
	public JS2Java() {}
	
	public static void main(String args[]) {
		String varsFilepath;
		String jsFilepath;
		String dir,dir2;
		
		if (args.length != 2) {
			Scanner reader = new Scanner(System.in);
			System.out.println("Javascript code file name: ");
			jsFilepath = reader.nextLine();
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
			varsFilepath = reader.nextLine();
			reader.close();
		}
		else {
			jsFilepath = args[0];
			varsFilepath = args[1];
		}
		
		dir = checkFileDirectory(jsFilepath);
		
		
		
		File jsFile = new File(dir + "/" + jsFilepath);
		JsonReader parser = new JsonReader();
		JsonObject js = parser.parse(jsFile);
		
		File varsFile = new File("files/" + varsFilepath + ".json");
		JsonObject vars = parser.parse(varsFile);
		
		TypeInferrer inferrer = new TypeInferrer(js, vars);
		System.out.println(inferrer.getFunctions());
		Output output = new Output(js, inferrer, jsFilepath.replace(".json", ""));
		output.start();
	}
	/*
	public Map<String, String> test(String jsFile, String typesFile) {
		JsonReader parser = new JsonReader();
		JsonObject js = parser.parse(new File(jsFile));
		JsonObject vars = parser.parse(new File(typesFile));
		TypeInferrer inferrer = new TypeInferrer(js, vars);
	}
	*/
	
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
}
