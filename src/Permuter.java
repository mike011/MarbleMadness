public class Permuter {

	public static void main(String[] args) {
		String list = "1234";

		splitter(list, 4);
	}

	private static void splitter(String list, int i) {
		if (i == 0) {
			return;
		}
		String substring = list.substring(0, i);
		extracted(substring);
		splitter(list, i - 1);
	}

	private static void extracted(String substring) {
		System.out.println(substring);
	}

	
}
