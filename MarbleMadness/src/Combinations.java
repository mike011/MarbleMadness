class Combinations {

	String input;
	StringBuilder cur;

	private void next(int pos, int reminder) {
		cur.append(input.charAt(pos));

		if (reminder == 1) {
			System.out.println(cur);
		} else {
			for (int i = pos + 1; i + reminder - 1 <= input.length(); i++)
				next(i, reminder - 1);
		}
		cur.deleteCharAt(cur.length() - 1);
	}

	public void generate(String input) {
		cur = new StringBuilder();
		this.input = input;
		for (int length = 1; length <= input.length(); length++)
			for (int pos = 0; pos + length <= input.length(); pos++)
				next(pos, length);
	}
	
	public static void main(String args[]) {
		new Combinations().generate("12345");	
	}
}