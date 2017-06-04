/*
JS code

// Assignment with different types
var number = 2;
var string1 = "abc";
var string2 = "2.0";
var bool = true;
console.log(number, string1, string2, bool);
console.log();
var var1 = number+string1;
var var2 = number+string2;
var var3 = number+bool;
var var4 = string1+string2;
var var5 = string1+bool;
var var6 = string2+bool;
console.log(var1, var2, var3, var4, var5, var6);
*/

package output;

public class Asgn4 {
	public static int number;
	public static String string1;
	public static String string2;
	public static boolean bool;
	public static String var1;
	public static String var2;
	public static int var3;
	public static String var4;
	public static String var5;
	public static String var6;

	public static void main(String args[]) {
		number = 2;
		string1 = "abc";
		string2 = "2.0";
		bool = true;
		System.out.println(number); System.out.println(string1); System.out.println(string2); System.out.println(bool);
		System.out.println();
		var1 = (number + string1);
		var2 = (number + string2);
		var3 = (number);
		var4 = (string1 + string2);
		var5 = (string1 + bool);
		var6 = (string2 + bool);
		System.out.println(var1); System.out.println(var2); System.out.println(var3); System.out.println(var4); System.out.println(var5); System.out.println(var6);
	}
}
