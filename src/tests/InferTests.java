package tests;

import static org.junit.Assert.assertEquals;
import java.util.Map;
import org.junit.Test;
import js2java.JS2Java;

public class InferTests {

	@Test
	public void arrays1() {
		JS2Java test = new JS2Java();
		Map<String, String> programVars = test.test("files/arrays/js1.json", "files/types1.json");
		assertEquals(programVars.toString(), "{a=int, b=double, c=double, e=Object[]}");
		
		System.out.println("var c = 3;\nvar e = [c, \"string\", 3.0];");
		System.out.println(programVars);
		System.out.println();
	}
	
	@Test
	public void arrays2() {
		JS2Java test = new JS2Java();
		Map<String, String> programVars = test.test("files/arrays/js2.json", "files/types1.json");
		assertEquals(programVars.toString(), "{a=int, b=double, c=double, d=Object[], e=Object[]}");
		
		System.out.println("var c = 3;\nvar e = [c, \"string\", 3.0];\nvar d = e;\ne = [];");
		System.out.println(programVars);
		System.out.println();
	}

	@Test
	public void arrays3() {
		JS2Java test = new JS2Java();
		Map<String, String> programVars = test.test("files/arrays/js3.json", "files/types1.json");
		assertEquals(programVars.toString(), "{a=int, b=double, c=double, d=Object[], e=int}");
		
		System.out.println("var d = [1, 2, 3, 4, 5];\nvar e = d[3];");
		System.out.println(programVars);
		System.out.println();
	}

	@Test
	public void arrays4() {
		JS2Java test = new JS2Java();
		Map<String, String> programVars = test.test("files/arrays/js4.json", "files/types1.json");
		assertEquals(programVars.toString(), "{a=int, b=double, c=double, d=Object[], e=int, f=int}");
		
		System.out.println("var d = [1, 2, 3, 4, 5];\nvar e = d[f];");
		System.out.println(programVars);
		System.out.println();
	}

	@Test
	public void assignments1() {
		JS2Java test = new JS2Java();
		Map<String, String> programVars = test.test("files/assignments/js1.json", "files/types1.json");
		assertEquals(programVars.toString(), "{a=int, b=double, c=double, d=int}");
		
		System.out.println("var c = 3;\nvar a = 4+2;\nd += d*a;");
		System.out.println(programVars);
		System.out.println();
	}
	
	@Test
	public void assignments2() {
		JS2Java test = new JS2Java();
		Map<String, String> programVars = test.test("files/assignments/js2.json", "files/types1.json");
		assertEquals(programVars.toString(), "{a=int, b=double, c=double, d=int, e=int}");
		
		System.out.println("var d = 3;\nvar e;\ne = d*3;");
		System.out.println(programVars);
		System.out.println();
	}
	
	@Test
	public void conditions1() {
		JS2Java test = new JS2Java();
		Map<String, String> programVars = test.test("files/conditions/js1.json", "files/types1.json");
		assertEquals(programVars.toString(), "{a=int, b=double, c=double, i=int}");
		
		System.out.println("if (i < 10) {}");
		System.out.println(programVars);
		System.out.println();
	}
	
	@Test
	public void conditions2() {
		JS2Java test = new JS2Java();
		Map<String, String> programVars = test.test("files/conditions/js2.json", "files/types1.json");
		assertEquals(programVars.toString(), "{a=int, b=double, c=double, s=boolean}");
		
		System.out.println("if (s) {}");
		System.out.println(programVars);
		System.out.println();
	}
	
	@Test
	public void declarations1() {
		JS2Java test = new JS2Java();
		Map<String, String> programVars = test.test("files/declarations/js1.json", "files/types1.json");
		assertEquals(programVars.toString(), "{a=int, b=double, c=double, d=double}");
		
		System.out.println("var d = 3*2*2.0;");
		System.out.println(programVars);
		System.out.println();
	}
	
	@Test
	public void declarations2() {
		JS2Java test = new JS2Java();
		Map<String, String> programVars = test.test("files/declarations/js2.json", "files/types1.json");
		assertEquals(programVars.toString(), "{a=int, b=double, c=double, d=double}");
		
		System.out.println("var a = 2;\nvar d = a*2*2.0;");
		System.out.println(programVars);
		System.out.println();
	}
	
	@Test
	public void declarations3() {
		JS2Java test = new JS2Java();
		Map<String, String> programVars = test.test("files/declarations/js3.json", "files/types1.json");
		assertEquals(programVars.toString(), "{a=int, b=double, c=double, d=int}");
		
		System.out.println("var a = 2;\nvar d = a*2*2;");
		System.out.println(programVars);
		System.out.println();
	}
	
	@Test
	public void declarations4() {
		JS2Java test = new JS2Java();
		Map<String, String> programVars = test.test("files/declarations/js4.json", "files/types1.json");
		assertEquals(programVars.toString(), "{a=int, b=double, c=double, d=double}");
		
		System.out.println("var b = 4;\nvar d = b*2*2;");
		System.out.println(programVars);
		System.out.println();
	}
	
	@Test
	public void declarations5() {
		JS2Java test = new JS2Java();
		Map<String, String> programVars = test.test("files/declarations/js5.json", "files/types1.json");
		assertEquals(programVars.toString(), "{a=int, b=double, c=double, d=double, e=double}");
		
		System.out.println("var e = 3.0;\nvar d = e*2*2;");
		System.out.println(programVars);
		System.out.println();
	}
	
	@Test
	public void declarations6() {
		JS2Java test = new JS2Java();
		Map<String, String> programVars = test.test("files/declarations/js6.json", "files/types1.json");
		assertEquals(programVars.toString(), "{a=int, b=double, c=double, d=int, e=int}");
		
		System.out.println("var e = 3;\nvar d = e*2*2;");
		System.out.println(programVars);
		System.out.println();
	}
	
	@Test
	public void declarations7() {
		JS2Java test = new JS2Java();
		Map<String, String> programVars = test.test("files/declarations/js7.json", "files/types1.json");
		assertEquals(programVars.toString(), "{a=int, b=double, c=double, d=double, e=double}");
		
		System.out.println("var e;\nvar d = e*2*2;\nvar e = 2.0;");
		System.out.println(programVars);
		System.out.println();
	}

	@Test
	public void declarations8() {
		JS2Java test = new JS2Java();
		Map<String, String> programVars = test.test("files/declarations/js8.json", "files/types1.json");
		assertEquals(programVars.toString(), "{a=int, b=double, c=double, s=boolean, d=boolean, e=boolean}");
		
		System.out.println("var d = true;\nvar s = d && e;");
		System.out.println(programVars);
		System.out.println();
	}
	
	@Test
	public void loops1() {
		JS2Java test = new JS2Java();
		Map<String, String> programVars = test.test("files/loops/js1.json", "files/types1.json");
		assertEquals(programVars.toString(), "{a=int, b=double, c=double, i=int}");
		
		System.out.println("for (var i = 0; i < 10; i++) {}");
		System.out.println(programVars);
		System.out.println();
	}
	
	@Test
	public void loops2() {
		JS2Java test = new JS2Java();
		Map<String, String> programVars = test.test("files/loops/js2.json", "files/types1.json");
		assertEquals(programVars.toString(), "{a=int, b=double, c=double, s=boolean}");
		
		System.out.println("while(s) {}");
		System.out.println(programVars);
		System.out.println();
	}
}
