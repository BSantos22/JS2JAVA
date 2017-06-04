public class Func1 {
	public static int x;
	public static int y;
	public static int z;
	public static int[] array;

	public static void main(String[] args) {
		x = 4;
		y = 6;
		z = 2;
		array = {4, 2, 1, 3};
		A.a(x, y);
	}
	public static class A {
		public static int x;
		public static int y;
		public static int abc;
		public static int xyz;

		public static int[] a() {
			x = (3) * (array[ind]);
			y = (2) * (array[ind]);
			abc = 2;
			xyz = ind;
			if (bool) {
				return B.b();
			}
			return {x, y, z};
		}
		public static class B {
			public static int x;

			public static int[] b() {
				x = (2) * (abc);
				return {x, y, z};
			}
		}
	}
	public static class C {

		public static int[] c() {
			z = 4;
			return {x, y, z};
		}
	}
}
