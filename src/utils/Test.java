package utils;

import js2java.JS2Java;

public class Test {
	public static void main(String args[]) {
		for (int i = 1; i <= 4; i++) {
			asgn(i);
		}
	}
	
	public static void asgn(int i) {
		String[] args = {"asgn" + i};
		JS2Java.main(args);
	}
}
