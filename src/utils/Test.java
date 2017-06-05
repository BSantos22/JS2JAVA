package utils;

import js2java.JS2Java;

public class Test {
	public static void main(String args[]) {
		int DECL_MAX = 1;
		int IF_MAX = 4;
		int LOOP_MAX = 3;
		int ASGN_MAX = 7;
		int CONV_MAX = 5;
		int ARRAY_MAX = 3;
		
		// Declarations and types
		for (int i = 1; i <= DECL_MAX; i++) {
			String[] s = {"decl" + i};
			JS2Java.main(s);
			System.out.println("for Decl"+i);
		}
		
		// Conditionals
		for (int i = 1; i <= IF_MAX; i++) {
			String[] s = {"if" + i};
			JS2Java.main(s);
			System.out.println("for If"+i);
		}
		
		// Loops
		for (int i = 1; i <= LOOP_MAX; i++) {
			String[] s = {"loop" + i};
			JS2Java.main(s);
			System.out.println("for Loop"+i);
		}

		// Assignments and expressions
		for (int i = 1; i <= ASGN_MAX; i++) {
			String[] s = {"asgn" + i};
			JS2Java.main(s);
			System.out.println("for Asgn"+i);
		}
		
		// Conversion
		for (int i = 1; i <= CONV_MAX; i++) {
			String[] s = {"conv" + i};
			JS2Java.main(s);
			System.out.println("for Conv"+i);
		}
		
		
		// Arrays
		for (int i = 2; i <= ARRAY_MAX; i++) {
			String[] s = {"array" + i};
			JS2Java.main(s);
			System.out.println("for Array"+i);
		}
	}
}
