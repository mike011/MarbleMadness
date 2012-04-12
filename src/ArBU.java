import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ArBU {

	// static final String colours[] = { "red", "green", "blue", "red+green",
	// "red+blue", "green+blue" };

	static final String colours[] = { "red", "green"}; // ,
																// "red+green"};

	public static void main(String[] args) {
		StringBuffer s = new StringBuffer();
		for (int x = 0; x < colours.length; x++) {
			s.append(x);
		}

		Set<String> allIndexes = new TreeSet<String>();
		Set<String> permutation = permutation(s.toString());
		for (String p : permutation) {
			for (int y = 0; y < s.length(); y++) {
				allIndexes.addAll(permutation(p.substring(y)));
			}
		}

		Set<String> allColours = new TreeSet<String>();
		for (String a : allIndexes) {
			StringBuffer out = new StringBuffer();
			boolean good = lookForDups(a, out);
			if (good) {
				allColours.add(out.toString().trim());
				// System.out.println(out);
			}
		}
		List<String> allCombos = new ArrayList<String>();
//		for (String out : allColours) {
//			String[] split = out.split(" ");
//			for (int x = 0; x < split.length - 1; x++) {
//				String stringReverse = swap(split, x);
//				boolean found = false;
//				for (String c : allCombos) {
//					if (c.contains(stringReverse)) {
//						found = true;
//						break;
//					}
//				}
//				if (!found) {
//					String no = out.replaceFirst(" ", "+");
//					allCombos.add(no);
//				}
//
//			}
//		}
		for (String out : allColours) {
			String[] split = out.split(" ");
			//System.out.println(out);
			for (int x = 0; x < split.length - 1; x++) {
				String o = split[x] + " " + split[x+1];
				String f = split[x] + "+" + split[x+1];
				String r = split[x+1] + "+" + split[x];
				String no = out.replace(o, f);
				String nor = out.replace(o, r);
				if(!allCombos.contains(nor)) {
					allCombos.add(no);
				}
			}
		}
		allColours.addAll(allCombos);
		for (String out : allColours) {
			System.out.println(out);
		}
		System.out.println(allColours.size());
	}

	private static String swap(String[] split, int x) {
		return split[x+1] + "+" + split[x];
	}

	private static boolean lookForDups(String a, StringBuffer out) {
		boolean good = true;
		for (char c : a.toCharArray()) {

			String string = colours[Character.digit(c, 10)];
			if (out.toString().contains(string)) {
				good = false;
				break;
			} else if (string.contains("+")) {
				String splits[] = string.split("\\+");
				for (String split : splits) {
					if (out.toString().contains(split)) {
						good = false;
						break;
					}
				}
			}

			out.append(string);
			out.append(' ');
		}
		return good;
	}

	public static Set<String> permutation(String str) {
		return permutation("", str, new TreeSet<String>());
	}

	private static Set<String> permutation(String prefix, String str,
			Set<String> result) {
		int n = str.length();
		if (n == 0) {
			result.add(prefix);
		} else {
			for (int i = 0; i < n; i++) {
				String newPrefix = prefix + str.charAt(i);
				String newString = str.substring(0, i)
						+ str.substring(i + 1, n);
				permutation(newPrefix, newString, result);
			}
		}
		return result;

	}
}