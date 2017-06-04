/*
JS code

// Assignments with order of operations
var a = (5+3.0)*2;
var b = 5+3.0*2;
console.log(a, b);
*/

package output;

public class Asgn3 {
	public static double a;
	public static double b;

	public static void main(String args[]) {
		a = ((5 + 3.0) * 2);
		b = (5 + (3.0 * 2));
		System.out.println(a); System.out.println(b);
	}
}
