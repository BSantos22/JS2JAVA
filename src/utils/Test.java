package utils;

import js2java.JS2Java;

public class Test {
	public static void main(String args[]) {
		for (int i = 1; i <= 4; i++) {
			System.out.println("Asgn"+i);
			asgn(i);
		}
		
		for (int i = 1; i <= 5; i++) {
			System.out.println("Conv"+i);
			conv(i);
		}
	}
	
	public static void asgn(int i) {
		String[] args = {"asgn" + i};
		JS2Java.main(args);
	}
	
	public static void conv(int i) {
		System.out.println();
		String[] args = {"conv" + i};
		JS2Java.main(args);
	}
}
