public class Main {
	public static void main(String[] args) {
		int a = 26;
		int b = -5;

		System.out.println(compute(a, b));
	}

	private static int compute(int a, int b) {
		return Utility.twice(Utility.add(a, b));
	}
}
