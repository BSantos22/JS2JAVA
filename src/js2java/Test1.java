public class Test1 {
	public static int[] array;

	public static void main(String args[]) {
		array = {1, 3, 2, 4, 6, 7, 2, 4, 5, 1};
		System.out.println(Max.max());
	}
	public static class Max {
		public static int max;
		public static int i;

		public static int max() {
			max = -1;
			for (; i < array.length; i++) {
				if (array[i] > max) {
					max = array[i];
				}
			}
			return max;
		}
	}
}
