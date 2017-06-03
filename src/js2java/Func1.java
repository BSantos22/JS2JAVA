package js2java;

public class Func1 {
	public static int a, b; // variables declared in this function in js
	  
	public static void main(String[] args) {
		Func1.func1();
	}
	  
	public static void func1() {
		a=4;
		b=2;
		A.a();
		System.out.println(a+b);
	}
	  
	public static class A {
		public static int b;
		  
		public static void a() {
			a = 1;
			b = 1;
			B.b();
		}
	    
		public static class B {
			public static void b() {
				a = 7;
			}
		}
	}
}