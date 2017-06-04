public class Test2 {
	public static int[] array;

	public static void main(String args[]) {
		array = new int[]{1, 3, 2, 4, 6, 7, 2, 4, 5, 1};

		System.out.println(HasBigger.hasBigger(3)); System.out.println(HasBigger.hasBigger(6)); System.out.println(HasBigger.hasBigger(8));
	}
	public static class HasBigger {
		public static int i;

		public static boolean hasBigger(int number) {
			for (i = 0; i < array.length; i++) {
				if (array[i] > number) {
					return true;
				}
			}
			return false;
		}
	}
}
